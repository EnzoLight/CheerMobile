package com.cheermobile.retrofit

import com.cheermobile.services.ViaCepApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ViaCepClient {
    val instance: ViaCepApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://viacep.com.br/ws/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ViaCepApiService::class.java)
    }
}
