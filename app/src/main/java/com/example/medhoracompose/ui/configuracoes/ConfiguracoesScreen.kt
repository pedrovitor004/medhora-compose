package com.example.medhoracompose.ui.configuracoes

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.medhoracompose.data.repository.PreferenciasRepository
import com.example.medhoracompose.notifications.Notificacoes
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracoesScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lembretesAtivados = PreferenciasRepository.lembretesAtivados
    val notificacoesAtivadas = PreferenciasRepository.notificacoesAtivadas
    var mostrarPermissaoNegada by remember { mutableStateOf(false) }

    val permissaoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedida ->
        if (concedida) {
            PreferenciasRepository.definirNotificacoes(true)
            PreferenciasRepository.definirLembretes(true)
        } else {
            PreferenciasRepository.definirLembretes(false)
            mostrarPermissaoNegada = true
        }
    }

    fun ativarLembretes() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val concedida = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (concedida) {
                PreferenciasRepository.definirNotificacoes(true)
                PreferenciasRepository.definirLembretes(true)
            } else {
                permissaoLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            PreferenciasRepository.definirNotificacoes(true)
            PreferenciasRepository.definirLembretes(true)
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { Text("Configurações") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Preferências",
                style = MaterialTheme.typography.titleMedium
            )

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Lembretes de medicamentos",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text("Ativar ou desativar os lembretes.")
                        }

                        Switch(
                            checked = lembretesAtivados,
                            onCheckedChange = { ativar ->
                                if (ativar) {
                                    ativarLembretes()
                                } else {
                                    PreferenciasRepository.definirLembretes(false)
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Notificações",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text("Ver as configurações de notificações do sistema.")
                        }

                        Switch(
                            checked = notificacoesAtivadas,
                            onCheckedChange = { ativar ->
                                if (ativar) {
                                    Notificacoes.abrirPreferencias(context)
                                } else {
                                    PreferenciasRepository.definirNotificacoes(false)
                                    PreferenciasRepository.definirLembretes(false)
                                }
                            }
                        )
                    }
                }
            }

            Text(
                text = "Sobre",
                style = MaterialTheme.typography.titleMedium
            )

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "MedHora",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "Organize seus medicamentos e acompanhe os horários das suas doses."
                    )
                    Text(
                        text = "Projeto acadêmico desenvolvido para a disciplina de Programação para Dispositivos Móveis (PDM)."
                    )
                    Text("Versão 1.0")
                }
            }

            Text(
                text = "A permissão permite exibir notificações. O agendamento automático dos horários será implementado na próxima etapa.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    if (mostrarPermissaoNegada) {
        AlertDialog(
            onDismissRequest = { mostrarPermissaoNegada = false },
            title = { Text("Permissão necessária") },
            text = {
                Text(
                    "Para receber lembretes, permita que o MedHora envie notificações nas configurações do Android."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarPermissaoNegada = false
                        Notificacoes.abrirPreferencias(context)
                    }
                ) {
                    Text("Abrir configurações")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mostrarPermissaoNegada = false }
                ) {
                    Text("Agora não")
                }
            }
        )
    }
}
