package com.example.ui.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.model.Project
import com.example.ui.clay.*
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.Screen

@Composable
fun ProjectsScreen(
    viewModel: MainViewModel,
    projects: List<Project>,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    var searchQuery by remember { mutableStateOf("") }
    var projectToDelete by remember { mutableStateOf<Project?>(null) }

    val filteredProjects = remember(projects, searchQuery) {
        if (searchQuery.isBlank()) projects
        else projects.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            it.sourceLanguageCode.contains(searchQuery, ignoreCase = true) ||
            it.targetLanguageCode.contains(searchQuery, ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Projects Library",
                        style = MaterialTheme.typography.headlineMedium,
                        color = if (isDark) ClayTextPrimaryDark else ClayTextPrimaryLight
                    )
                    Text(
                        text = "${projects.size} local projects saved offline",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isDark) ClayTextSecondaryDark else ClayTextSecondaryLight
                    )
                }

                ClayIconButton(
                    icon = Icons.Rounded.Add,
                    contentDescription = "New Project",
                    onClick = { onNavigate(Screen.HOME) },
                    isPrimary = true
                )
            }
        }

        item {
            ClaySearchField(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Search projects…"
            )
        }

        if (filteredProjects.isEmpty()) {
            item {
                ClayEmptyState(
                    title = if (searchQuery.isEmpty()) "No Saved Projects" else "No Matching Projects",
                    message = "Projects you create or import will appear here automatically.",
                    icon = Icons.Rounded.FolderOpen,
                    actionText = "Create New Project",
                    onActionClick = { onNavigate(Screen.HOME) }
                )
            }
        } else {
            items(filteredProjects, key = { it.id }) { project ->
                ProjectCardItem(
                    project = project,
                    onClick = { viewModel.selectProject(project) },
                    onDuplicate = { viewModel.duplicateProject(project) },
                    onDelete = { projectToDelete = project }
                )
            }
        }
    }

    // Delete Confirmation Dialog
    if (projectToDelete != null) {
        ClayDialog(
            title = "Delete Project?",
            onDismissRequest = { projectToDelete = null },
            confirmButton = {
                ClayPrimaryButton(
                    text = "Delete",
                    onClick = {
                        projectToDelete?.let { viewModel.deleteProject(it.id) }
                        projectToDelete = null
                    }
                )
            },
            dismissButton = {
                ClaySecondaryButton(
                    text = "Cancel",
                    onClick = { projectToDelete = null }
                )
            },
            content = {
                Text(
                    text = "Are you sure you want to delete '${projectToDelete?.title}'? This action cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        )
    }
}
