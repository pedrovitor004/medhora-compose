package com.example.medhoracompose.ui.detalhes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.medhoracompose.data.model.CorMedicamento
import com.example.medhoracompose.data.model.Frequencia
import com.example.medhoracompose.data.model.Medicamento
import java.time.format.DateTimeFormatter

private val VerdeDetalhes = Color(0xFF059669)
private val FundoDetalhes = Color(0xFFF8FAFC)
private val TextoDetalhes = Color(0xFF183B35)

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
        CorMedicamento.VERDE -> Color(0xFF059669)
        CorMedicamento.VERMELHO -> Color(0xFFEF4444)
        CorMedicamento.AMARELO -> Color(0xFFFACC15)
        CorMedicamento.LILAS -> Color(0xFFA78BFA)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = FundoDetalhes,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalhes do medicamento",
                        color = TextoDetalhes
                    )
                },
                navigationIcon = {
                    TextButton(onClick = onVoltarClick) {
                        Text("‹", color = TextoDetalhes)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (medicamento == null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Nenhum medicamento selecionado. Volte à tela inicial e selecione um medicamento.",
                        modifier = Modifier.padding(16.dp),
                        color = TextoDetalhes,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(
                        modifier = Modifier
                            .size(18.dp)
                            .background(corMedicamento, CircleShape)
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp)
                    ) {
                        Text(
                            text = medicamento.nome,
                            style = MaterialTheme.typography.headlineSmall,
                            color = TextoDetalhes
                        )

                        Text(
                            text = medicamento.dosagem,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ItemDetalhe(
                            titulo = "Frequência",
                            valor = when (medicamento.frequencia) {
                                Frequencia.TODOS_OS_DIAS -> "Todos os dias"
                                Frequencia.DIAS_UTEIS -> "Dias úteis"
                                Frequencia.FINS_DE_SEMANA -> "Fins de semana"
                            }
                        )

                        Text(
                            text = "Horários",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextoDetalhes
                        )

                        medicamento.horarios.sorted().forEach { horario ->
                            Text(
                                text = "• ${horario.format(DateTimeFormatter.ofPattern("HH:mm"))}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextoDetalhes
                            )
                        }

                        ItemDetalhe(
                            titulo = "Data de cadastro",
                            valor = medicamento.criadoEm.format(
                                DateTimeFormatter.ofPattern("dd/MM/yyyy")
                            )
                        )

                        ItemDetalhe(
                            titulo = "Observações",
                            valor = medicamento.observacoes.ifBlank {
                                "Nenhuma observação informada"
                            }
                        )
                    }
                }

                Button(
                    onClick = { onEditarClick(medicamento) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VerdeDetalhes
                    )
                ) {
                    Text("Editar medicamento")
                }

                Button(
                    onClick = { mostrarConfirmacaoExclusao = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFDC2626)
                    )
                ) {
                    Text("Excluir medicamento")
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onVoltarClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VerdeDetalhes
                )
            ) {
                Text("Voltar")
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
                    Text("Excluir", color = Color(0xFFDC2626))
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
private fun ItemDetalhe(
    titulo: String,
    valor: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.titleSmall,
            color = TextoDetalhes
        )

        Text(
            text = valor,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}
