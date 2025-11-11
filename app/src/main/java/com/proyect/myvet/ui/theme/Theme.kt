package com.proyect.myvet.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// Esquema de colores claro (tema principal)
private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = White,
    primaryContainer = PrimaryGreenDark,
    onPrimaryContainer = White,

    secondary = SecondaryOrange,
    onSecondary = White,
    secondaryContainer = SecondaryOrangeDark,
    onSecondaryContainer = White,

    tertiary = TealVet,
    onTertiary = White,
    tertiaryContainer = TealVetDark,
    onTertiaryContainer = White,

    background = BackgroundBeige,
    onBackground = GrayDark,

    surface = White,
    onSurface = GrayDark,
    surfaceVariant = GrayLight,
    onSurfaceVariant = GrayDark,

    error = ErrorRed,
    onError = White,
    errorContainer = ErrorRed.copy(alpha = 0.1f),
    onErrorContainer = ErrorRed,

    outline = GrayMedium,
    outlineVariant = GrayLight,
)

// Esquema de colores oscuro (opcional)
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryGreenDark,
    onPrimary = White,
    primaryContainer = PrimaryGreen,
    onPrimaryContainer = Black,

    secondary = SecondaryOrangeDark,
    onSecondary = White,
    secondaryContainer = SecondaryOrange,
    onSecondaryContainer = Black,

    tertiary = TealVetDark,
    onTertiary = White,
    tertiaryContainer = TealVet,
    onTertiaryContainer = Black,

    background = GrayDark,
    onBackground = White,

    surface = GrayDark,
    onSurface = White,
    surfaceVariant = GrayMedium,
    onSurfaceVariant = White,

    error = ErrorRed,
    onError = White,
    errorContainer = ErrorRed.copy(alpha = 0.2f),
    onErrorContainer = ErrorRed,

    outline = GrayMedium,
    outlineVariant = GrayDark,
)

@Composable
fun MyVetTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}