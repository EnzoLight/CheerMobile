package com.cheermobile.retrofit

import com.cheermobile.services.CheerApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://cheerapi.astrum.app.br/api/" // Substitua pela URL real da sua API

    private val loggingInterceptor = HttpLoggingInterceptor( ).apply {
        setLevel(HttpLoggingInterceptor.Level.BODY) // Para ver os logs completos das requisições e respostas
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val instance: CheerApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CheerApiService::class.java)
    }
}