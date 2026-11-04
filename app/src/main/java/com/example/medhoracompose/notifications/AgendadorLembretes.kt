package com.example.medhoracompose.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.net.Uri
import com.example.medhoracompose.data.model.Dose
import com.example.medhoracompose.data.model.Medicamento
import com.example.medhoracompose.data.repository.MedHoraRepositorioCompartilhado
import com.example.medhoracompose.data.repository.PreferenciasRepository
import com.example.medhoracompose.data.repository.MedicamentoRepository
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

object AgendadorLembretes {

    const val ACTION_LEMBRETE =
        "com.example.medhoracompose.LEMBRETE"

    const val ACTION_ADIADO = "com.example.medhoracompose.LEMBRETE_ADIADO"

    const val EXTRA_CHAVE_DOSE = "chave_dose"

    fun atualizar(context: Context) {
        val appContext = context.applicationContext
        val alarmManager =
            appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        cancelarTodos(appContext, alarmManager)

        if (!PreferenciasRepository.lembretesAtivados ||
            !PreferenciasRepository.notificacoesAtivadas ||
            !Notificacoes.permitidas(appContext)
        ) {
            return
        }

        val agora = LocalDateTime.now()

        MedHoraRepositorioCompartilhado.repository.medicamentos
            .forEach { medicamento ->
                agendarProximaDose(
                    appContext,
                    alarmManager,
                    medicamento,
                    agora
                )
            }
    }

    private fun agendarProximaDose(
        context: Context,
        alarmManager: AlarmManager,
        medicamento: Medicamento,
        agora: LocalDateTime
    ) {
        val limite = agora.toLocalDate().plusDays(7)

        var data = maxOf(agora.toLocalDate(), medicamento.criadoEm)

        while (!data.isAfter(limite)) {
            if (medicamento.frequencia.ocorreEm(data)) {
                for (horario in medicamento.horarios.sorted()) {
                    val momento = data.atTime(horario)

                    if (momento.isAfter(agora)) {
                        val dose = Dose(
                            medicamentoId = medicamento.id,
                            nome = medicamento.nome,
                            dosagem = medicamento.dosagem,
                            cor = medicamento.cor,
                            data = data,
                            horario = horario
                        )

                        agendar(context, alarmManager, dose)
                    }
                }
            }

            data = data.plusDays(1)
        }
    }

    private fun agendar(
        context: Context,
        alarmManager: AlarmManager,
        dose: Dose
    ) {
        val intent = Intent(context, LembreteReceiver::class.java).apply {
            action = ACTION_LEMBRETE
            putExtra(EXTRA_CHAVE_DOSE, dose.chave)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            dose.chave.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val instante = dose.data.atTime(dose.horario)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        agendarAlarme(alarmManager, AlarmManager.RTC_WAKEUP, instante, pendingIntent)
    }

    private fun agendarAlarme(manager: AlarmManager, tipo: Int, instante: Long, pendingIntent: PendingIntent) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !manager.canScheduleExactAlarms()) {
                manager.setAndAllowWhileIdle(tipo, instante, pendingIntent)
            } else {
                manager.setExactAndAllowWhileIdle(tipo, instante, pendingIntent)
            }
        } catch (_: SecurityException) {
            manager.setAndAllowWhileIdle(tipo, instante, pendingIntent)
        }
    }

    private fun intentAdiado(context: Context, chave: String) = Intent(context, LembreteReceiver::class.java).apply {
        action = ACTION_ADIADO
        data = Uri.parse("medhora://lembrete-adiado/${Uri.encode(chave)}")
        putExtra(EXTRA_CHAVE_DOSE, chave)
    }

    fun adiar(context: Context, dose: Dose) {
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pending = PendingIntent.getBroadcast(context, 0, intentAdiado(context, dose.chave),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        // Identidade separada: atualizar os hor?rios normais n?o remove o adiamento.
        agendarAlarme(manager, AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + 10 * 60 * 1000L, pending)
    }

    fun cancelarAdiado(context: Context, chave: String) {
        val pending = PendingIntent.getBroadcast(context, 0, intentAdiado(context, chave),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE) ?: return
        (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).cancel(pending)
        pending.cancel()
    }

    internal fun obterDosePendente(chave: String, repository: MedicamentoRepository = MedHoraRepositorioCompartilhado.repository): Dose? {
        val partes = chave.split("|")
        if (partes.size != 3) return null
        val id = partes[0].toLongOrNull() ?: return null
        val data = runCatching { LocalDate.parse(partes[1]) }.getOrNull() ?: return null
        val horario = runCatching { LocalTime.parse(partes[2]) }.getOrNull() ?: return null
        val dose = repository.obterDoses(data)
            .firstOrNull { it.medicamentoId == id && it.horario == horario } ?: return null
        if (dose.tomadaEm != null || data.atTime(horario).isAfter(LocalDateTime.now())) return null
        return dose
    }

    private fun cancelarTodos(
        context: Context,
        alarmManager: AlarmManager
    ) {
        val medicamentos =
            MedHoraRepositorioCompartilhado.repository.medicamentos

        medicamentos.forEach { medicamento ->
            cancelarAlarmesDoMedicamento(
                context,
                alarmManager,
                medicamento
            )
        }
    }

    private fun cancelarAlarmesDoMedicamento(
        context: Context,
        alarmManager: AlarmManager,
        medicamento: Medicamento
    ) {
        val hoje = LocalDate.now()
        val limite = hoje.plusDays(7)
        var data = maxOf(hoje, medicamento.criadoEm)

        while (!data.isAfter(limite)) {
            for (horario in medicamento.horarios) {
                val dose = Dose(
                    medicamentoId = medicamento.id,
                    nome = medicamento.nome,
                    dosagem = medicamento.dosagem,
                    cor = medicamento.cor,
                    data = data,
                    horario = horario
                )

                val intent = Intent(
                    context,
                    LembreteReceiver::class.java
                ).apply {
                    action = ACTION_LEMBRETE
                    putExtra(EXTRA_CHAVE_DOSE, dose.chave)
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    dose.chave.hashCode(),
                    intent,
                    PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
                )

                if (pendingIntent != null) {
                    alarmManager.cancel(pendingIntent)
                    pendingIntent.cancel()
                }
            }

            data = data.plusDays(1)
        }
    }


    fun entregar(context: Context, intent: Intent) {
        val chave = intent.getStringExtra(EXTRA_CHAVE_DOSE) ?: return
        if (!PreferenciasRepository.lembretesAtivados ||
            !PreferenciasRepository.notificacoesAtivadas || !Notificacoes.permitidas(context)) return

        val dose = obterDosePendente(chave)
        if (dose == null) {
            Notificacoes.cancelarDose(context, chave)
        } else {
            Notificacoes.mostrarDose(context, dose)
        }
        atualizar(context)
    }
}
