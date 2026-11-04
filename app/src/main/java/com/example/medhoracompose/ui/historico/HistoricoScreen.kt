package com.example.medhoracompose.ui.historico

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhoracompose.data.model.CorMedicamento
import com.example.medhoracompose.data.model.Dose
import com.example.medhoracompose.data.model.StatusDose
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val FundoHistorico = Color(0xFFF8FAFC)
private val TextoHistorico = Color(0xFF183B35)
private val VerdeHistorico = Color(0xFF059669)
private val VerdeClaroHistorico = Color(0xFFD1FAE5)
private val TextoSuaveHistorico = Color(0xFF71838A)
private val BordaHistorico = Color(0xFFE2E8EB)

private enum class PeriodoHistorico(
    val titulo: String,
    val dias: Long
) {
    HOJE("Hoje", 1),
    SETE_DIAS("7 dias", 7),
    TRINTA_DIAS("30 dias", 30)
}

@Composable
fun HistoricoScreen(
    onVoltarClick: () -> Unit,
    doses: List<Dose> = emptyList(),
    onConfiguracoesClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var periodoSelecionado by remember {
        mutableStateOf(PeriodoHistorico.HOJE)
    }

    val hoje = LocalDate.now()
    val dataInicial = hoje.minusDays(periodoSelecionado.dias - 1)

    val formatoData = DateTimeFormatter.ofPattern(
        "dd/MM/yyyy",
        Locale("pt", "BR")
    )

    val dosesOrdenadas = doses
        .filter { dose ->
            !dose.data.isBefore(dataInicial) &&
                    !dose.data.isAfter(hoje)
        }
        .sortedWith(
            compareByDescending<Dose> { it.data }
                .thenByDescending { it.horario }
        )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FundoHistorico)
    ) {
        com.example.medhoracompose.ui.common.MedHoraToolbar("Histórico", onSettings = onConfiguracoesClick)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            PeriodoHistorico.entries.forEach { periodo ->
                val selecionado = periodoSelecionado == periodo

                Button(
                    onClick = {
                        periodoSelecionado = periodo
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(0.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, VerdeHistorico),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selecionado) {
                            VerdeClaroHistorico
                        } else {
                            Color.White
                        },
                        contentColor = if (selecionado) {
                            VerdeHistorico
                        } else {
                            TextoHistorico
                        }
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 0.dp
                    )
                ) {
                    Text(
                        text = periodo.titulo,
                        fontSize = 13.sp,
                        fontWeight = if (selecionado) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Normal
                        }
                    )
                }
            }
        }

        if (dosesOrdenadas.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Nenhum registro de dose neste período.",
                    fontSize = 12.sp, letterSpacing = 0.4.sp,
                    color = TextoSuaveHistorico
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                dosesOrdenadas.groupBy { it.data }.forEach { (data, registros) ->
                    item(key = data.toString()) {
                        Text(data.format(formatoData), Modifier.padding(top = 16.dp, bottom = 8.dp), fontSize = 15.sp, letterSpacing = 0.15.sp, fontWeight = FontWeight.Bold, color = TextoHistorico)
                    }
                    items(registros, key = { it.chave }) { dose ->
                        com.example.medhoracompose.ui.common.DoseCard(dose)
                    }
                }
            }
        }
    }
}
