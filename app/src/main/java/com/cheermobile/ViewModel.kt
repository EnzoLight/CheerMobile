package com.cheermobile

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

class MyViewModel : ViewModel() {


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
                    onResult(false, null, "Falha na troca do código: ${response.code()}")
                }
            } catch (e: Exception) {
                onResult(false, null, "Erro de rede: ${e.message}")
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




}
