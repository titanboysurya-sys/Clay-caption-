package com.example.ui.clay

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*

/**
 * Standard Claymorphism effect for elevated tactile surfaces (cards, panels, sheets).
 * Combines soft ambient and spot shadows, inflated surface gradient, and 3D specular highlight bevel.
 */
fun Modifier.clayEffect(
    shape: Shape = ClayShapeTokens.card,
    elevation: Dp = DefaultClayElevations.level2,
    surfaceColor: Color? = null,
    borderWidth: Dp = DefaultClayBorders.standard,
    isDark: Boolean = false
): Modifier {
    val shadowColor = if (isDark) ClayDarkShadowDark else ClayLightShadowDark
    val borderBrush = if (isDark) ClayGradientTokens.bevelHighlightDark else ClayGradientTokens.bevelHighlightLight

    val backgroundModifier = if (surfaceColor != null) {
        Modifier.background(surfaceColor, shape)
    } else if (isDark) {
        Modifier.background(ClayGradientTokens.surfaceDark, shape)
    } else {
        Modifier.background(ClayGradientTokens.surfaceLight, shape)
    }

    return this
        .shadow(
            elevation = elevation,
            shape = shape,
            ambientColor = shadowColor,
            spotColor = shadowColor
        )
        .clip(shape)
        .then(backgroundModifier)
        .border(width = borderWidth, brush = borderBrush, shape = shape)
}

/**
 * High-emphasis Claymorphism effect for primary actions and CTA buttons.
 * Features a tactile vibrant gradient, specular upper reflection, and a tinted glow shadow.
 */
fun Modifier.clayPrimaryEffect(
    shape: Shape = ClayShapeTokens.button,
    elevation: Dp = DefaultClayElevations.level2,
    borderWidth: Dp = DefaultClayBorders.standard
): Modifier {
    return this
        .shadow(
            elevation = elevation,
            shape = shape,
            ambientColor = ClayPrimary.copy(alpha = 0.40f),
            spotColor = ClayPrimary.copy(alpha = 0.55f)
        )
        .clip(shape)
        .background(ClayGradientTokens.primary, shape)
        .border(width = borderWidth, brush = ClayGradientTokens.primaryButtonHighlight, shape = shape)
}

/**
 * Inset / recessed clay effect for depressed input fields, switch troughs, and slider tracks.
 */
fun Modifier.clayInsetEffect(
    shape: Shape = ClayShapeTokens.inputField,
    isDark: Boolean = false,
    borderWidth: Dp = DefaultClayBorders.hairline
): Modifier {
    val bgColor = if (isDark) ClayDarkSurfaceContainerLowest else ClayLightSurfaceContainerHigh
    val innerBorder = if (isDark) {
        Brush.verticalGradient(
            listOf(
                Color.Black.copy(alpha = 0.45f),
                Color.White.copy(alpha = 0.05f)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                ClayPrimary.copy(alpha = 0.18f),
                Color.White.copy(alpha = 0.85f)
            )
        )
    }

    return this
        .clip(shape)
        .background(bgColor, shape)
        .border(width = borderWidth, brush = innerBorder, shape = shape)
}
