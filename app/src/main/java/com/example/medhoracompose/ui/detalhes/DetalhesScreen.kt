package com.example.medhoracompose.ui.detalhes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DetalhesScreen(onVoltarClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Detalhes do medicamento", style = MaterialTheme.typography.headlineMedium)
        Text("As informações do medicamento serão exibidas aqui futuramente.")
        Button(onClick = onVoltarClick) { Text("Voltar") }
    }
}
