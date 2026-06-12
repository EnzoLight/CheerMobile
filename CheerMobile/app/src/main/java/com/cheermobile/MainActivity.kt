package com.cheermobile

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
import com.cheermobile.models.LoginRequest
import com.cheermobile.ui.screens.EventosScreen
import com.cheermobile.ui.screens.HomeScreen
import com.cheermobile.ui.screens.LoginScreen
import com.cheermobile.ui.theme.CheerMobileTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CheerMobileTheme {
                val myViewModel: MyViewModel = viewModel()
                var currentScreen by remember { mutableStateOf("home") }
                val eventos = remember { mutableStateListOf<Evento>() }
                var isLoadingEventos by remember { mutableStateOf(false) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (currentScreen) {
                            "home" -> HomeScreen(
                                onNavigateToEvents = {
                                    currentScreen = "eventos"
                                },
                                onNavigateToRegister = {
                                    currentScreen = "login"
                                }
                            )
                            "eventos" -> EventosScreen(
                                eventos = eventos,
                                isLoading = isLoadingEventos,
                                onBackClick = { currentScreen = "home" }
                            )
                            "login" -> LoginScreen(
                                onLoginClick = { email, otp ->
                                    myViewModel.loginVoluntario(LoginRequest(email, otp)) { success, message ->
                                        if (success) {
                                            currentScreen = "home"
                                        }
                                    }
                                },
                                onRegisterClick = { /* Navegar para registro */ }
                            )
                        }
                    }
                }
            }
        }
    }
}
