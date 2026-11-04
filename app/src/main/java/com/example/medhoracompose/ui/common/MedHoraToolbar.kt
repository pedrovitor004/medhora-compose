package com.example.medhoracompose.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhoracompose.R

@Composable
fun MedHoraToolbar(title: String, onBack: (() -> Unit)? = null, onSettings: (() -> Unit)? = null, logo: Boolean = false) {
    Row(Modifier.fillMaxWidth().height(56.dp).padding(start = if (onBack == null) 16.dp else 0.dp), verticalAlignment = Alignment.CenterVertically) {
        if (onBack != null) {
            IconButton(onClick = onBack, modifier = Modifier.width(48.dp)) { Icon(painterResource(R.drawable.ic_back), "Voltar", tint = Color(0xFF183B35)) }
            Spacer(Modifier.width(12.dp))
        }
        if (logo) {
            Icon(painterResource(R.drawable.ic_clock), null, Modifier.size(24.dp), tint = Color.Unspecified)
            Spacer(Modifier.width(0.dp))
        }
        Text(title, Modifier.weight(1f), fontSize = 22.sp, letterSpacing = 0.sp, fontWeight = FontWeight.Bold, color = Color(0xFF183B35), maxLines = 1, overflow = TextOverflow.Ellipsis)
        if (onSettings != null) {
            IconButton(onClick = onSettings) { Icon(painterResource(R.drawable.ic_bell), "Configurações", tint = Color.Unspecified) }
        }
    }
}
