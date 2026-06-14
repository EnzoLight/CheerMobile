package com.cheermobile.services

import com.cheermobile.models.ViaCepAddressResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ViaCepApiService {
    @GET("{cep}/json/")
    suspend fun buscarEndereco(@Path("cep") cep: String): Response<ViaCepAddressResponse>
}
