package com.cheermobile.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cheermobile.R
import com.cheermobile.ui.theme.*

@Composable
fun LoginScreen(
    onLoginExternalClick: () -> Unit, // Agora foca no login externo
    onNavigateToInstitutionRegister: () -> Unit,
    onNavigateToVolunteerRegister: () -> Unit,
    errorMessage: String? = null,
    onClearError: () -> Unit = {},
) {
    val clipboardManager = LocalClipboardManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFCFF)),
        contentAlignment = Alignment.Center
    ) {
        LoginBackgroundWaves()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(id = R.drawable.cheer_symbol),
                contentDescription = "Logo Cheer",
                modifier = Modifier.size(width = 148.dp, height = 142.dp),
                contentScale = ContentScale.Fit,
            )

            Text(
                text = "Cheer",
                fontSize = 54.sp,
                fontWeight = FontWeight.ExtraBold,
                color = CheerPrimary,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Voluntariado sem complicação",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = CheerText,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Entre para encontrar ações sociais, acompanhar suas inscrições ou gerenciar eventos da sua instituição.",
                style = MaterialTheme.typography.bodyLarge,
                color = CheerMutedText,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                modifier = Modifier.padding(horizontal = 12.dp),
            )

            Spacer(modifier = Modifier.height(42.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Acesse sua conta",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = CheerText,
                    )
                    Text(
                        text = "Login seguro via Authentik.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CheerMutedText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, bottom = 18.dp),
                    )

                    Button(
                        onClick = onLoginExternalClick,
                        modifier = Modifier.fillMaxWidth().height(62.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CheerPrimary, contentColor = Color.White),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Login, contentDescription = null)
                        Spacer(Modifier.width(12.dp))
                        Text("Entrar", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }

                    errorMessage?.let { message ->
                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFFEBEE),
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Erro no login",
                                    color = Color(0xFFC62828),
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall,
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                SelectionContainer {
                                    Text(
                                        text = message,
                                        color = Color(0xFF5F2120),
                                        style = MaterialTheme.typography.bodySmall,
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                ) {
                                    TextButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(message))
                                        },
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = null)
                                        Spacer(Modifier.width(6.dp))
                                        Text("Copiar")
                                    }
                                    TextButton(onClick = onClearError) {
                                        Text("Fechar")
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = CheerBrandBorder)
                        Text("ou", color = CheerMutedText, fontWeight = FontWeight.SemiBold)
                        HorizontalDivider(modifier = Modifier.weight(1f), color = CheerBrandBorder)
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        OutlinedButton(
                            onClick = onNavigateToVolunteerRegister,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CheerPrimary),
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Voluntário", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onNavigateToInstitutionRegister,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CheerAccent),
                        ) {
                            Icon(Icons.Default.AccountBalance, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Instituição", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoginBackgroundWaves() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        val leftWave = Path().apply {
            moveTo(0f, 0f)
            lineTo(w * 0.33f, 0f)
            cubicTo(w * 0.26f, h * 0.12f, w * 0.16f, h * 0.16f, w * 0.05f, h * 0.18f)
            cubicTo(-w * 0.08f, h * 0.21f, w * 0.06f, h * 0.33f, 0f, h * 0.42f)
            close()
        }
        drawPath(
            path = leftWave,
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFDCE8FF), Color(0x66DCE8FF), Color.Transparent),
            ),
        )

        val rightWave = Path().apply {
            moveTo(w, h * 0.45f)
            cubicTo(w * 0.84f, h * 0.54f, w * 0.89f, h * 0.67f, w * 0.74f, h * 0.76f)
            cubicTo(w * 0.88f, h * 0.88f, w * 0.92f, h, w, h)
            close()
        }
        drawPath(
            path = rightWave,
            brush = Brush.linearGradient(
                colors = listOf(Color.Transparent, Color(0x55FFE1C5), Color(0x88FFE1C5)),
            ),
        )

        val blueLine = Path().apply {
            moveTo(w * 0.28f, 0f)
            cubicTo(w * 0.39f, h * 0.05f, w * 0.35f, h * 0.15f, w * 0.18f, h * 0.15f)
        }
        drawPath(path = blueLine, color = CheerPrimary.copy(alpha = 0.55f), style = Stroke(width = 2.2f))

        val orangeLine = Path().apply {
            moveTo(w, h * 0.38f)
            cubicTo(w * 0.84f, h * 0.47f, w * 0.86f, h * 0.58f, w, h * 0.66f)
        }
        drawPath(path = orangeLine, color = CheerAccent.copy(alpha = 0.55f), style = Stroke(width = 2.2f))
    }
}
