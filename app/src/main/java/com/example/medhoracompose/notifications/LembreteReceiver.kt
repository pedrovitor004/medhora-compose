package com.example.medhoracompose.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class LembreteReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == AgendadorLembretes.ACTION_LEMBRETE) {
            AgendadorLembretes.entregar(context, intent)
        }
    }
}
