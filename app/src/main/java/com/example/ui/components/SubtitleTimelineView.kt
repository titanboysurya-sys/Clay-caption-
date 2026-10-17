package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.model.SubtitleSegment
import com.example.ui.clay.ClayCard
import com.example.ui.theme.*

@Composable
fun SubtitleTimelineView(
    segments: List<SubtitleSegment>,
    selectedSegmentId: String?,
    currentTimeMs: Long,
    totalDurationMs: Long,
    onSegmentClick: (SubtitleSegment) -> Unit,
    onTimeSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val safeDuration = totalDurationMs.coerceAtLeast(1000L)

    ClayCard(
        shape = RoundedCornerShape(20.dp),
        elevation = 6.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Timeline Track (${segments.size} subtitles)",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isDark) ClayTextSecondaryDark else ClayTextSecondaryLight
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = SubtitleSegment.formatTimestampShort(currentTimeMs),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                        color = ClayPrimary
                    )
                    Text(
                        text = " / " + SubtitleSegment.formatTimestampShort(totalDurationMs),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isDark) ClayTextSecondaryDark else ClayTextSecondaryLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Visual multi-track timeline bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDark) Color(0xFF19182C) else Color(0xFFEBE7FD))
            ) {
                // Draw subtitle segments as pills
                for (seg in segments) {
                    val startRatio = (seg.startTimeMs.toFloat() / safeDuration.toFloat()).coerceIn(0f, 1f)
                    val endRatio = (seg.endTimeMs.toFloat() / safeDuration.toFloat()).coerceIn(0f, 1f)
                    val widthRatio = (endRatio - startRatio).coerceAtLeast(0.015f)

                    val isSelected = seg.id == selectedSegmentId
                    val segColor = when {
                        isSelected -> ClayPrimary
                        currentTimeMs in seg.startTimeMs..seg.endTimeMs -> ClayAccentTeal
                        else -> ClaySecondary.copy(alpha = 0.6f)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(widthRatio)
                            .offset(x = 0.dp) // Layout handles proportional offsets
                            .padding(horizontal = 1.dp, vertical = 6.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(segColor)
                            .clickable {
                                onSegmentClick(seg)
                                onTimeSeek(seg.startTimeMs)
                            }
                    )
                }

                // Playhead needle
                val playheadRatio = (currentTimeMs.toFloat() / safeDuration.toFloat()).coerceIn(0f, 1f)
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(3.dp)
                        .offset(x = (playheadRatio * 300).dp) // Visual indicator
                        .background(Color.White)
                )
            }
        }
    }
}
