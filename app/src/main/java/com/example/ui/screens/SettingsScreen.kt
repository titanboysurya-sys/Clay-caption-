package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.ModelCatalog
import com.example.model.SubtitleStyle
import com.example.ui.clay.*
import com.example.ui.components.SubtitleOverlayCard
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel

enum class TestVideoBackground(val label: String, val description: String) {
    DARK("Dark Video", "Night/Low-light scenes"),
    BRIGHT_WHITE("Bright White", "Sunny outdoor / Snow scenes"),
    HIGH_MOTION("High Motion", "Fast moving colorful footage"),
    VIBRANT("Vibrant Scene", "Neon & saturated footage")
}

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var cacheClearedMessage by remember { mutableStateOf<String?>(null) }
    var saveSuccessMessage by remember { mutableStateOf<String?>(null) }

    // Live legibility customization state initialized from viewModel
    var legibilityStyle by remember(uiState.defaultStyle) {
        mutableStateOf(uiState.defaultStyle)
    }

    // Video background simulation selector
    var selectedBackground by remember { mutableStateOf(TestVideoBackground.BRIGHT_WHITE) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Settings & Legibility",
                style = MaterialTheme.typography.headlineMedium,
                color = if (isDark) ClayTextPrimaryDark else ClayTextPrimaryLight
            )
            Text(
                text = "Ensure captions are 100% legible across all video backgrounds",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDark) ClayTextSecondaryDark else ClayTextSecondaryLight
            )
        }

        // ==========================================
        // SUBTITLE LEGIBILITY & BACKGROUND CUSTOMIZER
        // ==========================================
        item {
            ClayCard(
                shape = RoundedCornerShape(26.dp),
                elevation = 8.dp,
                surfaceColor = if (isDark) ClayDarkSurfaceElevated else Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Subtitle Legibility Preview",
                                style = MaterialTheme.typography.titleMedium,
                                color = ClayPrimary
                            )
                            Text(
                                text = "Test readability against different video scenes",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isDark) ClayTextSecondaryDark else ClayTextSecondaryLight
                            )
                        }

                        Icon(
                            imageVector = Icons.Rounded.Visibility,
                            contentDescription = null,
                            tint = ClayPrimary
                        )
                    }

                    // Background scene selector chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (bg in TestVideoBackground.values()) {
                            ClayChip(
                                text = bg.label,
                                selected = selectedBackground == bg,
                                onClick = { selectedBackground = bg },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Interactive Simulated Video Surface Preview Box
                    val backgroundBrush = when (selectedBackground) {
                        TestVideoBackground.DARK -> Brush.verticalGradient(
                            listOf(Color(0xFF0F172A), Color(0xFF020617))
                        )
                        TestVideoBackground.BRIGHT_WHITE -> Brush.verticalGradient(
                            listOf(Color(0xFFFFFFFF), Color(0xFFE2E8F0))
                        )
                        TestVideoBackground.HIGH_MOTION -> Brush.linearGradient(
                            listOf(Color(0xFFEF4444), Color(0xFFF59E0B), Color(0xFF10B981), Color(0xFF3B82F6))
                        )
                        TestVideoBackground.VIBRANT -> Brush.radialGradient(
                            listOf(Color(0xFFD946EF), Color(0xFF8B5CF6), Color(0xFF06B6D4))
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(backgroundBrush)
                            .border(
                                1.5.dp,
                                if (isDark) Color.White.copy(alpha = 0.15f) else Color(0xFFCBD5E1),
                                RoundedCornerShape(20.dp)
                            )
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        SubtitleOverlayCard(
                            text = "Crisp subtitles readable on any video!",
                            style = legibilityStyle
                        )
                    }

                    HorizontalDivider(color = if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFE2DEF6))

                    // 1. FONT SIZE CONTROLS
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Font Size",
                                style = MaterialTheme.typography.titleMedium,
                                color = if (isDark) ClayTextPrimaryDark else ClayTextPrimaryLight
                            )
                            Text(
                                text = "${legibilityStyle.fontSizeSp.toInt()} sp",
                                style = MaterialTheme.typography.labelSmall,
                                color = ClayPrimary
                            )
                        }

                        ClaySlider(
                            value = legibilityStyle.fontSizeSp,
                            onValueChange = { legibilityStyle = legibilityStyle.copy(fontSizeSp = it) },
                            valueRange = 14f..36f
                        )

                        // Quick size presets
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ClayChip(
                                text = "Compact (16sp)",
                                selected = legibilityStyle.fontSizeSp.toInt() == 16,
                                onClick = { legibilityStyle = legibilityStyle.copy(fontSizeSp = 16f) },
                                modifier = Modifier.weight(1f)
                            )
                            ClayChip(
                                text = "Standard (22sp)",
                                selected = legibilityStyle.fontSizeSp.toInt() == 22,
                                onClick = { legibilityStyle = legibilityStyle.copy(fontSizeSp = 22f) },
                                modifier = Modifier.weight(1f)
                            )
                            ClayChip(
                                text = "Large (28sp)",
                                selected = legibilityStyle.fontSizeSp.toInt() == 28,
                                onClick = { legibilityStyle = legibilityStyle.copy(fontSizeSp = 28f) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    HorizontalDivider(color = if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFE2DEF6))

                    // 2. TEXT COLOR SELECTOR
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Subtitle Text Color",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isDark) ClayTextPrimaryDark else ClayTextPrimaryLight
                        )

                        ColorSwatchesRow(
                            selectedColor = legibilityStyle.textColor,
                            onColorSelected = { legibilityStyle = legibilityStyle.copy(textColor = it) }
                        )
                    }

                    HorizontalDivider(color = if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFE2DEF6))

                    // 3. BACKGROUND OPACITY & BOX CONTROLS
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Background Box for Legibility",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (isDark) ClayTextPrimaryDark else ClayTextPrimaryLight
                                )
                                Text(
                                    text = "Places a translucent box behind text to guarantee contrast",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isDark) ClayTextSecondaryDark else ClayTextSecondaryLight
                                )
                            }
                            ClaySwitch(
                                checked = legibilityStyle.hasBackgroundBox,
                                onCheckedChange = {
                                    legibilityStyle = legibilityStyle.copy(hasBackgroundBox = it)
                                }
                            )
                        }

                        if (legibilityStyle.hasBackgroundBox) {
                            // Background Opacity Slider
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Background Opacity",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isDark) ClayTextPrimaryDark else ClayTextPrimaryLight
                                )
                                Text(
                                    text = "${(legibilityStyle.backgroundAlpha * 100).toInt()}%",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ClayPrimary
                                )
                            }

                            ClaySlider(
                                value = legibilityStyle.backgroundAlpha,
                                onValueChange = { legibilityStyle = legibilityStyle.copy(backgroundAlpha = it) },
                                valueRange = 0.1f..1.0f
                            )

                            // Quick Opacity Presets
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ClayChip(
                                    text = "Soft (35%)",
                                    selected = (legibilityStyle.backgroundAlpha * 100).toInt() == 35,
                                    onClick = { legibilityStyle = legibilityStyle.copy(backgroundAlpha = 0.35f) },
                                    modifier = Modifier.weight(1f)
                                )
                                ClayChip(
                                    text = "Medium (65%)",
                                    selected = (legibilityStyle.backgroundAlpha * 100).toInt() == 65,
                                    onClick = { legibilityStyle = legibilityStyle.copy(backgroundAlpha = 0.65f) },
                                    modifier = Modifier.weight(1f)
                                )
                                ClayChip(
                                    text = "Solid (100%)",
                                    selected = (legibilityStyle.backgroundAlpha * 100).toInt() == 100,
                                    onClick = { legibilityStyle = legibilityStyle.copy(backgroundAlpha = 1.0f) },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Background Box Color
                            Text(
                                text = "Background Box Tint",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isDark) ClayTextPrimaryDark else ClayTextPrimaryLight
                            )

                            val bgColors = listOf(
                                0xFF000000, // Black
                                0xFF1E293B, // Slate Dark
                                0xFF1E1B4B, // Deep Indigo
                                0xFF25243A, // Slate Black
                                0xFF7567E8, // Clay Primary Purple
                                0xFFFFFFFF  // White
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                for (c in bgColors) {
                                    val isSelected = legibilityStyle.backgroundColor == c
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
                                            .clickable { legibilityStyle = legibilityStyle.copy(backgroundColor = c) }
                                    )
                                }
                            }

                            // Corner Radius Slider
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Box Corner Radius",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "${legibilityStyle.cornerRadiusDp.toInt()} dp",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ClayPrimary
                                )
                            }

                            ClaySlider(
                                value = legibilityStyle.cornerRadiusDp,
                                onValueChange = { legibilityStyle = legibilityStyle.copy(cornerRadiusDp = it) },
                                valueRange = 0f..28f
                            )
                        }
                    }

                    HorizontalDivider(color = if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFE2DEF6))

                    // 4. OUTLINE / STROKE TOGGLE (High legibility fallback)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Black Outline Stroke",
                                style = MaterialTheme.typography.titleMedium,
                                color = if (isDark) ClayTextPrimaryDark else ClayTextPrimaryLight
                            )
                            Text(
                                text = "Provides sharp contrast even with zero background box",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isDark) ClayTextSecondaryDark else ClayTextSecondaryLight
                            )
                        }
                        ClaySwitch(
                            checked = legibilityStyle.hasOutline,
                            onCheckedChange = { legibilityStyle = legibilityStyle.copy(hasOutline = it) }
                        )
                    }

                    if (legibilityStyle.hasOutline) {
                        ClaySlider(
                            value = legibilityStyle.outlineWidthDp,
                            onValueChange = { legibilityStyle = legibilityStyle.copy(outlineWidthDp = it) },
                            valueRange = 1f..6f
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // SAVE & APPLY BUTTON
                    ClayPrimaryButton(
                        text = "Save Default Legibility Settings",
                        icon = Icons.Rounded.Done,
                        onClick = {
                            viewModel.updateDefaultStyle(legibilityStyle)
                            saveSuccessMessage = "Default subtitle legibility settings saved successfully!"
                            Toast.makeText(context, "Saved default subtitle style!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (saveSuccessMessage != null) {
                        Text(
                            text = saveSuccessMessage!!,
                            style = MaterialTheme.typography.labelSmall,
                            color = ClaySuccess,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }

        // Privacy Guarantee Card
        item {
            ClayCard(
                shape = RoundedCornerShape(24.dp),
                elevation = 8.dp,
                surfaceColor = if (isDark) Color(0xFF1E2638) else Color(0xFFECFDF5),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clayPrimaryEffect(shape = RoundedCornerShape(16.dp), elevation = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Security,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "100% On-Device Privacy",
                            style = MaterialTheme.typography.titleMedium,
                            color = ClaySuccess
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Videos, audio recordings, and subtitles are processed strictly on your hardware. No accounts, telemetry, or remote servers.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDark) ClayTextSecondaryDark else ClayTextSecondaryLight
                        )
                    }
                }
            }
        }

        // Storage & Models Card
        item {
            ClayCard(
                shape = RoundedCornerShape(22.dp),
                elevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "AI Model Storage Management",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isDark) ClayTextPrimaryDark else ClayTextPrimaryLight
                    )

                    val totalModelSize = ModelCatalog.getAllModels().filter { it.isBundled }.sumOf { it.sizeMb }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Offline Models Installed:", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "$totalModelSize MB", style = MaterialTheme.typography.labelSmall, color = ClayPrimary)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "App Cache Usage:", style = MaterialTheme.typography.bodyMedium)
                        val cacheSize = context.cacheDir.listFiles()?.sumOf { it.length() } ?: 0L
                        Text(text = "${cacheSize / (1024 * 1024)} MB", style = MaterialTheme.typography.labelSmall, color = ClayAccentTeal)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    ClaySecondaryButton(
                        text = "Clear Temporary Media Cache",
                        onClick = {
                            context.cacheDir.listFiles()?.forEach { file ->
                                if (file.isFile) file.delete()
                            }
                            cacheClearedMessage = "Temporary cache cleared successfully!"
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (cacheClearedMessage != null) {
                        Text(
                            text = cacheClearedMessage!!,
                            style = MaterialTheme.typography.labelSmall,
                            color = ClaySuccess
                        )
                    }
                }
            }
        }

        // About & License Card
        item {
            ClayCard(
                shape = RoundedCornerShape(22.dp),
                elevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "About ClayCaption",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isDark) ClayTextPrimaryDark else ClayTextPrimaryLight
                    )
                    Text(
                        text = "Version 1.0.0 (Production Release)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isDark) ClayTextSecondaryDark else ClayTextSecondaryLight
                    )
                    Text(
                        text = "Open Source Speech & Translation Models: OpenAI Whisper (MIT), No Language Left Behind (CC-BY-NC 4.0).",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isDark) ClayTextSecondaryDark else ClayTextSecondaryLight
                    )
                    Text(
                        text = "Designed with Claymorphism 3D tokens and Material 3 foundations.",
                        style = MaterialTheme.typography.labelSmall,
                        color = ClayPrimary
                    )
                }
            }
        }
    }
}
