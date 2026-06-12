package com.cheermobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cheermobile.ui.theme.*

@Composable
fun LoginScreen(
    onLoginExternalClick: () -> Unit, // Agora foca no login externo
    onNavigateToInstitutionRegister: () -> Unit,
    onNavigateToVolunteerRegister: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(CheerPrimarySoft, Color.White, CheerAccentSoft)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Bem-vindo ao Cheer",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = CheerPrimary
                )
                Text(
                    text = "Acesse sua conta com segurança via Authentik.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CheerMutedText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // BOTÃO DE LOGIN EXTERNO
                Button(
                    onClick = onLoginExternalClick,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CheerPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Login, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Entrar com Authentik", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Divider(color = CheerBrandBorder, thickness = 1.dp)

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = onNavigateToVolunteerRegister) {
                    Text("Quero ser um voluntário", color = CheerPrimary)
                }

                TextButton(onClick = onNavigateToInstitutionRegister) {
                    Text("Sou uma instituição", color = CheerAccent)
                }
            }
        }
    }
}
