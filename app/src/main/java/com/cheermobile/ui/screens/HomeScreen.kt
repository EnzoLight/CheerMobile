package com.cheermobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cheermobile.ui.theme.*

@Composable
fun HomeScreen(
    onNavigateToEvents: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(CheerPrimarySoft, Color.White)
                )
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = "CHEER VOLUNTARIADO",
                color = CheerPrimary,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Conecte sua vontade de ajudar a ações reais.",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 40.sp
                ),
                textAlign = TextAlign.Center,
                color = CheerText
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Descubra eventos solidários, conecte-se a instituições e acompanhe sua jornada de impacto em um só lugar.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = CheerMutedText
            )
            Spacer(modifier = Modifier.height(32.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onNavigateToRegister,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = CheerPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Criar conta")
                }
                OutlinedButton(
                    onClick = onNavigateToEvents,
                    modifier = Modifier.weight(1f),
                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Buscar eventos", color = CheerPrimary)
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            HighlightItem(
                icon = Icons.Default.CalendarMonth,
                title = "Ações próximas",
                description = "Encontre iniciativas na sua região."
            )
            HighlightItem(
                icon = Icons.Default.Shield,
                title = "Confiança",
                description = "Perfis e participações com histórico."
            )
            HighlightItem(
                icon = Icons.Default.Favorite,
                title = "Impacto conjunto",
                description = "Voluntários, instituições e parceiros."
            )
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun HighlightItem(icon: ImageVector, title: String, description: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = CheerPrimary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, color = CheerText)
                Text(text = description, style = MaterialTheme.typography.bodySmall, color = CheerMutedText)
            }
        }
    }
}
