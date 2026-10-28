package com.example.medhoracompose

import android.app.Application
import com.example.medhoracompose.notifications.Notificacoes

class MedHoraApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Notificacoes.criarCanal(this)
    }
}