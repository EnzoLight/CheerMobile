package com.cheermobile.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cheermobile.models.Evento
import com.cheermobile.models.Inscricao
import com.cheermobile.ui.theme.CheerAccent
import com.cheermobile.ui.theme.CheerBackground
import com.cheermobile.ui.theme.CheerBrandBorder
import com.cheermobile.ui.theme.CheerMutedText
import com.cheermobile.ui.theme.CheerPrimary
import com.cheermobile.ui.theme.CheerPrimarySoft
import com.cheermobile.ui.theme.CheerSurface
import com.cheermobile.ui.theme.CheerText

@Composable
fun CalendarioScreen(
    isInstituicao: Boolean,
    eventos: List<Evento>,
    inscricoes: List<Inscricao>,
    isLoading: Boolean,
    errorMessage: String?,
    onRefresh: () -> Unit,
) {
    val hasItems = if (isInstituicao) eventos.isNotEmpty() else inscricoes.isNotEmpty()

    Surface(color = CheerBackground, modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 12.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                ScreenHeader(
                    kicker = "MINHAS ATIVIDADES",
                    title = if (isInstituicao) "Eventos publicados" else "Inscricoes em eventos",
                    description = if (isInstituicao) {
                        "Acompanhe as acoes publicadas pela instituicao."
                    } else {
                        "Veja os eventos vinculados a sua conta."
                    },
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = onRefresh) {
                    Icon(Icons.Default.Refresh, contentDescription = "Atualizar", tint = CheerPrimary)
                }
            }

            when {
                isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = CheerPrimary)
                }

                errorMessage != null -> StatePanel(text = errorMessage)

                !hasItems -> StatePanel(
                    text = if (isInstituicao) {
                        "Sua instituicao ainda nao publicou eventos."
                    } else {
                        "Voce ainda nao possui inscricoes."
                    },
                )

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if (isInstituicao) {
                        items(eventos, key = { it.id }) { evento ->
                            ActivityCard(
                                title = evento.titulo ?: "Evento sem titulo",
                                subtitle = evento.tipoEvento ?: "Geral",
                                date = evento.dataInicio,
                                location = listOf(evento.cidade, evento.uf).filter { !it.isNullOrBlank() }.joinToString(" - "),
                                badge = "${evento.inscritos ?: 0} inscritos",
                            )
                        }
                    } else {
                        items(inscricoes, key = { it.id }) { inscricao ->
                            ActivityCard(
                                title = inscricao.titulo ?: "Evento sem titulo",
                                subtitle = inscricao.instituicao ?: "Instituicao nao informada",
                                date = inscricao.data,
                                location = listOf(inscricao.cidade, inscricao.uf).filter { !it.isNullOrBlank() }.joinToString(" - "),
                                badge = inscricao.status ?: "inscrito",
                                extra = inscricao.dataInscricao?.let { "Inscricao: ${formatCompactDate(it)}" },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActivityCard(
    title: String,
    subtitle: String,
    date: String?,
    location: String,
    badge: String,
    extra: String? = null,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CheerSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, style = MaterialTheme.typography.titleMedium, color = CheerText, fontWeight = FontWeight.Bold)
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = CheerMutedText)
                }
                Surface(shape = RoundedCornerShape(50), color = CheerPrimarySoft) {
                    Text(
                        badge,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = CheerText,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            HorizontalDivider(color = CheerBrandBorder)
            DetailLine(Icons.Default.CalendarMonth, formatCompactDate(date))
            DetailLine(Icons.Default.LocationOn, location.ifBlank { "Local nao informado" })
            if (extra != null) {
                Text(extra, color = CheerAccent, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun DetailLine(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(icon, contentDescription = null, tint = CheerAccent, modifier = Modifier.padding(top = 1.dp))
        Text(text, color = CheerText, style = MaterialTheme.typography.bodySmall)
    }
}

fun formatCompactDate(value: String?): String {
    if (value.isNullOrBlank()) return "Nao informada"
    val date = value.take(10)
    val time = value.substringAfter("T", "").take(5)
    return if (time.length == 5) "$date as $time" else date
}
