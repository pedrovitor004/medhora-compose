package com.example.medhoracompose

import android.content.Intent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.medhoracompose.data.model.Medicamento
import com.example.medhoracompose.data.repository.MedHoraRepositorioCompartilhado
import com.example.medhoracompose.notifications.Notificacoes
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotificacaoNavegacaoTest {
    @Test
    fun abreDetalhesComTelaFechadaEComAplicativoAberto() = runBlocking(Dispatchers.IO) {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val app = context.applicationContext as MedHoraApplication
        withTimeout(10_000) { app.carregado.first { it } }
        val repository = MedHoraRepositorioCompartilhado.repository
        val medicamento = Medicamento(-987654322L, "Teste de navega\u00e7\u00e3o MedHora", "Exemplo", emptyList())
        fun intent() = Intent(context, MainActivity::class.java)
            .putExtra(Notificacoes.EXTRA_MEDICAMENTO_ID, medicamento.id)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        fun contem(node: AccessibilityNodeInfo?, text: String): Boolean {
            if (node == null) return false
            if (node.text?.toString() == text) return true
            return (0 until node.childCount).any { contem(node.getChild(it), text) }
        }
        suspend fun esperar(text: String) {
            withTimeout(15_000) {
                while (!contem(instrumentation.uiAutomation.rootInActiveWindow, text)) delay(150)
            }
        }
        try {
            repository.adicionar(medicamento)
            withTimeout(5_000) { repository.dados.filterNotNull().first { dados -> dados.medicamentos.any { it.id == medicamento.id } } }
            context.startActivity(intent())
            esperar(medicamento.nome)
            esperar("Detalhes do medicamento")
            instrumentation.uiAutomation.executeShellCommand("input keyevent 4").close()
            esperar("Resumo di\u00e1rio")
            context.startActivity(intent())
            esperar(medicamento.nome)
            esperar("Detalhes do medicamento")
        } finally {
            repository.remover(medicamento.id)
            instrumentation.uiAutomation.executeShellCommand("input keyevent 4").close()
        }
    }
}
