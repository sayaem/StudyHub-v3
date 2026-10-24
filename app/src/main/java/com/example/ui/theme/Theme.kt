package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val StudyHubLightColorScheme = lightColorScheme(
    primary = BrandTeal,
    onPrimary = Color.White,
    primaryContainer = BrandPaleTeal,
    onPrimaryContainer = BrandDeepTeal,
    secondary = BrandBlue,
    onSecondary = Color.White,
    secondaryContainer = BrandLightBlue,
    onSecondaryContainer = BrandBlue,
    tertiary = BrandDeepTeal,
    onTertiary = Color.White,
    background = AppBackgroundLight,
    onBackground = TextPrimary,
    surface = CardSurfaceLight,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtleLight,
    outlineVariant = Color(0xFFE2E8F0),
    error = StatusDanger,
    onError = Color.White
)

val StudyHubDarkColorScheme = darkColorScheme(
    primary = BrandTeal,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF0F3A40),
    onPrimaryContainer = Color(0xFFA5F3FC),
    secondary = BrandBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF1E3A8A),
    onSecondaryContainer = Color(0xFFBFDBFE),
    tertiary = BrandDeepTeal,
    onTertiary = Color.White,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder,
    outlineVariant = Color(0xFF475569),
    error = StatusDanger,
    onError = Color.White
)

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

@Composable
fun StudyHubTheme(
    themeMode: ThemeMode = ThemeMode.LIGHT,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = if (darkTheme) StudyHubDarkColorScheme else StudyHubLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
