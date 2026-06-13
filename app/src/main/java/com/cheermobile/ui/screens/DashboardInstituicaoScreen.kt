package com.cheermobile.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cheermobile.MyViewModel
import com.cheermobile.models.DashboardData
import com.cheermobile.models.Evento
import com.cheermobile.models.InscritoEvento
import com.cheermobile.ui.theme.CheerAccent
import com.cheermobile.ui.theme.CheerBackground
import com.cheermobile.ui.theme.CheerBrandBorder
import com.cheermobile.ui.theme.CheerMutedText
import com.cheermobile.ui.theme.CheerPrimary
import com.cheermobile.ui.theme.CheerPrimarySoft
import com.cheermobile.ui.theme.CheerSurface
import com.cheermobile.ui.theme.CheerStatusContainerColor
import com.cheermobile.ui.theme.CheerStatusContentColor
import com.cheermobile.ui.theme.CheerText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardInstituicaoScreen(
    dashboard: DashboardData?,
    isLoading: Boolean,
    errorMessage: String?,
    feedback: String?,
    onRefresh: () -> Unit,
    onEditEvento: (Evento) -> Unit,
    onDeleteEvento: (Evento) -> Unit,
    myViewModel: MyViewModel = viewModel(),
) {
    var selectedEvent by remember { mutableStateOf<Evento?>(null) }
    var inscritos by remember { mutableStateOf<List<InscritoEvento>>(emptyList()) }
    var inscritosStatus by remember { mutableStateOf("idle") }
    var inscritosError by remember { mutableStateOf<String?>(null) }
    var deleteCandidate by remember { mutableStateOf<Evento?>(null) }

    fun loadInscritos(evento: Evento) {
        selectedEvent = evento
        inscritosStatus = "loading"
        inscritosError = null
        myViewModel.getInscritosEvento(evento.id) { success, result, error ->
            inscritos = result
            inscritosStatus = if (success) "loaded" else "error"
            inscritosError = error
        }
    }

    Surface(color = CheerBackground, contentColor = CheerText, modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                ScreenHeader(
                    kicker = "INSTITUICAO",
                    title = "Dashboard",
                    description = "Indicadores, eventos publicados e inscricoes recentes.",
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = onRefresh) {
                    Icon(Icons.Default.Refresh, contentDescription = "Atualizar", tint = CheerPrimary)
                }
            }

            when {
                isLoading -> StatePanel("Carregando dashboard...") {
                    CircularProgressIndicator(color = CheerPrimary)
                }

                errorMessage != null -> StatePanel(errorMessage) {
                    OutlinedButton(onClick = onRefresh) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(Modifier.padding(4.dp))
                        Text("Tentar novamente")
                    }
                }

                dashboard == null -> StatePanel("Sem dados para exibir.")

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    feedback?.let {
                        item { FeedbackPanel(it) }
                    }

                    item {
                        val kpis = dashboard.kpis
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                                KpiCard("Eventos", "${kpis?.totalEventos ?: 0}", Modifier.weight(1f))
                                KpiCard("Futuros", "${kpis?.eventosFuturos ?: 0}", Modifier.weight(1f))
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                                KpiCard("Inscritos", "${kpis?.totalInscritos ?: 0}", Modifier.weight(1f))
                                KpiCard("Ocupacao", "${kpis?.taxaOcupacaoPercentual ?: 0.0}%", Modifier.weight(1f))
                            }
                        }
                    }

                    item {
                        SeriesPanel(
                            title = "Inscricoes por status",
                            rows = dashboard.series?.inscricoesPorStatus ?: emptyList(),
                        )
                    }

                    item {
                        SeriesPanel(
                            title = "Eventos por tipo",
                            rows = dashboard.series?.eventosPorTipo ?: emptyList(),
                        )
                    }

                    item {
                        SectionTitle("Eventos publicados")
                    }

                    val eventos = dashboard.tables?.eventos ?: emptyList()
                    if (eventos.isEmpty()) {
                        item { StatePanel("Nenhum evento publicado.") }
                    } else {
                        items(eventos, key = { it.id }) { evento ->
                            DashboardEventCard(
                                evento = evento,
                                onInscritos = { loadInscritos(evento) },
                                onEdit = { onEditEvento(evento) },
                                onDelete = { deleteCandidate = evento },
                            )
                        }
                    }

                    item {
                        SectionTitle("Inscritos recentes")
                    }

                    val recentes = dashboard.tables?.inscritosRecentes ?: emptyList()
                    if (recentes.isEmpty()) {
                        item { StatePanel("Nenhum inscrito recente.") }
                    } else {
                        items(recentes.take(8), key = { "${it.idEvento}-${it.idVoluntario}" }) { inscrito ->
                            InscritoRow(inscrito = inscrito)
                        }
                    }
                }
            }
        }
    }

    deleteCandidate?.let { evento ->
        AlertDialog(
            onDismissRequest = { deleteCandidate = null },
            title = { Text("Excluir evento") },
            text = { Text("Excluir \"${evento.titulo ?: "Evento"}\"? As inscricoes vinculadas tambem podem ser removidas.") },
            confirmButton = {
                TextButton(onClick = {
                    deleteCandidate = null
                    onDeleteEvento(evento)
                }) {
                    Text("Excluir", color = Color(0xFFC62828))
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteCandidate = null }) {
                    Text("Cancelar")
                }
            },
        )
    }

    selectedEvent?.let { evento ->
        ModalBottomSheet(
            onDismissRequest = { selectedEvent = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = CheerSurface,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                ScreenHeader(
                    kicker = "INSCRITOS",
                    title = evento.titulo ?: "Evento",
                    description = "Aprove, rejeite ou retorne inscricoes para pendente.",
                )
                when (inscritosStatus) {
                    "loading" -> CircularProgressIndicator(color = CheerPrimary)
                    "error" -> Text(inscritosError ?: "Nao foi possivel carregar inscritos.", color = CheerMutedText)
                    else -> {
                        if (inscritos.isEmpty()) {
                            Text("Nenhum inscrito neste evento.", color = CheerMutedText)
                        } else {
                            inscritos.forEach { inscrito ->
                                InscritoRow(
                                    inscrito = inscrito,
                                    actions = {
                                        StatusButton("Aprovar") {
                                            myViewModel.updateStatusInscrito(evento.id, inscrito.idVoluntario, "aprovado") { _, _ ->
                                                loadInscritos(evento)
                                                onRefresh()
                                            }
                                        }
                                        StatusButton("Pendente") {
                                            myViewModel.updateStatusInscrito(evento.id, inscrito.idVoluntario, "pendente") { _, _ ->
                                                loadInscritos(evento)
                                                onRefresh()
                                            }
                                        }
                                        StatusButton("Rejeitar") {
                                            myViewModel.updateStatusInscrito(evento.id, inscrito.idVoluntario, "rejeitado") { _, _ ->
                                                loadInscritos(evento)
                                                onRefresh()
                                            }
                                        }
                                    },
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun KpiCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CheerSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, color = CheerMutedText, style = MaterialTheme.typography.labelMedium)
            Text(value, color = CheerText, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun SeriesPanel(title: String, rows: List<com.cheermobile.models.DashboardSeriesPoint>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CheerSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, color = CheerText, fontWeight = FontWeight.Bold)
            if (rows.isEmpty()) {
                Text("Sem dados para exibir.", color = CheerMutedText)
            } else {
                rows.forEach { row ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(row.label ?: "Nao informado", color = CheerMutedText, modifier = Modifier.weight(1f))
                        Text("${row.value}", color = CheerText, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardEventCard(evento: Evento, onInscritos: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CheerSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(evento.titulo ?: "Evento sem titulo", color = CheerText, fontWeight = FontWeight.Bold)
                    Text("${formatCompactDate(evento.dataInicio)} • ${evento.tipoEvento ?: "Geral"}", color = CheerMutedText, style = MaterialTheme.typography.bodySmall)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = RoundedCornerShape(50), color = CheerPrimarySoft) {
                        Text(
                            "${evento.inscritos ?: 0}",
                            color = CheerText,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        )
                    }
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Acoes do evento", tint = CheerText)
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text("Ver inscritos") },
                            leadingIcon = { Icon(Icons.Default.Groups, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onInscritos()
                            },
                        )
                        DropdownMenuItem(
                            text = { Text("Editar evento") },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onEdit()
                            },
                        )
                        DropdownMenuItem(
                            text = { Text("Excluir evento", color = Color(0xFFC62828)) },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFC62828)) },
                            onClick = {
                                menuExpanded = false
                                onDelete()
                            },
                        )
                    }
                }
            }
            HorizontalDivider(color = CheerBrandBorder)
            Text(
                text = listOf(evento.cidade, evento.uf).filter { !it.isNullOrBlank() }.joinToString(" - ").ifBlank { "Local nao informado" },
                color = CheerMutedText,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun InscritoRow(inscrito: InscritoEvento, actions: (@Composable RowScope.() -> Unit)? = null) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CheerSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CheerBrandBorder),
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(inscrito.nome ?: "Voluntario", color = CheerText, fontWeight = FontWeight.Bold)
                    Text(inscrito.email ?: "Email nao informado", color = CheerMutedText, style = MaterialTheme.typography.bodySmall)
                }
                StatusChip(inscrito.status ?: "pendente")
            }
            Text(inscrito.evento ?: formatCompactDate(inscrito.dataInscricao), color = CheerMutedText, style = MaterialTheme.typography.bodySmall)
            if (actions != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    actions()
                }
            }
        }
    }
}

@Composable
private fun StatusChip(status: String) {
    Surface(shape = RoundedCornerShape(50), color = CheerStatusContainerColor(status)) {
        Text(
            status,
            color = CheerStatusContentColor(status),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
        )
    }
}

@Composable
private fun RowScope.StatusButton(label: String, onClick: () -> Unit) {
    OutlinedButton(onClick = onClick, modifier = Modifier.weight(1f)) {
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(title, color = CheerText, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
}

@Composable
private fun FeedbackPanel(message: String) {
    Surface(shape = RoundedCornerShape(12.dp), color = CheerPrimarySoft, modifier = Modifier.fillMaxWidth()) {
        Text(message, color = CheerText, modifier = Modifier.padding(12.dp), fontWeight = FontWeight.SemiBold)
    }
}
