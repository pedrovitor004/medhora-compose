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

data class DadosMedicamentos(
    val medicamentos: List<Medicamento>,
    val dosesRegistradas: List<Dose>
)

class MedicamentoRepository(private val dao: MedHoraDao, scope: CoroutineScope) {
    // O banco é a fonte dos dados. Cada alteração gera uma nova emissão para a UI.
    // null significa que a primeira leitura ainda não terminou.
    val dados = combine(dao.observarMedicamentos(), dao.observarDoses()) { medicamentos, doses ->
        DadosMedicamentos(medicamentos.map { it.paraModelo() }, doses.map { it.paraModelo() })
    }.stateIn(scope, SharingStarted.Eagerly, null)

    val medicamentos: List<Medicamento>
        get() = dados.value?.medicamentos.orEmpty()

    suspend fun adicionar(medicamento: Medicamento) = dao.adicionar(medicamento.paraEntidade())
    suspend fun atualizar(medicamento: Medicamento) = dao.atualizar(medicamento.paraEntidade())
    suspend fun remover(medicamentoId: Long) = dao.remover(medicamentoId)

    fun buscarPorId(medicamentoId: Long): Medicamento? = medicamentos.find { it.id == medicamentoId }

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

    suspend fun marcarDoseTomada(dose: Dose, momento: LocalDateTime = LocalDateTime.now()) =
        dao.registrarDose(dose.paraEntidade(momento))

    suspend fun desmarcarDose(dose: Dose) =
        dao.desmarcarDose(dose.medicamentoId, dose.data, dose.horario)

    fun obterDosesRegistradas(): List<Dose> = dados.value?.dosesRegistradas.orEmpty()

    suspend fun limpar() = dao.limpar()
}
