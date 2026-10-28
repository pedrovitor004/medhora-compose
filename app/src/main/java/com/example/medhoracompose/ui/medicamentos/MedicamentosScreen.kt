package com.example.medhoracompose.ui.medicamentos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.medhoracompose.data.model.CorMedicamento
import com.example.medhoracompose.data.model.Medicamento
import java.text.Normalizer
import java.util.Locale

private val VerdeMedicamentos = Color(0xFF059669)
private val FundoMedicamentos = Color(0xFFF8FAFC)
private val TextoMedicamentos = Color(0xFF183B35)

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
            normalizarNome(it.nome).contains(normalizarNome(busca.trim()))
        }
        .sortedBy { it.nome.lowercase(Locale("pt", "BR")) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FundoMedicamentos)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Meus medicamentos",
            style = MaterialTheme.typography.headlineMedium,
            color = TextoMedicamentos
        )

        Spacer(modifier = Modifier.size(16.dp))

        OutlinedTextField(
            value = busca,
            onValueChange = { busca = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Buscar medicamento") },
            placeholder = { Text("Digite o nome do medicamento") },
            singleLine = true,
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.size(16.dp))

        if (medicamentosFiltrados.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (busca.isBlank()) {
                        "Nenhum medicamento cadastrado.\nAdicione um medicamento para começar."
                    } else {
                        "Nenhum medicamento encontrado para essa busca."
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
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
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .background(
                                        color = corDoMedicamento(medicamento.cor),
                                        shape = CircleShape
                                    )
                            )

                            Spacer(modifier = Modifier.size(12.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = medicamento.nome,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextoMedicamentos
                                )

                                Text(
                                    text = medicamento.dosagem,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )

                                Text(
                                    text = "${medicamento.horarios.size} " +
                                            if (medicamento.horarios.size == 1) {
                                                "horário cadastrado"
                                            } else {
                                                "horários cadastrados"
                                            },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = VerdeMedicamentos
                                )
                            }

                            Text(
                                text = "›",
                                style = MaterialTheme.typography.headlineMedium,
                                color = VerdeMedicamentos
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.size(12.dp))

        Button(
            onClick = onAdicionarClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VerdeMedicamentos
            )
        ) {
            Text(
                text = "+ Adicionar medicamento",
                modifier = Modifier.padding(vertical = 6.dp)
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

private fun corDoMedicamento(cor: CorMedicamento): Color {
    return when (cor) {
        CorMedicamento.VERDE -> Color(0xFF059669)
        CorMedicamento.VERMELHO -> Color(0xFFDC2626)
        CorMedicamento.AMARELO -> Color(0xFFD97706)
        CorMedicamento.LILAS -> Color(0xFF9333EA)
    }
}
