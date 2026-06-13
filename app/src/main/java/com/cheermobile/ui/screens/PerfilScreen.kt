package com.cheermobile.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cheermobile.models.UserProfileData
import com.cheermobile.ui.theme.CheerAccent
import com.cheermobile.ui.theme.CheerBackground
import com.cheermobile.ui.theme.CheerBrandBorder
import com.cheermobile.ui.theme.CheerMutedText
import com.cheermobile.ui.theme.CheerPrimary
import com.cheermobile.ui.theme.CheerSurface
import com.cheermobile.ui.theme.CheerText

@Composable
fun PerfilScreen(
    profile: UserProfileData?,
    isLoading: Boolean,
    errorMessage: String?,
    onRefresh: () -> Unit,
    onPrimaryAction: () -> Unit,
    onLogout: () -> Unit,
) {
    Surface(color = CheerBackground, contentColor = CheerText, modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            ScreenHeader(
                kicker = "SEU PERFIL",
                title = if (profile?.tipo == "instituicao") "Perfil da instituicao" else "Perfil do voluntario",
                description = if (profile?.tipo == "instituicao") {
                    "Gerencie oportunidades publicadas pela sua organizacao."
                } else {
                    "Acompanhe oportunidades e atividades vinculadas a sua conta."
                },
            )

            if (isLoading) {
                StatePanel(text = "Carregando perfil...") {
                    CircularProgressIndicator(color = CheerPrimary)
                }
                return@Column
            }

            if (errorMessage != null) {
                StatePanel(text = errorMessage) {
                    OutlinedButton(onClick = onRefresh) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(Modifier.size(8.dp))
                        Text("Tentar novamente")
                    }
                }
                return@Column
            }

            val user = profile ?: return@Column
            val location = listOf(user.cidade, user.uf).filter { !it.isNullOrBlank() }.joinToString(" - ")

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CheerSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Surface(
                        modifier = Modifier.size(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = CheerAccent,
                    ) {
                        Icon(
                            imageVector = if (user.tipo == "instituicao") Icons.Default.Apartment else Icons.Default.VolunteerActivism,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.padding(12.dp),
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (user.tipo == "instituicao") "Instituicao" else "Voluntario",
                            style = MaterialTheme.typography.labelMedium,
                            color = CheerMutedText,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = user.nome,
                            style = MaterialTheme.typography.titleLarge,
                            color = CheerText,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CheerSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Text("Dados cadastrados", color = CheerText, fontWeight = FontWeight.Bold)
                    ProfileInfo(Icons.Default.Email, "Email", user.email)
                    ProfileInfo(Icons.Default.Phone, "Telefone", user.telefone)
                    if (user.tipo == "instituicao") {
                        ProfileInfo(Icons.Default.Apartment, "Categoria", user.categoria)
                    }
                    ProfileInfo(Icons.Default.LocationOn, "Localizacao", location.ifBlank { null })
                }
            }

            Button(
                onClick = onPrimaryAction,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(if (user.tipo == "instituicao") "Criar evento" else "Ver minhas atividades", fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
            ) {
                Icon(Icons.Default.Logout, contentDescription = null, tint = Color(0xFFC62828))
                Spacer(Modifier.size(8.dp))
                Text("Sair", color = Color(0xFFC62828), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ProfileInfo(icon: ImageVector, label: String, value: String?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(icon, contentDescription = null, tint = CheerPrimary, modifier = Modifier.size(20.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = CheerMutedText)
            Text(value?.takeIf { it.isNotBlank() } ?: "Nao informado", color = CheerText, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun ScreenHeader(
    kicker: String,
    title: String,
    description: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
) {
    Column(modifier = modifier) {
        Text(kicker, style = MaterialTheme.typography.labelMedium, color = CheerPrimary, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text(title, style = MaterialTheme.typography.headlineSmall, color = CheerText, fontWeight = FontWeight.ExtraBold)
        Text(description, style = MaterialTheme.typography.bodyMedium, color = CheerMutedText)
    }
}

@Composable
fun StatePanel(text: String, action: @Composable (() -> Unit)? = null) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CheerSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CheerBrandBorder),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(text, color = CheerMutedText)
            action?.invoke()
        }
    }
}
