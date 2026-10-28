package com.example.medhoracompose.ui.historico

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.medhoracompose.data.model.CorMedicamento
import com.example.medhoracompose.data.model.Dose
import com.example.medhoracompose.data.model.StatusDose
import java.time.format.DateTimeFormatter
import java.util.Locale

private val FundoHistorico = Color(0xFFF8FAFC)
private val TextoHistorico = Color(0xFF183B35)
private val VerdeHistorico = Color(0xFF059669)

@Composable
fun HistoricoScreen(
    onVoltarClick: () -> Unit,
    doses: List<Dose> = emptyList(),
    modifier: Modifier = Modifier
) {
    val formatoData = DateTimeFormatter.ofPattern(
        "dd/MM/yyyy",
        Locale("pt", "BR")
    )

    val dosesOrdenadas = doses.sortedWith(
        compareByDescending<Dose> { it.data }
            .thenByDescending { it.horario }
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FundoHistorico)
            .padding(20.dp)
    ) {
        Text(
            text = "Histórico de doses",
            style = MaterialTheme.typography.headlineMedium,
            color = TextoHistorico
        )

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = "Consulte os registros dos seus medicamentos.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.size(20.dp))

        if (dosesOrdenadas.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Nenhum registro encontrado",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextoHistorico
                    )

                    Spacer(modifier = Modifier.size(8.dp))

                    Text(
                        text = "As doses registradas aparecerão aqui.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = dosesOrdenadas,
                    key = { it.chave }
                ) { dose ->
                    HistoricoDoseItem(
                        dose = dose,
                        dataFormatada = dose.data.format(formatoData)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.size(16.dp))

        Button(
            onClick = onVoltarClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Voltar")
        }
    }
}

@Composable
private fun HistoricoDoseItem(
    dose: Dose,
    dataFormatada: String
) {
    val corMedicamento = when (dose.cor) {
        CorMedicamento.VERDE -> Color(0xFF059669)
        CorMedicamento.VERMELHO -> Color(0xFFDC2626)
        CorMedicamento.AMARELO -> Color(0xFFD97706)
        CorMedicamento.LILAS -> Color(0xFF9333EA)
    }

    val status = dose.status()

    val (textoStatus, corStatus) = when (status) {
        StatusDose.TOMADA -> "Tomada" to Color(0xFF059669)
        StatusDose.PENDENTE -> "Pendente" to Color(0xFFD97706)
        StatusDose.ATRASADA -> "Atrasada" to Color(0xFFDC2626)
        StatusDose.PROGRAMADA -> "Programada" to Color(0xFF64748B)
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
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(corMedicamento, CircleShape)
            )

            Spacer(modifier = Modifier.size(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = dose.nome,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextoHistorico
                )

                Text(
                    text = dose.dosagem,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Text(
                    text = "$dataFormatada • ${dose.horario}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Text(
                    text = textoStatus,
                    style = MaterialTheme.typography.labelLarge,
                    color = corStatus
                )
            }
        }
    }
}
