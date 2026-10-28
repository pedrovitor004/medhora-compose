package com.example.medhoracompose.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val VerdeCalendario = Color(0xFF059669)
private val FundoCalendario = Color(0xFFFFFFFF)
private val TextoCalendario = Color(0xFF183B35)

@Composable
fun CalendarioSemanal(
    dataSelecionada: LocalDate,
    onDataSelecionada: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val hoje = LocalDate.now()
    val inicioSemana = hoje.with(DayOfWeek.MONDAY)
    val locale = Locale.forLanguageTag("pt-BR")

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        repeat(7) { indice ->
            val data = inicioSemana.plusDays(indice.toLong())
            val selecionado = data == dataSelecionada
            val nomeDia = data.format(
                DateTimeFormatter.ofPattern("EEE", locale)
            ).replace(".", "")

            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 2.dp)
                    .height(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (selecionado) VerdeCalendario
                        else FundoCalendario
                    )
                    .clickable { onDataSelecionada(data) }
                    .semantics {
                        contentDescription =
                            "${data.format(DateTimeFormatter.ofPattern("EEEE", locale))}, " +
                                    data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$nomeDia\n${data.dayOfMonth}",
                    color = if (selecionado) Color.White else TextoCalendario,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
