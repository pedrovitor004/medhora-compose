package com.example.medhoracompose.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhoracompose.R
import com.example.medhoracompose.data.model.*
import java.time.format.DateTimeFormatter

@Composable
fun DoseCard(dose: Dose, onDetails: (() -> Unit)? = null, onTake: (() -> Unit)? = null) {
    val status = dose.status()
    val (label, color) = when (status) {
        StatusDose.TOMADA -> "Tomada" to Color(0xFF059669)
        StatusDose.PENDENTE -> "Pendente" to Color(0xFF946200)
        StatusDose.ATRASADA -> "Atrasada" to Color(0xFFE05252)
        StatusDose.PROGRAMADA -> "Programada" to Color(0xFF71838A)
    }
    val icon = when (dose.cor) {
        CorMedicamento.VERDE -> R.drawable.ic_capsule_green
        CorMedicamento.VERMELHO -> R.drawable.ic_capsule_red
        CorMedicamento.AMARELO -> R.drawable.ic_capsule_yellow
        CorMedicamento.LILAS -> R.drawable.ic_capsule_lilac
    }
    val text = Color(0xFF183B35)
    val canTake = onTake != null && dose.tomadaEm == null
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(1.dp)) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(painterResource(icon), null, Modifier.size(40.dp), tint = Color.Unspecified)
            Column(Modifier.weight(1f).clickable(enabled = onDetails != null) { onDetails?.invoke() }.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Text(dose.nome, fontSize = 15.sp, letterSpacing = 0.15.sp, fontWeight = FontWeight.Bold, color = text)
                Text(dose.dosagem, fontSize = 12.sp, letterSpacing = 0.4.sp, color = Color(0xFF71838A))
                if (dose.tomadaEm != null || onTake != null) {
                    Text(dose.tomadaEm?.let { "Registrada às ${it.format(DateTimeFormatter.ofPattern("HH:mm"))}" } ?: label,
                        Modifier.padding(top = 4.dp), fontSize = 12.sp, letterSpacing = 0.4.sp, color = color)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(dose.horario.format(DateTimeFormatter.ofPattern("HH:mm")), fontSize = 15.sp, letterSpacing = 0.15.sp, fontWeight = FontWeight.Bold, color = if (status == StatusDose.ATRASADA) color else text)
                OutlinedButton(onClick = { onTake?.invoke() }, enabled = canTake,
                    modifier = Modifier.widthIn(min = 88.dp).height(48.dp), shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, if (canTake) Color(0xFF059669) else color),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = if (canTake) Color(0xFF059669) else Color.White,
                        contentColor = Color.White, disabledContainerColor = Color.White, disabledContentColor = color)) {
                    Text(if (canTake) "Tomar" else label, fontSize = 12.sp)
                }
            }
        }
    }
}
