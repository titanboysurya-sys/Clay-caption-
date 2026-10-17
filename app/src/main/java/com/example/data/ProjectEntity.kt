package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val videoUri: String,
    val videoDurationMs: Long,
    val videoWidth: Int,
    val videoHeight: Int,
    val sourceLanguageCode: String,
    val targetLanguageCode: String,
    val segmentsJson: String,
    val styleJson: String,
    val createdAt: Long,
    val updatedAt: Long,
    val exportPath: String? = null
)
