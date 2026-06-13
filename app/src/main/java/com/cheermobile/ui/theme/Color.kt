package com.cheermobile.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val CheerPrimary = Color(0xFF0840CD)
val CheerPrimaryHover = Color(0xFF0634A8)
val CheerAccent = Color(0xFFFD6F08)
val CheerAccentHover = Color(0xFFD95A00)
val CheerPrimarySoft: Color
    @Composable get() = MaterialTheme.colorScheme.primaryContainer
val CheerAccentSoft: Color
    @Composable get() = MaterialTheme.colorScheme.tertiaryContainer
val CheerBackground: Color
    @Composable get() = MaterialTheme.colorScheme.background
val CheerSurface: Color
    @Composable get() = MaterialTheme.colorScheme.surface
val CheerText: Color
    @Composable get() = MaterialTheme.colorScheme.onSurface
val CheerMutedText: Color
    @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant

val CheerBrandBorder: Color
    @Composable get() = MaterialTheme.colorScheme.outlineVariant
val CheerSecondary = Color(0xFF1E293B)

@Composable
fun CheerStatusContainerColor(status: String?): Color {
    val darkTheme = isSystemInDarkTheme()
    return when (status?.lowercase()) {
        "aprovado" -> if (darkTheme) Color(0xFF123927) else Color(0xFFDCEFE3)
        "pendente" -> if (darkTheme) Color(0xFF493215) else Color(0xFFFFF0C7)
        "rejeitado" -> if (darkTheme) Color(0xFF4A1D1D) else Color(0xFFFDE2E2)
        else -> CheerAccent
    }
}

@Composable
fun CheerStatusContentColor(status: String?): Color {
    val darkTheme = isSystemInDarkTheme()
    return when (status?.lowercase()) {
        "aprovado" -> if (darkTheme) Color(0xFFA7F3D0) else Color(0xFF1B5E20)
        "pendente" -> if (darkTheme) Color(0xFFFFDCA8) else Color(0xFF6D4C00)
        "rejeitado" -> if (darkTheme) Color(0xFFFECACA) else Color(0xFF8A1C1C)
        else -> Color.White
    }
}

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
