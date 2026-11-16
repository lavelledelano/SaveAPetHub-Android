package com.example.saveapethub.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Orange = Color(0xFFF26B21)
private val Teal = Color(0xFF0E9F8E)
private val Cream = Color(0xFFFFF8F3)

private val SaphColors = lightColorScheme(
    primary = Orange,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE0CC),
    onPrimaryContainer = Color(0xFF5A2300),
    secondary = Teal,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCCEFEA),
    onSecondaryContainer = Color(0xFF00403A),
    tertiary = Color(0xFFFBB117),
    background = Cream,
    onBackground = Color(0xFF1F2937),
    surface = Cream,
    onSurface = Color(0xFF1F2937),
    surfaceContainer = Color.White,
    surfaceContainerHighest = Color.White,
    outline = Teal
)

@Composable
fun SaveAPetHubTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SaphColors,
        typography = Typography,
        content = content
    )
}