package com.example.medhoracompose.ui.configuracoes

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.medhoracompose.R
import com.example.medhoracompose.data.repository.PreferenciasRepository
import com.example.medhoracompose.notifications.AgendadorLembretes
import com.example.medhoracompose.notifications.Notificacoes

private val FundoConfiguracoes = Color(0xFFF8FAFC)
private val TextoConfiguracoes = Color(0xFF183B35)
private val VerdeConfiguracoes = Color(0xFF059669)
private val TextoSecundarioConfiguracoes = Color(0xFF71838A)
private val BordaConfiguracoes = Color(0xFFE2E8EB)
private val BrancoConfiguracoes = Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracoesScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val lembretesAtivados = PreferenciasRepository.lembretesAtivados
    val notificacoesAtivadas = PreferenciasRepository.notificacoesAtivadas

    var mostrarPermissaoNegada by remember { mutableStateOf(false) }
    var mostrarOpcoesSom by remember { mutableStateOf(false) }
    var somSelecionado by remember { mutableStateOf("Padrão") }

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

        AgendadorLembretes.atualizar(context)
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
                AgendadorLembretes.atualizar(context)
            } else {
                permissaoLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        } else {
            PreferenciasRepository.definirNotificacoes(true)
            PreferenciasRepository.definirLembretes(true)
            AgendadorLembretes.atualizar(context)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = FundoConfiguracoes,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Configurações",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoConfiguracoes
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FundoConfiguracoes,
                    titleContentColor = TextoConfiguracoes
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            TituloSecaoConfiguracoes("Notificações")

            CardConfiguracoes(
                modifier = Modifier.padding(top = 12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Lembretes",
                            modifier = Modifier.weight(1f),
                            fontSize = 15.sp,
                            color = TextoConfiguracoes
                        )

                        Switch(
                            checked = lembretesAtivados,
                            onCheckedChange = { ativar ->
                                if (ativar) {
                                    ativarLembretes()
                                } else {
                                    PreferenciasRepository.definirLembretes(false)
                                    AgendadorLembretes.atualizar(context)
                                }
                            }
                        )
                    }

                    Text(
                        text = "Ative os lembretes para receber avisos nos horários dos seus medicamentos.",
                        fontSize = 12.sp,
                        color = TextoSecundarioConfiguracoes
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clickable { mostrarOpcoesSom = true },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_bell),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = TextoConfiguracoes
                        )

                        Text(
                            text = "Som",
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 12.dp),
                            fontSize = 14.sp,
                            color = TextoConfiguracoes
                        )

                        Text(
                            text = somSelecionado,
                            fontSize = 12.sp,
                            color = TextoSecundarioConfiguracoes
                        )
                    }

                    Text(
                        text = "Escolha o som dos lembretes.",
                        fontSize = 12.sp,
                        color = TextoSecundarioConfiguracoes
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            BotaoContornadoConfiguracoes(
                texto = "Configurações de notificações",
                aoClicar = {
                    Notificacoes.abrirPreferencias(context)
                }
            )

            Text(
                text = "A disponibilidade dos lembretes depende das permissões de notificação do Android.",
                modifier = Modifier.padding(top = 8.dp),
                fontSize = 12.sp,
                color = TextoSecundarioConfiguracoes
            )

            Spacer(modifier = Modifier.height(24.dp))

            TituloSecaoConfiguracoes("Aparência")

            CardConfiguracoes(
                modifier = Modifier.padding(top = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_settings),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = TextoConfiguracoes
                    )

                    Text(
                        text = "Tema",
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp),
                        fontSize = 14.sp,
                        color = TextoConfiguracoes
                    )

                    Text(
                        text = "Claro",
                        fontSize = 12.sp,
                        color = TextoSecundarioConfiguracoes
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            TituloSecaoConfiguracoes("Sobre")

            CardConfiguracoes(
                modifier = Modifier.padding(top = 12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    LinhaInformacaoConfiguracoes(
                        icone = R.drawable.ic_settings,
                        titulo = "Versão",
                        valor = "1.0"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LinhaInformacaoConfiguracoes(
                        icone = R.drawable.ic_list,
                        titulo = "Desenvolvido por",
                        valor = "Equipe MedHora"
                    )
                }
            }

            Text(
                text = "Os lembretes dependem das permissões de notificação e das configurações de alarmes do Android.",
                modifier = Modifier.padding(top = 20.dp),
                fontSize = 12.sp,
                color = TextoSecundarioConfiguracoes
            )

            Text(
                text = "As informações dos medicamentos devem ser mantidas atualizadas para ajudar no acompanhamento dos horários.",
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
                fontSize = 12.sp,
                color = TextoSecundarioConfiguracoes
            )
        }
    }

    if (mostrarOpcoesSom) {
        AlertDialog(
            onDismissRequest = { mostrarOpcoesSom = false },
            title = {
                Text("Som dos lembretes")
            },
            text = {
                Column {
                    listOf("Padrão", "Silencioso").forEach { opcao ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    somSelecionado = opcao
                                    mostrarOpcoesSom = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (somSelecionado == opcao) "●" else "○",
                                color = VerdeConfiguracoes
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(opcao)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarOpcoesSom = false }) {
                    Text("Fechar")
                }
            }
        )
    }

    if (mostrarPermissaoNegada) {
        AlertDialog(
            onDismissRequest = {
                mostrarPermissaoNegada = false
            },
            title = {
                Text("Permissão necessária")
            },
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
                    onClick = {
                        mostrarPermissaoNegada = false
                    }
                ) {
                    Text("Agora não")
                }
            }
        )
    }
}

@Composable
private fun TituloSecaoConfiguracoes(texto: String) {
    Text(
        text = texto,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = TextoConfiguracoes
    )
}

@Composable
private fun CardConfiguracoes(
    modifier: Modifier = Modifier,
    conteudo: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = BrancoConfiguracoes
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            BordaConfiguracoes
        )
    ) {
        conteudo()
    }
}

@Composable
private fun BotaoContornadoConfiguracoes(
    texto: String,
    aoClicar: () -> Unit
) {
    androidx.compose.material3.OutlinedButton(
        onClick = aoClicar,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
            contentColor = VerdeConfiguracoes
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            VerdeConfiguracoes
        )
    ) {
        Text(
            text = texto,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun LinhaInformacaoConfiguracoes(
    icone: Int,
    titulo: String,
    valor: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icone),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = TextoConfiguracoes
        )

        Text(
            text = titulo,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            fontSize = 14.sp,
            color = TextoConfiguracoes
        )

        Text(
            text = valor,
            fontSize = 12.sp,
            color = TextoSecundarioConfiguracoes
        )
    }
}
