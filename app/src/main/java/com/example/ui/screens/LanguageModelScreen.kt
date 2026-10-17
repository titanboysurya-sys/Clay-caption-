package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.AiModelInfo
import com.example.model.Language
import com.example.model.LanguageRegistry
import com.example.model.ModelCatalog
import com.example.ui.clay.*
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.Screen
import com.example.viewmodel.UiState

@Composable
fun LanguageModelScreen(
    viewModel: MainViewModel,
    uiState: UiState,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    var showSourcePicker by remember { mutableStateOf(false) }
    var showTargetPicker by remember { mutableStateOf(false) }
    var shouldTranslate by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Back header
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                ClayIconButton(
                    icon = Icons.Rounded.ArrowBack,
                    contentDescription = "Back",
                    onClick = { onNavigate(Screen.HOME) },
                    size = 42.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Languages & AI Models",
                        style = MaterialTheme.typography.headlineMedium,
                        color = if (isDark) ClayTextPrimaryDark else ClayTextPrimaryLight
                    )
                    Text(
                        text = "Configure on-device speech & translation engine",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isDark) ClayTextSecondaryDark else ClayTextSecondaryLight
                    )
                }
            }
        }

        // Imported Video Duration Strip
        if (uiState.currentProject != null) {
            item {
                ClayCard(
                    shape = RoundedCornerShape(16.dp),
                    elevation = 3.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Videocam,
                            contentDescription = null,
                            tint = ClayPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = uiState.currentProject.title,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = if (isDark) ClayTextPrimaryDark else ClayTextPrimaryLight,
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ClayPrimary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Duration: ${uiState.currentProject.durationFormattedShort}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = ClayPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Language Selection Card
        item {
            ClayCard(
                shape = RoundedCornerShape(24.dp),
                elevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Speech & Subtitle Languages",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isDark) ClayTextPrimaryDark else ClayTextPrimaryLight
                    )

                    // Spoken Language Picker Row
                    LanguageSelectRow(
                        label = "Spoken Dialogue (Source)",
                        language = uiState.sourceLanguage,
                        onClick = { showSourcePicker = true }
                    )

                    // Auto-Detect Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Auto-Detect Language",
                                style = MaterialTheme.typography.titleMedium,
                                color = if (isDark) ClayTextPrimaryDark else ClayTextPrimaryLight
                            )
                            Text(
                                text = "Identify language automatically from audio",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isDark) ClayTextSecondaryDark else ClayTextSecondaryLight
                            )
                        }
                        ClaySwitch(
                            checked = uiState.autoDetectSource,
                            onCheckedChange = { viewModel.toggleAutoDetect(it) }
                        )
                    }

                    Divider(color = if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFE2DEF6))

                    // Translate Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Translate Subtitles",
                                style = MaterialTheme.typography.titleMedium,
                                color = if (isDark) ClayTextPrimaryDark else ClayTextPrimaryLight
                            )
                            Text(
                                text = "Generate captions in a different language",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isDark) ClayTextSecondaryDark else ClayTextSecondaryLight
                            )
                        }
                        ClaySwitch(
                            checked = shouldTranslate,
                            onCheckedChange = { shouldTranslate = it }
                        )
                    }

                    if (shouldTranslate) {
                        LanguageSelectRow(
                            label = "Subtitle Output (Target)",
                            language = uiState.targetLanguage,
                            onClick = { showTargetPicker = true }
                        )
                    }
                }
            }
        }

        // On-Device AI Models Section
        item {
            Text(
                text = "On-Device Speech Models",
                style = MaterialTheme.typography.titleLarge,
                color = if (isDark) ClayTextPrimaryDark else ClayTextPrimaryLight
            )
        }

        items(ModelCatalog.speechModels) { model ->
            ModelSelectionCard(
                model = model,
                isSelected = uiState.selectedSpeechModelId == model.id,
                onClick = { viewModel.setSpeechModel(model.id) }
            )
        }

        // Action CTA
        item {
            Spacer(modifier = Modifier.height(8.dp))
            ClayPrimaryButton(
                text = "Generate Captions (${if (shouldTranslate) "${uiState.sourceLanguage.code.uppercase()} → ${uiState.targetLanguage.code.uppercase()}" else uiState.sourceLanguage.name})",
                icon = Icons.Rounded.AutoAwesome,
                onClick = { viewModel.startCaptionGeneration(translateAlso = shouldTranslate) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    // Source Language Picker Modal
    if (showSourcePicker) {
        LanguagePickerDialog(
            title = "Select Spoken Speech Language",
            onDismiss = { showSourcePicker = false },
            onSelect = {
                viewModel.setSourceLanguage(it)
                showSourcePicker = false
            }
        )
    }

    // Target Language Picker Modal
    if (showTargetPicker) {
        LanguagePickerDialog(
            title = "Select Target Subtitle Language",
            onDismiss = { showTargetPicker = false },
            onSelect = {
                viewModel.setTargetLanguage(it)
                showTargetPicker = false
            }
        )
    }
}

@Composable
fun LanguageSelectRow(
    label: String,
    language: Language,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isDark) ClayTextSecondaryDark else ClayTextSecondaryLight
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(if (isDark) Color(0xFF19182D) else Color(0xFFEBE7FD))
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(ClayPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = language.code.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = ClayPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = language.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isDark) ClayTextPrimaryDark else ClayTextPrimaryLight
                        )
                        Text(
                            text = language.nativeName,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isDark) ClayTextSecondaryDark else ClayTextSecondaryLight
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = null,
                    tint = ClayPrimary
                )
            }
        }
    }
}

