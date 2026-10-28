package com.example.medhoracompose.ui.medicamentos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhoracompose.R
import com.example.medhoracompose.data.model.CorMedicamento
import com.example.medhoracompose.data.model.Medicamento
import java.text.Normalizer
import java.util.Locale

private val VerdeMedicamentos = Color(0xFF059669)
private val FundoMedicamentos = Color(0xFFF8FAFC)
private val TextoMedicamentos = Color(0xFF183B35)
private val TextoSecundario = Color(0xFF71838A)
private val BordaMedicamentos = Color(0xFFE2E8EB)

@Composable
fun MedicamentosScreen(
    medicamentos: List<Medicamento>,
    onMedicamentoClick: (Long) -> Unit,
    onAdicionarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var busca by remember { mutableStateOf("") }

    val medicamentosFiltrados = medicamentos
        .filter {
            normalizarNome(it.nome).contains(
                normalizarNome(busca.trim())
            )
        }
        .sortedBy {
            it.nome.lowercase(Locale("pt", "BR"))
        }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FundoMedicamentos)
    ) {
        // Título equivalente ao estilo MedHora.Title.
        Text(
            text = "Meus medicamentos",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            ),
            color = TextoMedicamentos
        )

        // Campo de busca com cantos de 10 dp e fundo branco.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 16.dp)
        ) {
            OutlinedTextField(
                value = busca,
                onValueChange = { busca = it.take(100) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_search),
                        contentDescription = "Buscar",
                        tint = TextoSecundario
                    )
                },
                placeholder = {
                    Text(
                        text = "Buscar medicamento",
                        color = TextoSecundario
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = VerdeMedicamentos,
                    unfocusedBorderColor = BordaMedicamentos,
                    focusedTextColor = TextoMedicamentos,
                    unfocusedTextColor = TextoMedicamentos,
                    cursorColor = VerdeMedicamentos,
                    focusedLabelColor = TextoSecundario,
                    unfocusedLabelColor = TextoSecundario
                )
            )
        }

        if (medicamentosFiltrados.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (busca.isBlank()) {
                        "Nenhum medicamento cadastrado.\nAdicione um medicamento para começar."
                    } else {
                        "Nenhum medicamento encontrado para essa busca."
                    },
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp
                    ),
                    color = TextoSecundario
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = medicamentosFiltrados,
                    key = { it.id }
                ) { medicamento ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onMedicamentoClick(medicamento.id)
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 1.dp
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(
                                    iconeDoMedicamento(medicamento.cor)
                                ),
                                contentDescription = "Medicamento",
                                modifier = Modifier.size(40.dp),
                                tint = Color.Unspecified
                            )

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 16.dp)
                            ) {
                                Text(
                                    text = medicamento.nome,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = TextoMedicamentos
                                )

                                Text(
                                    text = medicamento.dosagem,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 14.sp
                                    ),
                                    color = TextoMedicamentos
                                )

                                Text(
                                    text = "${medicamento.horarios.size} " +
                                            if (medicamento.horarios.size == 1) {
                                                "horário cadastrado"
                                            } else {
                                                "horários cadastrados"
                                            },
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 12.sp
                                    ),
                                    color = TextoSecundario
                                )
                            }

                            Icon(
                                painter = painterResource(R.drawable.ic_chevron),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = Color.Unspecified
                            )
                        }
                    }
                }
            }
        }

        // Botão equivalente ao estilo MedHora.Button.
        Button(
            onClick = onAdicionarClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .heightIn(min = 52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VerdeMedicamentos,
                contentColor = Color.White
            ),
            contentPadding = PaddingValues(
                horizontal = 16.dp,
                vertical = 12.dp
            )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_plus),
                contentDescription = null
            )

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = "Adicionar medicamento",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private fun normalizarNome(valor: String): String {
    return Normalizer.normalize(
        valor.lowercase(Locale("pt", "BR")),
        Normalizer.Form.NFD
    ).replace(Regex("\\p{M}"), "")
}

private fun iconeDoMedicamento(cor: CorMedicamento): Int {
    return when (cor) {
        CorMedicamento.VERDE -> R.drawable.ic_capsule_green
        CorMedicamento.VERMELHO -> R.drawable.ic_capsule_red
        CorMedicamento.AMARELO -> R.drawable.ic_capsule_yellow
        CorMedicamento.LILAS -> R.drawable.ic_capsule_lilac
    }
}
