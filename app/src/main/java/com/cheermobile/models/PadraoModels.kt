package com.cheermobile.models

import com.google.gson.annotations.SerializedName
data class Endereco(
    val id: Int,
    val rua: String,
    val bairro: String,
    val cidade: String,
    val uf: String,
    @SerializedName("codigo_postal") val cep: String,
    val lat: Double?,
    val lng: Double?
)
