package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = SoftRose,
    onPrimary = Color.White,
    primaryContainer = SoftPinkSurfaceVariant,
    onPrimaryContainer = DeepPlum,
    secondary = DustyPink,
    onSecondary = DeepPlum,
    secondaryContainer = SoftPinkBorder,
    onSecondaryContainer = DeepPlum,
    tertiary = LavenderAccent,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFF3EDF9),
    onTertiaryContainer = Color(0xFF493665),
    background = SoftPinkBackground,
    onBackground = TextPrimary,
    surface = SoftPinkCard,
    onSurface = TextPrimary,
    surfaceVariant = SoftPinkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = SoftPinkBorder,
    outlineVariant = Color(0xFFF9E8EC),
    error = Color(0xFFD32F2F),
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkBackground,
    primaryContainer = DarkSurfaceVariant,
    onPrimaryContainer = DarkTextPrimary,
    secondary = DarkSecondary,
    onSecondary = DarkBackground,
    secondaryContainer = DarkCard,
    onSecondaryContainer = DarkTextPrimary,
    tertiary = DarkAccent,
    onTertiary = DarkBackground,
    tertiaryContainer = Color(0xFF45364D),
    onTertiaryContainer = DarkTextPrimary,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkCard,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder,
    outlineVariant = Color(0xFF382C35),
    error = Color(0xFFEF9A9A),
    onError = Color.Black
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
