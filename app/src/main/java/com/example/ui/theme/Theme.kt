package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val SoftboardNeonPurple = Color(0xFFA855F7)
val SoftboardElectricBlue = Color(0xFF06B6D4)
val SoftboardDeepDark = Color(0xFF07050E)
val SoftboardSurface = Color(0xFF130E24)
val SoftboardSurfaceVariant = Color(0xFF1C1635)
val SoftboardBorder = Color(0xFF2D2350)
val SoftboardTextPrimary = Color(0xFFF8FAFC)
val SoftboardTextSecondary = Color(0xFFA78BFA)

private val SoftboardDarkColorScheme = darkColorScheme(
    primary = SoftboardNeonPurple,
    onPrimary = Color.White,
    primaryContainer = SoftboardSurfaceVariant,
    onPrimaryContainer = SoftboardTextPrimary,
    secondary = SoftboardElectricBlue,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF0C243B),
    onSecondaryContainer = Color(0xFF38BDF8),
    tertiary = Color(0xFFE879F9),
    background = SoftboardDeepDark,
    onBackground = SoftboardTextPrimary,
    surface = SoftboardSurface,
    onSurface = SoftboardTextPrimary,
    surfaceVariant = SoftboardSurfaceVariant,
    onSurfaceVariant = SoftboardTextSecondary,
    outline = SoftboardBorder
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SoftboardDarkColorScheme,
        typography = Typography,
        content = content
    )
}
