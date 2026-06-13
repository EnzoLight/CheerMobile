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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cheermobile.models.DashboardData
import com.cheermobile.models.Evento
import com.cheermobile.models.Inscricao
import com.cheermobile.models.LogEvento
import com.cheermobile.models.UserProfileData
import com.cheermobile.retrofit.RetrofitClient
import com.cheermobile.ui.screens.CadastroInstituicaoScreen
import com.cheermobile.ui.screens.CadastroVoluntarioScreen
import com.cheermobile.ui.screens.CalendarioScreen
import com.cheermobile.ui.screens.CriarEventoScreen
import com.cheermobile.ui.screens.DashboardInstituicaoScreen
import com.cheermobile.ui.screens.EventosScreen
import com.cheermobile.ui.screens.HomeScreen
import com.cheermobile.ui.screens.LoginScreen
import com.cheermobile.ui.screens.LoginWebViewScreen
import com.cheermobile.ui.screens.LogsFilters
import com.cheermobile.ui.screens.LogsOperacionaisScreen
import com.cheermobile.ui.screens.PerfilScreen
import com.cheermobile.ui.theme.CheerBackground
import com.cheermobile.ui.theme.CheerMobileTheme

class MainActivity : ComponentActivity() {
    private val mobileCallbackUri = mutableStateOf<Uri?>(null)
    private val resumeSignal = mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RetrofitClient.init(applicationContext)
        mobileCallbackUri.value = intent?.data
        enableEdgeToEdge()

