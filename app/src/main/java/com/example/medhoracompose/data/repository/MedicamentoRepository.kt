package com.example.medhoracompose.data.repository

import com.example.medhoracompose.data.local.MedHoraDao
import com.example.medhoracompose.data.local.paraEntidade
import com.example.medhoracompose.data.model.Dose
import com.example.medhoracompose.data.model.Medicamento
import java.time.LocalDate
import java.time.LocalDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

// Agrupa os dois resultados que a interface precisa observar juntos.
data class DadosMedicamentos(
    val medicamentos: List<Medicamento>,
    val dosesRegistradas: List<Dose>
)

/**
 * Intermediário entre as telas e o DAO. Recebe modelos do aplicativo, transforma-os
 * em entidades para salvar e transforma as entidades consultadas de volta em modelos.
 * Antes esta classe mantinha os dados apenas em listas/mapas; agora o banco é a fonte.
 */
class MedicamentoRepository(private val dao: MedHoraDao, scope: CoroutineScope) {
    // O banco é a fonte dos dados. Cada alteração gera uma nova emissão para a UI.
    // null significa que a primeira leitura ainda não terminou.
    // combine reúne a última lista de medicamentos e a última lista de doses tomadas.
    // stateIn compartilha esse resultado e mantém seu valor mais recente em memória.
    // Eagerly inicia a observação imediatamente, dentro do escopo da Application.
    // A diferença entre null e listas vazias é importante: carregando vs. banco vazio.
    val dados = combine(dao.observarMedicamentos(), dao.observarDoses()) { medicamentos, doses ->
        DadosMedicamentos(medicamentos.map { it.paraModelo() }, doses.map { it.paraModelo() })
    }.stateIn(scope, SharingStarted.Eagerly, null)

    val medicamentos: List<Medicamento>
        get() = dados.value?.medicamentos.orEmpty()

    suspend fun adicionar(medicamento: Medicamento) = dao.adicionar(medicamento.paraEntidade())
    suspend fun atualizar(medicamento: Medicamento) = dao.atualizar(medicamento.paraEntidade())
    suspend fun remover(medicamentoId: Long) = dao.remover(medicamentoId)

    fun buscarPorId(medicamentoId: Long): Medicamento? = medicamentos.find { it.id == medicamentoId }

    // As doses previstas são CALCULADAS a partir dos horários e da frequência.
    // Não criamos uma linha no banco para cada dose futura. Quando há um registro
    // da mesma chave em doses_tomadas, ele substitui a ocorrência calculada na lista.
    fun obterDoses(data: LocalDate = LocalDate.now()): List<Dose> {
        val registros = obterDosesRegistradas().associateBy { it.chave }
        return medicamentos
            .filter { !data.isBefore(it.criadoEm) && it.frequencia.ocorreEm(data) }
            .flatMap { medicamento ->
                medicamento.horarios.map { horario ->
                    val dose = Dose(medicamento.id, medicamento.nome, medicamento.dosagem,
                        medicamento.cor, data, horario)
                    registros[dose.chave] ?: dose
                }
            }.sortedBy { it.horario }
    }

    // O INSERT só acontece ao confirmar a tomada; tomadaEm guarda o horário real,
    // enquanto dose.horario continua sendo o horário originalmente programado.
    suspend fun marcarDoseTomada(dose: Dose, momento: LocalDateTime = LocalDateTime.now()) =
        dao.registrarDoseSeAusente(dose.paraEntidade(momento))

    suspend fun desmarcarDose(dose: Dose) =
        dao.desmarcarDose(dose.medicamentoId, dose.data, dose.horario)

    fun obterDosesRegistradas(): List<Dose> = dados.value?.dosesRegistradas.orEmpty()

    suspend fun limpar() = dao.limpar()
}
