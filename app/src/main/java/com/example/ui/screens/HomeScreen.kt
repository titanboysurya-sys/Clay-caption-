package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Project
import com.example.ui.clay.*
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.Screen
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    projects: List<Project>,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    // File picker launcher
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.importVideoUri(uri)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // App Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ClayCaption",
                        style = MaterialTheme.typography.displayMedium,
                        color = if (isDark) ClayTextPrimaryDark else ClayTextPrimaryLight
                    )
                    Text(
                        text = "Offline AI Video Subtitle Studio",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isDark) ClayTextSecondaryDark else ClayTextSecondaryLight
                    )
                }

                // 100% Offline Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(ClayAccentTeal.copy(alpha = 0.2f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(ClayAccentTeal)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Offline",
                            style = MaterialTheme.typography.labelSmall,
                            color = ClayAccentTeal,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Hero "Import Video" Card
        item {
            ClayCard(
                shape = RoundedCornerShape(26.dp),
                elevation = 10.dp,
                surfaceColor = if (isDark) ClayDarkSurfaceElevated else Color.White,
                onClick = { videoPickerLauncher.launch("video/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clayPrimaryEffect(shape = RoundedCornerShape(20.dp), elevation = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.VideoLibrary,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Import Video",
                            style = MaterialTheme.typography.titleLarge,
                            color = if (isDark) ClayTextPrimaryDark else ClayTextPrimaryLight
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Select MP4, MOV or MKV to generate captions",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDark) ClayTextSecondaryDark else ClayTextSecondaryLight
                        )
                    }

                    Icon(
                        imageVector = Icons.Rounded.ArrowForward,
                        contentDescription = null,
                        tint = ClayPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // One-Tap Sample Demo Video Card
        item {
            ClayCard(
                shape = RoundedCornerShape(24.dp),
                elevation = 8.dp,
                surfaceColor = if (isDark) Color(0xFF23213F) else Color(0xFFECE8FD),
                onClick = { viewModel.loadSampleVideoAndStart() },
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
                            .clip(RoundedCornerShape(16.dp))
                            .background(ClayAccentTeal.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PlayCircle,
                            contentDescription = null,
                            tint = ClayAccentTeal,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Try Sample Demo Video",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isDark) ClayTextPrimaryDark else ClayTextPrimaryLight
                        )
                        Text(
                            text = "Test Hindi → Tamil or English subtitles instantly",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDark) ClayTextSecondaryDark else ClayTextSecondaryLight
                        )
                    }

                    ClayPrimaryButton(
                        text = "Try",
                        onClick = { viewModel.loadSampleVideoAndStart() }
                    )
                }
            }
        }

        // Section Title: Recent Projects
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Projects",
                    style = MaterialTheme.typography.titleLarge,
                    color = if (isDark) ClayTextPrimaryDark else ClayTextPrimaryLight
                )
                if (projects.isNotEmpty()) {
                    Text(
                        text = "${projects.size} Saved",
                        style = MaterialTheme.typography.labelSmall,
                        color = ClayPrimary
                    )
                }
            }
        }

        // Projects List or Empty State
        if (projects.isEmpty()) {
            item {
                ClayEmptyState(
                    title = "No Projects Yet",
                    message = "Import a video or tap 'Try Sample' to generate your first subtitle video locally.",
                    icon = Icons.Rounded.Subtitles,
                    actionText = "Try Sample Demo Video",
                    onActionClick = { viewModel.loadSampleVideoAndStart() }
                )
            }
        } else {
            items(projects, key = { it.id }) { project ->
                ProjectCardItem(
                    project = project,
                    onClick = { viewModel.selectProject(project) },
                    onDuplicate = { viewModel.duplicateProject(project) },
                    onDelete = { viewModel.deleteProject(project.id) }
                )
            }
        }
    }
}

@Composable
fun ProjectCardItem(
    project: Project,
    onClick: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val dateStr = remember(project.updatedAt) {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        sdf.format(Date(project.updatedAt))
    }

    ClayCard(
        shape = RoundedCornerShape(22.dp),
        elevation = 6.dp,
        surfaceColor = if (isDark) ClayDarkSurface else Color.White,
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail avatar
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(ClayPrimary, ClaySecondary)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.ClosedCaption,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = project.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isDark) ClayTextPrimaryDark else ClayTextPrimaryLight,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${project.segmentCount} subtitles • ${project.durationFormatted}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isDark) ClayTextSecondaryDark else ClayTextSecondaryLight
                )
                Text(
                    text = "${project.sourceLanguageCode.uppercase()} → ${project.targetLanguageCode.uppercase()} • $dateStr",
                    style = MaterialTheme.typography.labelSmall,
                    color = ClayPrimary
                )
            }

            // Quick actions
            IconButton(
                onClick = onDuplicate,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.ContentCopy,
                    contentDescription = "Duplicate",
                    tint = ClaySecondary,
                    modifier = Modifier.size(18.dp)
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(36.dp)
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
}
