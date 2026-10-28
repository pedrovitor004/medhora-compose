package com.example.medhoracompose.ui.navegacao

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
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

private val VerdeMedHora = Color(0xFF059669)
private val VerdeEscuroMedHora = Color(0xFF064E3B)
private val VerdeClaroMedHora = Color(0xFFD1FAE5)

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
        containerColor = Color.White,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            with(this) {
                ItemNavegacao(destinoAtual, DestinoInferior.INICIO, "Início", R.drawable.ic_home, onDestinoSelecionado)
                ItemNavegacao(destinoAtual, DestinoInferior.HISTORICO, "Histórico", R.drawable.ic_clock, onDestinoSelecionado)
                ItemNavegacao(destinoAtual, DestinoInferior.ADICIONAR, "Adicionar", R.drawable.ic_add_circle, onDestinoSelecionado)
                ItemNavegacao(destinoAtual, DestinoInferior.MEDICAMENTOS, "Medicamentos", R.drawable.ic_list, onDestinoSelecionado)
                ItemNavegacao(destinoAtual, DestinoInferior.CONFIGURACOES, "Config.", R.drawable.ic_settings, onDestinoSelecionado)
            }
        }
    }
}

@Composable
private fun RowScope.ItemNavegacao(
    destinoAtual: DestinoInferior,
    destino: DestinoInferior,
    texto: String,
    icone: Int,
    aoSelecionar: (DestinoInferior) -> Unit
) {
    val selecionado = destinoAtual == destino

    Column(
        modifier = Modifier
            .weight(1f)
            .clickable { aoSelecionar(destino) }
            .padding(vertical = 8.dp, horizontal = 1.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (destino == DestinoInferior.ADICIONAR) {
            Box(
                modifier = Modifier.size(28.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add_circle),
                    contentDescription = texto,
                    tint = VerdeMedHora,
                    modifier = Modifier.fillMaxSize()
                )

                Text(
                    text = "+",
                    color = Color.White,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }
        } else {
            Icon(
                painter = painterResource(icone),
                contentDescription = texto,
                tint = VerdeMedHora,
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            text = texto,
            color = if (selecionado) VerdeEscuroMedHora else VerdeMedHora,
            fontSize = 9.sp,
            lineHeight = 11.sp,
            fontWeight = if (selecionado) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Clip,
            textAlign = TextAlign.Center
        )
    }
}
