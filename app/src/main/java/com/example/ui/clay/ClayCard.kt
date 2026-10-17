package com.example.ui.clay

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import com.example.ui.theme.ClayShapeTokens
import com.example.ui.theme.DefaultClayElevations

@Composable
fun ClayCard(
    modifier: Modifier = Modifier,
    shape: Shape = ClayShapeTokens.card,
    elevation: Dp = DefaultClayElevations.level2,
    surfaceColor: Color? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed && onClick != null) 0.98f else 1.0f, label = "clay_press")

    var cardModifier = modifier
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clayEffect(
            shape = shape,
            elevation = if (isPressed && onClick != null) elevation / 2 else elevation,
            surfaceColor = surfaceColor,
            isDark = isDark
        )

    if (onClick != null) {
        cardModifier = cardModifier
            .minimumInteractiveComponentSize()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    }

    Box(
        modifier = cardModifier,
        content = content
    )
}
