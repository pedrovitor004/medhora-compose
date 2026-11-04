package com.example.medhoracompose

import android.os.Bundle
import android.content.Intent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.medhoracompose.notifications.Notificacoes
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Modifier
import com.example.medhoracompose.navigation.MedHoraNavHost
import com.example.medhoracompose.ui.theme.MedHoraComposeTheme

class MainActivity : ComponentActivity() {
    private var medicamentoNotificacaoId by mutableStateOf<Long?>(null)

    private fun lerNotificacao(intent: Intent?) {
        medicamentoNotificacaoId = intent?.takeIf { it.hasExtra(Notificacoes.EXTRA_MEDICAMENTO_ID) }
            ?.getLongExtra(Notificacoes.EXTRA_MEDICAMENTO_ID, -1L)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        lerNotificacao(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lerNotificacao(intent)
        enableEdgeToEdge()
        setContent {
            MedHoraComposeTheme {
                val app = application as MedHoraApplication
                val carregado by app.carregado.collectAsState()
                val erro = app.erroCarregamento
                if (erro != null || !carregado) {
                    Box(Modifier.fillMaxSize().safeDrawingPadding(), contentAlignment = Alignment.Center) {
                        if (erro != null) Text(erro) else CircularProgressIndicator()
                    }
                } else {
                    MedHoraNavHost(medicamentoNotificacaoId = medicamentoNotificacaoId,
                        onNotificacaoConsumida = {
                            medicamentoNotificacaoId = null
                            intent.removeExtra(Notificacoes.EXTRA_MEDICAMENTO_ID)
                        })
                }
            }
        }
    }
}
