package com.example.medhoracompose.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.medhoracompose.MedHoraApplication
import com.example.medhoracompose.data.repository.MedHoraRepositorioCompartilhado
import com.example.medhoracompose.data.repository.PreferenciasRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import com.example.medhoracompose.data.repository.MedicamentoRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class AcoesNotificacaoReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action !in setOf(ACTION_TOMADA, ACTION_ADIAR)) return
        val resultado = goAsync()
        val app = context.applicationContext as MedHoraApplication
        app.applicationScope.launch {
            try {
                withTimeout(8_000) {
                    app.carregado.first { it }
                    processar(context, intent)
                }
            } catch (erro: Exception) {
                if (erro is CancellationException && erro !is TimeoutCancellationException) throw erro
                Log.e("MedHora", "Falha na ação do lembrete", erro)
                Toast.makeText(context, "Não foi possível concluir. Abra o MedHora e tente novamente.", Toast.LENGTH_LONG).show()
            } finally {
                resultado.finish()
            }
        }
    }

    internal suspend fun processar(context: Context, intent: Intent, repository: MedicamentoRepository = MedHoraRepositorioCompartilhado.repository) {
        val chave = intent.getStringExtra(AgendadorLembretes.EXTRA_CHAVE_DOSE) ?: return
        // Consulta os dados atuais: uma notificação antiga não pode registrar uma dose
        // removida, alterada, futura ou que já foi tomada.
        val dose = AgendadorLembretes.obterDosePendente(chave, repository)
        if (dose == null) {
            Notificacoes.cancelarDose(context, chave)
            return
        }
        when (intent.action) {
            ACTION_TOMADA -> {
                repository.marcarDoseTomada(dose)
                AgendadorLembretes.cancelarAdiado(context, chave)
                Notificacoes.mostrarConfirmacao(context, dose)
            }
            ACTION_ADIAR -> {
                if (!PreferenciasRepository.lembretesAtivados || !PreferenciasRepository.notificacoesAtivadas) {
                    Notificacoes.cancelarDose(context, chave)
                    return
                }
                AgendadorLembretes.adiar(context, dose)
                Notificacoes.ocultarDose(context, chave)
                Toast.makeText(context, "Vamos lembrar novamente em 10 minutos.", Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        const val ACTION_TOMADA = "com.example.medhoracompose.DOSE_TOMADA"
        const val ACTION_ADIAR = "com.example.medhoracompose.ADIAR_DOSE"
    }
}
