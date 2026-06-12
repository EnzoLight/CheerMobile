package com.cheermobile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cheermobile.models.Evento
import com.cheermobile.retrofit.RetrofitClient
import com.cheermobile.ui.screens.*
import com.cheermobile.ui.theme.CheerMobileTheme

class MainActivity : ComponentActivity() {
    private val mobileCallbackUri = mutableStateOf<Uri?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RetrofitClient.init(applicationContext)
        mobileCallbackUri.value = intent?.data
        enableEdgeToEdge()
        setContent {
            CheerMobileTheme {
                val myViewModel: MyViewModel = viewModel()
                var currentScreen by remember { mutableStateOf("home") }
                val callbackUri = mobileCallbackUri.value

                LaunchedEffect(callbackUri) {
                    callbackUri?.let { uri ->
                        exchangeMobileCallback(myViewModel, uri) { screen -> currentScreen = screen }
                        mobileCallbackUri.value = null
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
                                            currentScreen = "login_webview"
                                        }
                                    }
                                },
                                onNavigateToInstitutionRegister = { currentScreen = "cadastro_instituicao" },
                                onNavigateToVolunteerRegister = { currentScreen = "cadastro_voluntario" }
                            )

                            "login_webview" -> LoginWebViewScreen(
                                onLoginSuccess = { code ->
                                    val callbackUri = Uri.parse("cheer://auth/callback")
                                        .buildUpon()
                                        .appendQueryParameter("code", code)
                                        .build()

                                    exchangeMobileCallback(myViewModel, callbackUri) { screen ->
                                        currentScreen = screen
                                    }
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
                                    onBackClick = { currentScreen = "home" }
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
                                onBackClick = { currentScreen = "home" }
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

    private fun exchangeMobileCallback(
        vm: MyViewModel,
        uri: Uri,
        navigate: (String) -> Unit
    ) {
        vm.handleMobileCallback(uri) { success, perfil, message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            if (success && perfil != null) {
                navigate(if (perfil.tipo == "instituicao") "criar_evento" else "eventos")
            }
        }
    }
}
