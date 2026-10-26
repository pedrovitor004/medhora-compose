package com.example.medhoracompose.ui.home

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
fun HomeScreen(
    onCadastroClick: () -> Unit,
    onDetalhesClick: () -> Unit,
    onHistoricoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("MedHora", style = MaterialTheme.typography.headlineMedium)
        Text("Tela inicial. Seus medicamentos aparecerão aqui futuramente.")
        Button(onClick = onCadastroClick) { Text("Cadastro") }
        Button(onClick = onDetalhesClick) { Text("Detalhes") }
        Button(onClick = onHistoricoClick) { Text("Histórico") }
    }
}