        setContent {
            CheerMobileTheme {
                val myViewModel: MyViewModel = viewModel()
                var currentScreen by rememberSaveable { mutableStateOf("login") }
                var authenticatedUserType by rememberSaveable { mutableStateOf<String?>(null) }
                var authErrorMessage by remember { mutableStateOf<String?>(null) }
                var profile by remember { mutableStateOf<UserProfileData?>(null) }
                var profileLoading by remember { mutableStateOf(false) }
                var profileError by remember { mutableStateOf<String?>(null) }

                var eventos by remember { mutableStateOf<List<Evento>>(emptyList()) }
                var eventosLoading by remember { mutableStateOf(false) }
                var eventosError by remember { mutableStateOf<String?>(null) }
                var eventosFeedback by remember { mutableStateOf<String?>(null) }

                var meusEventos by remember { mutableStateOf<List<Evento>>(emptyList()) }
                var minhasInscricoes by remember { mutableStateOf<List<Inscricao>>(emptyList()) }
                var calendarioLoading by remember { mutableStateOf(false) }
                var calendarioError by remember { mutableStateOf<String?>(null) }

                var dashboard by remember { mutableStateOf<DashboardData?>(null) }
                var dashboardLoading by remember { mutableStateOf(false) }
                var dashboardError by remember { mutableStateOf<String?>(null) }
                var dashboardFeedback by remember { mutableStateOf<String?>(null) }
                var editingEventId by rememberSaveable { mutableStateOf<Int?>(null) }

                var logs by remember { mutableStateOf<List<LogEvento>>(emptyList()) }
                var logsTotal by remember { mutableIntStateOf(0) }
                var logsLoading by remember { mutableStateOf(false) }
                var logsError by remember { mutableStateOf<String?>(null) }
                var logsFilters by remember { mutableStateOf(LogsFilters()) }

                val callbackUri = mobileCallbackUri.value
                val resumeCount = resumeSignal.intValue

                fun navigateAuthenticated(tipo: String?) {
                    authenticatedUserType = tipo
                    currentScreen = if (tipo == "instituicao") "dashboard" else "eventos"
                }

                fun refreshProfile() {
                    profileLoading = true
                    profileError = null
                    myViewModel.getMe { success, perfil, error ->
                        profileLoading = false
                        if (success && perfil != null) {
                            profile = perfil
                            authenticatedUserType = perfil.tipo
                            profileError = null
                        } else {
                            profileError = error ?: "Nao foi possivel carregar seu perfil."
                        }
                    }
                }

                fun loadEventos() {
                    eventosLoading = true
                    eventosError = null
                    myViewModel.getEventos { success, resultado, erro ->
                        eventosLoading = false
                        if (success) {
                            eventos = resultado
                            eventosError = null
                            if (authenticatedUserType == "voluntario") {
                                myViewModel.getMinhasInscricoes { inscricoesSuccess, result, _ ->
                                    if (inscricoesSuccess) {
                                        minhasInscricoes = result
                                    }
                                }
                            }
                        } else {
                            eventos = emptyList()
                            eventosError = erro
                        }
                    }
                }

                fun loadCalendario() {
                    calendarioLoading = true
                    calendarioError = null
                    if (authenticatedUserType == "instituicao") {
                        myViewModel.getMeusEventos { success, result, error ->
                            calendarioLoading = false
                            meusEventos = if (success) result else emptyList()
                            calendarioError = if (success) null else error
                        }
                    } else {
                        myViewModel.getMinhasInscricoes { success, result, error ->
                            calendarioLoading = false
                            minhasInscricoes = if (success) result else emptyList()
                            calendarioError = if (success) null else error
                        }
                    }
                }

                fun loadDashboard() {
                    if (authenticatedUserType != "instituicao") return
                    dashboardLoading = true
                    dashboardError = null
                    myViewModel.getDashboardInstituicao { success, data, error ->
                        dashboardLoading = false
                        dashboard = data
                        dashboardError = if (success) null else error
                    }
                }

                fun loadLogs() {
                    if (authenticatedUserType != "instituicao") return
                    logsLoading = true
                    logsError = null
                    myViewModel.getLogs(logsFilters.toQueryMap()) { success, items, total, error ->
                        logsLoading = false
                        logs = items
                        logsTotal = total
                        logsError = if (success) null else error
                    }
                }

                fun clearAuthenticatedState() {
                    authenticatedUserType = null
                    profile = null
                    profileError = null
                    eventos = emptyList()
                    meusEventos = emptyList()
                    minhasInscricoes = emptyList()
                    dashboard = null
                    dashboardFeedback = null
                    logs = emptyList()
                    logsTotal = 0
                    editingEventId = null
                }

                LaunchedEffect(callbackUri) {
                    callbackUri?.let { uri ->
                        if (uri.host == "auth" && uri.path == "/logout") {
                            RetrofitClient.clearSessionCookies()
                            clearAuthenticatedState()
                            authErrorMessage = null
                            currentScreen = "login"
                            Toast.makeText(this@MainActivity, "Sessao encerrada.", Toast.LENGTH_SHORT).show()
                        } else {
                            exchangeMobileCallback(
                                vm = myViewModel,
                                uri = uri,
                                navigate = { screen ->
                                    authErrorMessage = null
                                    currentScreen = screen
                                    refreshProfile()
                                },
                                onError = { message ->
                                    authErrorMessage = message
                                    currentScreen = "login"
                                },
                            )
                        }
                        mobileCallbackUri.value = null
                    }
                }

                LaunchedEffect(resumeCount) {
                    if (callbackUri != null || currentScreen != "login") {
                        return@LaunchedEffect
                    }

                    myViewModel.getMe { success, perfil, error ->
                        if (success && perfil != null) {
                            authErrorMessage = null
                            profile = perfil
                            navigateAuthenticated(perfil.tipo)
                        } else if (error != null) {
                            Log.d("CheerAuth", "Sessao ainda nao autenticada: $error")
                        }
                    }
                }

                LaunchedEffect(currentScreen, authenticatedUserType) {
                    when (currentScreen) {
                        "eventos" -> loadEventos()
                        "calendario" -> loadCalendario()
                        "dashboard" -> loadDashboard()
                        "logs" -> loadLogs()
                        "perfil" -> refreshProfile()
                    }
                }

                LaunchedEffect(logsFilters) {
                    if (currentScreen == "logs") {
                        loadLogs()
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                    bottomBar = {
                        val tipo = authenticatedUserType
                        if (tipo != null && currentScreen in authenticatedScreens) {
                            CheerBottomBar(
                                userType = tipo,
                                currentScreen = currentScreen,
                                onNavigate = { currentScreen = it },
                            )
                        }
                    },
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (currentScreen) {
                            "home" -> HomeScreen(
                                onStartClick = { currentScreen = "login" },
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
                                    val uri = Uri.parse("cheer://auth/callback")
                                        .buildUpon()
                                        .appendQueryParameter("code", code)
                                        .build()

                                    exchangeMobileCallback(
                                        vm = myViewModel,
                                        uri = uri,
                                        navigate = { screen ->
                                            authErrorMessage = null
                                            currentScreen = screen
                                            refreshProfile()
                                        },
                                        onError = { message ->
                                            authErrorMessage = message
                                            currentScreen = "login"
                                        },
                                    )
                                },
                                onBack = { currentScreen = "login" },
                            )

                            "eventos" -> EventosScreen(
                                eventos = eventos,
                                isLoading = eventosLoading,
                                errorMessage = eventosError,
                                onBackClick = {
                                    currentScreen = if (authenticatedUserType == null) "login" else currentScreen
                                },
                                onRefresh = { loadEventos() },
                                canSubscribe = authenticatedUserType == "voluntario",
                                subscriptionStatuses = if (authenticatedUserType == "voluntario") {
                                    minhasInscricoes.associate { it.id to (it.status ?: "pendente") }
                                } else {
                                    emptyMap()
                                },
                                feedbackMessage = eventosFeedback,
                                onSubscribe = { evento ->
                                    myViewModel.inscreverEvento(evento.id) { success, message ->
                                        eventosFeedback = message
                                        if (success) {
                                            loadEventos()
                                            loadCalendario()
                                        }
                                    }
                                },
                            )

                            "calendario" -> CalendarioScreen(
                                isInstituicao = authenticatedUserType == "instituicao",
                                eventos = meusEventos,
                                inscricoes = minhasInscricoes,
                                isLoading = calendarioLoading,
                                errorMessage = calendarioError,
                                onRefresh = { loadCalendario() },
                            )

                            "perfil" -> PerfilScreen(
                                profile = profile,
                                isLoading = profileLoading,
                                errorMessage = profileError,
                                onRefresh = { refreshProfile() },
                                onPrimaryAction = {
                                    currentScreen = if (authenticatedUserType == "instituicao") "criar_evento" else "calendario"
                                },
                                onLogout = {
                                    myViewModel.logout { _, message ->
                                        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                                        clearAuthenticatedState()
                                        currentScreen = "login"
                                    }
                                },
                            )

                            "dashboard" -> DashboardInstituicaoScreen(
                                dashboard = dashboard,
                                isLoading = dashboardLoading,
                                errorMessage = dashboardError,
                                feedback = dashboardFeedback,
                                onRefresh = { loadDashboard() },
                                onEditEvento = { evento ->
                                    editingEventId = evento.id
                                    currentScreen = "editar_evento"
                                },
                                onDeleteEvento = { evento ->
                                    myViewModel.deleteEvento(evento.id) { success, message ->
                                        dashboardFeedback = message
                                        if (success) {
                                            loadDashboard()
                                            loadCalendario()
                                        }
                                    }
                                },
                            )

                            "logs" -> LogsOperacionaisScreen(
                                logs = logs,
                                total = logsTotal,
                                isLoading = logsLoading,
                                errorMessage = logsError,
                                filters = logsFilters,
                                onFiltersChange = { logsFilters = it },
                                onRefresh = { loadLogs() },
                            )

                            "cadastro_instituicao" -> CadastroInstituicaoScreen(
                                onBackClick = { currentScreen = "login" },
                                onSuccessNavigate = {
                                    authenticatedUserType = "instituicao"
                                    currentScreen = "dashboard"
                                    refreshProfile()
                                },
                            )

                            "cadastro_voluntario" -> CadastroVoluntarioScreen(
                                onBackClick = { currentScreen = "login" },
                                onSuccessNavigate = {
                                    authenticatedUserType = "voluntario"
                                    currentScreen = "eventos"
                                    refreshProfile()
                                },
                            )

                            "criar_evento" -> CriarEventoScreen(
                                onBackClick = { currentScreen = "dashboard" },
                                onSaved = {
                                    loadDashboard()
                                    loadCalendario()
                                },
                            )

                            "editar_evento" -> CriarEventoScreen(
                                onBackClick = {
                                    editingEventId = null
                                    currentScreen = "dashboard"
                                },
                                eventoIdToEdit = editingEventId,
                                onSaved = {
                                    editingEventId = null
                                    currentScreen = "dashboard"
                                    loadDashboard()
                                    loadCalendario()
                                },
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
        resumeSignal.intValue += 1
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
                navigate(if (perfil.tipo == "instituicao") "dashboard" else "eventos")
            }
        }
    }

    companion object {
        private val authenticatedScreens = setOf("eventos", "calendario", "perfil", "dashboard", "criar_evento", "editar_evento", "logs")
    }
}

private data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

@Composable
private fun CheerBottomBar(
    userType: String,
    currentScreen: String,
    onNavigate: (String) -> Unit,
) {
    val items = if (userType == "instituicao") {
        listOf(
            BottomNavItem("dashboard", "Painel", Icons.Default.Dashboard),
            BottomNavItem("criar_evento", "Criar", Icons.Default.AddCircle),
            BottomNavItem("calendario", "Agenda", Icons.Default.CalendarMonth),
            BottomNavItem("logs", "Logs", Icons.Default.ListAlt),
            BottomNavItem("perfil", "Perfil", Icons.Default.Person),
        )
    } else {
        listOf(
            BottomNavItem("eventos", "Eventos", Icons.Default.Event),
            BottomNavItem("calendario", "Agenda", Icons.Default.CalendarMonth),
            BottomNavItem("perfil", "Perfil", Icons.Default.Person),
        )
    }

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentScreen == item.route,
                onClick = { onNavigate(item.route) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
            )
        }
    }
}
