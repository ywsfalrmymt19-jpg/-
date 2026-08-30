package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.model.ReaderTheme

data class BookThemeColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val border: Color,
    val text: Color,
    val subtext: Color,
    val primary: Color,
    val accent: Color,
    val isDark: Boolean
)

fun getThemeColors(theme: ReaderTheme): BookThemeColors {
    return when (theme) {
        ReaderTheme.PARCHMENT -> BookThemeColors(
            background = ParchmentBg,
            surface = ParchmentSurface,
            surfaceVariant = Color(0xFFEBE0CE),
            border = ParchmentBorder,
            text = ParchmentText,
            subtext = ParchmentSubtext,
            primary = IslamicNavy,
            accent = IslamicGold,
            isDark = false
        )
        ReaderTheme.LIGHT -> BookThemeColors(
            background = LightBg,
            surface = LightSurface,
            surfaceVariant = Color(0xFFEAEFF5),
            border = LightBorder,
            text = LightText,
            subtext = LightSubtext,
            primary = IslamicNavyLight,
            accent = FloralCyan,
            isDark = false
        )
        ReaderTheme.SEPIA -> BookThemeColors(
            background = SepiaBg,
            surface = SepiaSurface,
            surfaceVariant = Color(0xFFDFCFAF),
            border = SepiaBorder,
            text = SepiaText,
            subtext = SepiaSubtext,
            primary = Color(0xFF6B4219),
            accent = Color(0xFF9E6B38),
            isDark = false
        )
        ReaderTheme.DARK -> BookThemeColors(
            background = DarkBg,
            surface = DarkSurface,
            surfaceVariant = Color(0xFF2D333B),
            border = DarkBorder,
            text = DarkText,
            subtext = DarkSubtext,
            primary = Color(0xFF58A6FF),
            accent = IslamicGoldLight,
            isDark = true
        )
        ReaderTheme.OLED -> BookThemeColors(
            background = OledBg,
            surface = OledSurface,
            surfaceVariant = Color(0xFF1E1E1E),
            border = OledBorder,
            text = OledText,
            subtext = OledSubtext,
            primary = IslamicGoldLight,
            accent = Color(0xFF80D8FF),
            isDark = true
        )
    }
}

@Composable
fun MyApplicationTheme(
    readerTheme: ReaderTheme = ReaderTheme.PARCHMENT,
    content: @Composable () -> Unit
) {
    val colors = getThemeColors(readerTheme)
    val colorScheme = if (colors.isDark) {
        darkColorScheme(
            primary = colors.primary,
            secondary = colors.accent,
            background = colors.background,
            surface = colors.surface,
            onBackground = colors.text,
            onSurface = colors.text
        )
    } else {
        lightColorScheme(
            primary = colors.primary,
            secondary = colors.accent,
            background = colors.background,
            surface = colors.surface,
            onBackground = colors.text,
            onSurface = colors.text
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
