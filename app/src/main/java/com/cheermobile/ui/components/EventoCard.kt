package com.cheermobile.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cheermobile.models.Evento
import com.cheermobile.ui.theme.*

@Composable
fun EventoCard(
    evento: Evento,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                EventTypeChip(text = evento.tipoEvento ?: "Geral")
                Text(
                    text = evento.constancia?.replaceFirstChar { it.uppercase() } ?: "Único",
                    style = MaterialTheme.typography.labelSmall,
                    color = CheerMutedText,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = evento.titulo ?: "Evento sem titulo",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = CheerText,
            )

            if (!evento.descricao.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = evento.descricao,
                    style = MaterialTheme.typography.bodyMedium,
                    color = CheerMutedText,
                    maxLines = 3
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = CheerBrandBorder)
            Spacer(modifier = Modifier.height(12.dp))

            EventDetail(icon = Icons.Default.Event, text = formatDateRange(evento))
            Spacer(modifier = Modifier.height(8.dp))
            EventDetail(icon = Icons.Default.LocationOn, text = formatLocation(evento))
            Spacer(modifier = Modifier.height(8.dp))
            EventDetail(icon = Icons.Default.People, text = formatCapacity(evento))
        }
    }
}

@Composable
private fun EventTypeChip(text: String) {
    Surface(
        color = CheerPrimarySoft,
        shape = RoundedCornerShape(50),
    ) {
        Text(
            text = text.replaceFirstChar { it.uppercase() },
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelSmall,
            color = CheerPrimary,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun EventDetail(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = CheerAccent,
            modifier = Modifier
                .padding(top = 1.dp)
                .size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = CheerText,
            modifier = Modifier.weight(1f)
        )
    }
}

private fun formatDateRange(evento: Evento): String {
    val start = evento.dataInicio?.let(::formatDateTime) ?: "Início não informado"
    val end = evento.dataTermino?.let(::formatDateTime)
    return if (end.isNullOrBlank()) start else "$start até $end"
}

private fun formatDateTime(value: String): String {
    val date = value.take(10)
    val time = value.substringAfter("T", "").take(5)
    return if (time.length == 5) "$date às $time" else date.ifBlank { "Não informado" }
}

private fun formatLocation(evento: Evento): String {
    val address = evento.endereco
    return when {
        address != null -> listOf(
            address.rua,
            address.bairro,
            listOf(address.cidade, address.uf).filter { !it.isNullOrBlank() }.joinToString("/")
        ).filter { it.isNotBlank() }.joinToString(" - ").ifBlank { "Local não informado" }
        !evento.cidade.isNullOrBlank() || !evento.uf.isNullOrBlank() ->
            listOf(evento.cidade, evento.uf).filter { !it.isNullOrBlank() }.joinToString("/")
        else -> "Local não informado"
    }
}

private fun formatCapacity(evento: Evento): String {
    val subscribed = evento.inscritos ?: 0
    val max = evento.maxVoluntarios
    return if (max != null) "$subscribed inscritos de $max vagas" else "$subscribed inscritos"
}
