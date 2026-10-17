package com.example.ui.screens

import android.net.Uri
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SubtitleSegment
import com.example.ui.clay.*
import com.example.ui.components.SubtitleTimelineView
import com.example.ui.components.VideoPlayerView
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.Screen
import com.example.viewmodel.UiState

@Composable
fun SubtitleEditorScreen(
    viewModel: MainViewModel,
    uiState: UiState,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val project = uiState.currentProject ?: return
    val selectedSegment = uiState.selectedSegment

    var editingSourceText by remember(selectedSegment?.id) {
        mutableStateOf(selectedSegment?.sourceText ?: "")
    }
    var editingTransText by remember(selectedSegment?.id) {
        mutableStateOf(selectedSegment?.translatedText ?: "")
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Toolbar Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ClayIconButton(
                    icon = Icons.Rounded.ArrowBack,
                    contentDescription = "Back",
                    onClick = { onNavigate(Screen.HOME) },
                    size = 40.dp
                )

                // Project Title & Total Duration Subtitle
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = project.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) ClayTextPrimaryDark else ClayTextPrimaryLight,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Schedule,
                            contentDescription = "Total Video Duration",
                            tint = ClayPrimary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Total Duration: ${project.durationFormattedShort}",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isDark) ClayTextSecondaryDark else ClayTextSecondaryLight,
                            modifier = Modifier.testTag("header_video_total_duration")
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Undo
                    ClayIconButton(
                        icon = Icons.Rounded.Undo,
                        contentDescription = "Undo",
                        onClick = { viewModel.undo() },
                        enabled = uiState.canUndo,
                        size = 38.dp,
                        iconSize = 18.dp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    // Redo
                    ClayIconButton(
                        icon = Icons.Rounded.Redo,
                        contentDescription = "Redo",
                        onClick = { viewModel.redo() },
                        enabled = uiState.canRedo,
                        size = 38.dp,
                        iconSize = 18.dp
                    )
                }
            }
        }

        // Imported Video Details & Total Duration Banner
        item {
            ClayCard(
                shape = RoundedCornerShape(18.dp),
                elevation = 4.dp,
                surfaceColor = if (isDark) ClayDarkSurfaceElevated else Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("imported_video_duration_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(ClayPrimary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Timer,
                                contentDescription = "Total Video Duration",
                                tint = ClayPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "TOTAL VIDEO DURATION",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    letterSpacing = 0.5.sp
                                ),
                                color = ClayPrimary
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = project.durationFormattedShort,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = if (isDark) ClayTextPrimaryDark else ClayTextPrimaryLight,
                                    modifier = Modifier.testTag("editor_total_duration_badge")
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (project.videoDurationMs >= 60000L) {
                                        "(${String.format(java.util.Locale.US, "%.1f min", project.videoDurationMs / 60000.0f)})"
                                    } else {
                                        "(${String.format(java.util.Locale.US, "%.1f sec", project.videoDurationMs / 1000.0f)})"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isDark) ClayTextSecondaryDark else ClayTextSecondaryLight
                                )
                            }
                        }
                    }

                    // Resolution & Subtitle Count Pills
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (project.videoWidth > 0 && project.videoHeight > 0) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.05f)
                            ) {
                                Text(
                                    text = "${project.videoWidth}x${project.videoHeight}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                    color = if (isDark) ClayTextSecondaryDark else ClayTextSecondaryLight,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                )
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ClaySecondary.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "${project.segments.size} cues",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = ClaySecondary,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }

        // Synchronized Video Player & Live Subtitle Overlay
        item {
            VideoPlayerView(
                videoUri = Uri.parse(project.videoUri),
                activeSegment = selectedSegment,
                style = project.style,
                useTranslatedText = uiState.showTranslatedText,
                currentTimeMs = uiState.currentTimeMs,
                totalDurationMs = project.videoDurationMs,
                isPlaying = uiState.isPlaying,
                onPlayPauseToggle = { viewModel.togglePlayback() },
                onSeek = { viewModel.setPlaybackPosition(it) }
            )
        }

        // Timeline Scrub Track
        item {
            SubtitleTimelineView(
                segments = project.segments,
                selectedSegmentId = selectedSegment?.id,
                currentTimeMs = uiState.currentTimeMs,
                totalDurationMs = project.videoDurationMs,
                onSegmentClick = { viewModel.selectSegment(it) },
                onTimeSeek = { viewModel.setPlaybackPosition(it) }
            )
        }

        // Quick Navigation Buttons (Style & Export)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ClaySecondaryButton(
                    text = "Style (${project.style.presetName})",
                    icon = Icons.Rounded.Palette,
                    onClick = { onNavigate(Screen.FONT_AND_STYLE) },
                    modifier = Modifier.weight(1f)
                )

                ClayPrimaryButton(
                    text = "Export MP4",
                    icon = Icons.Rounded.FileDownload,
                    onClick = { onNavigate(Screen.EXPORT) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Subtitle Toggle Row & Actions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Toggle between source & translated display
                ClayChip(
                    text = if (uiState.showTranslatedText) "Showing: ${uiState.targetLanguage.name} (Subtitles)" else "Showing: ${uiState.sourceLanguage.name} (Dialogue)",
                    selected = uiState.showTranslatedText,
                    onClick = { viewModel.toggleShowTranslated() }
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Add subtitle at current time
                    ClayIconButton(
                        icon = Icons.Rounded.Add,
                        contentDescription = "Add Subtitle",
                        onClick = { viewModel.addSegmentAtCurrentTime() },
                        isPrimary = true,
                        size = 38.dp,
                        iconSize = 20.dp
                    )
                }
            }
        }

        // Active Segment Editor Card
        if (selectedSegment != null) {
            item {
                ClayCard(
                    shape = RoundedCornerShape(22.dp),
                    elevation = 8.dp,
                    surfaceColor = if (isDark) ClayDarkSurfaceElevated else Color.White,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Edit Selected Subtitle",
                                style = MaterialTheme.typography.titleMedium,
                                color = ClayPrimary
                            )

                            // Segment Action Icons
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                // Split at playhead
                                IconButton(
                                    onClick = { viewModel.splitSegmentAtCurrentTime(selectedSegment.id) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.CallSplit,
                                        contentDescription = "Split",
                                        tint = ClaySecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                // Merge with next
                                IconButton(
                                    onClick = { viewModel.mergeSegmentWithNext(selectedSegment.id) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.MergeType,
                                        contentDescription = "Merge",
                                        tint = ClaySecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                // Delete
                                IconButton(
                                    onClick = { viewModel.deleteSegment(selectedSegment.id) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.DeleteOutline,
                                        contentDescription = "Delete",
                                        tint = ClayError,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        // Timing Adjusters Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            TimingBox(
                                label = "Start Time",
                                timeMs = selectedSegment.startTimeMs,
                                onAdjust = { delta ->
                                    viewModel.updateSegmentTimings(
                                        selectedSegment.id,
                                        selectedSegment.startTimeMs + delta,
                                        selectedSegment.endTimeMs
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )

                            TimingBox(
                                label = "End Time",
                                timeMs = selectedSegment.endTimeMs,
                                onAdjust = { delta ->
                                    viewModel.updateSegmentTimings(
                                        selectedSegment.id,
                                        selectedSegment.startTimeMs,
                                        selectedSegment.endTimeMs + delta
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Dialogue Text Field
                        ClayInputField(
                            value = editingSourceText,
                            onValueChange = {
                                editingSourceText = it
                                viewModel.updateSegmentText(selectedSegment.id, it, editingTransText)
                            },
                            label = "Spoken Dialogue (${uiState.sourceLanguage.name})",
                            singleLine = false,
                            maxLines = 3
                        )

                        // Translated Text Field
                        ClayInputField(
                            value = editingTransText,
                            onValueChange = {
                                editingTransText = it
                                viewModel.updateSegmentText(selectedSegment.id, editingSourceText, it)
                            },
                            label = "Translated Subtitle (${uiState.targetLanguage.name})",
                            singleLine = false,
                            maxLines = 3
                        )
                    }
                }
            }
        }

        // Subtitle Segments List Header
        item {
            Text(
                text = "All Subtitles (${project.segments.size})",
                style = MaterialTheme.typography.titleMedium,
                color = if (isDark) ClayTextPrimaryDark else ClayTextPrimaryLight
            )
        }

        // Subtitles List
        items(project.segments, key = { it.id }) { seg ->
            val isSelected = seg.id == selectedSegment?.id
            SubtitleSegmentListItem(
                segment = seg,
                isSelected = isSelected,
                showTranslated = uiState.showTranslatedText,
                onClick = {
                    viewModel.selectSegment(seg)
                    viewModel.setPlaybackPosition(seg.startTimeMs)
                }
            )
        }
    }
}

@Composable
fun TimingBox(
    label: String,
    timeMs: Long,
    onAdjust: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isDark) ClayTextSecondaryDark else ClayTextSecondaryLight
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(if (isDark) Color(0xFF19182D) else Color(0xFFEBE7FD))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onAdjust(-100L) },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Remove,
                    contentDescription = "-100ms",
                    tint = ClayPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                text = SubtitleSegment.formatTimestamp(timeMs),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if (isDark) ClayTextPrimaryDark else ClayTextPrimaryLight
            )

            IconButton(
                onClick = { onAdjust(100L) },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "+100ms",
                    tint = ClayPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun SubtitleSegmentListItem(
    segment: SubtitleSegment,
    isSelected: Boolean,
    showTranslated: Boolean,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    ClayCard(
        shape = RoundedCornerShape(18.dp),
        elevation = if (isSelected) 6.dp else 2.dp,
        surfaceColor = if (isSelected) {
            if (isDark) Color(0xFF2C2850) else Color(0xFFECE7FD)
        } else {
            if (isDark) ClayDarkSurface else Color.White
        },
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Time Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(ClayPrimary.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = SubtitleSegment.formatTimestamp(segment.startTimeMs),
                    style = MaterialTheme.typography.labelSmall,
                    color = ClayPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = segment.displayText(showTranslated),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isDark) ClayTextPrimaryDark else ClayTextPrimaryLight,
                    maxLines = 2
                )
                if (showTranslated && segment.sourceText.isNotBlank()) {
                    Text(
                        text = segment.sourceText,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isDark) ClayTextSecondaryDark else ClayTextSecondaryLight,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
