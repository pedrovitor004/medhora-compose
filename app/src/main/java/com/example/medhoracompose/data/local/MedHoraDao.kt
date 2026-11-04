package com.example.medhoracompose.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de acesso ao banco: declaramos as operações e o Room gera sua implementação
 * durante a compilação. As telas não precisam escrever SQL ou abrir conexões.
 * Flow representa consultas observáveis; suspend permite aguardar as escritas sem
 * bloquear a interface. O Room cuida da execução assíncrona dessas operações.
 */
@Dao
abstract class MedHoraDao {
    @Query("SELECT * FROM medicamentos ORDER BY id")
    abstract fun observarMedicamentos(): Flow<List<MedicamentoEntity>>

    @Query("SELECT * FROM doses_tomadas ORDER BY data, horario")
    abstract fun observarDoses(): Flow<List<DoseEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun adicionar(medicamento: MedicamentoEntity)

    @Update
    abstract suspend fun atualizar(medicamento: MedicamentoEntity)

    @Query("DELETE FROM medicamentos WHERE id = :id")
    abstract suspend fun remover(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun registrarDose(dose: DoseEntity)

    // IGNORE preserva o primeiro registro se a mesma dose for confirmada duas vezes.
    // A chave composta da Entity é quem define o que conta como a mesma dose.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun registrarDoseSeAusente(dose: DoseEntity)

    @Query("DELETE FROM doses_tomadas WHERE medicamentoId = :id AND data = :data AND horario = :horario")
    abstract suspend fun desmarcarDose(id: Long, data: java.time.LocalDate, horario: java.time.LocalTime)

    @Query("DELETE FROM medicamentos")
    protected abstract suspend fun limparMedicamentos()

    @Query("DELETE FROM doses_tomadas")
    protected abstract suspend fun limparDoses()

    // As duas exclusões são uma única transação: ambas acontecem ou nenhuma fica salva.
    @Transaction
    open suspend fun limpar() {
        limparDoses()
        limparMedicamentos()
    }
}
