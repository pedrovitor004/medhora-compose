package com.example.medhoracompose

import android.app.Application
import com.example.medhoracompose.data.repository.MedHoraRepositorioCompartilhado
import com.example.medhoracompose.notifications.AgendadorLembretes
import com.example.medhoracompose.notifications.Notificacoes

class MedHoraApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Notificacoes.criarCanal(this)

        // Inicializa o repositório compartilhado.
        MedHoraRepositorioCompartilhado.repository

        // Atualiza os alarmes conforme as preferências atuais.
        AgendadorLembretes.atualizar(this)
    }
}