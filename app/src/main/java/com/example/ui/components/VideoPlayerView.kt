package com.example.ui.components

import android.graphics.Color as AndroidColor
import android.media.MediaPlayer
import android.net.Uri
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.model.*
import com.example.ui.clay.ClayCard
import com.example.ui.clay.ClayIconButton
import com.example.ui.clay.ClaySlider
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun VideoPlayerView(
    videoUri: Uri,
    activeSegment: SubtitleSegment?,
    style: SubtitleStyle,
    useTranslatedText: Boolean,
    currentTimeMs: Long,
    totalDurationMs: Long,
    isPlaying: Boolean,
    onPlayPauseToggle: () -> Unit,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(Color(0xFF0F0E17))
            .border(
                1.5.dp,
                if (isDark) Color.White.copy(alpha = 0.1f) else ClayPrimary.copy(alpha = 0.2f),
                RoundedCornerShape(26.dp)
            )
    ) {
        // Video Surface Holder
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                SurfaceView(ctx).apply {
                    holder.addCallback(object : SurfaceHolder.Callback {
                        override fun surfaceCreated(holder: SurfaceHolder) {
                            // Surface created
                        }
                        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
                        override fun surfaceDestroyed(holder: SurfaceHolder) {}
                    })
                }
            }
        )

        // Subtitle Overlay layer (Completely transparent background, zero color tint)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            // Live Subtitle Overlay
            AnimatedVisibility(
                visible = activeSegment != null && activeSegment.displayText(useTranslatedText).isNotBlank(),
                enter = fadeIn(tween(180)) + scaleIn(tween(180), initialScale = 0.95f),
                exit = fadeOut(tween(120)),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                if (activeSegment != null) {
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
                            text = activeSegment.displayText(useTranslatedText),
                            style = style
                        )
                    }
                }
            }
        }

        // Top Badges Overlay (Total Video Duration Badge)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xB3000000)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Timer,
                        contentDescription = "Total Video Duration",
                        tint = ClayAccentTeal,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Total: ${SubtitleSegment.formatTimestampShort(totalDurationMs)}",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        }

        // Floating On-Screen Quick Play / Pause Button in Center
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            if (!isPlaying) {
                ClayIconButton(
                    icon = Icons.Rounded.PlayArrow,
                    contentDescription = "Play",
                    onClick = onPlayPauseToggle,
                    size = 54.dp,
                    iconSize = 30.dp,
                    isPrimary = true
                )
            }
        }

        // Bottom Controls Overlay (Time badge & scrub bar)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 10.dp, start = 14.dp, end = 14.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xCC000000))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onPlayPauseToggle,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = SubtitleSegment.formatTimestampShort(currentTimeMs),
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium)
                )

                Slider(
                    value = if (totalDurationMs > 0) currentTimeMs.toFloat() / totalDurationMs.toFloat() else 0f,
                    onValueChange = { ratio ->
                        onSeek((ratio * totalDurationMs).toLong())
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = ClayPrimary,
                        activeTrackColor = ClayPrimary,
                        inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                    )
                )

                Text(
                    text = SubtitleSegment.formatTimestampShort(totalDurationMs),
                    color = Color.White.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
fun SubtitleOverlayCard(
    text: String,
    style: SubtitleStyle,
    modifier: Modifier = Modifier
) {
    val fontFamily = when (style.fontFamilyName.lowercase()) {
        "outfit" -> OutfitFontFamily
        "inter" -> InterFontFamily
        "lora" -> LoraFontFamily
        "rubik" -> RubikFontFamily
        else -> OutfitFontFamily
    }

    val textAlign = when (style.alignment) {
        SubtitleAlignment.START -> TextAlign.Start
        SubtitleAlignment.CENTER -> TextAlign.Center
        SubtitleAlignment.END -> TextAlign.End
    }

    val boxModifier = if (style.hasBackgroundBox) {
        Modifier
            .clip(RoundedCornerShape(style.cornerRadiusDp.dp))
            .background(Color(style.backgroundColor).copy(alpha = style.backgroundAlpha))
            .padding(
                horizontal = style.paddingHorizontalDp.dp,
                vertical = style.paddingVerticalDp.dp
            )
    } else {
        Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    }

    val shadow = if (style.hasShadow) {
        Shadow(
            color = Color(style.shadowColor),
            offset = androidx.compose.ui.geometry.Offset(
                x = style.shadowOffsetXDp,
                y = style.shadowOffsetYDp
            ),
            blurRadius = style.shadowRadiusDp
        )
    } else null

    Box(
        modifier = modifier.then(boxModifier),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color(style.textColor).copy(alpha = style.textAlpha),
            fontFamily = fontFamily,
            fontSize = style.fontSizeSp.sp,
            fontWeight = if (style.isBold) FontWeight.Bold else FontWeight.Normal,
            fontStyle = if (style.isItalic) FontStyle.Italic else FontStyle.Normal,
            textAlign = textAlign,
            maxLines = style.maxLines,
            style = androidx.compose.ui.text.TextStyle(
                shadow = shadow,
                letterSpacing = style.letterSpacingSp.sp
            )
        )
    }
}
