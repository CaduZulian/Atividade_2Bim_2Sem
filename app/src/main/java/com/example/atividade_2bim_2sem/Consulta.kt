package com.example.atividade_2bim_2sem

import java.io.Serializable

data class Consulta(
    val id: Long = 0,
    val codigoProduto: String,
    val nomeProduto: String,
    val marca: String = "",
    val resultadoJson: String,
    val dataHora: String
) : Serializable
