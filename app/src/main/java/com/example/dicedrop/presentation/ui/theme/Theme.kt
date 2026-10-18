package com.example.dicedrop.presentation.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// У игры одна фирменная палитра, поэтому системная тёмная тема и dynamic color не используются
private val ColorScheme = lightColorScheme(
    primary = Yellow,
    onPrimary = ButtonText,
    secondary = MidBlue,
    onSecondary = White,
    background = DarkBlue,
    onBackground = White,
    surface = DarkBlue,
    onSurface = White
)

@Composable
fun DiceDropTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ColorScheme,
        typography = Typography,
        content = content
    )
}
