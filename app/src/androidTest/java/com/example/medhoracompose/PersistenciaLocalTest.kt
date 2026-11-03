package com.example.medhoracompose

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.medhoracompose.data.local.MedHoraDatabase
import com.example.medhoracompose.data.local.paraEntidade
import com.example.medhoracompose.data.model.*
import com.example.medhoracompose.data.repository.ArmazenamentoPreferencias
import com.example.medhoracompose.data.repository.Preferencias
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PersistenciaLocalTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val medicamento = Medicamento(
        42, "Medicamento de teste", "10 mg",
        listOf(LocalTime.of(8, 30), LocalTime.of(20, 15, 30)),
        Frequencia.DIAS_UTEIS, "Após a refeição", CorMedicamento.LILAS,
        LocalDate.of(2026, 11, 2)
    )
    private val dose = Dose(42, medicamento.nome, medicamento.dosagem, medicamento.cor,
        LocalDate.of(2026, 11, 3), LocalTime.of(8, 30))
    private val momento = LocalDateTime.of(2026, 11, 3, 8, 32)

    @Test
    fun medicamentosEHistoricoSobrevivemAReabertura() = runBlocking {
        val nome = "persistencia-${UUID.randomUUID()}.db"
        fun abrir() = Room.databaseBuilder(context, MedHoraDatabase::class.java, nome).build()
        var banco = abrir()
        try {
            banco.medHoraDao().adicionar(medicamento.paraEntidade())
            banco.medHoraDao().registrarDose(dose.paraEntidade(momento))
            banco.close()
            banco = abrir()
            val dao = banco.medHoraDao()
            assertEquals(medicamento, dao.observarMedicamentos().first().single().paraModelo())
            assertEquals(dose.copy(tomadaEm = momento), dao.observarDoses().first().single().paraModelo())

            dao.atualizar(medicamento.copy(nome = "Nome editado").paraEntidade())
            banco.close()
            banco = abrir()
            assertEquals("Nome editado", banco.medHoraDao().observarMedicamentos().first().single().nome)
            banco.medHoraDao().remover(42)
            banco.close()
            banco = abrir()
            assertTrue(banco.medHoraDao().observarMedicamentos().first().isEmpty())
            // O histórico mantém o retrato da dose antes da edição/exclusão.
            assertEquals(medicamento.nome, banco.medHoraDao().observarDoses().first().single().nome)
        } finally {
            banco.close()
            context.deleteDatabase(nome)
        }
    }

    @Test
    fun mesmaDoseNaoDuplicaEDesmarcacaoELimpezaPersistem() = runBlocking {
        val nome = "doses-${UUID.randomUUID()}.db"
        fun abrir() = Room.databaseBuilder(context, MedHoraDatabase::class.java, nome).build()
        var banco = abrir()
        try {
            var dao = banco.medHoraDao()
            dao.adicionar(medicamento.paraEntidade())
            dao.registrarDose(dose.paraEntidade(momento))
            dao.registrarDose(dose.paraEntidade(momento.plusMinutes(1)))
            assertEquals(1, dao.observarDoses().first().size)
            dao.desmarcarDose(42, dose.data, dose.horario)
            banco.close()
            banco = abrir()
            dao = banco.medHoraDao()
            assertTrue(dao.observarDoses().first().isEmpty())
            dao.registrarDose(dose.paraEntidade(momento))
            dao.limpar()
            banco.close()
            banco = abrir()
            assertTrue(banco.medHoraDao().observarMedicamentos().first().isEmpty())
            assertTrue(banco.medHoraDao().observarDoses().first().isEmpty())
        } finally {
            banco.close()
            context.deleteDatabase(nome)
        }
    }

    @Test
    fun preferenciasSobrevivemANovaInstanciaDoDataStore() = runBlocking {
        val arquivo = File(context.cacheDir, "teste-${UUID.randomUUID()}.preferences_pb")
        var job = Job()
        fun abrir() = ArmazenamentoPreferencias(PreferenceDataStoreFactory.create(
            scope = CoroutineScope(Dispatchers.IO + job), produceFile = { arquivo }
        ))
        try {
            var armazenamento = abrir()
            assertEquals(Preferencias(), armazenamento.dados.first())
            armazenamento.ativarLembretes()
            job.cancelAndJoin()
            job = Job()
            armazenamento = abrir()
            assertEquals(Preferencias(true, true), armazenamento.dados.first())
            armazenamento.definirLembretes(false)
            armazenamento.definirNotificacoes(false)
            armazenamento.definirSom(false)
            job.cancelAndJoin()
            job = Job()
            armazenamento = abrir()
            assertEquals(Preferencias(false, false, false), armazenamento.dados.first())
        } finally {
            job.cancelAndJoin()
            arquivo.delete()
        }
    }
}
