package com.example.model

import java.util.UUID

data class Project(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val videoUri: String,
    val videoDurationMs: Long = 0L,
    val videoWidth: Int = 1920,
    val videoHeight: Int = 1080,
    val sourceLanguageCode: String = "en",
    val targetLanguageCode: String = "hi",
    val segments: List<SubtitleSegment> = emptyList(),
    val style: SubtitleStyle = SubtitleStyle(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val exportPath: String? = null
) {
    val durationFormatted: String
        get() = SubtitleSegment.formatTimestamp(videoDurationMs)

    val durationFormattedShort: String
        get() = SubtitleSegment.formatTimestampShort(videoDurationMs)

    val segmentCount: Int
        get() = segments.size
}
