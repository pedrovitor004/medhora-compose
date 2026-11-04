package com.example.medhoracompose.notifications

import android.Manifest
import android.app.Notification
import android.graphics.Color
import android.net.Uri
import androidx.core.graphics.drawable.toBitmap
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.example.medhoracompose.data.model.CorMedicamento
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.medhoracompose.MainActivity
import com.example.medhoracompose.R
import com.example.medhoracompose.data.repository.PreferenciasRepository
import com.example.medhoracompose.data.model.Dose

object Notificacoes {

    const val CANAL_DOSES = "medhora_doses_v1"
    const val EXTRA_MEDICAMENTO_ID = "medicamento_notificacao_id"
    private val formatoHorario = DateTimeFormatter.ofPattern("HH:mm")

    fun criarCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL_DOSES,
                "Lembretes de medicamentos",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificações nos horários dos medicamentos"
                enableVibration(true)
            }

            val manager = context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

            manager.createNotificationChannel(canal)
        }
    }

    fun permitidas(context: Context): Boolean {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }

        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            return false
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

            val canal = manager.getNotificationChannel(CANAL_DOSES)

            if (
                canal != null &&
                canal.importance == NotificationManager.IMPORTANCE_NONE
            ) {
                return false
            }
        }

        return true
    }

    fun abrirPreferencias(context: Context) {
        criarCanal(context)

        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(
                android.provider.Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS
            ).apply {
                putExtra(
                    android.provider.Settings.EXTRA_APP_PACKAGE,
                    context.packageName
                )
                putExtra(
                    android.provider.Settings.EXTRA_CHANNEL_ID,
                    CANAL_DOSES
                )
            }
        } else {
            Intent(
                android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            ).apply {
                data = android.net.Uri.parse("package:${context.packageName}")
            }
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun mostrarDose(context: Context, dose: Dose): Boolean {
        criarCanal(context)

        if (!permitidas(context)) return false

        return publicar(context, dose.chave, construirDose(context, dose))
    }

    private fun abrirMedicamento(context: Context, dose: Dose): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            data = Uri.parse("medhora://medicamento/${dose.medicamentoId}")
            putExtra(EXTRA_MEDICAMENTO_ID, dose.medicamentoId)
        }
        return PendingIntent.getActivity(context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    private fun acao(context: Context, dose: Dose, action: String): PendingIntent {
        val intent = Intent(context, AcoesNotificacaoReceiver::class.java).apply {
            this.action = action
            data = Uri.parse("medhora://acao/${Uri.encode(dose.chave)}")
            putExtra(AgendadorLembretes.EXTRA_CHAVE_DOSE, dose.chave)
        }
        return PendingIntent.getBroadcast(context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    private fun base(context: Context, dose: Dose) = NotificationCompat.Builder(context, CANAL_DOSES)
        .setSmallIcon(R.drawable.ic_notification_medication)
        .setColor(Color.rgb(5, 150, 105))
        .setCategory(NotificationCompat.CATEGORY_REMINDER)
        .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
        .setContentIntent(abrirMedicamento(context, dose))
        .setAutoCancel(true)
        .setPublicVersion(NotificationCompat.Builder(context, CANAL_DOSES)
            .setSmallIcon(R.drawable.ic_notification_medication)
            .setContentTitle("MedHora")
            .setContentText("Você tem um lembrete de medicamento.")
            .build())

    internal fun construirDose(context: Context, dose: Dose): Notification {
        val horario = dose.horario.format(formatoHorario)
        val icone = when (dose.cor) {
            CorMedicamento.VERDE -> R.drawable.ic_capsule_green
            CorMedicamento.VERMELHO -> R.drawable.ic_capsule_red
            CorMedicamento.AMARELO -> R.drawable.ic_capsule_yellow
            CorMedicamento.LILAS -> R.drawable.ic_capsule_lilac
        }
        return base(context, dose)
            .setSubText("Hora do seu medicamento")
            .setContentTitle("${dose.nome} — ${dose.dosagem}")
            .setContentText("Horário programado: $horario")
            .setStyle(NotificationCompat.BigTextStyle().bigText(
                "Horário programado: $horario\nDepois de tomar, toque em \"Já tomei\" para registrar."))
            .setLargeIcon(ContextCompat.getDrawable(context, icone)?.toBitmap(96, 96))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSilent(!PreferenciasRepository.somAtivado)
            .addAction(R.drawable.ic_check, "Já tomei", acao(context, dose, AcoesNotificacaoReceiver.ACTION_TOMADA))
            .addAction(R.drawable.ic_clock, "Lembrar em 10 min", acao(context, dose, AcoesNotificacaoReceiver.ACTION_ADIAR))
            .build()
    }

    fun mostrarConfirmacao(context: Context, dose: Dose) {
        if (!permitidas(context)) return
        val notificacao = base(context, dose)
            .setContentTitle("Dose registrada")
            .setContentText("${dose.nome} — ${dose.dosagem}")
            .setStyle(NotificationCompat.BigTextStyle().bigText(
                "${dose.nome} — ${dose.dosagem}\nRegistrada às ${LocalDateTime.now().format(formatoHorario)}."))
            .setSilent(true)
            .setOnlyAlertOnce(true)
            .setTimeoutAfter(30_000)
            .build()
        publicar(context, dose.chave, notificacao)
    }

    private fun publicar(context: Context, chave: String, notificacao: Notification): Boolean {
        return try {
            NotificationManagerCompat.from(context).notify(chave, 1, notificacao)
            true
        } catch (_: SecurityException) {
            false
        }
    }

    fun ocultarDose(context: Context, chave: String) {
        NotificationManagerCompat.from(context).cancel(chave, 1)
    }

    fun cancelarDose(context: Context, chave: String) {
        ocultarDose(context, chave)
        AgendadorLembretes.cancelarAdiado(context, chave)
    }
}
