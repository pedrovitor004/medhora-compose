package com.example.medhoracompose.notifications

import android.Manifest
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
import com.example.medhoracompose.data.model.Dose

object Notificacoes {

    const val CANAL_DOSES = "medhora_doses_v1"

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

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            dose.chave.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val notificacao = NotificationCompat.Builder(context, CANAL_DOSES)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Hora do medicamento: ${dose.nome}")
            .setContentText("${dose.dosagem} • ${dose.horario}")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Dose de ${dose.dosagem} às ${dose.horario}.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        return try {
            NotificationManagerCompat.from(context).notify(
                dose.chave,
                1,
                notificacao
            )
            true
        } catch (_: SecurityException) {
            false
        }
    }

    fun cancelarDose(context: Context, chave: String) {
        NotificationManagerCompat.from(context).cancel(chave, 1)
    }
}