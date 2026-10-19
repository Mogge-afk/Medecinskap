package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PineTealPrimaryDark,
    onPrimary = PineTealOnPrimaryDark,
    primaryContainer = PineTealPrimaryContainerDark,
    onPrimaryContainer = PineTealOnPrimaryContainerDark,
    secondary = SageSecondaryDark,
    onSecondary = SageOnSecondaryDark,
    secondaryContainer = SageSecondaryContainerDark,
    onSecondaryContainer = SageOnSecondaryContainerDark,
    background = HealthBackgroundDark,
    onBackground = HealthOnBackgroundDark,
    surface = HealthSurfaceDark,
    onSurface = HealthOnSurfaceDark,
    surfaceVariant = HealthSurfaceVariantDark,
    onSurfaceVariant = HealthOnSurfaceVariantDark,
)

private val LightColorScheme = lightColorScheme(
    primary = PineTealPrimary,
    onPrimary = PineTealOnPrimary,
    primaryContainer = PineTealPrimaryContainerDark, // crisp container
    onPrimaryContainer = PineTealOnPrimaryContainer,
    secondary = SageSecondary,
    onSecondary = SageOnSecondary,
    secondaryContainer = SageSecondaryContainer,
    onSecondaryContainer = SageOnSecondaryContainer,
    tertiary = OceanTertiary,
    onTertiary = OceanOnTertiary,
    tertiaryContainer = OceanTertiaryContainer,
    onTertiaryContainer = OceanOnTertiaryContainer,
    background = HealthBackground,
    onBackground = HealthOnBackground,
    surface = HealthSurface,
    onSurface = HealthOnSurface,
    surfaceVariant = HealthSurfaceVariant,
    onSurfaceVariant = HealthOnSurfaceVariant,
    outline = HealthOutline,
)

@Composable
fun MedicinskapetTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep distinct custom Nordic branding by default
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
