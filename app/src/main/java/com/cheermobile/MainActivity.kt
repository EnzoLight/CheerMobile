package com.cheermobile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cheermobile.models.Evento
import com.cheermobile.retrofit.RetrofitClient
import com.cheermobile.ui.screens.*
import com.cheermobile.ui.theme.CheerMobileTheme

class MainActivity : ComponentActivity() {
    private val mobileCallbackUri = mutableStateOf<Uri?>(null)
    private val resumeSignal = mutableStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RetrofitClient.init(applicationContext)
        mobileCallbackUri.value = intent?.data
        enableEdgeToEdge()
        setContent {
            CheerMobileTheme {
                val myViewModel: MyViewModel = viewModel()
                var currentScreen by rememberSaveable { mutableStateOf("home") }
                var authenticatedUserType by rememberSaveable { mutableStateOf<String?>(null) }
                var authErrorMessage by remember { mutableStateOf<String?>(null) }
                val callbackUri = mobileCallbackUri.value
                val resumeCount = resumeSignal.value

                fun navigateAuthenticated(tipo: String?) {
                    authenticatedUserType = tipo
                    currentScreen = if (tipo == "instituicao") {
                        "criar_evento"
                    } else {
                        "eventos"
                    }
                }

                LaunchedEffect(callbackUri) {
                    callbackUri?.let { uri ->
                        exchangeMobileCallback(
                            vm = myViewModel,
                            uri = uri,
                            navigate = { screen ->
                                authErrorMessage = null
                                authenticatedUserType = if (screen == "criar_evento") "instituicao" else "voluntario"
                                currentScreen = screen
                            },
                            onError = { message ->
                                authErrorMessage = message
                                currentScreen = "login"
                            },
                        )
                        mobileCallbackUri.value = null
                    }
                }

                LaunchedEffect(resumeCount) {
                    if (callbackUri != null || currentScreen !in setOf("home", "login")) {
                        return@LaunchedEffect
                    }

                    myViewModel.getMe { success, perfil, error ->
                        if (success && perfil != null) {
                            authErrorMessage = null
                            navigateAuthenticated(perfil.tipo)
                        } else if (error != null) {
                            Log.d("CheerAuth", "Sessao ainda nao autenticada: $error")
                        }
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (currentScreen) {
                            "home" -> HomeScreen(
                                onStartClick = { currentScreen = "login" }
                            )

                            "login" -> LoginScreen(
                                onLoginExternalClick = {
                                    myViewModel.startMobileLogin(this@MainActivity) { success, message ->
                                        if (!success) {
                                            Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                                            authErrorMessage = message
                                            currentScreen = "login_webview"
                                        }
                                    }
                                },
                                onNavigateToInstitutionRegister = { currentScreen = "cadastro_instituicao" },
                                onNavigateToVolunteerRegister = { currentScreen = "cadastro_voluntario" },
                                errorMessage = authErrorMessage,
                                onClearError = { authErrorMessage = null },
                            )

                            "login_webview" -> LoginWebViewScreen(
                                onLoginSuccess = { code ->
                                    val callbackUri = Uri.parse("cheer://auth/callback")
                                        .buildUpon()
                                        .appendQueryParameter("code", code)
                                        .build()

                                    exchangeMobileCallback(
                                        vm = myViewModel,
                                        uri = callbackUri,
                                        navigate = { screen ->
                                            authErrorMessage = null
                                            authenticatedUserType = if (screen == "criar_evento") "instituicao" else "voluntario"
                                            currentScreen = screen
                                        },
                                        onError = { message ->
                                            authErrorMessage = message
                                            currentScreen = "login"
                                        },
                                    )
                                },
                                onBack = { currentScreen = "login" }
                            )

                            "eventos" -> {
                                val listaDeEventos = remember { mutableStateListOf<Evento>() }
                                var estaCarregando by remember { mutableStateOf(true) }
                                var erroEventos by remember { mutableStateOf<String?>(null) }

                                LaunchedEffect(Unit) {
                                    estaCarregando = true
                                    myViewModel.getEventos { success, resultado, erro ->
                                        estaCarregando = false
                                        erroEventos = if (success) null else erro
                                        listaDeEventos.clear()
                                        if (success) {
                                            listaDeEventos.addAll(resultado)
                                        }
                                    }
                                }

                                EventosScreen(
                                    eventos = listaDeEventos,
                                    isLoading = estaCarregando,
                                    errorMessage = erroEventos,
                                    onBackClick = {
                                        currentScreen = if (authenticatedUserType == null) "home" else "eventos"
                                    }
                                )
                            }

                            "cadastro_instituicao" -> CadastroInstituicaoScreen(
                                onBackClick = { currentScreen = "login" },
                                onSuccessNavigate = { currentScreen = "criar_evento" }
                            )

                            "cadastro_voluntario" -> CadastroVoluntarioScreen(
                                onBackClick = { currentScreen = "login" },
                                onSuccessNavigate = { currentScreen = "eventos" }
                            )

                            "criar_evento" -> CriarEventoScreen(
                                onBackClick = {
                                    currentScreen = if (authenticatedUserType == null) "home" else "criar_evento"
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        mobileCallbackUri.value = intent.data
    }

    override fun onResume() {
        super.onResume()
        resumeSignal.value += 1
    }

    private fun exchangeMobileCallback(
        vm: MyViewModel,
        uri: Uri,
        navigate: (String) -> Unit,
        onError: (String) -> Unit,
    ) {
        vm.handleMobileCallback(uri) { success, perfil, message ->
            if (!success) {
                Log.e("CheerAuth", message)
                onError(message)
            }
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            if (success && perfil != null) {
                navigate(if (perfil.tipo == "instituicao") "criar_evento" else "eventos")
            }
        }
    }
}
