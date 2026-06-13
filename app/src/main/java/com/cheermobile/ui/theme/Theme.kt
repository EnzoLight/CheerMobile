package com.cheermobile.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF9DB8FF),
    onPrimary = Color(0xFF08245F),
    secondary = CheerAccent,
    onSecondary = Color(0xFF2E1600),
    tertiary = Color(0xFFFFB77C),
    onTertiary = Color(0xFF321200),
    tertiaryContainer = Color(0xFF4B2405),
    onTertiaryContainer = Color(0xFFFFD9BE),
    primaryContainer = Color(0xFF102A5F),
    onPrimaryContainer = Color(0xFFDCE6FF),
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFE5E7EB),
    surface = Color(0xFF172033),
    onSurface = Color(0xFFE5E7EB),
    surfaceVariant = Color(0xFF263247),
    onSurfaceVariant = Color(0xFFB9C3D4),
    outline = Color(0xFF64748B),
    outlineVariant = Color(0xFF334155),
)

private val LightColorScheme = lightColorScheme(
    primary = CheerPrimary,
    onPrimary = Color.White,
    secondary = CheerAccent,
    onSecondary = Color.White,
    tertiary = CheerAccent,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFF1E7),
    onTertiaryContainer = Color(0xFF5D2600),
    primaryContainer = Color(0xFFE9F0FF),
    onPrimaryContainer = Color(0xFF08245F),
    background = Color(0xFFF7FAFF),
    onBackground = Color(0xFF333333),
    surface = Color.White,
    onSurface = Color(0xFF333333),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF68777D),
    outline = Color(0xFFCBD5E1),
    outlineVariant = Color(0xFFE2E8F0),
)

@Composable
fun CheerMobileTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
