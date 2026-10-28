package com.example.medhoracompose.data.repository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object PreferenciasRepository {

    var lembretesAtivados by mutableStateOf(false)
        private set

    var notificacoesAtivadas by mutableStateOf(true)
        private set

    fun definirLembretes(ativados: Boolean) {
        lembretesAtivados = ativados
    }

    fun definirNotificacoes(ativadas: Boolean) {
        notificacoesAtivadas = ativadas
    }
}
