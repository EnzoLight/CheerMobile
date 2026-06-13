package com.cheermobile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.cheermobile.models.RegisterInstituicaoRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.cheermobile.models.RegisterVoluntarioRequest
import com.cheermobile.models.Evento
import com.cheermobile.models.AuthResponse
import com.cheermobile.models.CreateEventoRequest
import com.cheermobile.models.LoginRequest
import com.cheermobile.models.UserProfileData
import com.cheermobile.models.MobileExchangeRequest
//import com.cheermobile.models.RegisterInstituicaoRequest
import com.cheermobile.retrofit.RetrofitClient
import java.util.UUID

class MyViewModel : ViewModel() {
    private var pendingMobileState: String? = null


    // Em com.cheermobile.MyViewModel.kt

    fun fetchUserProfile(onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.getMe()
                if (response.isSuccessful) {
                    val profile = response.body()?.data
                    onResult(true, profile?.tipo) // Retorna o tipo: "voluntario" ou "instituicao"
                } else {
                    onResult(false, null)
                }
            } catch (e: Exception) {
                onResult(false, null)
            }
        }
    }


    // Em com.cheermobile.MyViewModel.kt

    fun registerInstituicao(request: RegisterInstituicaoRequest, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.registerInstituicao(request)
                if (response.isSuccessful) {
                    // Se a API retornar 201 (Created), o cadastro foi um sucesso
                    onResult(true, "Instituição cadastrada! Bem-vindo.")
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Erro desconhecido"
                    onResult(false, "Falha no cadastro: $errorMsg")
                }
            } catch (e: Exception) {
                onResult(false, "Erro de rede: ${e.message}")
            }
        }
    }


    // função cadastro
    fun registerNewVoluntario(
        request: RegisterVoluntarioRequest,
        onResult: (Boolean, String) -> Unit // Adicione este parâmetro
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.registerVoluntario(request)
                if (response.isSuccessful) {
                    onResult(true, "Cadastro realizado com sucesso!")
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Erro desconhecido"
                    onResult(false, "Erro no registro: $errorMsg")
                }
            } catch (e: Exception) {
                onResult(false, "Erro de rede: ${e.message}")
            }
        }
    }

    fun createEvento(request: CreateEventoRequest, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
        try {
            val response = RetrofitClient.instance.createEvento(request)
            if (response.isSuccessful) {
                onResult(true, response.body()?.message ?: "Evento criado com sucesso.")
            } else {
                onResult(false, "Erro ao criar evento: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            onResult(false, "Erro de rede: Verifique sua conexão à internet")
        }
    }
}

    fun getMeusEventos(onResult: (Boolean, List<Evento>, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.getMeusEventos()
                if (response.isSuccessful) {
                    val responseBody = response.body()

                    val listaEventos = responseBody?.data ?: emptyList<Evento>()

                    onResult(true, listaEventos, null)
                } else {
                    onResult(false, emptyList<Evento>(), "Não foi possível carregar os eventos.")
                }
            } catch (e: Exception) {
                onResult(false, emptyList<Evento>(), "Erro de rede: Verifique sua conexão à internet")
            }
        }
    }

    fun getEventos(onResult: (Boolean, List<Evento>, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.getEventos()
                val responseBody = response.body()

                if (response.isSuccessful && responseBody?.status != "error") {
                    onResult(true, responseBody?.data ?: emptyList(), null)
                } else {
                    onResult(false, emptyList(), responseBody?.message ?: "Nao foi possivel carregar os eventos.")
                }
            } catch (e: Exception) {
                onResult(false, emptyList(), "Erro de rede: verifique sua conexao.")
            }
        }
    }

    fun startMobileLogin(context: Context, onResult: (Boolean, String) -> Unit) {
        val state = UUID.randomUUID().toString()
        pendingMobileState = state

        val loginUri = Uri.parse("${RetrofitClient.API_ORIGIN}/api/auth/mobile/login")
            .buildUpon()
            .appendQueryParameter("redirect_uri", MOBILE_REDIRECT_URI)
            .appendQueryParameter("state", state)
            .build()

        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, loginUri))
        } catch (e: Exception) {
            pendingMobileState = null
            onResult(false, "Nao foi possivel abrir o navegador para login.")
        }
    }

    fun handleMobileCallback(uri: Uri, onResult: (Boolean, UserProfileData?, String) -> Unit) {
        val error = uri.getQueryParameter("error")
        if (error != null) {
            onResult(false, null, uri.getQueryParameter("error_description") ?: "Login cancelado.")
            return
        }

        val expectedState = pendingMobileState
        val state = uri.getQueryParameter("state")
        if (expectedState != null && state != expectedState) {
            onResult(false, null, "Resposta de login invalida.")
            return
        }

        val code = uri.getQueryParameter("code")
        if (code.isNullOrBlank()) {
            onResult(false, null, "Codigo de login ausente.")
            return
        }

        exchangeMobileCode(code) { success, perfil, message ->
            if (success) {
                pendingMobileState = null
            }
            onResult(success, perfil, message ?: if (success) "Login realizado!" else "Nao foi possivel concluir o login.")
        }
    }

    /**
     * Exchanges the one-time mobile auth code (received via cheer://auth/callback?code=...)
     * for a real session cookie by calling POST /api/auth/mobile/exchange.
     * After this call the OkHttp CookieJar holds the session cookie and all
     * subsequent API calls are authenticated.
     */
    fun exchangeMobileCode(code: String, onResult: (Boolean, UserProfileData?, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.mobileExchange(MobileExchangeRequest(code))
                if (response.isSuccessful) {
                    onResult(true, response.body()?.data, null)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val message = listOfNotNull(
                        "Falha na troca do codigo: HTTP ${response.code()}",
                        errorBody?.takeIf { it.isNotBlank() },
                    ).joinToString("\n")

                    Log.e("CheerAuth", message)
                    onResult(false, null, message)
                }
            } catch (e: Exception) {
                val message = "Erro de rede: ${e::class.java.simpleName}: ${e.message}"
                Log.e("CheerAuth", message, e)
                onResult(false, null, message)
            }
        }
    }

    fun getMe(onResult: (Boolean, UserProfileData?, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.getMe() // Chama a função da Service
                if (response.isSuccessful) {
                    // Aqui usamos .body()?.data porque UserProfileResponse tem o campo 'data'
                    onResult(true, response.body()?.data, null)
                } else {
                    onResult(false, null, "Erro ao buscar perfil: ${response.code()}")
                }
            } catch (e: Exception) {
                onResult(false, null, e.message)
            }
        }
    }

    companion object {
        private const val MOBILE_REDIRECT_URI = "cheer://auth/callback"
    }
}
