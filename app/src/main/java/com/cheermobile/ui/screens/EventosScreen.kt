package com.cheermobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cheermobile.models.Evento
import com.cheermobile.ui.components.EventoCard
import com.cheermobile.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
//@Preview(showBackground = true)
@Composable
fun EventosScreen(
    eventos: List<Evento>,
    isLoading: Boolean,
    errorMessage: String? = null,
    onBackClick: () -> Unit,
    onRefresh: (() -> Unit)? = null,
    canSubscribe: Boolean = false,
    onSubscribe: ((Evento) -> Unit)? = null,
    feedbackMessage: String? = null,
) {
    var searchTerm by remember { mutableStateOf("") }
    val filteredEventos by remember(eventos, searchTerm) {
        derivedStateOf {
            val query = searchTerm.trim().lowercase()
            if (query.isBlank()) {
                eventos
            } else {
                eventos.filter { evento ->
                    listOf(
                        evento.titulo,
                        evento.descricao,
                        evento.tipoEvento,
                        evento.cidade,
                        evento.uf,
                        evento.endereco?.cidade,
                        evento.endereco?.uf,
                        evento.endereco?.bairro,
                    ).any { value -> value?.lowercase()?.contains(query) == true }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Eventos", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    if (onRefresh != null) {
                        IconButton(onClick = onRefresh) {
                            Icon(Icons.Default.Refresh, contentDescription = "Recarregar eventos")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = CheerText
                )
            )
        },
        containerColor = CheerBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = searchTerm,
                onValueChange = { searchTerm = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar por nome, tipo ou cidade") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    unfocusedBorderColor = CheerBrandBorder,
                    focusedBorderColor = CheerPrimary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            feedbackMessage?.let {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = MaterialTheme.shapes.medium,
                    color = CheerAccentSoft,
                ) {
                    Text(
                        text = it,
                        modifier = Modifier.padding(12.dp),
                        color = CheerText,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = CheerPrimary)
                }
            } else if (errorMessage != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(errorMessage, color = CheerMutedText, textAlign = TextAlign.Center)
                        if (onRefresh != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedButton(onClick = onRefresh) {
                                Icon(Icons.Default.Refresh, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Tentar novamente")
                            }
                        }
                    }
                }
            } else if (filteredEventos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (eventos.isEmpty()) {
                            "Nenhum evento disponível no momento."
                        } else {
                            "Nenhum evento combina com a busca."
                        },
                        color = CheerMutedText,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    item {
                        Text(
                            text = "CHEER EVENTOS",
                            color = CheerPrimary,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Eventos disponíveis",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = CheerText
                        )
                        Text(
                            text = "${filteredEventos.size} oportunidade(s) para participar.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = CheerMutedText
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    items(filteredEventos, key = { it.id }) { evento ->
                        EventoCard(
                            evento = evento,
                            actionLabel = if (canSubscribe) "Inscrever-se" else null,
                            onActionClick = if (canSubscribe && onSubscribe != null) {
                                { onSubscribe(evento) }
                            } else {
                                null
                            },
                        )
                    }
                }
            }
        }
    }
}


