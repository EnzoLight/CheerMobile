package com.cheermobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cheermobile.ui.theme.*
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun HomeScreen(onStartClick: () -> Unit) {
    val principles = listOf(
        PrincipleData(Icons.Default.Flag, "Missão", "Conectar pessoas e organizações sociais, ampliando o alcance de projetos."),
        PrincipleData(Icons.Default.AutoAwesome, "Visão", "Criar um ecossistema digital seguro para ações voluntárias e impacto."),
        PrincipleData(Icons.Default.Handshake, "Valores", "Colaboração, confiança, transparência e melhoria contínua.")
    )

    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {
            // --- HERO SECTION ---
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(CheerPrimary, CheerPrimarySoft)
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            modifier = Modifier.size(80.dp),
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.padding(16.dp).size(48.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Cheer",
                            fontSize = 56.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )

                        Text(
                            text = "VOLUNTARIADO",
                            fontSize = 14.sp,
                            letterSpacing = 4.sp,
                            fontWeight = FontWeight.Light,
                            color = Color.White.copy(alpha = 0.8f)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Conecte sua vontade de ajudar a ações reais.",
                            fontSize = 20.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }
                }
            }

            // --- CTA BUTTON ---
            item {
                Box(modifier = Modifier.padding(horizontal = 24.dp).offset(y = (-28).dp)) {
                    Button(
                        onClick = onStartClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CheerAccent),
                        elevation = ButtonDefaults.buttonElevation(8.dp)
                    ) {
                        Text("VAMOS COMEÇAR", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // --- SOBRE NÓS SECTION ---
            item {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "O que é o Cheer?",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = CheerPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Somos uma plataforma dedicada a facilitar o encontro entre quem quer ajudar e quem precisa de apoio.",
                        color = CheerText,
                        lineHeight = 22.sp
                    )
                }
            }

            // --- PRINCIPLES (MISSION/VISION/VALUES) ---
            items(principles) { principle ->
                PrincipleCard(principle)
            }

            // --- FOOTER INFO ---
            item {
                Spacer(modifier = Modifier.height(32.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CheerPrimarySoft.copy(alpha = 0.3f))
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Feito com ❤️ pela comunidade Cheer",
                        style = MaterialTheme.typography.bodySmall,
                        color = CheerMutedText
                    )
                }
            }
        }
    }
}

data class PrincipleData(val icon: ImageVector, val title: String, val description: String)

@Composable
fun PrincipleCard(data: PrincipleData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = CheerPrimary.copy(alpha = 0.1f)
            ) {
                Icon(
                    imageVector = data.icon,
                    contentDescription = null,
                    tint = CheerPrimary,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = data.title, fontWeight = FontWeight.Bold, color = CheerText, fontSize = 18.sp)
                Text(text = data.description, fontSize = 14.sp, color = CheerMutedText)
            }
        }
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    // Substitua 'CheerMobileTheme' pelo nome exato do seu tema
    // (geralmente definido em ui.theme.Theme.kt)
    MaterialTheme {
        HomeScreen(onStartClick = {})
    }
}
