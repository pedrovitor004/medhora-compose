package com.example.medhoracompose.ui.cadastro

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.medhoracompose.data.model.CorMedicamento
import com.example.medhoracompose.data.model.Frequencia
import com.example.medhoracompose.data.model.Medicamento
import java.time.LocalTime

private val VerdeMedHora = Color(0xFF059669)
private val FundoMedHora = Color(0xFFF8FAFC)
private val TextoMedHora = Color(0xFF183B35)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadastroScreen(
    onVoltarClick: () -> Unit,
    modifier: Modifier = Modifier,
    onSalvarClick: (Medicamento) -> Unit = {}
) {
    var nome by remember { mutableStateOf("") }
    var dosagem by remember { mutableStateOf("") }
    var observacoes by remember { mutableStateOf("") }
    var frequencia by remember {
        mutableStateOf(Frequencia.TODOS_OS_DIAS)
    }
    var cor by remember { mutableStateOf(CorMedicamento.VERDE) }
    var frequenciaAberta by remember { mutableStateOf(false) }
    var erro by remember { mutableStateOf("") }

    val horarios = remember {
        mutableStateListOf("08:00")
    }

    val cores = listOf(
        CorMedicamento.VERDE to Color(0xFF059669),
        CorMedicamento.VERMELHO to Color(0xFFEF4444),
        CorMedicamento.AMARELO to Color(0xFFFACC15),
        CorMedicamento.LILAS to Color(0xFFA78BFA)
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = FundoMedHora,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Cadastro de medicamento",
                        color = TextoMedHora,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    TextButton(onClick = onVoltarClick) {
                        Text("‹", color = TextoMedHora)
                    }
                }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(FundoMedHora)
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Button(
                    onClick = {
                        val horariosConvertidos = horarios.mapNotNull {
                            runCatching { LocalTime.parse(it.trim()) }.getOrNull()
                        }

                        when {
                            nome.isBlank() -> erro = "Informe o nome do medicamento."
                            dosagem.isBlank() -> erro = "Informe a dosagem."
                            horariosConvertidos.size != horarios.size ->
                                erro = "Confira os horários. Use o formato HH:mm."
                            horariosConvertidos.isEmpty() ->
                                erro = "Adicione pelo menos um horário."
                            horariosConvertidos.distinct().size != horariosConvertidos.size ->
                                erro = "Não repita o mesmo horário."
                            else -> {
                                val medicamento = Medicamento(
                                    id = System.currentTimeMillis(),
                                    nome = nome.trim(),
                                    dosagem = dosagem.trim(),
                                    horarios = horariosConvertidos.sorted(),
                                    frequencia = frequencia,
                                    observacoes = observacoes.trim(),
                                    cor = cor
                                )
                                erro = ""
                                onSalvarClick(medicamento)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VerdeMedHora
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Salvar medicamento")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(72.dp)
                    .background(Color(0xFFD1FAE5), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("✚", color = VerdeMedHora, style = MaterialTheme.typography.headlineLarge)
            }

            Text(
                "Escolha a cor do medicamento",
                modifier = Modifier.fillMaxWidth(),
                color = TextoMedHora,
                style = MaterialTheme.typography.bodySmall
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                cores.forEach { (corMedicamento, corVisual) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(corVisual, CircleShape)
                                .clickable { cor = corMedicamento }
                                .then(
                                    if (cor == corMedicamento) {
                                        Modifier.padding(3.dp)
                                    } else {
                                        Modifier
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (cor == corMedicamento) {
                                Text("✓", color = Color.White)
                            }
                        }
                        Text(
                            when (corMedicamento) {
                                CorMedicamento.VERDE -> "Verde"
                                CorMedicamento.VERMELHO -> "Vermelho"
                                CorMedicamento.AMARELO -> "Amarelo"
                                CorMedicamento.LILAS -> "Lilás"
                            },
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            CampoCadastro(
                titulo = "Nome do medicamento",
                valor = nome,
                aoMudar = { nome = it },
                dica = "Ex.: Losartana",
                limite = 80
            )

            CampoCadastro(
                titulo = "Dosagem",
                valor = dosagem,
                aoMudar = { dosagem = it },
                dica = "Ex.: 50 mg",
                limite = 80
            )

            Text(
                "Frequência",
                color = TextoMedHora,
                fontWeight = FontWeight.SemiBold
            )

            Box {
                OutlinedButton(
                    onClick = { frequenciaAberta = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        when (frequencia) {
                            Frequencia.TODOS_OS_DIAS -> "Todos os dias"
                            Frequencia.DIAS_UTEIS -> "Dias úteis"
                            Frequencia.FINS_DE_SEMANA -> "Fins de semana"
                        }
                    )
                }

                DropdownMenu(
                    expanded = frequenciaAberta,
                    onDismissRequest = { frequenciaAberta = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Todos os dias") },
                        onClick = {
                            frequencia = Frequencia.TODOS_OS_DIAS
                            frequenciaAberta = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Dias úteis") },
                        onClick = {
                            frequencia = Frequencia.DIAS_UTEIS
                            frequenciaAberta = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Fins de semana") },
                        onClick = {
                            frequencia = Frequencia.FINS_DE_SEMANA
                            frequenciaAberta = false
                        }
                    )
                }
            }

            Text(
                "Horários das doses",
                color = TextoMedHora,
                fontWeight = FontWeight.SemiBold
            )

            horarios.forEachIndexed { indice, horario ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = horario,
                        onValueChange = { horarios[indice] = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("Horário ${indice + 1}") },
                        placeholder = { Text("08:00") },
                        singleLine = true
                    )

                    if (horarios.size > 1) {
                        TextButton(
                            onClick = { horarios.removeAt(indice) }
                        ) {
                            Text("Remover", color = Color(0xFFDC2626))
                        }
                    }
                }
            }

            OutlinedButton(
                onClick = {
                    if (horarios.size < 12) {
                        horarios.add("")
                    } else {
                        erro = "Você pode adicionar até 12 horários."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("+ Adicionar horário")
            }

            CampoCadastro(
                titulo = "Observações",
                valor = observacoes,
                aoMudar = {
                    if (it.length <= 500) observacoes = it
                },
                dica = "Instruções ou observações (opcional)",
                limite = 500,
                linhas = 3
            )

            if (erro.isNotBlank()) {
                Text(
                    erro,
                    color = Color(0xFFDC2626),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun CampoCadastro(
    titulo: String,
    valor: String,
    aoMudar: (String) -> Unit,
    dica: String,
    limite: Int,
    linhas: Int = 1
) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(
            titulo,
            color = TextoMedHora,
            fontWeight = FontWeight.SemiBold
        )

        OutlinedTextField(
            value = valor,
            onValueChange = { if (it.length <= limite) aoMudar(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(dica) },
            singleLine = linhas == 1,
            minLines = linhas,
            maxLines = if (linhas == 1) 1 else 5
        )
    }
}
