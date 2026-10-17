package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

class ProjectRepository(private val projectDao: ProjectDao) {

    val allProjects: Flow<List<Project>> = projectDao.getAllProjects().map { entities ->
        entities.map { entityToProject(it) }
    }

    fun getProject(id: String): Flow<Project?> = projectDao.getProjectById(id).map { entity ->
        entity?.let { entityToProject(it) }
    }

    suspend fun saveProject(project: Project) {
        val entity = projectToEntity(project)
        projectDao.insert(entity)
    }

    suspend fun updateProject(project: Project) {
        val updated = project.copy(updatedAt = System.currentTimeMillis())
        val entity = projectToEntity(updated)
        projectDao.update(entity)
    }

    suspend fun deleteProject(id: String) {
        projectDao.deleteById(id)
    }

    suspend fun duplicateProject(project: Project): Project {
        val duplicated = project.copy(
            id = java.util.UUID.randomUUID().toString(),
            title = "${project.title} (Copy)",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        saveProject(duplicated)
        return duplicated
    }

    private fun projectToEntity(project: Project): ProjectEntity {
        return ProjectEntity(
            id = project.id,
            title = project.title,
            videoUri = project.videoUri,
            videoDurationMs = project.videoDurationMs,
            videoWidth = project.videoWidth,
            videoHeight = project.videoHeight,
            sourceLanguageCode = project.sourceLanguageCode,
            targetLanguageCode = project.targetLanguageCode,
            segmentsJson = serializeSegments(project.segments),
            styleJson = serializeStyle(project.style),
            createdAt = project.createdAt,
            updatedAt = project.updatedAt,
            exportPath = project.exportPath
        )
    }

    private fun entityToProject(entity: ProjectEntity): Project {
        return Project(
            id = entity.id,
            title = entity.title,
            videoUri = entity.videoUri,
            videoDurationMs = entity.videoDurationMs,
            videoWidth = entity.videoWidth,
            videoHeight = entity.videoHeight,
            sourceLanguageCode = entity.sourceLanguageCode,
            targetLanguageCode = entity.targetLanguageCode,
            segments = deserializeSegments(entity.segmentsJson),
            style = deserializeStyle(entity.styleJson),
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            exportPath = entity.exportPath
        )
    }

    companion object {
        fun serializeSegments(segments: List<SubtitleSegment>): String {
            val array = JSONArray()
            for (seg in segments) {
                val obj = JSONObject()
                obj.put("id", seg.id)
                obj.put("start", seg.startTimeMs)
                obj.put("end", seg.endTimeMs)
                obj.put("source", seg.sourceText)
                obj.put("trans", seg.translatedText)
                obj.put("conf", seg.confidence.toDouble())
                obj.put("edited", seg.isEdited)

                val wordsArray = JSONArray()
                for (w in seg.wordTimings) {
                    val wObj = JSONObject()
                    wObj.put("word", w.word)
                    wObj.put("start", w.startMs)
                    wObj.put("end", w.endMs)
                    wordsArray.put(wObj)
                }
                obj.put("words", wordsArray)
                array.put(obj)
            }
            return array.toString()
        }

        fun deserializeSegments(json: String): List<SubtitleSegment> {
            if (json.isBlank()) return emptyList()
            val list = mutableListOf<SubtitleSegment>()
            try {
                val array = JSONArray(json)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val wordsList = mutableListOf<WordTiming>()
                    if (obj.has("words")) {
                        val wordsArray = obj.getJSONArray("words")
                        for (j in 0 until wordsArray.length()) {
                            val wObj = wordsArray.getJSONObject(j)
                            wordsList.add(
                                WordTiming(
                                    word = wObj.getString("word"),
                                    startMs = wObj.getLong("start"),
                                    endMs = wObj.getLong("end")
                                )
                            )
                        }
                    }
                    list.add(
                        SubtitleSegment(
                            id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                            startTimeMs = obj.getLong("start"),
                            endTimeMs = obj.getLong("end"),
                            sourceText = obj.getString("source"),
                            translatedText = obj.optString("trans", ""),
                            wordTimings = wordsList,
                            confidence = obj.optDouble("conf", 0.95).toFloat(),
                            isEdited = obj.optBoolean("edited", false)
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return list
        }

        fun serializeStyle(style: SubtitleStyle): String {
            val obj = JSONObject()
            obj.put("presetId", style.presetId)
            obj.put("presetName", style.presetName)
            obj.put("font", style.fontFamilyName)
            obj.put("size", style.fontSizeSp.toDouble())
            obj.put("bold", style.isBold)
            obj.put("italic", style.isItalic)
            obj.put("textColor", style.textColor)
            obj.put("textAlpha", style.textAlpha.toDouble())
            obj.put("hasOutline", style.hasOutline)
            obj.put("outlineColor", style.outlineColor)
            obj.put("outlineWidth", style.outlineWidthDp.toDouble())
            obj.put("hasShadow", style.hasShadow)
            obj.put("shadowColor", style.shadowColor)
            obj.put("shadowRadius", style.shadowRadiusDp.toDouble())
            obj.put("hasBg", style.hasBackgroundBox)
            obj.put("bgColor", style.backgroundColor)
            obj.put("bgAlpha", style.backgroundAlpha.toDouble())
            obj.put("radius", style.cornerRadiusDp.toDouble())
            obj.put("padH", style.paddingHorizontalDp.toDouble())
            obj.put("padV", style.paddingVerticalDp.toDouble())
            obj.put("align", style.alignment.name)
            obj.put("pos", style.verticalPosition.name)
            obj.put("offsetY", style.verticalOffsetPercent.toDouble())
            obj.put("anim", style.animationType.name)
            obj.put("maxLines", style.maxLines)
            return obj.toString()
        }

        fun deserializeStyle(json: String): SubtitleStyle {
            if (json.isBlank()) return SubtitleStyle()
            return try {
                val obj = JSONObject(json)
                SubtitleStyle(
                    presetId = obj.optString("presetId", "classic_white"),
                    presetName = obj.optString("presetName", "Classic White"),
                    fontFamilyName = obj.optString("font", "Outfit"),
                    fontSizeSp = obj.optDouble("size", 22.0).toFloat(),
                    isBold = obj.optBoolean("bold", true),
                    isItalic = obj.optBoolean("italic", false),
                    textColor = obj.optLong("textColor", 0xFFFFFFFF),
                    textAlpha = obj.optDouble("textAlpha", 1.0).toFloat(),
                    hasOutline = obj.optBoolean("hasOutline", true),
                    outlineColor = obj.optLong("outlineColor", 0xFF000000),
                    outlineWidthDp = obj.optDouble("outlineWidth", 2.0).toFloat(),
                    hasShadow = obj.optBoolean("hasShadow", true),
                    shadowColor = obj.optLong("shadowColor", 0xAA000000),
                    shadowRadiusDp = obj.optDouble("shadowRadius", 3.0).toFloat(),
                    hasBackgroundBox = obj.optBoolean("hasBg", false),
                    backgroundColor = obj.optLong("bgColor", 0x80000000),
                    backgroundAlpha = obj.optDouble("bgAlpha", 0.6).toFloat(),
                    cornerRadiusDp = obj.optDouble("radius", 12.0).toFloat(),
                    paddingHorizontalDp = obj.optDouble("padH", 12.0).toFloat(),
                    paddingVerticalDp = obj.optDouble("padV", 6.0).toFloat(),
                    alignment = try {
                        SubtitleAlignment.valueOf(obj.optString("align", SubtitleAlignment.CENTER.name))
                    } catch (e: Exception) { SubtitleAlignment.CENTER },
                    verticalPosition = try {
                        SubtitlePosition.valueOf(obj.optString("pos", SubtitlePosition.BOTTOM.name))
                    } catch (e: Exception) { SubtitlePosition.BOTTOM },
                    verticalOffsetPercent = obj.optDouble("offsetY", 0.85).toFloat(),
                    animationType = try {
                        CaptionAnimationType.valueOf(obj.optString("anim", CaptionAnimationType.NONE.name))
                    } catch (e: Exception) { CaptionAnimationType.NONE },
                    maxLines = obj.optInt("maxLines", 2)
                )
            } catch (e: Exception) {
                SubtitleStyle()
            }
        }
    }
}
