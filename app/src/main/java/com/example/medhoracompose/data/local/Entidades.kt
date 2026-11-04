package com.example.medhoracompose.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.medhoracompose.data.model.CorMedicamento
import com.example.medhoracompose.data.model.Dose
import com.example.medhoracompose.data.model.Frequencia
import com.example.medhoracompose.data.model.Medicamento
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Cada instância representa uma linha da tabela medicamentos.
 * O modelo usado pelas telas continua sendo Medicamento; esta classe descreve
 * a estrutura persistida. As conversões abaixo fazem a ligação entre os dois.
 */
@Entity(tableName = "medicamentos")
data class MedicamentoEntity(
    @PrimaryKey val id: Long,
    val nome: String,
    val dosagem: String,
    val horarios: List<LocalTime>,
    val frequencia: Frequencia,
    val observacoes: String,
    val cor: CorMedicamento,
    val criadoEm: LocalDate
) {
    fun paraModelo() = Medicamento(id, nome, dosagem, horarios, frequencia, observacoes, cor, criadoEm)
}

fun Medicamento.paraEntidade() = MedicamentoEntity(
    id, nome, dosagem, horarios, frequencia, observacoes, cor, criadoEm
)

// Guarda apenas doses tomadas, com um retrato do nome, dosagem e cor naquele momento.
// A combinação medicamentoId + data + horario identifica uma ocorrência única.
// Não há chave estrangeira nem exclusão em cascata: apagar o cadastro do medicamento
// não deve apagar os registros anteriores do histórico.
@Entity(tableName = "doses_tomadas", primaryKeys = ["medicamentoId", "data", "horario"])
data class DoseEntity(
    val medicamentoId: Long,
    val nome: String,
    val dosagem: String,
    val cor: CorMedicamento,
    val data: LocalDate,
    val horario: LocalTime,
    val tomadaEm: LocalDateTime
) {
    fun paraModelo() = Dose(medicamentoId, nome, dosagem, cor, data, horario, tomadaEm)
}

fun Dose.paraEntidade(momento: LocalDateTime) = DoseEntity(
    medicamentoId, nome, dosagem, cor, data, horario, momento
)
