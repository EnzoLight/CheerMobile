package com.cheermobile

import android.content.Intent
import android.os.Bundle
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
import com.cheermobile.ui.screens.*
import com.cheermobile.ui.theme.CheerMobileTheme

class MainActivity : ComponentActivity() {

    // Holds a deep-link code that arrived before the UI was ready
    private var pendingDeepLinkCode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Capture a deep-link code if the app was cold-started via cheer://auth/callback
        pendingDeepLinkCode = intent?.data
            ?.takeIf { it.scheme == "cheer" && it.host == "auth" }
            ?.getQueryParameter("code")

        enableEdgeToEdge()
        setContent {
            CheerMobileTheme {
                val myViewModel: MyViewModel = viewModel()
                var currentScreen by remember { mutableStateOf("home") }

                // If the activity was launched by a deep link while already running,
                // onNewIntent fires and sets this flag so the exchange can be triggered.
                var deepLinkCode by remember { mutableStateOf(pendingDeepLinkCode) }

                // Exchange deep-link code as soon as we have one
                LaunchedEffect(deepLinkCode) {
                    val code = deepLinkCode ?: return@LaunchedEffect
                    deepLinkCode = null
                    exchangeMobileCode(myViewModel, code) { screen -> currentScreen = screen }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (currentScreen) {
                            "home" -> HomeScreen(
                                onStartClick = { currentScreen = "login" }
                            )

                            "login" -> LoginScreen(
                                onLoginExternalClick = { currentScreen = "login_webview" },
                                onNavigateToInstitutionRegister = { currentScreen = "cadastro_instituicao" },
                                onNavigateToVolunteerRegister = { currentScreen = "cadastro_voluntario" }
                            )

                            "login_webview" -> LoginWebViewScreen(
                                onLoginSuccess = { code ->
                                    // The WebView intercepted cheer://auth/callback?code=...
                                    // Exchange the one-time code for a real session
                                    exchangeMobileCode(myViewModel, code) { screen ->
                                        currentScreen = screen
                                    }
                                },
                                onBack = { currentScreen = "login" }
                            )

                            "eventos" -> {
                                val listaDeEventos = remember { mutableStateListOf<Evento>() }
                                var estaCarregando by remember { mutableStateOf(false) }
                                EventosScreen(
                                    eventos = listaDeEventos,
                                    isLoading = estaCarregando,
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

    // Called when the app is already running and receives a new deep-link intent
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        // The LaunchedEffect above will pick this up on the next recomposition
        // via the state variable; we store it so the composable can react.
        intent.data
            ?.takeIf { it.scheme == "cheer" && it.host == "auth" }
            ?.getQueryParameter("code")
            ?.let { pendingDeepLinkCode = it }
    }

    // ---------------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------------

    private fun exchangeMobileCode(
        vm: MyViewModel,
        code: String,
        navigate: (String) -> Unit
    ) {
        vm.exchangeMobileCode(code) { success, perfil, _ ->
            if (success && perfil != null) {
                navigate(if (perfil.tipo == "instituicao") "criar_evento" else "eventos")
            }
            // If it fails the user stays on whatever screen they were on;
            // the ViewModel can surface an error message if needed.
        }
    }
}
