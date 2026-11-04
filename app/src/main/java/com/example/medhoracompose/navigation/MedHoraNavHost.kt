package com.example.medhoracompose.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import com.example.medhoracompose.notifications.Notificacoes
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import com.example.medhoracompose.ui.common.SplashScreen
import com.example.medhoracompose.ui.common.OnboardingScreen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.medhoracompose.data.repository.MedHoraRepositorioCompartilhado
import com.example.medhoracompose.ui.cadastro.CadastroScreen
import com.example.medhoracompose.ui.configuracoes.ConfiguracoesScreen
import com.example.medhoracompose.ui.detalhes.DetalhesScreen
import com.example.medhoracompose.ui.historico.HistoricoScreen
import com.example.medhoracompose.ui.home.HomeScreen
import com.example.medhoracompose.ui.medicamentos.MedicamentosScreen
import com.example.medhoracompose.ui.navegacao.BarraNavegacaoInferior
import com.example.medhoracompose.ui.navegacao.DestinoInferior
import java.time.LocalDate

private object Routes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val CADASTRO = "cadastro"
    const val DETALHES = "detalhes"
    const val HISTORICO = "historico"
    const val MEDICAMENTOS = "medicamentos"
    const val CONFIGURACOES = "configuracoes"
}

@Composable
fun MedHoraNavHost(
    modifier: Modifier = Modifier,
    medicamentoNotificacaoId: Long? = null,
    onNotificacaoConsumida: () -> Unit = {}
) {
    val context = LocalContext.current
    val rotaInicial = rememberSaveable { if (medicamentoNotificacaoId != null) Routes.HOME else Routes.SPLASH }
    val navController = rememberNavController()
    val repository = MedHoraRepositorioCompartilhado.repository
    // Converte as emissões do repositório em estado do Compose.
    // Quando o Room emitir novos dados, as telas que dependem deles serão recompostas.
    val dados by repository.dados.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }
    var salvando by remember { mutableStateOf(false) }

    // As telas enviam eventos; a escrita acontece em uma corrotina.
    // O callback de cadastro só volta à tela anterior depois que o DAO concluir.
    // Se ocorrer erro, mostramos uma mensagem em vez de fingir que o dado foi salvo.
    fun gravar(operacao: suspend () -> Unit) {
        if (salvando) return
        salvando = true
        scope.launch {
            try {
                operacao()
            } catch (cancelada: CancellationException) {
                throw cancelada
            } catch (erro: Exception) {
                snackbar.showSnackbar("Não foi possível salvar. Tente novamente.")
            } finally {
                salvando = false
            }
        }
    }

    var medicamentoSelecionadoId by rememberSaveable {
        mutableStateOf<Long?>(null)
    }

    val entradaAtual by navController.currentBackStackEntryAsState()
    val rotaAtual = entradaAtual?.destination?.route

    val destinoAtual = when (rotaAtual) {
        Routes.HISTORICO -> DestinoInferior.HISTORICO
        Routes.CADASTRO -> DestinoInferior.ADICIONAR
        Routes.MEDICAMENTOS, Routes.DETALHES ->
            DestinoInferior.MEDICAMENTOS
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
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = {
            if (mostrarBarra) {
                BarraNavegacaoInferior(
                    destinoAtual = destinoAtual,
                    onDestinoSelecionado = { destino ->
                        val rota = when (destino) {
                            DestinoInferior.INICIO -> Routes.HOME
                            DestinoInferior.HISTORICO -> Routes.HISTORICO
                            DestinoInferior.ADICIONAR -> Routes.CADASTRO
                            DestinoInferior.MEDICAMENTOS -> Routes.MEDICAMENTOS
                            DestinoInferior.CONFIGURACOES -> Routes.CONFIGURACOES
                        }

                        if (destino == DestinoInferior.ADICIONAR) {
                            medicamentoSelecionadoId = null
                        }

                        navController.navigate(rota) {
                            popUpTo(
                                Routes.HOME
                            ) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingInterno ->
        Box(modifier = Modifier.padding(paddingInterno).consumeWindowInsets(paddingInterno)) {
            NavHost(
                navController = navController,
                startDestination = rotaInicial
            ) {
                composable(Routes.SPLASH) {
                    LaunchedEffect(Unit) {
                        delay(1400)
                        navController.navigate(Routes.ONBOARDING) { popUpTo(Routes.SPLASH) { inclusive = true } }
                    }
                    SplashScreen()
                }
                composable(Routes.ONBOARDING) {
                    OnboardingScreen {
                        navController.navigate(Routes.HOME) { popUpTo(Routes.ONBOARDING) { inclusive = true } }
                    }
                }
                composable(Routes.HOME) {
                    var dataSelecionada by remember {
                        mutableStateOf(LocalDate.now())
                    }

                    val doses = dados?.let { repository.obterDoses(dataSelecionada) }.orEmpty()

                    HomeScreen(
                        onConfiguracoesClick = { navController.navigate(Routes.CONFIGURACOES) },
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
                        onDataSelecionada = { data ->
                            dataSelecionada = data
                        },
                        onDoseClick = { medicamentoId ->
                            medicamentoSelecionadoId = medicamentoId
                            navController.navigate(Routes.DETALHES)
                        },
                        onMarcarTomada = { dose, concluido ->
                            gravar {
                                repository.marcarDoseTomada(dose)
                                Notificacoes.cancelarDose(context, dose.chave)
                                concluido()
                            }
                        }
                    )
                }

                composable(Routes.CADASTRO) {
                    val medicamentoParaEditar =
                        medicamentoSelecionadoId?.let { id ->
                            repository.buscarPorId(id)
                        }

                    CadastroScreen(
                        onVoltarClick = {
                            navController.popBackStack()
                        },
                        medicamentoExistente = medicamentoParaEditar,
                        onSalvarClick = { medicamento ->
                            gravar {
                                if (medicamentoParaEditar == null) {
                                    repository.adicionar(medicamento)
                                } else {
                                    repository.atualizar(medicamento)
                                }
                                navController.popBackStack()
                            }
                        }
                    )
                }

                composable(Routes.DETALHES) {
                    val medicamento = medicamentoSelecionadoId?.let { id ->
                        repository.buscarPorId(id)
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
                            gravar {
                                repository.remover(medicamentoParaExcluir.id)
                                medicamentoSelecionadoId = null
                                navController.popBackStack()
                            }
                        }
                    )
                }

                composable(Routes.HISTORICO) {
                    val dosesHistorico = dados?.dosesRegistradas.orEmpty()

                    HistoricoScreen(
                        onConfiguracoesClick = { navController.navigate(Routes.CONFIGURACOES) },
                        onVoltarClick = {
                            navController.popBackStack()
                        },
                        doses = dosesHistorico
                    )
                }

                composable(Routes.MEDICAMENTOS) {
                    val medicamentos = dados?.medicamentos.orEmpty()

                    MedicamentosScreen(
                        onConfiguracoesClick = { navController.navigate(Routes.CONFIGURACOES) },
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
                    ConfiguracoesScreen()
                }
            }
        }
    }
    LaunchedEffect(medicamentoNotificacaoId) {
        val id = medicamentoNotificacaoId ?: return@LaunchedEffect
        // Mant?m In?cio no hist?rico de navega??o, inclusive ao abrir com o app fechado.
        navController.navigate(Routes.HOME) {
            popUpTo(navController.graph.id)
            launchSingleTop = true
        }
        if (repository.buscarPorId(id) != null) {
            medicamentoSelecionadoId = id
            navController.navigate(Routes.DETALHES) { launchSingleTop = true }
            onNotificacaoConsumida()
        } else {
            onNotificacaoConsumida()
            scope.launch { snackbar.showSnackbar("Este medicamento não está mais disponível.") }
        }
    }

}
