package com.cheermobile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.cheermobile.models.RegisterVoluntarioRequest
import com.cheermobile.models.AuthResponse
import com.cheermobile.models.LoginRequest
import com.cheermobile.retrofit.RetrofitClient

class MyViewModel : ViewModel() {

    // função cadastro
    fun registerNewVoluntario(request: RegisterVoluntarioRequest) {
        viewModelScope.launch {
            try {
                // Chamada com RetrofitClient
                val response = RetrofitClient.instance.registerVoluntario(request)

                if (response.isSuccessful) {
                    val authResponse: AuthResponse? = response.body()

                    println("Registro bem-sucedido: ${authResponse?.message}")
                } else {
                    println("Erro no registro: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                println("Erro de rede: ${e.message}")
            }
        }
    }

    fun loginVoluntario(request: LoginRequest, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try{

                val response = RetrofitClient.instance.login(request)

                if (response.isSuccessful) {

                    val authResponse = response.body()

                    onResult(true, authResponse?.message ?: "Login realizado!")
                } else {
                    onResult(false, "Falha ao logar: Informações inválidas")
                //println("Erro no registro: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                onResult(false, "Erro de Rede: Verifique sua conexão a internet")
            }
        }
    }




}
