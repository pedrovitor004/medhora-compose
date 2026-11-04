package com.example.medhoracompose.data.local

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * SQLite não possui tipos próprios para LocalDate, LocalTime ou listas Kotlin.
 * Neste projeto, o Room usa estes conversores para gravá-los como TEXT e depois
 * reconstruí-los. Ex.: [08:00, 20:00] vira "08:00;20:00".
 * São duas direções: objeto -> texto ao salvar; texto -> objeto ao consultar.
 * Os enums CorMedicamento e Frequencia usam a conversão padrão do próprio Room.
 */
class Conversores {
    @TypeConverter fun dataParaTexto(valor: LocalDate): String = valor.toString()
    @TypeConverter fun textoParaData(valor: String): LocalDate = LocalDate.parse(valor)
    @TypeConverter fun horaParaTexto(valor: LocalTime): String = valor.toString()
    @TypeConverter fun textoParaHora(valor: String): LocalTime = LocalTime.parse(valor)
    @TypeConverter fun momentoParaTexto(valor: LocalDateTime): String = valor.toString()
    @TypeConverter fun textoParaMomento(valor: String): LocalDateTime = LocalDateTime.parse(valor)
    @TypeConverter fun horariosParaTexto(valor: List<LocalTime>): String = valor.joinToString(";")
    @TypeConverter fun textoParaHorarios(valor: String): List<LocalTime> =
        if (valor.isEmpty()) emptyList() else valor.split(";").map(LocalTime::parse)
}
