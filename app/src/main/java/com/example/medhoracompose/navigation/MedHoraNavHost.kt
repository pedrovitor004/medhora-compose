package com.example.medhoracompose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.medhoracompose.ui.home.HomeScreen
import com.example.medhoracompose.ui.cadastro.CadastroScreen
import com.example.medhoracompose.ui.detalhes.DetalhesScreen
import com.example.medhoracompose.ui.historico.HistoricoScreen

private object Routes {
    const val HOME = "home"
    const val CADASTRO = "cadastro"
    const val DETALHES = "detalhes"
    const val HISTORICO = "historico"
}

@Composable
fun MedHoraNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.HOME, modifier = modifier) {
        composable(Routes.HOME) {
            HomeScreen(
                onCadastroClick = { navController.navigate(Routes.CADASTRO) },
                onDetalhesClick = { navController.navigate(Routes.DETALHES) },
                onHistoricoClick = { navController.navigate(Routes.HISTORICO) }
            )
        }
        composable(Routes.CADASTRO) {
            CadastroScreen(onVoltarClick = { navController.popBackStack() })
        }
        composable(Routes.DETALHES) {
            DetalhesScreen(onVoltarClick = { navController.popBackStack() })
        }
        composable(Routes.HISTORICO) {
            HistoricoScreen(onVoltarClick = { navController.popBackStack() })
        }
    }
}
