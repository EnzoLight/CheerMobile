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
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = CheerPrimarySoft,
    onPrimary = CheerText,
    secondary = CheerAccent,
    onSecondary = CheerSurface,
    tertiary = CheerAccentSoft,
    background = CheerSecondary,
    onBackground = CheerSurface,
    surface = CheerSecondary,
    onSurface = CheerSurface,
)

private val LightColorScheme = lightColorScheme(
    primary = CheerPrimary,
    onPrimary = CheerSurface,
    secondary = CheerAccent,
    onSecondary = CheerSurface,
    tertiary = CheerAccentSoft,
    onTertiary = CheerText,
    background = CheerBackground,
    onBackground = CheerText,
    surface = CheerSurface,
    onSurface = CheerText,
    outline = CheerBrandBorder,
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
