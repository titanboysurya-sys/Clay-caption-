package com.example.model

import java.util.Locale
import java.util.UUID

data class WordTiming(
    val word: String,
    val startMs: Long,
    val endMs: Long,
    val confidence: Float = 0.95f
)

data class SubtitleSegment(
    val id: String = UUID.randomUUID().toString(),
    val startTimeMs: Long,
    val endTimeMs: Long,
    val sourceText: String,
    val translatedText: String = "",
    val wordTimings: List<WordTiming> = emptyList(),
    val confidence: Float = 0.95f,
    val isEdited: Boolean = false
) {
    init {
        require(startTimeMs >= 0) { "Start time must be non-negative: $startTimeMs" }
        require(endTimeMs >= startTimeMs) { "End time ($endTimeMs) must be >= start time ($startTimeMs)" }
    }

    val durationMs: Long get() = endTimeMs - startTimeMs

    fun displayText(showTranslated: Boolean): String {
        return if (showTranslated && translatedText.isNotBlank()) {
            translatedText
        } else {
            sourceText
        }
    }

    companion object {
        fun formatTimestamp(ms: Long): String {
            val totalSeconds = ms / 1000
            val milliseconds = ms % 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            val hours = minutes / 60
            val remMinutes = minutes % 60

            return if (hours > 0) {
                String.format(Locale.US, "%02d:%02d:%02d.%03d", hours, remMinutes, seconds, milliseconds)
            } else {
                String.format(Locale.US, "%02d:%02d.%03d", remMinutes, seconds, milliseconds)
            }
        }

        fun formatTimestampShort(ms: Long): String {
            val totalSeconds = ms / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            val hours = minutes / 60
            val remMinutes = minutes % 60

            return if (hours > 0) {
                String.format(Locale.US, "%02d:%02d:%02d", hours, remMinutes, seconds)
            } else {
                String.format(Locale.US, "%02d:%02d", remMinutes, seconds)
            }
        }

        fun formatTimestampSrt(ms: Long): String {
            val totalSeconds = ms / 1000
            val milliseconds = ms % 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            val hours = minutes / 60
            val remMinutes = minutes % 60
            return String.format(Locale.US, "%02d:%02d:%02d,%03d", hours, remMinutes, seconds, milliseconds)
        }

        fun parseTimestamp(timeStr: String): Long? {
            return try {
                val clean = timeStr.trim().replace(',', '.')
                val parts = clean.split(":")
                when (parts.size) {
                    3 -> {
                        val hours = parts[0].toLong()
                        val minutes = parts[1].toLong()
                        val secParts = parts[2].split(".")
                        val seconds = secParts[0].toLong()
                        val millis = if (secParts.size > 1) secParts[1].padEnd(3, '0').take(3).toLong() else 0L
                        (hours * 3600 + minutes * 60 + seconds) * 1000 + millis
                    }
                    2 -> {
                        val minutes = parts[0].toLong()
                        val secParts = parts[1].split(".")
                        val seconds = secParts[0].toLong()
                        val millis = if (secParts.size > 1) secParts[1].padEnd(3, '0').take(3).toLong() else 0L
                        (minutes * 60 + seconds) * 1000 + millis
                    }
                    else -> null
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}
