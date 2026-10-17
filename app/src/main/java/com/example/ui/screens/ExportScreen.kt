package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.engine.ExportConfig
import com.example.engine.ExportFormat
import com.example.engine.VideoExportEngine
import com.example.engine.VideoQualityMode
import com.example.ui.clay.*
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.Screen
import com.example.viewmodel.UiState
import java.io.File

@Composable
fun ExportScreen(
    viewModel: MainViewModel,
    uiState: UiState,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val project = uiState.currentProject ?: return

    var selectedFormat by remember { mutableStateOf(ExportFormat.BURNED_IN_MP4) }
    var selectedQuality by remember { mutableStateOf(VideoQualityMode.ULTRA_HIGH) }
    var selectedResWidth by remember { mutableStateOf(1080) }
    var selectedResHeight by remember { mutableStateOf(1920) }
    var exportTranslated by remember { mutableStateOf(uiState.showTranslatedText) }
    var isSavedToGallery by remember { mutableStateOf(false) }

    val exportEngine = remember { VideoExportEngine(context) }

    fun shareFile(file: File) {
        val uri: Uri = try {
            FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        } catch (e: Exception) {
            Uri.fromFile(file)
        }
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = if (file.name.endsWith(".mp4")) "video/mp4" else "text/plain"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share Exported Subtitles"))
    }

    fun playFile(file: File) {
        try {
            val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "video/mp4")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Cannot open video player", Toast.LENGTH_SHORT).show()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                ClayIconButton(
                    icon = Icons.Rounded.ArrowBack,
                    contentDescription = "Back",
                    onClick = { onNavigate(Screen.SUBTITLE_EDITOR) },
                    size = 40.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Fast Video Export",
                        style = MaterialTheme.typography.headlineMedium,
                        color = if (isDark) ClayTextPrimaryDark else ClayTextPrimaryLight
                    )
                    Text(
                        text = "Save high-quality captioned video directly to phone",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isDark) ClayTextSecondaryDark else ClayTextSecondaryLight
                    )
                }
            }
        }

        // Export State: Completed State
        if (uiState.exportedFile != null) {
            item {
                ClayCard(
                    shape = RoundedCornerShape(26.dp),
                    elevation = 10.dp,
                    surfaceColor = if (isDark) Color(0xFF1E2A38) else Color(0xFFECFDF5),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clayPrimaryEffect(shape = RoundedCornerShape(20.dp), elevation = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Export Finished & Saved!",
                            style = MaterialTheme.typography.titleLarge,
                            color = ClaySuccess
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Saved in Device Gallery: Movies/ClayCaption/${uiState.exportedFile.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDark) ClayTextSecondaryDark else ClayTextSecondaryLight
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Actions Row 1: Play & Share
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ClayPrimaryButton(
                                text = "Play Video",
                                icon = Icons.Rounded.PlayArrow,
                                onClick = { playFile(uiState.exportedFile) },
                                modifier = Modifier.weight(1f)
                            )
                            ClaySecondaryButton(
                                text = "Share",
                                icon = Icons.Rounded.Share,
                                onClick = { shareFile(uiState.exportedFile) },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Save to Gallery explicit confirmation button
                        ClaySecondaryButton(
                            text = if (isSavedToGallery) "✓ Saved to Phone Gallery" else "Save to Gallery Again",
                            icon = Icons.Rounded.SaveAlt,
                            onClick = {
                                val uri = exportEngine.saveVideoToGallery(uiState.exportedFile)
                                isSavedToGallery = true
                                Toast.makeText(context, "Saved to Device Movies/Gallery!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        ClaySecondaryButton(
                            text = "Back to Editor",
                            onClick = { onNavigate(Screen.SUBTITLE_EDITOR) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        } else if (uiState.isExporting) {
            // Live Progress State
            item {
                ClayCard(
                    shape = RoundedCornerShape(26.dp),
                    elevation = 10.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Fast Hardware Rendering…",
                            style = MaterialTheme.typography.titleLarge,
                            color = ClayPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = uiState.exportMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDark) ClayTextSecondaryDark else ClayTextSecondaryLight
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        ClayProgressIndicator(
                            progress = uiState.exportProgress,
                            modifier = Modifier.fillMaxWidth(),
                            height = 14.dp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "${(uiState.exportProgress * 100).toInt()}% Complete",
                            style = MaterialTheme.typography.labelSmall,
                            color = ClayPrimary
                        )

                        Spacer(modifier = Modifier.height(22.dp))

                        ClaySecondaryButton(
                            text = "Cancel Export",
                            onClick = { viewModel.cancelExport() }
                        )
                    }
                }
            }
        } else {
            // Export Configuration Options
            item {
                ClayCard(
                    shape = RoundedCornerShape(24.dp),
                    elevation = 6.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Output Format",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isDark) ClayTextPrimaryDark else ClayTextPrimaryLight
                        )

                        // Format Options
                        ClayChip(
                            text = "MP4 Video (Captions Burned In)",
                            selected = selectedFormat == ExportFormat.BURNED_IN_MP4,
                            onClick = { selectedFormat = ExportFormat.BURNED_IN_MP4 },
                            modifier = Modifier.fillMaxWidth()
                        )

                        ClayChip(
                            text = "Instant SRT Subtitle File (.srt)",
                            selected = selectedFormat == ExportFormat.SRT_SUBTITLES,
                            onClick = { selectedFormat = ExportFormat.SRT_SUBTITLES },
                            modifier = Modifier.fillMaxWidth()
                        )

                        ClayChip(
                            text = "Instant WebVTT Subtitle File (.vtt)",
                            selected = selectedFormat == ExportFormat.VTT_SUBTITLES,
                            onClick = { selectedFormat = ExportFormat.VTT_SUBTITLES },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Divider(color = if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFE2DEF6))

                        // Video Quality & Speed Mode
                        if (selectedFormat == ExportFormat.BURNED_IN_MP4) {
                            Text(
                                text = "Speed & Quality Mode",
                                style = MaterialTheme.typography.titleMedium,
                                color = if (isDark) ClayTextPrimaryDark else ClayTextPrimaryLight
                            )

                            for (mode in VideoQualityMode.values()) {
                                val isSelected = selectedQuality == mode
                                ClayCard(
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = if (isSelected) 6.dp else 2.dp,
                                    surfaceColor = if (isSelected) {
                                        if (isDark) Color(0xFF2C2850) else Color(0xFFECE7FD)
                                    } else {
                                        if (isDark) ClayDarkSurface else Color.White
                                    },
                                    onClick = { selectedQuality = mode },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { selectedQuality = mode },
                                            colors = RadioButtonDefaults.colors(selectedColor = ClayPrimary)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = mode.displayName,
                                                style = MaterialTheme.typography.titleMedium,
                                                color = if (isDark) ClayTextPrimaryDark else ClayTextPrimaryLight
                                            )
                                            Text(
                                                text = mode.description,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (isSelected) ClayPrimary else ClayTextSecondaryLight
                                            )
                                        }
                                    }
                                }
                            }

                            Divider(color = if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFE2DEF6))
                        }

                        // Language Track Selection
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Burn Translated Subtitles",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (isDark) ClayTextPrimaryDark else ClayTextPrimaryLight
                                )
                                Text(
                                    text = if (exportTranslated) "Output Language: ${uiState.targetLanguage.name}" else "Output Language: ${uiState.sourceLanguage.name}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ClayPrimary
                                )
                            }
                            ClaySwitch(
                                checked = exportTranslated,
                                onCheckedChange = { exportTranslated = it }
                            )
                        }

                        // Resolution Selector
                        if (selectedFormat == ExportFormat.BURNED_IN_MP4) {
                            Divider(color = if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFE2DEF6))

                            Text(
                                text = "Video Resolution",
                                style = MaterialTheme.typography.titleMedium,
                                color = if (isDark) ClayTextPrimaryDark else ClayTextPrimaryLight
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                ClayChip(
                                    text = "1080p Full HD",
                                    selected = selectedResWidth == 1080,
                                    onClick = {
                                        selectedResWidth = 1080
                                        selectedResHeight = 1920
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                                ClayChip(
                                    text = "720p HD",
                                    selected = selectedResWidth == 720,
                                    onClick = {
                                        selectedResWidth = 720
                                        selectedResHeight = 1280
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            // Export Button
            item {
                ClayPrimaryButton(
                    text = when (selectedFormat) {
                        ExportFormat.BURNED_IN_MP4 -> "Start Fast MP4 Export (Saved to Gallery)"
                        ExportFormat.SRT_SUBTITLES -> "Export SRT Subtitles"
                        ExportFormat.VTT_SUBTITLES -> "Export VTT Subtitles"
                    },
                    icon = Icons.Rounded.FileDownload,
                    onClick = {
                        val config = ExportConfig(
                            format = selectedFormat,
                            qualityMode = selectedQuality,
                            resolutionWidth = selectedResWidth,
                            resolutionHeight = selectedResHeight,
                            targetBitrate = selectedQuality.bitrate,
                            useTranslatedText = exportTranslated
                        )
                        viewModel.startExport(config)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
