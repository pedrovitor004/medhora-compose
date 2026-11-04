package com.example.medhoracompose.data.local

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/** SQLite guarda textos; os conversores recuperam os tipos usados pelo app. */
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
