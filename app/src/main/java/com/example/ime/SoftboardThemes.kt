package com.example.ime

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

data class SoftboardThemeColors(
    val id: String,
    val name: String,
    val background: Color,
    val surface: Color,
    val keyBackground: Color,
    val keyPressedBackground: Color,
    val specialKeyBackground: Color,
    val accentColor: Color,
    val primaryText: Color,
    val secondaryText: Color,
    val returnKeyBackground: Color,
    val returnKeyText: Color,
    val keyBorder: Color,
    val glowBrush: Brush,
    val isDark: Boolean
)

object SoftboardThemes {
    val NeonPurple = SoftboardThemeColors(
        id = "neon_purple",
        name = "Neon Purple",
        background = Color(0xFF090614),
        surface = Color(0xFF140D28),
        keyBackground = Color(0xFF1C1338),
        keyPressedBackground = Color(0xFF332066),
        specialKeyBackground = Color(0xFF271A4E),
        accentColor = Color(0xFFC084FC),
        primaryText = Color(0xFFF8FAFC),
        secondaryText = Color(0xFFA78BFA),
        returnKeyBackground = Color(0xFF9333EA),
        returnKeyText = Color(0xFFFFFFFF),
        keyBorder = Color(0xFF3B2875),
        glowBrush = Brush.linearGradient(
            listOf(Color(0xFF9333EA), Color(0xFF38BDF8))
        ),
        isDark = true
    )

    val CyberBlue = SoftboardThemeColors(
        id = "cyber_blue",
        name = "Cyber Blue",
        background = Color(0xFF050C18),
        surface = Color(0xFF0C1930),
        keyBackground = Color(0xFF122544),
        keyPressedBackground = Color(0xFF1B3D70),
        specialKeyBackground = Color(0xFF193258),
        accentColor = Color(0xFF38BDF8),
        primaryText = Color(0xFFF0F9FF),
        secondaryText = Color(0xFF7DD3FC),
        returnKeyBackground = Color(0xFF0284C7),
        returnKeyText = Color(0xFFFFFFFF),
        keyBorder = Color(0xFF1E3A6B),
        glowBrush = Brush.linearGradient(
            listOf(Color(0xFF06B6D4), Color(0xFF3B82F6))
        ),
        isDark = true
    )

    val MidnightBlack = SoftboardThemeColors(
        id = "midnight_black",
        name = "Midnight Black",
        background = Color(0xFF000000),
        surface = Color(0xFF0F0F12),
        keyBackground = Color(0xFF16161A),
        keyPressedBackground = Color(0xFF2E2E36),
        specialKeyBackground = Color(0xFF222228),
        accentColor = Color(0xFFE2E8F0),
        primaryText = Color(0xFFF4F4F5),
        secondaryText = Color(0xFFA1A1AA),
        returnKeyBackground = Color(0xFF3F3F46),
        returnKeyText = Color(0xFFFFFFFF),
        keyBorder = Color(0xFF27272A),
        glowBrush = Brush.linearGradient(
            listOf(Color(0xFF52525B), Color(0xFF71717A))
        ),
        isDark = true
    )

    val CleanLight = SoftboardThemeColors(
        id = "clean_light",
        name = "Clean Light",
        background = Color(0xFFF1F5F9),
        surface = Color(0xFFE2E8F0),
        keyBackground = Color(0xFFFFFFFF),
        keyPressedBackground = Color(0xFFCBD5E1),
        specialKeyBackground = Color(0xFFE2E8F0),
        accentColor = Color(0xFF7C3AED),
        primaryText = Color(0xFF0F172A),
        secondaryText = Color(0xFF475569),
        returnKeyBackground = Color(0xFF7C3AED),
        returnKeyText = Color(0xFFFFFFFF),
        keyBorder = Color(0xFFE2E8F0),
        glowBrush = Brush.linearGradient(
            listOf(Color(0xFF7C3AED), Color(0xFF0284C7))
        ),
        isDark = false
    )

    fun getTheme(type: SoftboardThemeType): SoftboardThemeColors {
        return when (type) {
            SoftboardThemeType.NEON_PURPLE -> NeonPurple
            SoftboardThemeType.CYBER_BLUE -> CyberBlue
            SoftboardThemeType.MIDNIGHT_BLACK -> MidnightBlack
            SoftboardThemeType.CLEAN_LIGHT -> CleanLight
        }
    }
}
