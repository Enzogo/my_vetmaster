package com.proyect.myvet.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// Paleta oscura; si ya tienes colores propios (Purple80, etc.), puedes configurarlos aquí.
private val DarkColorScheme = darkColorScheme()

@Composable
fun MyVetTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}