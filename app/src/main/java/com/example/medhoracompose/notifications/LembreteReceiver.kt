package com.example.medhoracompose.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.medhoracompose.MedHoraApplication
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class LembreteReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != AgendadorLembretes.ACTION_LEMBRETE) return
        val resultado = goAsync()
        val app = context.applicationContext as MedHoraApplication
        app.applicationScope.launch {
            try {
                // O alarme pode iniciar um processo novo: aguarda os dados do disco.
                withTimeout(8_000) {
                    app.carregado.first { it }
                    AgendadorLembretes.entregar(context, intent)
                }
            } catch (erro: Exception) {
                Log.e("MedHora", "Não foi possível entregar o lembrete", erro)
            } finally {
                resultado.finish()
            }
        }
    }
}
