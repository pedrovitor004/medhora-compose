package com.example.medhoracompose.data.repository

import android.content.Context
import com.example.medhoracompose.data.local.MedHoraDatabase
import kotlinx.coroutines.CoroutineScope

/** Uma única instância do banco e do repositório, criada pela Application. */
object MedHoraRepositorioCompartilhado {
    lateinit var repository: MedicamentoRepository
        private set

    fun inicializar(context: Context, scope: CoroutineScope) {
        val database = MedHoraDatabase.criar(context)
        repository = MedicamentoRepository(database.medHoraDao(), scope)
    }
}
