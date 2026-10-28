package com.example.medhoracompose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.medhoracompose.data.repository.MedicamentoRepository
import com.example.medhoracompose.ui.cadastro.CadastroScreen
import com.example.medhoracompose.ui.detalhes.DetalhesScreen
import com.example.medhoracompose.ui.historico.HistoricoScreen
import com.example.medhoracompose.ui.home.HomeScreen
import java.time.LocalDate

private object Routes {
    const val HOME = "home"
    const val CADASTRO = "cadastro"
    const val DETALHES = "detalhes"
    const val HISTORICO = "historico"
}

@Composable
fun MedHoraNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val repository = remember { MedicamentoRepository() }

    var atualizacao by remember { mutableIntStateOf(0) }
    var medicamentoSelecionadoId by remember {
        mutableStateOf<Long?>(null)
    }

    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier
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
                    navController.navigate(Routes.DETALHES)
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
            val medicamentoParaEditar = medicamentoSelecionadoId?.let {
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
    }
}
