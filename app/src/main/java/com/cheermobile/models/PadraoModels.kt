package com.cheermobile.models

import com.google.gson.annotations.SerializedName
data class Endereco(
    val id: Int? = null,
    val rua: String = "",
    val numero: String? = null,
    val complemento: String? = null,
    val bairro: String = "",
    val cidade: String = "",
    val uf: String = "",
    @SerializedName("codigo_postal") val cep: String,
    val lat: Double? = null,
    val lng: Double? = null
)
