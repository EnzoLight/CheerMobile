package com.cheermobile.models

import com.google.gson.annotations.SerializedName

// Em com.cheermobile.models.InstituicaoModels.kt

data class RegisterInstituicaoRequest(
    val nome: String,
    val email: String,
    val password: String,
    val cnpj: String,
    val telefone: String? = null,
    val endereco: EnderecoRequest, // Objeto aninhado obrigatório
    val tipo: String? = null,
    val categoria: String? = null,
    val anoFundacao: Int? = null,
    val internacional: Boolean? = null
)

data class EnderecoRequest(
    val rua: String,
    val numero: String = "S/N",
    val complemento: String = "",
    val bairro: String,
    val cidade: String,
    val uf: String,
    @SerializedName("codigo_postal") val codigoPostal: String
)
