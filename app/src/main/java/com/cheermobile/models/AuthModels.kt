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
// Em com.cheermobile.models.AuthModels.kt


data class RegisterVoluntarioRequest(
    val nome: String,
    val email: String,
    val password: String,
    val telefone: String?,
    val cpf: String,
    val rg: String?,
    val genero: String?,
    @SerializedName("data_nascimento") val dataNascimento: String,
    val endereco: EnderecoRequest // Objeto de endereço
)

data class ProfileResponse(
    val status: String,
    val data: ProfileData
)

data class ProfileData(
    val tipo: String, // "voluntario" ou "instituicao"
    val id: Int,
    val nome: String,
    val email: String
    // Adicione outros campos se precisar (telefone, cidade, etc)
)


// Resposta padrão para Auth
data class AuthResponse(
    val message: String,
    val user: User? = null,
    val token: String? = null
)

// Em com.cheermobile.models.AuthModels.kt

data class UserProfileResponse(
    val status: String,
    val data: UserProfileData
)

data class UserProfileData(
    val id: Int,
    val nome: String,
    val email: String,
    val tipo: String, // "voluntario" ou "instituicao"
    val telefone: String? = null,
    val categoria: String? = null,
    val cidade: String? = null,
    val uf: String? = null,
    val endereco: Endereco? = null,
)


// Request body for /api/auth/mobile/exchange
data class MobileExchangeRequest(
    val code: String
)
