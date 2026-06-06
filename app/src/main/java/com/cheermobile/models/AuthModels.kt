package com.cheermobile.models

import com.google.gson.annotations.SerializedName

// Modelo do Usuário (usado em várias respostas)
data class User(
    val id: Int,
    val nome: String,
    val email: String,
    val telefone: String?,
    val cpf: String?,
    @SerializedName("authentik_user") val authentikUser: String?
)

// Requisição de Login
data class LoginRequest(
    val email: String,
    val otp: String
)

// Requisição de Registro de Voluntário
data class RegisterVoluntarioRequest(
    val nome: String,
    val email: String,
    val telefone: String?,
    @SerializedName("id_endereco") val idEndereco: Int,
    val cpf: String,
    val rg: String?,
    val genero: String?,
    @SerializedName("data_nascimento") val dataNascimento: String
)

// Resposta padrão para Auth
data class AuthResponse(
    val message: String,
    val user: User? = null,
    val token: String? = null
)
