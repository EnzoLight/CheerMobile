package com.cheermobile.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cheermobile.models.LogEvento
import com.cheermobile.ui.theme.CheerAccentSoft
import com.cheermobile.ui.theme.CheerBackground
import com.cheermobile.ui.theme.CheerBrandBorder
import com.cheermobile.ui.theme.CheerMutedText
import com.cheermobile.ui.theme.CheerPrimary
import com.cheermobile.ui.theme.CheerSurface
import com.cheermobile.ui.theme.CheerText

data class LogsFilters(
    val nivel: String = "",
    val tipoEvento: String = "",
    val origem: String = "",
) {
    fun toQueryMap(): Map<String, String> = mapOf(
        "nivel" to nivel,
        "tipo_evento" to tipoEvento,
        "origem" to origem,
        "per_page" to "100",
    )
}

@Composable
fun LogsOperacionaisScreen(
    logs: List<LogEvento>,
    total: Int,
    isLoading: Boolean,
    errorMessage: String?,
    filters: LogsFilters,
    onFiltersChange: (LogsFilters) -> Unit,
    onRefresh: () -> Unit,
) {
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
                    kicker = "OPERACOES",
                    title = "Logs",
                    description = "Registros operacionais vinculados a sua instituicao.",
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = onRefresh) {
                    Icon(Icons.Default.Refresh, contentDescription = "Atualizar", tint = CheerPrimary)
                }
            }

            LogsFilterPanel(
                filters = filters,
                onFiltersChange = onFiltersChange,
                onRefresh = onRefresh,
                onClear = { onFiltersChange(LogsFilters()) },
            )

            Spacer(Modifier.height(12.dp))

            when {
                isLoading -> StatePanel("Carregando logs...") {
                    CircularProgressIndicator(color = CheerPrimary)
                }

                errorMessage != null -> StatePanel(errorMessage) {
                    OutlinedButton(onClick = onRefresh) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(Modifier.padding(4.dp))
                        Text("Tentar novamente")
                    }
                }

                logs.isEmpty() -> StatePanel("Nenhum log encontrado.")

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    item {
                        Text("$total registros encontrados", color = CheerMutedText, style = MaterialTheme.typography.bodySmall)
                    }
                    items(logs, key = { it.id }) { log ->
                        LogCard(log)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LogsFilterPanel(
    filters: LogsFilters,
    onFiltersChange: (LogsFilters) -> Unit,
    onRefresh: () -> Unit,
    onClear: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val nivelLabel = filters.nivel.ifBlank { "Todos" }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CheerSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.FilterAlt, contentDescription = null, tint = CheerPrimary)
                Text("Filtros", color = CheerText, fontWeight = FontWeight.Bold)
            }

            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                OutlinedTextField(
                    value = nivelLabel,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Nivel") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = fieldColors(),
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    listOf("" to "Todos", "info" to "Info", "warning" to "Warning", "error" to "Error").forEach { (value, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                onFiltersChange(filters.copy(nivel = value))
                                expanded = false
                            },
                        )
                    }
                }
            }

            OutlinedTextField(
                value = filters.tipoEvento,
                onValueChange = { onFiltersChange(filters.copy(tipoEvento = it)) },
                label = { Text("Tipo") },
                placeholder = { Text("CRIACAO_EVENTO") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = fieldColors(),
            )
            OutlinedTextField(
                value = filters.origem,
                onValueChange = { onFiltersChange(filters.copy(origem = it)) },
                label = { Text("Origem") },
                placeholder = { Text("api") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = fieldColors(),
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onRefresh, modifier = Modifier.weight(1f)) {
                    Text("Atualizar")
                }
                OutlinedButton(onClick = onClear, modifier = Modifier.weight(1f)) {
                    Text("Limpar")
                }
            }
        }
    }
}

@Composable
private fun LogCard(log: LogEvento) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CheerSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(log.tipoEvento ?: "Evento", color = CheerText, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Surface(
                    shape = RoundedCornerShape(50),
                    color = when (log.nivel) {
                        "error" -> Color(0xFFFFEBEE)
                        "warning" -> CheerAccentSoft
                        else -> Color(0xFFE8F5E9)
                    },
                ) {
                    Text(
                        log.nivel ?: "info",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        color = CheerText,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            Text(formatCompactDate(log.dataHora), color = CheerMutedText, style = MaterialTheme.typography.bodySmall)
            HorizontalDivider(color = CheerBrandBorder)
            Text(log.descricao ?: "Sem descricao.", color = CheerText, style = MaterialTheme.typography.bodyMedium)
            Text("Origem: ${log.origem ?: "nao informada"} • IP: ${log.ipOrigem ?: "nao informado"}", color = CheerMutedText, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = CheerPrimary,
    unfocusedBorderColor = CheerBrandBorder,
    focusedContainerColor = CheerSurface,
    unfocusedContainerColor = CheerSurface,
)
