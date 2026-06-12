package com.cheermobile.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.cheermobile.ui.theme.CheerPrimary

@Composable
fun CheerLogo(
    modifier: Modifier = Modifier,
    tint: Color = CheerPrimary
) {
    // Nota: Como não temos o SVG exato aqui, usaremos um placeholder ou o ícone do app
    // O usuário deve adicionar o cheer.svg aos drawables
    Icon(
        painter = painterResource(id = android.R.drawable.ic_menu_share), // Placeholder
        contentDescription = "Cheer Logo",
        modifier = modifier.size(40.dp),
        tint = tint
    )
}
