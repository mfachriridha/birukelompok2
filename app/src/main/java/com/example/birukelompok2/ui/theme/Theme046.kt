package com.example.birukelompok2.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val BiruLightColorScheme = lightColorScheme(
    primary = BiruPrimary,
    onPrimary = BiruSurface,
    primaryContainer = BiruPrimaryLight,
    onPrimaryContainer = BiruPrimaryDark,
    secondary = BiruSecondary,
    onSecondary = BiruSurface,
    secondaryContainer = BiruSecondaryLight,
    background = BiruBackground,
    onBackground = BiruTextPrimary,
    surface = BiruSurface,
    onSurface = BiruTextPrimary,
    surfaceVariant = BiruSurfaceVariant,
    onSurfaceVariant = BiruTextSecondary,
    outline = BiruBorder,
    error = BiruError,
    onError = BiruSurface,
    errorContainer = BiruErrorLight,
)

@Composable
fun BiruTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = BiruLightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = BiruPrimary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = BiruTypography,
        shapes = BiruShapes,
        content = content
    )
}
