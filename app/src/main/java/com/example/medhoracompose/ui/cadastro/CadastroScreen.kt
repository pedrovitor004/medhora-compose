package com.example.medhoracompose.ui.cadastro

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhoracompose.R
import com.example.medhoracompose.data.model.CorMedicamento
import com.example.medhoracompose.data.model.Frequencia
import com.example.medhoracompose.data.model.Medicamento
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val VerdeMedHora = Color(0xFF059669)
private val VerdeEscuroMedHora = Color(0xFF064E3B)
private val VerdeClaroMedHora = Color(0xFFD1FAE5)
private val FundoMedHora = Color(0xFFF8FAFC)
private val TextoMedHora = Color(0xFF183B35)
private val TextoSecundarioMedHora = Color(0xFF71838A)
private val BordaMedHora = Color(0xFFE2E8EB)
private val VermelhoMedHora = Color(0xFFE05252)
private val FundoCampoMedHora = Color.White

private val FormatoHorario = DateTimeFormatter.ofPattern("HH:mm")

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun CadastroScreen(
    onVoltarClick: () -> Unit,
    modifier: Modifier = Modifier,
    onSalvarClick: (Medicamento) -> Unit = {},
    medicamentoExistente: Medicamento? = null
) {
    val contexto = LocalContext.current
    val modoEdicao = medicamentoExistente != null

    var nome by remember(medicamentoExistente?.id) {
        mutableStateOf(medicamentoExistente?.nome ?: "")
    }
    var dosagem by remember(medicamentoExistente?.id) {
        mutableStateOf(medicamentoExistente?.dosagem ?: "")
    }
    var observacoes by remember(medicamentoExistente?.id) {
        mutableStateOf(medicamentoExistente?.observacoes ?: "")
    }
    var frequencia by remember(medicamentoExistente?.id) {
        mutableStateOf(
            medicamentoExistente?.frequencia ?: Frequencia.TODOS_OS_DIAS
        )
    }
    var cor by remember(medicamentoExistente?.id) {
        mutableStateOf(medicamentoExistente?.cor ?: CorMedicamento.VERDE)
    }
    var frequenciaAberta by remember { mutableStateOf(false) }
    var erro by remember(medicamentoExistente?.id) {
        mutableStateOf("")
    }

    val horarios = remember(medicamentoExistente?.id) {
        mutableStateListOf<String>().apply {
            addAll(
                medicamentoExistente?.horarios
                    ?.sorted()
                    ?.map { it.format(FormatoHorario) }
                    ?: listOf("08:00")
            )
        }
    }

    val cores = listOf(
        Triple(CorMedicamento.VERDE, "Verde", Color(0xFF059669)),
        Triple(CorMedicamento.VERMELHO, "Vermelho", Color(0xFFEF4444)),
        Triple(CorMedicamento.AMARELO, "Amarelo", Color(0xFFF2B500)),
        Triple(CorMedicamento.LILAS, "Lilás", Color(0xFF9B7BC5))
    )

    fun abrirSeletorHorario(indice: Int? = null) {
        val horarioAtual = indice
            ?.let { horarios.getOrNull(it) }
            ?.let { runCatching { LocalTime.parse(it) }.getOrNull() }
            ?: LocalTime.of(8, 0)

        TimePickerDialog(
            contexto,
            { _, hora, minuto ->
                val novoHorario = LocalTime.of(hora, minuto)
                    .format(FormatoHorario)

                val horarioDuplicado = horarios.withIndex().any { (i, valor) ->
                    i != indice && runCatching {
                        LocalTime.parse(valor) == LocalTime.parse(novoHorario)
                    }.getOrDefault(false)
                }

                when {
                    horarioDuplicado -> {
                        erro = "Este horário já foi adicionado."
                    }

                    indice != null && indice in horarios.indices -> {
                        horarios[indice] = novoHorario
                        horarios.sortBy {
                            runCatching { LocalTime.parse(it) }
                                .getOrDefault(LocalTime.MAX)
                        }
                        erro = ""
                    }

                    horarios.size < 12 -> {
                        horarios.add(novoHorario)
                        horarios.sortBy {
                            runCatching { LocalTime.parse(it) }
                                .getOrDefault(LocalTime.MAX)
                        }
                        erro = ""
                    }

                    else -> {
                        erro = "Você pode cadastrar até 12 horários."
                    }
                }
            },
            horarioAtual.hour,
            horarioAtual.minute,
            true
        ).show()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = FundoMedHora,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (modoEdicao) {
                            "Editar medicamento"
                        } else {
                            "Novo medicamento"
                        },
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoMedHora
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVoltarClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = "Voltar",
                            tint = TextoMedHora
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FundoMedHora,
                    titleContentColor = TextoMedHora,
                    navigationIconContentColor = TextoMedHora
                )
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(FundoMedHora)
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 12.dp, top = 4.dp)
            ) {
                Button(
                    onClick = {
                        val horariosConvertidos = horarios.mapNotNull {
                            runCatching {
                                LocalTime.parse(it.trim())
                            }.getOrNull()
                        }

                        when {
                            nome.isBlank() -> {
                                erro = "Informe o nome do medicamento."
                            }

                            dosagem.isBlank() -> {
                                erro = "Informe a dosagem."
                            }

                            horarios.isEmpty() -> {
                                erro = "Adicione pelo menos um horário."
                            }

                            horariosConvertidos.size != horarios.size -> {
                                erro = "Confira os horários. Use o formato HH:mm."
                            }

                            horariosConvertidos.distinct().size !=
                                    horariosConvertidos.size -> {
                                erro = "Este horário já foi adicionado."
                            }

                            else -> {
                                val medicamento = Medicamento(
                                    id = medicamentoExistente?.id
                                        ?: System.currentTimeMillis(),
                                    nome = nome.trim(),
                                    dosagem = dosagem.trim(),
                                    horarios = horariosConvertidos.sorted(),
                                    frequencia = frequencia,
                                    observacoes = observacoes.trim(),
                                    cor = cor,
                                    criadoEm = medicamentoExistente?.criadoEm
                                        ?: LocalDate.now()
                                )

                                erro = ""
                                onSalvarClick(medicamento)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VerdeMedHora,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        "Salvar",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
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
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(72.dp)
                    .background(
                        VerdeClaroMedHora,
                        RoundedCornerShape(100.dp)
                    )
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(
                        when (cor) {
                            CorMedicamento.VERDE -> R.drawable.ic_capsule_green
                            CorMedicamento.VERMELHO -> R.drawable.ic_capsule_red
                            CorMedicamento.AMARELO -> R.drawable.ic_capsule_yellow
                            CorMedicamento.LILAS -> R.drawable.ic_capsule_lilac
                        }
                    ),
                    contentDescription = "Ícone de medicamento",
                    modifier = Modifier.fillMaxSize(),
                    tint = Color.Unspecified
                )
            }

            Text(
                text = "Cor de identificação",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                color = TextoSecundarioMedHora,
                fontSize = 12.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                cores.forEach { (corMedicamento, nomeCor, corVisual) ->
                    FilterChip(
                        selected = cor == corMedicamento,
                        onClick = {
                            cor = corMedicamento
                        },
                        label = {
                            Text(
                                nomeCor,
                                fontSize = 12.sp
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = VerdeClaroMedHora,
                            selectedLabelColor = VerdeEscuroMedHora,
                            containerColor = Color.White,
                            labelColor = TextoMedHora
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = cor == corMedicamento,
                            borderColor = BordaMedHora,
                            selectedBorderColor = VerdeMedHora
                        ),
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(corVisual, CircleShape)
                            )
                        }
                    )
                }
            }

            CampoCadastro(
                titulo = "Nome do medicamento",
                valor = nome,
                aoMudar = { nome = it },
                dica = "Ex.: Losartana",
                limite = 80,
                modifier = Modifier.padding(top = 18.dp)
            )

            CampoCadastro(
                titulo = "Dose",
                valor = dosagem,
                aoMudar = { dosagem = it },
                dica = "Ex.: 50 mg",
                limite = 80,
                modifier = Modifier.padding(top = 18.dp)
            )

            Text(
                "Frequência",
                modifier = Modifier.padding(top = 18.dp, bottom = 6.dp),
                color = TextoMedHora,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Box {
                OutlinedTextField(
                    value = when (frequencia) {
                        Frequencia.TODOS_OS_DIAS -> "Todos os dias"
                        Frequencia.DIAS_UTEIS -> "Dias úteis"
                        Frequencia.FINS_DE_SEMANA -> "Fins de semana"
                    },
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { frequenciaAberta = true },
                    readOnly = true,
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    trailingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_chevron),
                            contentDescription = "Selecionar frequência",
                            tint = TextoSecundarioMedHora
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = FundoCampoMedHora,
                        unfocusedContainerColor = FundoCampoMedHora,
                        focusedBorderColor = VerdeMedHora,
                        unfocusedBorderColor = BordaMedHora,
                        focusedTextColor = TextoMedHora,
                        unfocusedTextColor = TextoMedHora
                    )
                )

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { frequenciaAberta = true }
                )

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
                "Horários",
                modifier = Modifier.padding(top = 18.dp, bottom = 8.dp),
                color = TextoMedHora,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            horarios.forEachIndexed { indice, horario ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .background(
                            FundoCampoMedHora,
                            RoundedCornerShape(10.dp)
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .clickable {
                                abrirSeletorHorario(indice)
                            }
                            .padding(start = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_clock),
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                            tint = VerdeMedHora
                        )

                        Spacer(Modifier.width(12.dp))

                        Text(
                            text = horario,
                            color = TextoMedHora,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    IconButton(
                        onClick = {
                            if (horarios.size > 1) {
                                horarios.removeAt(indice)
                                erro = ""
                            } else {
                                erro = "Adicione pelo menos um horário."
                            }
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_delete),
                            contentDescription = "Excluir horário ${indice + 1}",
                            modifier = Modifier.size(20.dp),
                            tint = VermelhoMedHora
                        )
                    }
                }
            }

            OutlinedButton(
                onClick = {
                    if (horarios.size < 12) {
                        abrirSeletorHorario()
                    } else {
                        erro = "Você pode cadastrar até 12 horários."
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = VerdeMedHora
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color(0xFF10B981)
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_plus),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = VerdeMedHora
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Adicionar horário",
                    fontSize = 14.sp
                )
            }

            CampoCadastro(
                titulo = "Informações adicionais",
                valor = observacoes,
                aoMudar = { observacoes = it },
                dica = "Ex.: tomar após a refeição",
                limite = 500,
                linhas = 2,
                modifier = Modifier.padding(top = 18.dp)
            )

            if (erro.isNotBlank()) {
                Text(
                    text = erro,
                    modifier = Modifier.padding(top = 8.dp),
                    color = VermelhoMedHora,
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.height(12.dp))
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
    linhas: Int = 1,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = titulo,
            modifier = Modifier.padding(bottom = 6.dp),
            color = TextoMedHora,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = valor,
            onValueChange = {
                if (it.length <= limite) aoMudar(it)
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    dica,
                    color = TextoSecundarioMedHora,
                    fontSize = 14.sp
                )
            },
            singleLine = linhas == 1,
            minLines = linhas,
            maxLines = if (linhas == 1) 1 else 5,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = FundoCampoMedHora,
                unfocusedContainerColor = FundoCampoMedHora,
                focusedBorderColor = VerdeMedHora,
                unfocusedBorderColor = BordaMedHora,
                focusedTextColor = TextoMedHora,
                unfocusedTextColor = TextoMedHora,
                cursorColor = VerdeMedHora,
                focusedPlaceholderColor = TextoSecundarioMedHora,
                unfocusedPlaceholderColor = TextoSecundarioMedHora
            ),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp
            )
        )
    }
}
