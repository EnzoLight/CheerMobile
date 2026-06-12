package com.cheermobile.services

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

import com.cheermobile.models.RegisterVoluntarioRequest
import com.cheermobile.models.AuthResponse
import com.cheermobile.models.CreateEventoRequest
import com.cheermobile.models.CreateEventoResponse
import com.cheermobile.models.LoginRequest
import com.cheermobile.models.User
import com.cheermobile.models.Evento
import com.cheermobile.models.EventosResponse
import com.cheermobile.models.MeusEventosResponse
import com.cheermobile.models.MobileExchangeRequest
import com.cheermobile.models.RegisterInstituicaoRequest
import com.cheermobile.models.UserProfileResponse

interface CheerApiService {

    @POST("auth/register-voluntario")
    suspend fun registerVoluntario(@Body request: RegisterVoluntarioRequest): Response<AuthResponse>

    @POST("auth/register-instituicao")
    suspend fun registerInstituicao(@Body request: RegisterInstituicaoRequest): Response<AuthResponse>

    // Exchanges the one-time mobile code for a session cookie
    @POST("auth/mobile/exchange")
    suspend fun mobileExchange(@Body request: MobileExchangeRequest): Response<UserProfileResponse>

    @GET("me")
    suspend fun getMe(): Response<UserProfileResponse>

    @GET("eventos")
    suspend fun getEventos(): Response<EventosResponse>

    @POST("eventos")
    suspend fun createEvento(@Body request: CreateEventoRequest): Response<CreateEventoResponse>

    @GET("meus-eventos")
    suspend fun getMeusEventos(): Response<MeusEventosResponse>
}
