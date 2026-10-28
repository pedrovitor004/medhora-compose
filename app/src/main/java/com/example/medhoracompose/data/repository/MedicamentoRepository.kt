package com.example.medhoracompose.data.repository

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import com.example.medhoracompose.data.model.CorMedicamento
import com.example.medhoracompose.data.model.Dose
import com.example.medhoracompose.data.model.Frequencia
import com.example.medhoracompose.data.model.Medicamento
import java.time.LocalDate
import java.time.LocalDateTime

class MedicamentoRepository {

    // Lista observável pela interface Compose.
    private val listaMedicamentos = mutableStateListOf<Medicamento>()

    // Guarda os registros de doses já marcadas como tomadas.
    private val dosesRegistradas = mutableStateMapOf<String, Dose>()

    val medicamentos: List<Medicamento>
        get() = listaMedicamentos.toList()

    fun adicionar(medicamento: Medicamento) {
        if (listaMedicamentos.none { it.id == medicamento.id }) {
            listaMedicamentos.add(medicamento)
        }
    }

    fun atualizar(medicamento: Medicamento) {
        val indice = listaMedicamentos.indexOfFirst {
            it.id == medicamento.id
        }

        if (indice >= 0) {
            listaMedicamentos[indice] = medicamento
        }
    }

    fun remover(medicamentoId: Long) {
        listaMedicamentos.removeAll {
            it.id == medicamentoId
        }
    }

    fun buscarPorId(medicamentoId: Long): Medicamento? {
        return listaMedicamentos.find {
            it.id == medicamentoId
        }
    }

    fun obterDoses(data: LocalDate = LocalDate.now()): List<Dose> {
        return listaMedicamentos
            .filter { it.frequencia.ocorreEm(data) }
            .flatMap { medicamento ->
                medicamento.horarios.map { horario ->
                    val doseBase = Dose(
                        medicamentoId = medicamento.id,
                        nome = medicamento.nome,
                        dosagem = medicamento.dosagem,
                        cor = medicamento.cor,
                        data = data,
                        horario = horario
                    )

                    dosesRegistradas[doseBase.chave] ?: doseBase
                }
            }
            .sortedBy { it.horario }
    }

    fun marcarDoseTomada(
        dose: Dose,
        momento: LocalDateTime = LocalDateTime.now()
    ) {
        dosesRegistradas[dose.chave] = dose.copy(
            tomadaEm = momento
        )
    }

    fun desmarcarDose(dose: Dose) {
        val doseRegistrada = dosesRegistradas[dose.chave]

        if (doseRegistrada != null) {
            dosesRegistradas[dose.chave] =
                doseRegistrada.copy(tomadaEm = null)
        }
    }

    fun obterDosesRegistradas(): List<Dose> {
        return dosesRegistradas.values
            .sortedWith(compareBy({ it.data }, { it.horario }))
    }

    fun limpar() {
        listaMedicamentos.clear()
        dosesRegistradas.clear()
    }
}
