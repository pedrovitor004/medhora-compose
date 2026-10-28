package com.example.medhoracompose.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.compose.material3.Scaffold
import com.example.medhoracompose.data.repository.MedicamentoRepository
import com.example.medhoracompose.ui.cadastro.CadastroScreen
import com.example.medhoracompose.ui.detalhes.DetalhesScreen
import com.example.medhoracompose.ui.historico.HistoricoScreen
import com.example.medhoracompose.ui.home.HomeScreen
import com.example.medhoracompose.ui.medicamentos.MedicamentosScreen
import com.example.medhoracompose.ui.navegacao.BarraNavegacaoInferior
import com.example.medhoracompose.ui.navegacao.DestinoInferior
import java.time.LocalDate

private object Routes {
    const val HOME = "home"
    const val CADASTRO = "cadastro"
    const val DETALHES = "detalhes"
    const val HISTORICO = "historico"
    const val MEDICAMENTOS = "medicamentos"
    const val CONFIGURACOES = "configuracoes"
}

@Composable
fun MedHoraNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val repository = remember { MedicamentoRepository() }

    var atualizacao by remember { mutableIntStateOf(0) }
    var medicamentoSelecionadoId by remember {
        mutableStateOf<Long?>(null)
    }

    val entradaAtual by navController.currentBackStackEntryAsState()
    val rotaAtual = entradaAtual?.destination?.route

    val destinoAtual = when (rotaAtual) {
        Routes.HISTORICO -> DestinoInferior.HISTORICO
        Routes.CADASTRO -> DestinoInferior.ADICIONAR
        Routes.MEDICAMENTOS, Routes.DETALHES -> DestinoInferior.MEDICAMENTOS
        Routes.CONFIGURACOES -> DestinoInferior.CONFIGURACOES
        else -> DestinoInferior.INICIO
    }

    val mostrarBarra = rotaAtual in setOf(
        Routes.HOME,
        Routes.HISTORICO,
        Routes.MEDICAMENTOS,
        Routes.CONFIGURACOES
    )

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (mostrarBarra) {
                BarraNavegacaoInferior(
                    destinoAtual = destinoAtual,
                    onDestinoSelecionado = { destino ->
                        when (destino) {
                            DestinoInferior.INICIO -> Routes.HOME
                            DestinoInferior.HISTORICO -> Routes.HISTORICO
                            DestinoInferior.ADICIONAR -> Routes.CADASTRO
                            DestinoInferior.MEDICAMENTOS -> Routes.MEDICAMENTOS
                            DestinoInferior.CONFIGURACOES -> Routes.CONFIGURACOES
                        }.let { rota ->
                            if (destino == DestinoInferior.ADICIONAR) {
                                medicamentoSelecionadoId = null
                            }

                            navController.navigate(rota) {
                                popUpTo(
                                    navController.graph.findStartDestination().id
                                ) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { paddingInterno ->
        Box(modifier = Modifier.padding(paddingInterno)) {
            NavHost(
                navController = navController,
                startDestination = Routes.HOME
            ) {
                composable(Routes.HOME) {
                    val doses = remember(atualizacao) {
                        repository.obterDoses(LocalDate.now())
                    }

                    HomeScreen(
                        doses = doses,
                        onCadastroClick = {
                            medicamentoSelecionadoId = null
                            navController.navigate(Routes.CADASTRO)
                        },
                        onDetalhesClick = {
                            navController.navigate(Routes.MEDICAMENTOS)
                        },
                        onHistoricoClick = {
                            navController.navigate(Routes.HISTORICO)
                        },
                        onDoseClick = { medicamentoId ->
                            medicamentoSelecionadoId = medicamentoId
                            navController.navigate(Routes.DETALHES)
                        },
                        onMarcarTomada = { dose ->
                            repository.marcarDoseTomada(dose)
                            atualizacao++
                        }
                    )
                }

                composable(Routes.CADASTRO) {
                    val medicamentoParaEditar =
                        medicamentoSelecionadoId?.let {
                            repository.buscarPorId(it)
                        }

                    CadastroScreen(
                        onVoltarClick = {
                            navController.popBackStack()
                        },
                        medicamentoExistente = medicamentoParaEditar,
                        onSalvarClick = { medicamento ->
                            if (medicamentoParaEditar == null) {
                                repository.adicionar(medicamento)
                            } else {
                                repository.atualizar(medicamento)
                            }

                            atualizacao++
                            navController.popBackStack()
                        }
                    )
                }

                composable(Routes.DETALHES) {
                    val medicamento = medicamentoSelecionadoId?.let {
                        repository.buscarPorId(it)
                    }

                    DetalhesScreen(
                        onVoltarClick = {
                            navController.popBackStack()
                        },
                        medicamento = medicamento,
                        onEditarClick = { medicamentoParaEditar ->
                            medicamentoSelecionadoId = medicamentoParaEditar.id
                            navController.navigate(Routes.CADASTRO)
                        },
                        onExcluirClick = { medicamentoParaExcluir ->
                            repository.remover(medicamentoParaExcluir.id)
                            medicamentoSelecionadoId = null
                            atualizacao++
                            navController.popBackStack()
                        }
                    )
                }

                composable(Routes.HISTORICO) {
                    val dosesHistorico = remember(atualizacao) {
                        repository.obterDosesRegistradas()
                    }

                    HistoricoScreen(
                        onVoltarClick = {
                            navController.popBackStack()
                        },
                        doses = dosesHistorico
                    )
                }

                composable(Routes.MEDICAMENTOS) {
                    val medicamentos = remember(atualizacao) {
                        repository.medicamentos
                    }

                    MedicamentosScreen(
                        medicamentos = medicamentos,
                        onMedicamentoClick = { medicamentoId ->
                            medicamentoSelecionadoId = medicamentoId
                            navController.navigate(Routes.DETALHES)
                        },
                        onAdicionarClick = {
                            medicamentoSelecionadoId = null
                            navController.navigate(Routes.CADASTRO)
                        }
                    )
                }

                composable(Routes.CONFIGURACOES) {
                    androidx.compose.foundation.layout.Column {
                        androidx.compose.material3.Text(
                            text = "Configurações",
                            modifier = Modifier.padding(24.dp),
                            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
                        )
                        androidx.compose.material3.Text(
                            text = "Esta tela será implementada no próximo passo.",
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            }
        }
    }
}
