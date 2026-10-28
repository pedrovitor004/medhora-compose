package com.example.medhoracompose.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.medhoracompose.data.model.Dose
import com.example.medhoracompose.data.model.Medicamento
import com.example.medhoracompose.data.repository.MedHoraRepositorioCompartilhado
import com.example.medhoracompose.data.repository.PreferenciasRepository
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

object AgendadorLembretes {

    const val ACTION_LEMBRETE =
        "com.example.medhoracompose.LEMBRETE"

    private const val EXTRA_CHAVE_DOSE = "chave_dose"

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

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                !alarmManager.canScheduleExactAlarms()
            ) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    instante,
                    pendingIntent
                )
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    instante,
                    pendingIntent
                )
            }
        } catch (_: SecurityException) {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                instante,
                pendingIntent
            )
        }
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
        val repository = MedHoraRepositorioCompartilhado.repository

        if (!PreferenciasRepository.lembretesAtivados ||
            !PreferenciasRepository.notificacoesAtivadas ||
            !Notificacoes.permitidas(context)
        ) {
            return
        }

        val partes = chave.split("|")
        if (partes.size != 3) return

        val medicamentoId = partes[0].toLongOrNull() ?: return
        val data = runCatching {
            LocalDate.parse(partes[1])
        }.getOrNull() ?: return
        val horario = runCatching {
            LocalTime.parse(partes[2])
        }.getOrNull() ?: return

        val medicamento = repository.buscarPorId(medicamentoId) ?: return

        // Confirma que o horário ainda pertence ao medicamento.
        if (horario !in medicamento.horarios) return

        // Confirma que a frequência inclui a data do alarme.
        if (!medicamento.frequencia.ocorreEm(data)) return

        // Não mostra notificações para doses futuras ou já vencidas.
        val agora = LocalDateTime.now()
        val momentoDose = data.atTime(horario)
        if (momentoDose.isAfter(agora)) return

        // A consulta do repositório retorna as doses registradas.
        val doseRegistrada = repository.obterDosesRegistradas()
            .firstOrNull { it.chave == chave }

        if (doseRegistrada?.tomadaEm != null) {
            Notificacoes.cancelarDose(context, chave)
            atualizar(context)
            return
        }

        val dose = Dose(
            medicamentoId = medicamento.id,
            nome = medicamento.nome,
            dosagem = medicamento.dosagem,
            cor = medicamento.cor,
            data = data,
            horario = horario
        )

        Notificacoes.mostrarDose(context, dose)

        // Agenda novamente as próximas doses.
        atualizar(context)
    }
}
