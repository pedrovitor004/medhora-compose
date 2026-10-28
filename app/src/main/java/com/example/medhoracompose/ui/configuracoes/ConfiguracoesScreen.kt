package com.example.medhoracompose.ui.configuracoes

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
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracoesScreen(
    modifier: Modifier = Modifier
) {
    var lembretesAtivados by remember { mutableStateOf(false) }
    var notificacoesAtivadas by remember { mutableStateOf(true) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Configurações") }
            )
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

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Lembretes de medicamentos",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = "Ativar ou desativar a preferência de lembretes."
                            )
                        }

                        Switch(
                            checked = lembretesAtivados,
                            onCheckedChange = { lembretesAtivados = it }
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Notificações",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = "Controlar a preferência de notificações do aplicativo."
                            )
                        }

                        Switch(
                            checked = notificacoesAtivadas,
                            onCheckedChange = { notificacoesAtivadas = it }
                        )
                    }
                }
            }

            Text(
                text = "Sobre",
                style = MaterialTheme.typography.titleMedium
            )

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
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
                    Text(
                        text = "Versão 1.0"
                    )
                }
            }

            Text(
                text = "Os interruptores ainda controlam apenas as preferências nesta tela. A gravação das opções e o agendamento real de notificações serão implementados em uma próxima etapa.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
