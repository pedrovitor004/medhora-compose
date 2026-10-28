package com.example.medhoracompose.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.medhoracompose.data.model.CorMedicamento
import com.example.medhoracompose.data.model.Dose
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val VerdeMedHora = Color(0xFF059669)
private val FundoMedHora = Color(0xFFF8FAFC)
private val TextoMedHora = Color(0xFF183B35)

@Composable
fun HomeScreen(
    doses: List<Dose>,
    onCadastroClick: () -> Unit,
    onDetalhesClick: () -> Unit,
    onHistoricoClick: () -> Unit,
    onDoseClick: (Long) -> Unit = {},
    onMarcarTomada: (Dose) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var doseConfirmacao by remember { mutableStateOf<Dose?>(null) }

    val hoje = LocalDate.now()

    val dataFormatada = hoje.format(
        DateTimeFormatter.ofPattern(
            "EEEE, d 'de' MMMM",
            Locale("pt", "BR")
        )
    ).replaceFirstChar {
        it.titlecase(Locale("pt", "BR"))
    }

    val dosesTomadas = doses.count { it.tomadaEm != null }
    val totalDoses = doses.size
    val dosesPendentes = totalDoses - dosesTomadas

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FundoMedHora)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "MedHora",
            style = MaterialTheme.typography.headlineMedium,
            color = VerdeMedHora
        )

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = "Olá!",
            style = MaterialTheme.typography.headlineSmall,
            color = TextoMedHora
        )

        Text(
            text = dataFormatada,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.size(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE6F6EF)
            )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Resumo de hoje",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextoMedHora
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = "$dosesTomadas de $totalDoses doses tomadas",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextoMedHora
                )

                Text(
                    text = "$dosesPendentes doses restantes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoMedHora
                )
            }
        }

        Spacer(modifier = Modifier.size(24.dp))

        Text(
            text = "Próximas doses",
            style = MaterialTheme.typography.titleLarge,
            color = TextoMedHora
        )

        Spacer(modifier = Modifier.size(12.dp))

        if (doses.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Text(
                    text = "Nenhum medicamento cadastrado para hoje. " +
                            "Toque em \"Adicionar medicamento\" para começar.",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = doses,
                    key = { it.chave }
                ) { dose ->
                    DoseItem(
                        dose = dose,
                        onClick = {
                            onDoseClick(dose.medicamentoId)
                        },
                        onTomarClick = {
                            doseConfirmacao = dose
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.size(16.dp))

        Button(
            onClick = onCadastroClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VerdeMedHora
            )
        ) {
            Text(
                text = "+ Adicionar medicamento",
                modifier = Modifier.padding(vertical = 6.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = onDetalhesClick) {
                Text("Detalhes")
            }

            Button(onClick = onHistoricoClick) {
                Text("Histórico")
            }
        }
    }

    doseConfirmacao?.let { dose ->
        AlertDialog(
            onDismissRequest = {
                doseConfirmacao = null
            },
            title = {
                Text("Dose registrada!")
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("Medicamento: ${dose.nome}")
                    Text("Dosagem: ${dose.dosagem}")
                    Text("Horário previsto: ${dose.horario}")
                    Text(
                        "Confirme para registrar que você tomou esta dose."
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onMarcarTomada(dose)
                        doseConfirmacao = null
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        doseConfirmacao = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun DoseItem(
    dose: Dose,
    onClick: () -> Unit,
    onTomarClick: () -> Unit
) {
    val cor = when (dose.cor) {
        CorMedicamento.VERDE -> Color(0xFF059669)
        CorMedicamento.VERMELHO -> Color(0xFFDC2626)
        CorMedicamento.AMARELO -> Color(0xFFD97706)
        CorMedicamento.LILAS -> Color(0xFF9333EA)
    }

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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(cor, CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onClick)
            ) {
                Text(
                    text = dose.nome,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextoMedHora
                )

                Text(
                    text = dose.dosagem,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Text(
                    text = "Horário: ${dose.horario}",
                    style = MaterialTheme.typography.bodySmall,
                    color = cor
                )

                if (dose.tomadaEm != null) {
                    Text(
                        text = "Tomada ✓ às ${
                            dose.tomadaEm.format(
                                DateTimeFormatter.ofPattern("HH:mm")
                            )
                        }",
                        style = MaterialTheme.typography.labelLarge,
                        color = VerdeMedHora
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (dose.tomadaEm == null) {
                Button(
                    onClick = onTomarClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VerdeMedHora
                    )
                ) {
                    Text("Tomar")
                }
            } else {
                Text(
                    text = "Tomada ✓",
                    style = MaterialTheme.typography.labelMedium,
                    color = VerdeMedHora
                )
            }
        }
    }
}
