package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.clay.*
import com.example.ui.components.SubtitleOverlayCard
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.Screen
import com.example.viewmodel.UiState

@Composable
fun FontAndStyleScreen(
    viewModel: MainViewModel,
    uiState: UiState,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val project = uiState.currentProject ?: return
    var style by remember(project.style) { mutableStateOf(project.style) }

    val sampleText = remember(project.segments, uiState.showTranslatedText) {
        project.segments.firstOrNull()?.displayText(uiState.showTranslatedText)
            ?: "Preview viral subtitles with ClayCaption"
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp),
        contentPadding = PaddingValues(top = 14.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ClayIconButton(
                    icon = Icons.Rounded.ArrowBack,
                    contentDescription = "Back",
                    onClick = { onNavigate(Screen.SUBTITLE_EDITOR) },
                    size = 40.dp
                )

                Text(
                    text = "Caption Typography & Styles",
                    style = MaterialTheme.typography.titleLarge,
                    color = if (isDark) ClayTextPrimaryDark else ClayTextPrimaryLight
                )

                ClayPrimaryButton(
                    text = "Apply",
                    onClick = {
                        viewModel.updateStyle(style)
                        onNavigate(Screen.SUBTITLE_EDITOR)
                    }
                )
            }
        }

        // Live Subtitle Preview Box
        item {
            ClayCard(
                shape = RoundedCornerShape(26.dp),
                elevation = 10.dp,
                surfaceColor = Color(0xFF151426),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = when (style.verticalPosition) {
                        SubtitlePosition.TOP -> Alignment.TopCenter
                        SubtitlePosition.CENTER -> Alignment.Center
                        SubtitlePosition.BOTTOM -> Alignment.BottomCenter
                    }
                ) {
                    SubtitleOverlayCard(
                        text = sampleText,
                        style = style
                    )
                }
            }
        }

        // 22 Presets Horizontal Selector
        item {
            Column {
                Text(
                    text = "Caption Design Presets (22 Available)",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isDark) ClayTextPrimaryDark else ClayTextPrimaryLight
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(CaptionPresetRegistry.presets, key = { it.id }) { preset ->
                        val isSelected = style.presetId == preset.id
                        ClayCard(
                            shape = RoundedCornerShape(18.dp),
                            elevation = if (isSelected) 8.dp else 2.dp,
                            surfaceColor = if (isSelected) {
                                if (isDark) Color(0xFF2C2850) else Color(0xFFECE7FD)
                            } else {
                                if (isDark) ClayDarkSurface else Color.White
                            },
                            onClick = {
                                style = preset.style
                                viewModel.applyPreset(preset)
                            },
                            modifier = Modifier.width(135.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = preset.name,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) ClayPrimary else if (isDark) ClayTextPrimaryDark else ClayTextPrimaryLight,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = preset.category,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ClayAccentTeal,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = preset.description,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                                    color = if (isDark) ClayTextSecondaryDark else ClayTextSecondaryLight,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }
        }

        // Typography Card
        item {
            ClayCard(
                shape = RoundedCornerShape(22.dp),
                elevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Font Family",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isDark) ClayTextPrimaryDark else ClayTextPrimaryLight
                    )

                    val fonts = listOf("Outfit", "Inter", "Lora", "Rubik")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (f in fonts) {
                            ClayChip(
                                text = f,
                                selected = style.fontFamilyName.equals(f, ignoreCase = true),
                                onClick = { style = style.copy(fontFamilyName = f) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Font Size Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Font Size", style = MaterialTheme.typography.labelSmall)
                        Text(text = "${style.fontSizeSp.toInt()} sp", style = MaterialTheme.typography.labelSmall, color = ClayPrimary)
                    }
                    ClaySlider(
                        value = style.fontSizeSp,
                        onValueChange = { style = style.copy(fontSizeSp = it) },
                        valueRange = 14f..36f
                    )

                    // Bold & Italic Toggles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ClayChip(
                            text = "Bold Text",
                            selected = style.isBold,
                            onClick = { style = style.copy(isBold = !style.isBold) },
                            modifier = Modifier.weight(1f)
                        )
                        ClayChip(
                            text = "Italic Text",
                            selected = style.isItalic,
                            onClick = { style = style.copy(isItalic = !style.isItalic) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Color Palettes Card
        item {
            ClayCard(
                shape = RoundedCornerShape(22.dp),
                elevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Text & Background Colors",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isDark) ClayTextPrimaryDark else ClayTextPrimaryLight
                    )

                    // Text Color Swatches
                    Text(text = "Text Color", style = MaterialTheme.typography.labelSmall)
                    ColorSwatchesRow(
                        selectedColor = style.textColor,
                        onColorSelected = { style = style.copy(textColor = it) }
                    )

                    // Outline Toggle & Thickness
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Text Outline Border", style = MaterialTheme.typography.labelSmall)
                        ClaySwitch(
                            checked = style.hasOutline,
                            onCheckedChange = { style = style.copy(hasOutline = it) }
                        )
                    }

                    if (style.hasOutline) {
                        ClaySlider(
                            value = style.outlineWidthDp,
                            onValueChange = { style = style.copy(outlineWidthDp = it) },
                            valueRange = 1f..6f
                        )
                    }

                    // Background Box Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Background Box Pill", style = MaterialTheme.typography.labelSmall)
                        ClaySwitch(
                            checked = style.hasBackgroundBox,
                            onCheckedChange = { style = style.copy(hasBackgroundBox = it) }
                        )
                    }

                    if (style.hasBackgroundBox) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Background Opacity", style = MaterialTheme.typography.labelSmall)
                            Text(text = "${(style.backgroundAlpha * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = ClayPrimary)
                        }
                        ClaySlider(
                            value = style.backgroundAlpha,
                            onValueChange = { style = style.copy(backgroundAlpha = it) },
                            valueRange = 0.1f..1.0f
                        )

                        Text(text = "Corner Radius: ${style.cornerRadiusDp.toInt()} dp", style = MaterialTheme.typography.labelSmall)
                        ClaySlider(
                            value = style.cornerRadiusDp,
                            onValueChange = { style = style.copy(cornerRadiusDp = it) },
                            valueRange = 0f..28f
                        )
                    }
                }
            }
        }

        // Alignment & Position Card
        item {
            ClayCard(
                shape = RoundedCornerShape(22.dp),
                elevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Position & Alignment",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isDark) ClayTextPrimaryDark else ClayTextPrimaryLight
                    )

                    // Alignment
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (a in SubtitleAlignment.values()) {
                            ClayChip(
                                text = a.name,
                                selected = style.alignment == a,
                                onClick = { style = style.copy(alignment = a) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Vertical Position
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (p in SubtitlePosition.values()) {
                            ClayChip(
                                text = p.name,
                                selected = style.verticalPosition == p,
                                onClick = { style = style.copy(verticalPosition = p) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Animation Effects Card
        item {
            ClayCard(
                shape = RoundedCornerShape(22.dp),
                elevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Subtitle Animation Effect",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isDark) ClayTextPrimaryDark else ClayTextPrimaryLight
                    )

                    val anims = CaptionAnimationType.values()
                    for (chunk in anims.toList().chunked(3)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (anim in chunk) {
                                ClayChip(
                                    text = anim.name.replace("_", " "),
                                    selected = style.animationType == anim,
                                    onClick = { style = style.copy(animationType = anim) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ColorSwatchesRow(
    selectedColor: Long,
    onColorSelected: (Long) -> Unit
) {
    val swatches = listOf(
        0xFFFFFFFF, // Pure White
        0xFFFFD700, // Gold Yellow
        0xFF38BDF8, // Sky Blue
        0xFF45C9C2, // Teal
        0xFFF472B6, // Pink
        0xFFCCFF00, // Lime
        0xFFF97316, // Orange
        0xFF25243A  // Slate Black
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (c in swatches) {
            val isSelected = selectedColor == c
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color(c))
                    .border(
                        width = if (isSelected) 3.dp else 1.dp,
                        color = if (isSelected) ClayPrimary else Color.Gray.copy(alpha = 0.4f),
                        shape = CircleShape
                    )
                    .clickable { onColorSelected(c) }
            )
        }
    }
}
