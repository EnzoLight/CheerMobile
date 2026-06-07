package com.cheermobile.services

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

import com.cheermobile.models.RegisterVoluntarioRequest
import com.cheermobile.models.AuthResponse
import com.cheermobile.models.LoginRequest
import com.cheermobile.models.User
import com.cheermobile.models.Evento

interface CheerApiService {

    @POST("auth/register-voluntario" )
    suspend fun registerVoluntario(@Body request: RegisterVoluntarioRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/register-instituicao")
    suspend fun registerInstituicao(@Body request: RegisterInstituicaoRequest): Response<AuthResponse>

    @GET("me")
    suspend fun getMe(): Response<User>

    @GET("eventos")
    suspend fun getEventos(): Response<List<Evento>>

    @POST("eventos")
    suspend fun createEvento(@Body request: CreateEventoRequest): Response<CreateEventoResponse>

    @GET("instituicao/eventos")
    suspend fun getMeusEventos(): Response<MeusEventosResponse>
    
    // Adicione outros endpoints conforme necessário
}