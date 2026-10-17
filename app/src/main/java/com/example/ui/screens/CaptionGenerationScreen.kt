package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.clay.*
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.UiState

@Composable
fun CaptionGenerationScreen(
    viewModel: MainViewModel,
    uiState: UiState,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_vad")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        ClayCard(
            shape = RoundedCornerShape(32.dp),
            elevation = 12.dp,
            surfaceColor = if (isDark) ClayDarkSurfaceElevated else Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 60.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Animated Speech Waveform Badge
                Box(
                    modifier = Modifier
                        .size((90 * pulseScale).dp)
                        .clip(CircleShape)
                        .background(ClayPrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clayPrimaryEffect(shape = CircleShape, elevation = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.GraphicEq,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(38.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Generating Captions",
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (isDark) ClayTextPrimaryDark else ClayTextPrimaryLight,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = uiState.generationStage.ifBlank { "Processing video on device…" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isDark) ClayTextSecondaryDark else ClayTextSecondaryLight,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                ClayProgressIndicator(
                    progress = uiState.generationProgress,
                    modifier = Modifier.fillMaxWidth(),
                    height = 14.dp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "On-Device Neural Model",
                        style = MaterialTheme.typography.labelSmall,
                        color = ClayAccentTeal
                    )
                    Text(
                        text = "${(uiState.generationProgress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = ClayPrimary
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                ClaySecondaryButton(
                    text = "Cancel Generation",
                    onClick = { viewModel.cancelCaptionGeneration() },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
