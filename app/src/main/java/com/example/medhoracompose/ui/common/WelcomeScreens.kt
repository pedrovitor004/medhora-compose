package com.example.medhoracompose.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhoracompose.R

@Composable
fun SplashScreen() {
    Column(
        Modifier.fillMaxSize().background(Color(0xFFF8FAFC)).drawBehind {
            drawCircle(Color(0xFFD1FAE5), 110.dp.toPx(), Offset(10.dp.toPx(), 10.dp.toPx()))
            drawCircle(Color(0xFFD1FAE5), 130.dp.toPx(), Offset(size.width - 20.dp.toPx(), size.height))
        }.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(painterResource(R.drawable.medhora_logo), "MedHora", Modifier.size(260.dp))
        Text("Seu organizador\nde medicamentos", Modifier.fillMaxWidth().padding(top = 16.dp), fontSize = 14.sp, letterSpacing = 0.25.sp, textAlign = TextAlign.Center)
        LinearProgressIndicator(Modifier.padding(top = 64.dp).width(88.dp).height(4.dp), color = Color(0xFF059669), trackColor = Color(0xFFD1FAE5))
    }
}

@Composable
fun OnboardingScreen(onStart: () -> Unit) {
    Column(Modifier.fillMaxSize().background(Color(0xFFF8FAFC))) {
        TextButton(onClick = onStart, modifier = Modifier.align(Alignment.End).padding(end = 20.dp).height(48.dp)) { Text("Pular", fontSize = 14.sp, fontWeight = FontWeight.Normal) }
        Column(Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painterResource(R.drawable.medhora_logo), "MedHora", Modifier.padding(top = 32.dp).size(240.dp))
            Text("Nunca esqueça\nseus medicamentos", Modifier.fillMaxWidth().padding(top = 24.dp), fontSize = 22.sp, letterSpacing = 0.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text("Cadastre seus remédios e organize os horários de forma simples e prática.", Modifier.fillMaxWidth().padding(top = 16.dp), fontSize = 14.sp, letterSpacing = 0.25.sp, color = Color(0xFF71838A), textAlign = TextAlign.Center)
            Text("Seus medicamentos ficam salvos neste celular para você consultar quando precisar.", Modifier.fillMaxWidth().padding(top = 24.dp), fontSize = 12.sp, letterSpacing = 0.4.sp, color = Color(0xFF71838A), textAlign = TextAlign.Center)
        }
        Button(onClick = onStart, modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 24.dp).height(44.dp), shape = RoundedCornerShape(12.dp)) {
            Text("Começar →", fontSize = 14.sp, fontWeight = FontWeight.Normal)
        }
    }
}
