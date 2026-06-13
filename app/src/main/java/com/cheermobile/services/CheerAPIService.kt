package com.cheermobile.services

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.QueryMap

import com.cheermobile.models.RegisterVoluntarioRequest
import com.cheermobile.models.AuthResponse
import com.cheermobile.models.CreateEventoRequest
import com.cheermobile.models.CreateEventoResponse
import com.cheermobile.models.EventosResponse
import com.cheermobile.models.MeusEventosResponse
import com.cheermobile.models.MobileExchangeRequest
import com.cheermobile.models.RegisterInstituicaoRequest
import com.cheermobile.models.UserProfileResponse
import com.cheermobile.models.InscricaoRequest
import com.cheermobile.models.InscricaoResponse
import com.cheermobile.models.InscricoesResponse
import com.cheermobile.models.InscritosEventoResponse
import com.cheermobile.models.StatusInscritoRequest
import com.cheermobile.models.DashboardInstituicaoResponse
import com.cheermobile.models.LogsResponse

interface CheerApiService {

    @POST("auth/register-voluntario")
    suspend fun registerVoluntario(@Body request: RegisterVoluntarioRequest): Response<AuthResponse>

    @POST("auth/register-instituicao")
    suspend fun registerInstituicao(@Body request: RegisterInstituicaoRequest): Response<AuthResponse>

    // Exchanges the one-time mobile code for a session cookie
    @POST("auth/mobile/exchange")
    suspend fun mobileExchange(@Body request: MobileExchangeRequest): Response<UserProfileResponse>

    @POST("auth/logout")
    suspend fun logout(): Response<Unit>

    @GET("me")
    suspend fun getMe(): Response<UserProfileResponse>

    @GET("eventos")
    suspend fun getEventos(@QueryMap filters: Map<String, String> = emptyMap()): Response<EventosResponse>

    @POST("eventos")
    suspend fun createEvento(@Body request: CreateEventoRequest): Response<CreateEventoResponse>

    @GET("eventos/{id}")
    suspend fun getEvento(@Path("id") id: Int): Response<CreateEventoResponse>

    @PUT("eventos/{id}")
    suspend fun updateEvento(@Path("id") id: Int, @Body request: CreateEventoRequest): Response<CreateEventoResponse>

    @DELETE("eventos/{id}")
    suspend fun deleteEvento(@Path("id") id: Int): Response<Unit>

    @POST("eventos/inscrever")
    suspend fun inscreverEvento(@Body request: InscricaoRequest): Response<InscricaoResponse>

    @GET("meus-eventos")
    suspend fun getMeusEventos(): Response<MeusEventosResponse>

    @GET("minhas-inscricoes")
    suspend fun getMinhasInscricoes(): Response<InscricoesResponse>

    @GET("eventos/{id}/inscritos")
    suspend fun getInscritosEvento(@Path("id") id: Int): Response<InscritosEventoResponse>

    @PATCH("eventos/{id}/inscritos/{voluntario_id}/status")
    suspend fun updateStatusInscrito(
        @Path("id") eventoId: Int,
        @Path("voluntario_id") voluntarioId: Int,
        @Body request: StatusInscritoRequest,
    ): Response<Unit>

    @GET("dashboard/instituicao")
    suspend fun getDashboardInstituicao(): Response<DashboardInstituicaoResponse>

    @GET("logs")
    suspend fun getLogs(@QueryMap filters: Map<String, String> = emptyMap()): Response<LogsResponse>
}
