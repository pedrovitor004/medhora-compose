package com.example.medhoracompose.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [MedicamentoEntity::class, DoseEntity::class], version = 1, exportSchema = true)
@TypeConverters(Conversores::class)
abstract class MedHoraDatabase : RoomDatabase() {
    abstract fun medHoraDao(): MedHoraDao

    companion object {
        fun criar(context: Context): MedHoraDatabase = Room.databaseBuilder(
            context.applicationContext,
            MedHoraDatabase::class.java,
            "medhora.db"
        ).build()
    }
}
