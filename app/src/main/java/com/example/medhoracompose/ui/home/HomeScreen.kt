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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.foundation.Image
import com.example.medhoracompose.R
import com.example.medhoracompose.data.model.CorMedicamento
import com.example.medhoracompose.data.model.Dose
import com.example.medhoracompose.ui.common.CalendarioSemanal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val VerdeMedHora = Color(0xFF059669)
private val FundoMedHora = Color(0xFFF8FAFC)
private val VerdeClaroMedHora = Color(0xFFD1FAE5)
private val TextoMedHora = Color(0xFF183B35)
private val TextoSuaveMedHora = Color(0xFF71838A)
private val BrancoMedHora = Color(0xFFFFFFFF)

@Composable
fun HomeScreen(
    doses: List<Dose>,
    onCadastroClick: () -> Unit,
    onDetalhesClick: () -> Unit,
    onHistoricoClick: () -> Unit,
    onDataSelecionada: (LocalDate) -> Unit = {},
    onDoseClick: (Long) -> Unit = {},
    onMarcarTomada: (Dose) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var doseConfirmacao by remember {
        mutableStateOf<Dose?>(null)
    }

    var dataSelecionada by remember {
        mutableStateOf(LocalDate.now())
    }

    val hoje = LocalDate.now()
    val locale = Locale("pt", "BR")

    val dataFormatada = dataSelecionada.format(
        DateTimeFormatter.ofPattern(
            "EEEE, d 'de' MMMM",
            locale
        )
    ).replaceFirstChar {
        it.titlecase(locale)
    }

    val dosesDoDia = doses.filter {
        it.data == dataSelecionada
    }

    val dosesTomadas = dosesDoDia.count {
        it.tomadaEm != null
    }

    val totalDoses = dosesDoDia.size
    val dosesPendentes = totalDoses - dosesTomadas
    val progresso = if (totalDoses > 0) {
        dosesTomadas.toFloat() / totalDoses.toFloat()
    } else {
        0f
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FundoMedHora)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(BrancoMedHora)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_clock),
                contentDescription = null,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "MedHora",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextoMedHora
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "Olá! Cuide de você.",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoMedHora
                )

                Text(
                    text = if (dataSelecionada == hoje) {
                        "Hoje, $dataFormatada"
                    } else {
                        dataFormatada
                    },
                    modifier = Modifier.padding(top = 4.dp),
                    fontSize = 12.sp,
                    color = TextoSuaveMedHora
                )

                Spacer(modifier = Modifier.height(16.dp))

                CalendarioSemanal(
                    dataSelecionada = dataSelecionada,
                    onDataSelecionada = { data ->
                        dataSelecionada = data
                        onDataSelecionada(data)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = VerdeClaroMedHora
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 1.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Text(
                            text = "Resumo diário",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextoMedHora
                        )

                        Text(
                            text = "$dosesTomadas de $totalDoses doses tomadas",
                            modifier = Modifier.padding(top = 8.dp),
                            fontSize = 14.sp,
                            color = TextoMedHora
                        )

                        LinearProgressIndicator(
                            progress = { progresso },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
                                .height(6.dp),
                            color = VerdeMedHora,
                            trackColor = BrancoMedHora,
                            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                        )

                        Text(
                            text = "$dosesPendentes pendentes",
                            fontSize = 12.sp,
                            color = TextoSuaveMedHora
                        )
                    }
                }

                Text(
                    text = if (dataSelecionada == hoje) {
                        "Próximos horários"
                    } else {
                        "Horários de $dataFormatada"
                    },
                    modifier = Modifier.padding(top = 20.dp),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoMedHora
                )
            }

            if (dosesDoDia.isEmpty()) {
                Text(
                    text = "Adicione um medicamento para organizar seus horários.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    fontSize = 12.sp,
                    color = TextoSuaveMedHora,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    dosesDoDia.forEach { dose ->
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
        }

        Button(
            onClick = onCadastroClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 8.dp)
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VerdeMedHora
            )
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_plus),
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Adicionar medicamento",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = BrancoMedHora
            )
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
                    Text("Confirme para registrar que você tomou esta dose.")
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
            containerColor = BrancoMedHora
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
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoMedHora
                )

                Text(
                    text = dose.dosagem,
                    fontSize = 14.sp,
                    color = TextoSuaveMedHora
                )

                Text(
                    text = "Horário: ${dose.horario}",
                    fontSize = 12.sp,
                    color = cor
                )

                if (dose.tomadaEm != null) {
                    Text(
                        text = "Tomada ✓ às ${
                            dose.tomadaEm.format(
                                DateTimeFormatter.ofPattern("HH:mm")
                            )
                        }",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
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
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = VerdeMedHora
                )
            }
        }
    }
}
