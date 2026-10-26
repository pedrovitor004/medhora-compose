package com.example.medhoracompose.data.model

/** Modelo inicial; as regras de cadastro serão definidas posteriormente. */
data class Medicamento(
    val id: Long,
    val nome: String,
    val dosagem: String,
    val intervaloHoras: Int,
    val observacoes: String = ""
)