@Composable
fun ModelSelectionCard(
    model: AiModelInfo,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    ClayCard(
        shape = RoundedCornerShape(20.dp),
        elevation = if (isSelected) 8.dp else 4.dp,
        surfaceColor = if (isSelected) {
            if (isDark) Color(0xFF2C2852) else Color(0xFFECE7FD)
        } else {
            if (isDark) ClayDarkSurface else Color.White
        },
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = isSelected,
                        onClick = onClick,
                        colors = RadioButtonDefaults.colors(selectedColor = ClayPrimary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = model.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isDark) ClayTextPrimaryDark else ClayTextPrimaryLight
                        )
                        Text(
                            text = if (model.sizeMb > 0) "${model.sizeMb} MB • RAM: ${model.ramRequiredMb} MB" else "Zero Storage • System Engine",
                            style = MaterialTheme.typography.labelSmall,
                            color = ClayPrimary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(ClayAccentTeal.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Installed",
                        style = MaterialTheme.typography.labelSmall,
                        color = ClayAccentTeal,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = model.description,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDark) ClayTextSecondaryDark else ClayTextSecondaryLight
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "License: ${model.license}",
                style = MaterialTheme.typography.labelSmall,
                color = if (isDark) ClayTextSecondaryDark.copy(alpha = 0.7f) else ClayTextSecondaryLight.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun LanguagePickerDialog(
    title: String,
    onDismiss: () -> Unit,
    onSelect: (Language) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredLangs = remember(searchQuery) { LanguageRegistry.search(searchQuery) }

    ClayDialog(
        title = title,
        onDismissRequest = onDismiss,
        confirmButton = {
            ClaySecondaryButton(text = "Close", onClick = onDismiss)
        },
        content = {
            Column(modifier = Modifier.fillMaxWidth()) {
                ClaySearchField(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Search languages or code…"
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(modifier = Modifier.height(300.dp)) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredLangs, key = { it.code }) { lang ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable { onSelect(lang) }
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = lang.name,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            text = "${lang.nativeName} • ${lang.script}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = ClayTextSecondaryLight
                                        )
                                    }
                                    Text(
                                        text = lang.code.uppercase(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ClayPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}
