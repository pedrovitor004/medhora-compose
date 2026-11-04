package com.example.medhoracompose

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.medhoracompose.data.repository.MedHoraRepositorioCompartilhado
import com.example.medhoracompose.data.repository.PreferenciasRepository
import com.example.medhoracompose.notifications.AgendadorLembretes
import com.example.medhoracompose.notifications.Notificacoes
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

/**
 * Inicialização comum a toda a aplicação, antes da Activity ou de um receiver usar dados.
 * O escopo acompanha o processo: trocar de tela não interrompe a observação do banco.
 * Ao iniciar um novo processo, os objetos são recriados e os arquivos são lidos de novo.
 */
class MedHoraApplication : Application() {
    var erroCarregamento by mutableStateOf<String?>(null)
        private set
    val carregado = MutableStateFlow(false)
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate +
        CoroutineExceptionHandler { _, erro ->
            Log.e("MedHora", "Falha ao carregar a persistência local", erro)
            erroCarregamento = "Não foi possível carregar os dados locais. Feche e abra o aplicativo para tentar novamente."
        })

    override fun onCreate() {
        super.onCreate()
        Notificacoes.criarCanal(this)
        MedHoraRepositorioCompartilhado.inicializar(this, applicationScope)
        PreferenciasRepository.inicializar(this, applicationScope)

        applicationScope.launch {
            // filterNotNull aguarda a primeira leitura real de cada armazenamento.
            // Assim não agendamos alarmes usando listas vazias provisórias nem valores
            // de preferência que ainda não foram recuperados do arquivo.
            combine(
                MedHoraRepositorioCompartilhado.repository.dados.filterNotNull(),
                PreferenciasRepository.dados.filterNotNull()
            ) { medicamentos, preferencias -> medicamentos to preferencias }
                .collect {
                    // Só agenda depois de recuperar AMBAS as fontes do disco.
                    AgendadorLembretes.atualizar(this@MedHoraApplication)
                    carregado.value = true
                }
        }
    }
}
