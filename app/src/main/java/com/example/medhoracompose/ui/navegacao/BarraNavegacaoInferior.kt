package com.example.medhoracompose.ui.navegacao

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.medhoracompose.R

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
            icon = {
                androidx.compose.material3.Icon(
                    painter = painterResource(R.drawable.ic_home),
                    contentDescription = "Início",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Início") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = VerdeMedHora,
                selectedTextColor = VerdeMedHora,
                indicatorColor = Color(0xFFE6F6EF),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )

        NavigationBarItem(
            selected = destinoAtual == DestinoInferior.HISTORICO,
            onClick = { onDestinoSelecionado(DestinoInferior.HISTORICO) },
            icon = {
                androidx.compose.material3.Icon(
                    painter = painterResource(R.drawable.ic_clock),
                    contentDescription = "Histórico",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Histórico") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = VerdeMedHora,
                selectedTextColor = VerdeMedHora,
                indicatorColor = Color(0xFFE6F6EF),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )

        NavigationBarItem(
            selected = destinoAtual == DestinoInferior.ADICIONAR,
            onClick = { onDestinoSelecionado(DestinoInferior.ADICIONAR) },
            icon = {
                androidx.compose.material3.Icon(
                    painter = painterResource(R.drawable.ic_add_circle),
                    contentDescription = "Adicionar",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Adicionar") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = VerdeMedHora,
                selectedTextColor = VerdeMedHora,
                indicatorColor = Color(0xFFE6F6EF),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )

        NavigationBarItem(
            selected = destinoAtual == DestinoInferior.MEDICAMENTOS,
            onClick = { onDestinoSelecionado(DestinoInferior.MEDICAMENTOS) },
            icon = {
                androidx.compose.material3.Icon(
                    painter = painterResource(R.drawable.ic_list),
                    contentDescription = "Medicamentos",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Medicamentos") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = VerdeMedHora,
                selectedTextColor = VerdeMedHora,
                indicatorColor = Color(0xFFE6F6EF),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )

        NavigationBarItem(
            selected = destinoAtual == DestinoInferior.CONFIGURACOES,
            onClick = { onDestinoSelecionado(DestinoInferior.CONFIGURACOES) },
            icon = {
                androidx.compose.material3.Icon(
                    painter = painterResource(R.drawable.ic_settings),
                    contentDescription = "Configurações",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Config.") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = VerdeMedHora,
                selectedTextColor = VerdeMedHora,
                indicatorColor = Color(0xFFE6F6EF),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
    }
}
