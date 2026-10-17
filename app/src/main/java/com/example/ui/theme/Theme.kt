package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

// =========================================================================
// Material 3 Color Schemes with Claymorphism Aesthetics
// =========================================================================

val ClayDarkColorScheme = darkColorScheme(
    primary = ClayPrimary,
    onPrimary = Color.White,
    primaryContainer = ClayPrimaryDark,
    onPrimaryContainer = ClaySoftLavender,
    inversePrimary = ClayPrimaryLight,

    secondary = ClaySecondary,
    onSecondary = Color.White,
    secondaryContainer = ClaySecondaryDark,
    onSecondaryContainer = Color.White,

    tertiary = ClayAccentTeal,
    onTertiary = Color(0xFF003734),
    tertiaryContainer = ClayAccentTealDark,
    onTertiaryContainer = ClaySoftMint,

    background = ClayDarkBg,
    onBackground = ClayTextPrimaryDark,

    surface = ClayDarkSurface,
    onSurface = ClayTextPrimaryDark,
    surfaceVariant = ClayDarkSurfaceVariant,
    onSurfaceVariant = ClayTextSecondaryDark,
    surfaceTint = ClayPrimary,
    inverseSurface = ClayLightSurface,
    inverseOnSurface = ClayTextPrimaryLight,

    surfaceContainerLowest = ClayDarkSurfaceContainerLowest,
    surfaceContainerLow = ClayDarkSurfaceContainerLow,
    surfaceContainer = ClayDarkSurfaceContainer,
    surfaceContainerHigh = ClayDarkSurfaceContainerHigh,
    surfaceContainerHighest = ClayDarkSurfaceContainerHighest,

    outline = ClayDarkOutline,
    outlineVariant = ClayDarkOutlineVariant,

    error = ClayError,
    onError = Color.White,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = ClayErrorContainer
)

val ClayLightColorScheme = lightColorScheme(
    primary = ClayPrimary,
    onPrimary = Color.White,
    primaryContainer = ClaySoftLavender,
    onPrimaryContainer = ClayPrimaryDark,
    inversePrimary = ClayPrimaryLight,

    secondary = ClaySecondary,
    onSecondary = Color.White,
    secondaryContainer = ClaySoftBlue,
    onSecondaryContainer = ClayPrimaryDark,

    tertiary = ClayAccentTeal,
    onTertiary = Color.White,
    tertiaryContainer = ClaySoftMint,
    onTertiaryContainer = Color(0xFF004D48),

    background = ClayLightBg,
    onBackground = ClayTextPrimaryLight,

    surface = ClayLightSurface,
    onSurface = ClayTextPrimaryLight,
    surfaceVariant = ClayLightSurfaceVariant,
    onSurfaceVariant = ClayTextSecondaryLight,
    surfaceTint = ClayPrimary,
    inverseSurface = ClayDarkSurface,
    inverseOnSurface = ClayTextPrimaryDark,

    surfaceContainerLowest = ClayLightSurfaceContainerLowest,
    surfaceContainerLow = ClayLightSurfaceContainerLow,
    surfaceContainer = ClayLightSurfaceContainer,
    surfaceContainerHigh = ClayLightSurfaceContainerHigh,
    surfaceContainerHighest = ClayLightSurfaceContainerHighest,

    outline = ClayLightOutline,
    outlineVariant = ClayLightOutlineVariant,

    error = ClayError,
    onError = Color.White,
    errorContainer = ClayErrorContainer,
    onErrorContainer = ClayOnErrorContainer
)

/**
 * Main Claymorphism Application Theme.
 * Configures Material 3 with custom clay color schemes, typography, and pillowy shape tokens.
 * Also provides CompositionLocals for tactile elevations, borders, and soft shadows.
 */
@Composable
fun ClayCaptionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) ClayDarkColorScheme else ClayLightColorScheme
    val shadowTokens = if (darkTheme) DarkClayShadows else LightClayShadows

    CompositionLocalProvider(
        LocalClayElevations provides DefaultClayElevations,
        LocalClayBorders provides DefaultClayBorders,
        LocalClayShadows provides shadowTokens,
        LocalClayShapes provides ClayShapeTokens
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            shapes = ClayShapes,
            content = content
        )
    }
}

// Backward compatibility alias
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    ClayCaptionTheme(darkTheme = darkTheme, content = content)
}

// =========================================================================
// Ergonomic MaterialTheme Accessors for Clay Design System
// =========================================================================

val MaterialTheme.clayShapes: ClayShapeTokens
    @Composable
    @ReadOnlyComposable
    get() = LocalClayShapes.current

val MaterialTheme.clayElevations: ClayElevationTokens
    @Composable
    @ReadOnlyComposable
    get() = LocalClayElevations.current

val MaterialTheme.clayShadows: ClayShadowTokens
    @Composable
    @ReadOnlyComposable
    get() = LocalClayShadows.current

val MaterialTheme.clayBorders: ClayBorderTokens
    @Composable
    @ReadOnlyComposable
    get() = LocalClayBorders.current
