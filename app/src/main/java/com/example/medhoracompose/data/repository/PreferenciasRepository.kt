package com.example.medhoracompose.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

// Um único DataStore por arquivo em todo o processo.
private val Context.preferenciasDataStore by preferencesDataStore(name = "preferencias_medhora")

// Configurações pequenas usam DataStore Preferences, separado das tabelas do Room.
// Estes valores também são os padrões quando o arquivo ainda não possui as chaves.
data class Preferencias(
    val lembretes: Boolean = false,
    val notificacoes: Boolean = true,
    val somAtivado: Boolean = true
)

/**
 * Traduz as chaves do arquivo DataStore para o objeto Preferencias.
 * store.data observa o arquivo; store.edit grava uma alteração transacional.
 * Foi separado do object abaixo para que os testes possam usar arquivos temporários.
 */
class ArmazenamentoPreferencias(private val store: DataStore<Preferences>) {
    private val lembretes = booleanPreferencesKey("lembretes_ativados")
    private val notificacoes = booleanPreferencesKey("notificacoes_ativadas")
    private val som = booleanPreferencesKey("som_ativado")

    val dados = store.data.map { preferencias ->
        Preferencias(preferencias[lembretes] ?: false, preferencias[notificacoes] ?: true,
            preferencias[som] ?: true)
    }

    suspend fun definirLembretes(ativados: Boolean) {
        store.edit { it[lembretes] = ativados }
    }

    suspend fun definirNotificacoes(ativadas: Boolean) {
        store.edit { it[notificacoes] = ativadas }
    }

    suspend fun definirSom(ativado: Boolean) {
        store.edit { it[som] = ativado }
    }

    suspend fun ativarLembretes() {
        store.edit {
            it[lembretes] = true
            it[notificacoes] = true
        }
    }
}

// Expõe as preferências atuais como StateFlow e centraliza as operações de escrita.
// null na primeira emissão significa que a leitura inicial ainda não terminou.
object PreferenciasRepository {
    private lateinit var armazenamento: ArmazenamentoPreferencias
    private lateinit var instancia: kotlinx.coroutines.flow.StateFlow<Preferencias?>
    val dados get() = instancia

    val lembretesAtivados get() = dados.value?.lembretes ?: false
    val notificacoesAtivadas get() = dados.value?.notificacoes ?: true
    val somAtivado get() = dados.value?.somAtivado ?: true

    fun inicializar(context: Context, scope: CoroutineScope) {
        armazenamento = ArmazenamentoPreferencias(context.applicationContext.preferenciasDataStore)
        instancia = armazenamento.dados.stateIn(scope, SharingStarted.Eagerly, null)
    }

    suspend fun definirLembretes(ativados: Boolean) = armazenamento.definirLembretes(ativados)
    suspend fun definirNotificacoes(ativadas: Boolean) = armazenamento.definirNotificacoes(ativadas)
    suspend fun ativarLembretes() = armazenamento.ativarLembretes()
    suspend fun definirSom(ativado: Boolean) = armazenamento.definirSom(ativado)
}
