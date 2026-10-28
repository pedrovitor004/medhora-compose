package com.example.medhoracompose.ui.detalhes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhoracompose.R
import com.example.medhoracompose.data.model.CorMedicamento
import com.example.medhoracompose.data.model.Frequencia
import com.example.medhoracompose.data.model.Medicamento
import java.time.format.DateTimeFormatter

private val VerdeDetalhes = Color(0xFF059669)
private val FundoDetalhes = Color(0xFFF8FAFC)
private val TextoDetalhes = Color(0xFF183B35)
private val TextoSuaveDetalhes = Color(0xFF71838A)
private val VerdeClaroDetalhes = Color(0xFFD1FAE5)
private val BordaDetalhes = Color(0xFFE2E8EB)
private val ErroDetalhes = Color(0xFFE05252)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalhesScreen(
    onVoltarClick: () -> Unit,
    medicamento: Medicamento? = null,
    modifier: Modifier = Modifier,
    onEditarClick: (Medicamento) -> Unit = {},
    onExcluirClick: (Medicamento) -> Unit = {}
) {
    var mostrarConfirmacaoExclusao by remember {
        mutableStateOf(false)
    }

    val corMedicamento = when (medicamento?.cor ?: CorMedicamento.VERDE) {
        CorMedicamento.VERDE -> VerdeDetalhes
        CorMedicamento.VERMELHO -> Color(0xFFDC2626)
        CorMedicamento.AMARELO -> Color(0xFFD97706)
        CorMedicamento.LILAS -> Color(0xFF9333EA)
    }

    val iconeCapsula = when (medicamento?.cor ?: CorMedicamento.VERDE) {
        CorMedicamento.VERDE -> R.drawable.ic_capsule_green
        CorMedicamento.VERMELHO -> R.drawable.ic_capsule_red
        CorMedicamento.AMARELO -> R.drawable.ic_capsule_yellow
        CorMedicamento.LILAS -> R.drawable.ic_capsule_lilac
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = FundoDetalhes,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalhes do medicamento",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoDetalhes
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVoltarClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = "Voltar",
                            tint = TextoDetalhes
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FundoDetalhes
                )
            )
        },
        bottomBar = {
            if (medicamento != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(FundoDetalhes)
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { onEditarClick(medicamento) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = TextoDetalhes
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            BordaDetalhes
                        )
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_edit),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text("Editar", fontSize = 14.sp)
                    }

                    Button(
                        onClick = { mostrarConfirmacaoExclusao = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = ErroDetalhes
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            ErroDetalhes
                        )
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_delete),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = ErroDetalhes
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text("Excluir", fontSize = 14.sp)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            if (medicamento == null) {
                Text(
                    text = "Nenhum medicamento selecionado. Volte à tela inicial e selecione um medicamento.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    color = TextoSuaveDetalhes,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .align(Alignment.CenterHorizontally)
                        .background(
                            color = VerdeClaroDetalhes,
                            shape = RoundedCornerShape(50)
                        )
                        .padding(18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(iconeCapsula),
                        contentDescription = "Medicamento",
                        modifier = Modifier.fillMaxSize(),
                        tint = Color.Unspecified
                    )
                }

                Text(
                    text = medicamento.nome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoDetalhes,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = medicamento.dosagem,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    fontSize = 14.sp,
                    color = TextoSuaveDetalhes,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                CartaoDetalhes(titulo = "Horários diários") {
                    if (medicamento.horarios.isEmpty()) {
                        Text(
                            text = "Nenhum horário cadastrado",
                            fontSize = 14.sp,
                            color = TextoSuaveDetalhes
                        )
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            medicamento.horarios.sorted().forEach { horario ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_clock),
                                        contentDescription = null,
                                        tint = VerdeDetalhes,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.size(10.dp))
                                    Text(
                                        text = horario.format(
                                            DateTimeFormatter.ofPattern("HH:mm")
                                        ),
                                        fontSize = 14.sp,
                                        color = TextoDetalhes
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                CartaoDetalhes(titulo = "Frequência") {
                    Text(
                        text = when (medicamento.frequencia) {
                            Frequencia.TODOS_OS_DIAS -> "Todos os dias"
                            Frequencia.DIAS_UTEIS -> "Dias úteis"
                            Frequencia.FINS_DE_SEMANA -> "Fins de semana"
                        },
                        fontSize = 14.sp,
                        color = TextoDetalhes
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                CartaoDetalhes(titulo = "Última dose") {
                    Text(
                        text = "Consulte o histórico para verificar os registros de doses.",
                        fontSize = 14.sp,
                        color = TextoSuaveDetalhes
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                CartaoDetalhes(titulo = "Observações") {
                    Text(
                        text = medicamento.observacoes.ifBlank {
                            "Nenhuma observação informada"
                        },
                        fontSize = 14.sp,
                        color = if (medicamento.observacoes.isBlank()) {
                            TextoSuaveDetalhes
                        } else {
                            TextoDetalhes
                        }
                    )
                }
            }
        }
    }

    if (mostrarConfirmacaoExclusao && medicamento != null) {
        AlertDialog(
            onDismissRequest = {
                mostrarConfirmacaoExclusao = false
            },
            title = {
                Text("Excluir medicamento?")
            },
            text = {
                Text(
                    "Deseja realmente excluir ${medicamento.nome}? " +
                            "Essa ação não poderá ser desfeita."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarConfirmacaoExclusao = false
                        onExcluirClick(medicamento)
                    }
                ) {
                    Text("Excluir", color = ErroDetalhes)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarConfirmacaoExclusao = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun CartaoDetalhes(
    titulo: String,
    conteudo: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = titulo,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextoDetalhes
            )
            conteudo()
        }
    }
}
