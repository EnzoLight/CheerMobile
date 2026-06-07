package com.cheermobile.models

import com.google.gson.annotations.SerializedName

// Requisição de Registro de Instituição
data class RegisterInstituicaoRequest(
    val nome: String,
    val email: String,
    val password: String,
    val telefone: String?,
    val cnpj: String,
    val tipo: String?,
    val categoria: String?,
    @SerializedName("ano_fundacao") val anoFundacao: Int?,
    val internacional: Boolean?,
    val endereco: EnderecoRequest
)

data class EnderecoRequest(
    val rua: String,
    val numero: String,
    val complemento: String,
    val bairro: String,
    val cidade: String,
    val uf: String,
    @SerializedName("codigo_postal") val codigoPostal: String
)