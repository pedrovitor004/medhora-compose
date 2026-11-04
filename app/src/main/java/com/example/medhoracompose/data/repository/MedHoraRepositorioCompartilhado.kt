package com.example.medhoracompose.data.repository

import android.content.Context
import com.example.medhoracompose.data.local.MedHoraDatabase
import kotlinx.coroutines.CoroutineScope

/**
 * Ponto compartilhado de acesso: a Application chama inicializar uma vez por processo.
 * Telas e receivers usam o mesmo repositório. O object sozinho não persiste nada;
 * quem mantém os dados após o processo terminar é o arquivo do banco aberto pelo Room.
 */
object MedHoraRepositorioCompartilhado {
    lateinit var repository: MedicamentoRepository
        private set

    fun inicializar(context: Context, scope: CoroutineScope) {
        val database = MedHoraDatabase.criar(context)
        repository = MedicamentoRepository(database.medHoraDao(), scope)
    }
}
