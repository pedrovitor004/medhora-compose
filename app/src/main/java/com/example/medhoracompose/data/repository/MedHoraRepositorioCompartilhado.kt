package com.example.medhoracompose.data.repository

/**
 * Disponibiliza uma única instância do repositório
 * para as telas e os componentes de notificação.
 */
object MedHoraRepositorioCompartilhado {
    val repository: MedicamentoRepository by lazy {
        MedicamentoRepository()
    }
}
