package com.cheermobile.retrofit

import android.content.Context
import com.cheermobile.services.CheerApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    const val API_ORIGIN = "https://cheerapi.astrum.app.br"
    private const val BASE_URL = "$API_ORIGIN/api/"

    private var applicationContext: Context? = null

    fun init(context: Context) {
        applicationContext = context.applicationContext
    }

    private val loggingInterceptor = HttpLoggingInterceptor( ).apply {
        setLevel(HttpLoggingInterceptor.Level.BODY) // Para ver os logs completos das requisições e respostas
    }

    private val cookieJar: SharedPreferencesCookieJar by lazy {
        val context = requireNotNull(applicationContext) {
            "RetrofitClient.init(context) must be called before using the API."
        }

        SharedPreferencesCookieJar(context)
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    fun clearSessionCookies() {
        cookieJar.clear()
    }

    val instance: CheerApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CheerApiService::class.java)
    }
}
