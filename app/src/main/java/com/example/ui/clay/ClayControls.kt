package com.example.ui.clay

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*

@Composable
fun ClayChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = ClayShapeTokens.chip
) {
    val isDark = isSystemInDarkTheme()
    val elevation by animateDpAsState(targetValue = if (selected) 6.dp else 2.dp, label = "chip_elev")

    val bgModifier = if (selected) {
        Modifier.clayPrimaryEffect(shape = shape, elevation = elevation)
    } else {
        Modifier.clayEffect(shape = shape, elevation = elevation, isDark = isDark)
    }

    Box(
        modifier = modifier
            .then(bgModifier)
            .minimumInteractiveComponentSize()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) Color.White else if (isDark) ClayTextPrimaryDark else ClayTextPrimaryLight
        )
    }
}

@Composable
fun <T> ClaySegmentedControl(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    labelProvider: (T) -> String,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val shape = ClayShapeTokens.segmentedControl
    val containerBg = if (isDark) ClayDarkSurfaceContainerLowest else ClayLightSurfaceContainerHigh

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(containerBg)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        for (item in items) {
            val isSelected = item == selectedItem
            val itemShape = ClayShapeTokens.buttonSmall

            Box(
                modifier = Modifier
                    .weight(1f)
                    .then(
                        if (isSelected) {
                            Modifier.clayPrimaryEffect(shape = itemShape, elevation = 4.dp)
                        } else {
                            Modifier.clip(itemShape)
                        }
                    )
                    .minimumInteractiveComponentSize()
                    .clickable { onItemSelected(item) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = labelProvider(item),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) Color.White else if (isDark) ClayTextSecondaryDark else ClayTextSecondaryLight
                )
            }
        }
    }
}

@Composable
fun ClaySwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val thumbOffset by animateDpAsState(targetValue = if (checked) 24.dp else 4.dp, label = "thumb_offset")
    val trackBg by animateColorAsState(
        targetValue = if (checked) ClayPrimary else if (isDark) Color(0xFF26243F) else Color(0xFFDDD9F3),
        label = "track_bg"
    )

    Box(
        modifier = modifier
            .width(52.dp)
            .height(30.dp)
            .clip(CircleShape)
            .background(trackBg)
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 3.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(24.dp)
                .shadow(elevation = 3.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(Color.White)
                .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape)
        )
    }
}

@Composable
fun ClaySlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        valueRange = valueRange,
        colors = SliderDefaults.colors(
            thumbColor = ClayPrimary,
            activeTrackColor = ClayPrimary,
            inactiveTrackColor = ClaySoftBlue.copy(alpha = 0.5f)
        ),
        modifier = modifier
    )
}

@Composable
fun ClayProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 12.dp
) {
    val isDark = isSystemInDarkTheme()
    val shape = RoundedCornerShape(height / 2)
    val trackColor = if (isDark) Color(0xFF26243F) else Color(0xFFE2DEF6)
    val animatedProgress by animateFloatAsState(targetValue = progress.coerceIn(0f, 1f), label = "progress")

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(shape)
            .background(trackColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress)
                .fillMaxHeight()
                .clip(shape)
                .background(
                    Brush.horizontalGradient(
                        listOf(ClayPrimary, ClayAccentTeal)
                    )
                )
        )
    }
}
