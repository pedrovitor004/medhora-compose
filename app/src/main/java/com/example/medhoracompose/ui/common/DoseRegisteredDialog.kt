package com.example.medhoracompose.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.medhoracompose.R
import com.example.medhoracompose.data.model.Dose
import java.time.format.DateTimeFormatter

@Composable
fun DoseRegisteredDialog(dose: Dose, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(20.dp), color = Color.White) {
            Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(painterResource(R.drawable.ic_check), "Dose confirmada",
                    Modifier.size(64.dp).background(Color(0xFFD1FAE5), CircleShape).padding(16.dp), tint = Color(0xFF059669))
                Text("Dose registrada!", Modifier.fillMaxWidth().padding(top = 20.dp), fontSize = 22.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text("${dose.nome} ${dose.dosagem}\nàs ${dose.horario.format(DateTimeFormatter.ofPattern("HH:mm"))}",
                    Modifier.fillMaxWidth().padding(top = 12.dp), fontSize = 14.sp, letterSpacing = 0.25.sp, textAlign = TextAlign.Center)
                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth().padding(top = 24.dp).height(52.dp), shape = RoundedCornerShape(12.dp)) {
                    Text("OK", fontSize = 14.sp, fontWeight = FontWeight.Normal)
                }
            }
        }
    }
}
