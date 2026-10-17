package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Elevation tokens for elevated surfaces in the Claymorphism design language.
 * Clay surfaces feel soft, tactile, and physically raised above their backgrounds.
 */
@Immutable
data class ClayElevationTokens(
    /** Flat surface with no elevation (flush with background) */
    val level0: Dp = 0.dp,
    /** Inset / recessed / sunken state (for depressed text fields or pressed buttons) */
    val inset: Dp = 2.dp,
    /** Low elevation for chips, small badges, and pressed card states */
    val level1: Dp = 4.dp,
    /** Default elevation for standard cards, buttons, and switches */
    val level2: Dp = 8.dp,
    /** Medium elevation for highlighted cards, active toggles, and popups */
    val level3: Dp = 12.dp,
    /** High elevation for modal dialogs and overlay banners */
    val level4: Dp = 16.dp,
    /** Floating elevation for persistent floating bottom bars and hero banners */
    val level5: Dp = 24.dp
)

/**
 * Border and bevel tokens for the 3D clay effect.
 * Clay surfaces rely on smooth specular highlight borders (light source from top)
 * combined with subtle shadow reflection on bottom.
 */
@Immutable
data class ClayBorderTokens(
    /** Hairline border for subtle dividers and minimal borders */
    val hairline: Dp = 1.dp,
    /** Standard bevel stroke width for clay cards and buttons */
    val standard: Dp = 1.5.dp,
    /** Prominent border for active/selected states and high-contrast emphasis */
    val prominent: Dp = 2.dp,
    /** Thick highlight for focused inputs */
    val thick: Dp = 2.5.dp
)

/**
 * Shadow color and opacity tokens for soft ambient & spot shadows.
 * Unlike flat Material shadows, Clay shadows use diffused, colored ambient bounce
 * to simulate physical clay on colored backdrops.
 */
@Immutable
data class ClayShadowTokens(
    /** Ambient diffuse shadow color */
    val ambientShadowColor: Color,
    /** Spot directional shadow color */
    val spotShadowColor: Color,
    /** Highlight reflection color (top bevel light) */
    val highlightColor: Color,
    /** Specular glare color for high-gloss primary buttons */
    val specularGlowColor: Color
)

/**
 * Pre-defined gradient tokens that provide the signature 3D inflated look of Claymorphism.
 */
@Immutable
object ClayGradientTokens {
    /** Inflated light mode card surface gradient */
    val surfaceLight: Brush = Brush.verticalGradient(
        listOf(
            Color(0xFFFFFFFF),
            Color(0xFFF7F5FE)
        )
    )

    /** Inflated dark mode card surface gradient */
    val surfaceDark: Brush = Brush.verticalGradient(
        listOf(
            Color(0xFF252342),
            Color(0xFF1D1C33)
        )
    )

    /** Primary purple tactile gradient */
    val primary: Brush = Brush.verticalGradient(
        listOf(
            ClaySecondary,
            ClayPrimary
        )
    )

    /** Specular bevel highlight gradient for light surfaces */
    val bevelHighlightLight: Brush = Brush.verticalGradient(
        listOf(
            Color.White.copy(alpha = 0.95f),
            Color.White.copy(alpha = 0.45f),
            ClayPrimary.copy(alpha = 0.12f)
        )
    )

    /** Specular bevel highlight gradient for dark surfaces */
    val bevelHighlightDark: Brush = Brush.verticalGradient(
        listOf(
            Color.White.copy(alpha = 0.22f),
            Color.White.copy(alpha = 0.04f),
            ClayPrimary.copy(alpha = 0.12f)
        )
    )

    /** Primary button glossy reflection border */
    val primaryButtonHighlight: Brush = Brush.verticalGradient(
        listOf(
            Color.White.copy(alpha = 0.55f),
            Color.White.copy(alpha = 0.15f),
            ClayPrimaryDark.copy(alpha = 0.25f)
        )
    )
}

val DefaultClayElevations = ClayElevationTokens()
val DefaultClayBorders = ClayBorderTokens()

val LightClayShadows = ClayShadowTokens(
    ambientShadowColor = Color(0x2E6354B8),
    spotShadowColor = Color(0x386354B8),
    highlightColor = Color(0xFFFFFFFF),
    specularGlowColor = Color(0x667567E8)
)

val DarkClayShadows = ClayShadowTokens(
    ambientShadowColor = Color(0x66080712),
    spotShadowColor = Color(0x8005040B),
    highlightColor = Color(0x1AFFFFFF),
    specularGlowColor = Color(0x407567E8)
)

val LocalClayElevations = staticCompositionLocalOf { DefaultClayElevations }
val LocalClayBorders = staticCompositionLocalOf { DefaultClayBorders }
val LocalClayShadows = staticCompositionLocalOf { LightClayShadows }
val LocalClayShapes = staticCompositionLocalOf { ClayShapeTokens }
