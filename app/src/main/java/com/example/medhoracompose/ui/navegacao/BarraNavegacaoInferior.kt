package com.example.medhoracompose.ui.navegacao

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val VerdeMedHora = Color(0xFF059669)

enum class DestinoInferior {
    INICIO,
    HISTORICO,
    ADICIONAR,
    MEDICAMENTOS,
    CONFIGURACOES
}

@Composable
fun BarraNavegacaoInferior(
    destinoAtual: DestinoInferior,
    onDestinoSelecionado: (DestinoInferior) -> Unit
) {
    NavigationBar(
        containerColor = Color.White
    ) {
        NavigationBarItem(
            selected = destinoAtual == DestinoInferior.INICIO,
            onClick = { onDestinoSelecionado(DestinoInferior.INICIO) },
            icon = { Text("⌂", color = VerdeMedHora) },
            label = { Text("Início") }
        )

        NavigationBarItem(
            selected = destinoAtual == DestinoInferior.HISTORICO,
            onClick = { onDestinoSelecionado(DestinoInferior.HISTORICO) },
            icon = { Text("◷", color = VerdeMedHora) },
            label = { Text("Histórico") }
        )

        NavigationBarItem(
            selected = destinoAtual == DestinoInferior.ADICIONAR,
            onClick = { onDestinoSelecionado(DestinoInferior.ADICIONAR) },
            icon = { Text("+", color = VerdeMedHora) },
            label = { Text("Adicionar") }
        )

        NavigationBarItem(
            selected = destinoAtual == DestinoInferior.MEDICAMENTOS,
            onClick = { onDestinoSelecionado(DestinoInferior.MEDICAMENTOS) },
            icon = { Text("☷", color = VerdeMedHora) },
            label = { Text("Medicamentos") }
        )

        NavigationBarItem(
            selected = destinoAtual == DestinoInferior.CONFIGURACOES,
            onClick = { onDestinoSelecionado(DestinoInferior.CONFIGURACOES) },
            icon = { Text("⚙", color = VerdeMedHora) },
            label = { Text("Config.") }
        )
    }
}
