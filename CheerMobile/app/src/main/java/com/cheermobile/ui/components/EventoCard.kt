package com.cheermobile.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Surface(
                color = CheerPrimarySoft,
                shape = RoundedCornerShape(50),
            ) {
                Text(
                    text = evento.tipoEvento,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = CheerPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = evento.titulo,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = CheerText
                )
            )

            evento.descricao?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = CheerMutedText,
                    maxLines = 3
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = CheerBrandBorder)
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Início", style = MaterialTheme.typography.labelSmall, color = CheerMutedText)
                    Text(evento.dataInicio, style = MaterialTheme.typography.bodySmall, color = CheerText)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Fim", style = MaterialTheme.typography.labelSmall, color = CheerMutedText)
                    Text(evento.dataTermino ?: "Não informado", style = MaterialTheme.typography.bodySmall, color = CheerText)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column {
                Text("Local", style = MaterialTheme.typography.labelSmall, color = CheerMutedText)
                val localStr = evento.endereco?.let { "${it.rua}, ${it.bairro} - ${it.cidade}/${it.uf}" } ?: "Local não informado"
                Text(localStr, style = MaterialTheme.typography.bodySmall, color = CheerText)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Text("Participação", style = MaterialTheme.typography.labelSmall, color = CheerMutedText)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Máx: ${evento.maxVoluntarios}", style = MaterialTheme.typography.bodySmall, color = CheerText)
            }
        }
    }
}
