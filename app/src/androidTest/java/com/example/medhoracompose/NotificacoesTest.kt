package com.example.medhoracompose

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.medhoracompose.data.local.MedHoraDatabase
import com.example.medhoracompose.data.model.*
import com.example.medhoracompose.data.repository.MedicamentoRepository
import com.example.medhoracompose.notifications.*
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotificacoesTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context = instrumentation.targetContext
    private val dose = Dose(-987654321L, "Exemplo MedHora", "Dose de teste", CorMedicamento.VERDE,
        LocalDate.now().minusDays(1), LocalTime.of(8, 0))

    @Test
    fun textoLegivelAcoesIndependentesEPrivacidade() {
        val notification = Notificacoes.construirDose(context, dose)
        val outraDose = Notificacoes.construirDose(context, dose.copy(horario = LocalTime.of(9, 0)))
        assertEquals("${dose.nome} — ${dose.dosagem}", notification.extras.getString(Notification.EXTRA_TITLE))
        assertEquals("Horário programado: 08:00", notification.extras.getString(Notification.EXTRA_TEXT))
        assertEquals(R.drawable.ic_notification_medication, notification.smallIcon.resId)
        assertEquals(listOf("Já tomei", "Lembrar em 10 min"), notification.actions.map { it.title.toString() })
        assertNotEquals(notification.actions[0].actionIntent, notification.actions[1].actionIntent)
        assertNotEquals(notification.actions[0].actionIntent, outraDose.actions[0].actionIntent)
        assertEquals(Notification.VISIBILITY_PRIVATE, notification.visibility)
        assertFalse(notification.publicVersion.extras.getString(Notification.EXTRA_TEXT)!!.contains(dose.nome))
        (notification.actions + outraDose.actions).forEach { it.actionIntent.cancel() }
    }

    @Test
    fun acaoRegistraNoBancoUmaVezERejeitaNotificacaoObsoleta() = runBlocking {
        val db = Room.inMemoryDatabaseBuilder(context, MedHoraDatabase::class.java).build()
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        val repository = MedicamentoRepository(db.medHoraDao(), scope)
        val medicamento = Medicamento(dose.medicamentoId, dose.nome, dose.dosagem, listOf(dose.horario),
            criadoEm = dose.data)
        try {
            repository.adicionar(medicamento)
            withTimeout(5_000) { repository.dados.filterNotNull().first { it.medicamentos.isNotEmpty() } }
            val intent = Intent().setAction(AcoesNotificacaoReceiver.ACTION_TOMADA)
                .putExtra(AgendadorLembretes.EXTRA_CHAVE_DOSE, dose.chave)
            AcoesNotificacaoReceiver().processar(context, intent, repository)
            val registro = db.medHoraDao().observarDoses().first().single()
            // Mesmo com uma segunda ação antes de a UI atualizar, preserva o primeiro registro.
            repository.marcarDoseTomada(dose, registro.tomadaEm.plusMinutes(5))
            assertEquals(registro, db.medHoraDao().observarDoses().first().single())
            assertNull(AgendadorLembretes.obterDosePendente("chave inválida", repository))
            assertNull(AgendadorLembretes.obterDosePendente(dose.copy(data = LocalDate.now().plusDays(1)).chave, repository))
            repository.remover(medicamento.id)
            withTimeout(5_000) { repository.dados.filterNotNull().first { it.medicamentos.isEmpty() } }
            AcoesNotificacaoReceiver().processar(context, intent, repository)
            assertEquals(1, db.medHoraDao().observarDoses().first().size)
        } finally {
            Notificacoes.cancelarDose(context, dose.chave)
            scope.cancel()
            db.close()
        }
    }

    @Test
    fun adiamentoTemIdentidadePropriaEPodeSerCancelado() {
        val intent = Intent(context, LembreteReceiver::class.java).apply {
            action = AgendadorLembretes.ACTION_ADIADO
            data = Uri.parse("medhora://lembrete-adiado/${Uri.encode(dose.chave)}")
        }
        fun pendente() = PendingIntent.getBroadcast(context, 0, intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
        try {
            AgendadorLembretes.adiar(context, dose)
            val primeiro = pendente()
            assertNotNull(primeiro)
            AgendadorLembretes.adiar(context, dose)
            assertEquals(primeiro, pendente())
            AgendadorLembretes.cancelarAdiado(context, dose.chave)
            assertNull(pendente())
        } finally {
            AgendadorLembretes.cancelarAdiado(context, dose.chave)
        }
    }

    @Test
    fun notificacaoPodeSerExibidaNoAparelho() {
        assumeTrue("Permissão de notificações não concedida", Notificacoes.permitidas(context))
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        try {
            Notificacoes.criarCanal(context)
            manager.notify("medhora-preview", 99, Notificacoes.construirDose(context, dose))
            assertTrue(manager.activeNotifications.any { it.tag == "medhora-preview" })
            instrumentation.uiAutomation.executeShellCommand("cmd statusbar expand-notifications").close()
            Thread.sleep(1200)
            instrumentation.uiAutomation.takeScreenshot()?.let { bitmap ->
                File(context.getExternalFilesDir(null), "notificacao-preview.png").outputStream().use {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                }
                bitmap.recycle()
            }
        } finally {
            if (InstrumentationRegistry.getArguments().getString("manterPrevia") != "true") {
                manager.cancel("medhora-preview", 99)
            }
            instrumentation.uiAutomation.executeShellCommand("cmd statusbar collapse").close()
        }
    }
}
