package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.clay.ClayBottomNavigationBar
import com.example.ui.clay.NavItem
import com.example.ui.screens.*
import com.example.ui.theme.ClayCaptionTheme
import com.example.ui.theme.ClayDarkBg
import com.example.ui.theme.ClayLightBg
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.Screen

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ClayCaptionTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val projects by viewModel.allProjects.collectAsStateWithLifecycle()
                val isDark = isSystemInDarkTheme()

                // System Back Button Handling
                BackHandler(enabled = uiState.currentScreen != Screen.HOME) {
                    when (uiState.currentScreen) {
                        Screen.FONT_AND_STYLE -> viewModel.navigateTo(Screen.SUBTITLE_EDITOR)
                        Screen.EXPORT -> viewModel.navigateTo(Screen.SUBTITLE_EDITOR)
                        Screen.SUBTITLE_EDITOR -> viewModel.navigateTo(Screen.HOME)
                        Screen.LANGUAGE_MODEL -> viewModel.navigateTo(Screen.HOME)
                        Screen.CAPTION_GENERATION -> viewModel.cancelCaptionGeneration()
                        Screen.PROJECTS -> viewModel.navigateTo(Screen.HOME)
                        Screen.SETTINGS -> viewModel.navigateTo(Screen.HOME)
                        else -> viewModel.navigateTo(Screen.HOME)
                    }
                }

                val navItems = listOf(
                    NavItem(Screen.HOME.name, "Home", Icons.Rounded.Home),
                    NavItem(Screen.PROJECTS.name, "Projects", Icons.Rounded.Folder),
                    NavItem(Screen.SETTINGS.name, "Settings", Icons.Rounded.Settings)
                )

                val showBottomNav = uiState.currentScreen == Screen.HOME ||
                        uiState.currentScreen == Screen.PROJECTS ||
                        uiState.currentScreen == Screen.SETTINGS

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = if (isDark) ClayDarkBg else ClayLightBg,
                    contentWindowInsets = WindowInsets.statusBars,
                    bottomBar = {
                        if (showBottomNav) {
                            ClayBottomNavigationBar(
                                items = navItems,
                                currentRoute = uiState.currentScreen.name,
                                onNavigate = { route ->
                                    val target = when (route) {
                                        Screen.PROJECTS.name -> Screen.PROJECTS
                                        Screen.SETTINGS.name -> Screen.SETTINGS
                                        else -> Screen.HOME
                                    }
                                    viewModel.navigateTo(target)
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AnimatedContent(
                            targetState = uiState.currentScreen,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(220)) togetherWith
                                        fadeOut(animationSpec = tween(180))
                            },
                            label = "screen_nav"
                        ) { screen ->
                            when (screen) {
                                Screen.HOME -> HomeScreen(
                                    viewModel = viewModel,
                                    projects = projects,
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                                Screen.LANGUAGE_MODEL -> LanguageModelScreen(
                                    viewModel = viewModel,
                                    uiState = uiState,
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                                Screen.CAPTION_GENERATION -> CaptionGenerationScreen(
                                    viewModel = viewModel,
                                    uiState = uiState
                                )
                                Screen.SUBTITLE_EDITOR -> SubtitleEditorScreen(
                                    viewModel = viewModel,
                                    uiState = uiState,
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                                Screen.FONT_AND_STYLE -> FontAndStyleScreen(
                                    viewModel = viewModel,
                                    uiState = uiState,
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                                Screen.EXPORT -> ExportScreen(
                                    viewModel = viewModel,
                                    uiState = uiState,
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                                Screen.PROJECTS -> ProjectsScreen(
                                    viewModel = viewModel,
                                    projects = projects,
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                                Screen.SETTINGS -> SettingsScreen(
                                    viewModel = viewModel
                                )
                                else -> HomeScreen(
                                    viewModel = viewModel,
                                    projects = projects,
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
