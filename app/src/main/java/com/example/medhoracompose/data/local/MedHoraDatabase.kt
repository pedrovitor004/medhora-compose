package com.example.medhoracompose.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * Declara as tabelas, os conversores e o ponto de acesso ao DAO.
 * version identifica a versão da estrutura, não a versão comercial do aplicativo.
 * exportSchema gera um JSON para documentar essa estrutura; ele não contém os dados.
 * Se as tabelas mudarem, será necessário versionar e planejar uma migração.
 */
@Database(entities = [MedicamentoEntity::class, DoseEntity::class], version = 1, exportSchema = true)
@TypeConverters(Conversores::class)
abstract class MedHoraDatabase : RoomDatabase() {
    abstract fun medHoraDao(): MedHoraDao

    companion object {
        // Abre o arquivo existente ou cria o banco no primeiro uso.
        // applicationContext evita manter uma Activity na memória por causa do banco.
        fun criar(context: Context): MedHoraDatabase = Room.databaseBuilder(
            context.applicationContext,
            MedHoraDatabase::class.java,
            "medhora.db"
        ).build()
    }
}
