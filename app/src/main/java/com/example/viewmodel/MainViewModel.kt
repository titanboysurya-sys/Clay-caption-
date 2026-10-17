package com.example.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.ProjectRepository
import com.example.engine.*
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

enum class Screen {
    HOME,
    VIDEO_IMPORT,
    LANGUAGE_MODEL,
    CAPTION_GENERATION,
    SUBTITLE_EDITOR,
    FONT_AND_STYLE,
    EXPORT,
    PROJECTS,
    SETTINGS
}

data class UiState(
    val currentScreen: Screen = Screen.HOME,
    val currentProject: Project? = null,
    val selectedSegment: SubtitleSegment? = null,
    val currentTimeMs: Long = 0L,
    val isPlaying: Boolean = false,
    val showTranslatedText: Boolean = false,
    // Generation State
    val isGenerating: Boolean = false,
    val generationProgress: Float = 0f,
    val generationStage: String = "",
    val generationError: String? = null,
    // Export State
    val isExporting: Boolean = false,
    val exportProgress: Float = 0f,
    val exportMessage: String = "",
    val exportedFile: File? = null,
    val exportError: String? = null,
    // Languages & Models
    val sourceLanguage: Language = LanguageRegistry.getByCode("en"),
    val targetLanguage: Language = LanguageRegistry.getByCode("hi"),
    val autoDetectSource: Boolean = false,
    val selectedSpeechModelId: String = "whisper_tiny",
    val searchQuery: String = "",
    // Preset & Typography
    val currentPresetId: String = "classic_white",
    val defaultStyle: SubtitleStyle = SubtitleStyle(
        hasBackgroundBox = true,
        backgroundAlpha = 0.65f,
        backgroundColor = 0xFF000000
    ),
    val canUndo: Boolean = false,
    val canRedo: Boolean = false
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = ProjectRepository(db.projectDao())
    private val speechEngine = OfflineSpeechRecognitionEngine()
    private val translationEngine = OfflineTranslationEngine()
    private val exportEngine = VideoExportEngine(application)
    private val sampleProvider = SampleVideoProvider(application)
    private val audioExtractor = AudioExtractor(application)

    val allProjects: StateFlow<List<Project>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var playbackJob: Job? = null
    private var generationJob: Job? = null
    private var exportJob: Job? = null

    // Undo / Redo stacks
    private val undoStack = mutableListOf<List<SubtitleSegment>>()
    private val redoStack = mutableListOf<List<SubtitleSegment>>()

    fun navigateTo(screen: Screen) {
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun selectProject(project: Project) {
        undoStack.clear()
        redoStack.clear()
        _uiState.update {
            it.copy(
                currentProject = project,
                currentScreen = Screen.SUBTITLE_EDITOR,
                sourceLanguage = LanguageRegistry.getByCode(project.sourceLanguageCode),
                targetLanguage = LanguageRegistry.getByCode(project.targetLanguageCode),
                selectedSegment = project.segments.firstOrNull(),
                currentTimeMs = 0L,
                showTranslatedText = project.segments.any { s -> s.translatedText.isNotBlank() },
                canUndo = false,
                canRedo = false
            )
        }
    }

    fun loadSampleVideoAndStart() {
        viewModelScope.launch {
            _uiState.update { it.copy(generationStage = "Loading bundled sample dialogue clip…") }
            val uri = sampleProvider.getOrCreateSampleVideo()
            importVideoUri(uri, "Sample Dialogue Project")
        }
    }

    fun importVideoUri(uri: Uri, title: String? = null) {
        viewModelScope.launch {
            val meta = audioExtractor.extractMetadata(uri)
            val projectTitle = title ?: "Video Project #${System.currentTimeMillis() % 1000}"

            val newProject = Project(
                title = projectTitle,
                videoUri = uri.toString(),
                videoDurationMs = meta.durationMs,
                videoWidth = meta.width,
                videoHeight = meta.height,
                sourceLanguageCode = _uiState.value.sourceLanguage.code,
                targetLanguageCode = _uiState.value.targetLanguage.code,
                segments = emptyList(),
                style = SubtitleStyle()
            )

            repository.saveProject(newProject)

            _uiState.update {
                it.copy(
                    currentProject = newProject,
                    currentScreen = Screen.LANGUAGE_MODEL,
                    currentTimeMs = 0L
                )
            }
        }
    }

    fun setSourceLanguage(lang: Language) {
        _uiState.update { it.copy(sourceLanguage = lang) }
        _uiState.value.currentProject?.let { p ->
            val updated = p.copy(sourceLanguageCode = lang.code)
            viewModelScope.launch { repository.updateProject(updated) }
        }
    }

    fun setTargetLanguage(lang: Language) {
        _uiState.update { it.copy(targetLanguage = lang) }
        _uiState.value.currentProject?.let { p ->
            val updated = p.copy(targetLanguageCode = lang.code)
            viewModelScope.launch { repository.updateProject(updated) }
        }
    }

    fun toggleAutoDetect(enabled: Boolean) {
        _uiState.update { it.copy(autoDetectSource = enabled) }
    }

    fun setSpeechModel(modelId: String) {
        _uiState.update { it.copy(selectedSpeechModelId = modelId) }
    }

    fun startCaptionGeneration(translateAlso: Boolean = true) {
        val project = _uiState.value.currentProject ?: return
        val uri = Uri.parse(project.videoUri)

        _uiState.update {
            it.copy(
                currentScreen = Screen.CAPTION_GENERATION,
                isGenerating = true,
                generationProgress = 0f,
                generationStage = "Starting speech recognition engine…",
                generationError = null
            )
        }

        generationJob?.cancel()
        generationJob = viewModelScope.launch {
            try {
                val sourceLang = if (_uiState.value.autoDetectSource) "en" else _uiState.value.sourceLanguage.code
                val segments = speechEngine.transcribe(
                    context = getApplication(),
                    videoUri = uri,
                    languageCode = sourceLang,
                    modelId = _uiState.value.selectedSpeechModelId
                ) { progress, stage ->
                    _uiState.update {
                        it.copy(
                            generationProgress = progress * if (translateAlso) 0.7f else 1.0f,
                            generationStage = stage
                        )
                    }
                }

                val finalSegments = if (translateAlso && segments.isNotEmpty()) {
                    _uiState.update {
                        it.copy(generationStage = "Translating captions to ${_uiState.value.targetLanguage.name}…")
                    }
                    translationEngine.translateSegments(
                        segments = segments,
                        sourceLang = sourceLang,
                        targetLang = _uiState.value.targetLanguage.code
                    ) { tProgress ->
                        _uiState.update {
                            it.copy(
                                generationProgress = 0.7f + (tProgress * 0.3f),
                                generationStage = "Translating sentence ${(tProgress * segments.size).toInt()} / ${segments.size}…"
                            )
                        }
                    }
                } else {
                    segments
                }

                val updatedProject = project.copy(
                    segments = finalSegments,
                    sourceLanguageCode = sourceLang,
                    targetLanguageCode = _uiState.value.targetLanguage.code,
                    updatedAt = System.currentTimeMillis()
                )
                repository.updateProject(updatedProject)

                undoStack.clear()
                redoStack.clear()

                _uiState.update {
                    it.copy(
                        currentProject = updatedProject,
                        isGenerating = false,
                        generationProgress = 1.0f,
                        showTranslatedText = translateAlso,
                        selectedSegment = finalSegments.firstOrNull(),
                        currentScreen = Screen.SUBTITLE_EDITOR,
                        canUndo = false,
                        canRedo = false
                    )
                }
            } catch (e: Exception) {
                if (e !is kotlinx.coroutines.CancellationException) {
                    _uiState.update {
                        it.copy(
                            isGenerating = false,
                            generationError = e.message ?: "Failed to generate captions"
                        )
                    }
                }
            }
        }
    }

    fun cancelCaptionGeneration() {
        generationJob?.cancel()
        _uiState.update {
            it.copy(
                isGenerating = false,
                currentScreen = Screen.LANGUAGE_MODEL
            )
        }
    }

    fun toggleShowTranslated() {
        _uiState.update { it.copy(showTranslatedText = !it.showTranslatedText) }
    }

    fun selectSegment(segment: SubtitleSegment) {
        _uiState.update {
            it.copy(
                selectedSegment = segment,
                currentTimeMs = segment.startTimeMs
            )
        }
    }

    fun setPlaybackPosition(ms: Long) {
        val duration = _uiState.value.currentProject?.videoDurationMs ?: 10000L
        val clamped = ms.coerceIn(0L, duration)
        _uiState.update {
            it.copy(
                currentTimeMs = clamped,
                selectedSegment = it.currentProject?.segments?.firstOrNull { s ->
                    clamped in s.startTimeMs..s.endTimeMs
                } ?: it.selectedSegment
            )
        }
    }

    fun togglePlayback() {
        val willPlay = !_uiState.value.isPlaying
        _uiState.update { it.copy(isPlaying = willPlay) }

        if (willPlay) {
            playbackJob?.cancel()
            playbackJob = viewModelScope.launch {
                val totalDuration = _uiState.value.currentProject?.videoDurationMs ?: 15000L
                while (_uiState.value.isPlaying) {
                    delay(50)
                    val next = _uiState.value.currentTimeMs + 50
                    if (next >= totalDuration) {
                        setPlaybackPosition(0L)
                        _uiState.update { it.copy(isPlaying = false) }
                        break
                    } else {
                        setPlaybackPosition(next)
                    }
                }
            }
        } else {
            playbackJob?.cancel()
        }
    }

    // Subtitle editing functions with Undo / Redo
    fun updateSegmentText(segmentId: String, newSource: String, newTrans: String) {
        saveUndoState()
        val project = _uiState.value.currentProject ?: return
        val updatedSegments = project.segments.map {
            if (it.id == segmentId) {
                it.copy(sourceText = newSource, translatedText = newTrans, isEdited = true)
            } else it
        }
        updateProjectSegments(updatedSegments)
    }

    fun updateSegmentTimings(segmentId: String, newStartMs: Long, newEndMs: Long) {
        saveUndoState()
        val project = _uiState.value.currentProject ?: return
        val updatedSegments = project.segments.map {
            if (it.id == segmentId) {
                val safeStart = newStartMs.coerceAtLeast(0L)
                val safeEnd = newEndMs.coerceAtLeast(safeStart + 200L)
                it.copy(startTimeMs = safeStart, endTimeMs = safeEnd, isEdited = true)
            } else it
        }.sortedBy { it.startTimeMs }
        updateProjectSegments(updatedSegments)
    }

    fun addSegmentAtCurrentTime() {
        saveUndoState()
        val project = _uiState.value.currentProject ?: return
        val start = _uiState.value.currentTimeMs
        val end = (start + 2500L).coerceAtMost(project.videoDurationMs)

        val newSeg = SubtitleSegment(
            startTimeMs = start,
            endTimeMs = end,
            sourceText = "New subtitle text",
            translatedText = "New subtitle translated"
        )
        val updated = (project.segments + newSeg).sortedBy { it.startTimeMs }
        updateProjectSegments(updated)
        _uiState.update { it.copy(selectedSegment = newSeg) }
    }

    fun deleteSegment(segmentId: String) {
        saveUndoState()
        val project = _uiState.value.currentProject ?: return
        val updated = project.segments.filterNot { it.id == segmentId }
        updateProjectSegments(updated)
        _uiState.update { it.copy(selectedSegment = updated.firstOrNull()) }
    }

    fun splitSegmentAtCurrentTime(segmentId: String) {
        val project = _uiState.value.currentProject ?: return
        val seg = project.segments.firstOrNull { it.id == segmentId } ?: return
        val playhead = _uiState.value.currentTimeMs

        if (playhead in (seg.startTimeMs + 200L)..(seg.endTimeMs - 200L)) {
            saveUndoState()
            val seg1 = seg.copy(endTimeMs = playhead)
            val seg2 = SubtitleSegment(
                startTimeMs = playhead,
                endTimeMs = seg.endTimeMs,
                sourceText = "Part 2",
                translatedText = "Part 2"
            )
            val updated = project.segments.filterNot { it.id == segmentId } + listOf(seg1, seg2)
            updateProjectSegments(updated.sortedBy { it.startTimeMs })
        }
    }

    fun mergeSegmentWithNext(segmentId: String) {
        val project = _uiState.value.currentProject ?: return
        val idx = project.segments.indexOfFirst { it.id == segmentId }
        if (idx >= 0 && idx < project.segments.size - 1) {
            saveUndoState()
            val current = project.segments[idx]
            val next = project.segments[idx + 1]

            val merged = current.copy(
                endTimeMs = next.endTimeMs,
                sourceText = "${current.sourceText} ${next.sourceText}",
                translatedText = "${current.translatedText} ${next.translatedText}".trim()
            )
            val updated = project.segments.toMutableList()
            updated.removeAt(idx + 1)
            updated[idx] = merged
            updateProjectSegments(updated)
            _uiState.update { it.copy(selectedSegment = merged) }
        }
    }

    fun applyPreset(preset: CaptionPreset) {
        val project = _uiState.value.currentProject ?: return
        val updatedStyle = preset.style
        val updatedProject = project.copy(style = updatedStyle)
        _uiState.update { it.copy(currentProject = updatedProject, currentPresetId = preset.id) }
        viewModelScope.launch { repository.updateProject(updatedProject) }
    }

    fun updateStyle(newStyle: SubtitleStyle) {
        val project = _uiState.value.currentProject ?: return
        val updatedProject = project.copy(style = newStyle)
        _uiState.update { it.copy(currentProject = updatedProject, defaultStyle = newStyle) }
        viewModelScope.launch { repository.updateProject(updatedProject) }
    }

    fun updateDefaultStyle(newStyle: SubtitleStyle) {
        _uiState.update { it.copy(defaultStyle = newStyle) }
        val project = _uiState.value.currentProject
        if (project != null) {
            updateStyle(newStyle)
        }
    }

    private fun saveUndoState() {
        _uiState.value.currentProject?.let {
            undoStack.add(it.segments)
            redoStack.clear()
            _uiState.update { s -> s.copy(canUndo = true, canRedo = false) }
        }
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            val project = _uiState.value.currentProject ?: return
            redoStack.add(project.segments)
            val previous = undoStack.removeAt(undoStack.size - 1)
            updateProjectSegments(previous, saveUndo = false)
            _uiState.update {
                it.copy(
                    canUndo = undoStack.isNotEmpty(),
                    canRedo = true
                )
            }
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            val project = _uiState.value.currentProject ?: return
            undoStack.add(project.segments)
            val next = redoStack.removeAt(redoStack.size - 1)
            updateProjectSegments(next, saveUndo = false)
            _uiState.update {
                it.copy(
                    canUndo = true,
                    canRedo = redoStack.isNotEmpty()
                )
            }
        }
    }

    private fun updateProjectSegments(segments: List<SubtitleSegment>, saveUndo: Boolean = true) {
        val project = _uiState.value.currentProject ?: return
        val updated = project.copy(segments = segments, updatedAt = System.currentTimeMillis())
        _uiState.update { it.copy(currentProject = updated) }
        viewModelScope.launch { repository.updateProject(updated) }
    }

    fun startExport(config: ExportConfig) {
        val project = _uiState.value.currentProject ?: return
        _uiState.update {
            it.copy(
                isExporting = true,
                exportProgress = 0f,
                exportMessage = "Initializing export engine…",
                exportError = null,
                exportedFile = null
            )
        }

        exportJob?.cancel()
        exportJob = viewModelScope.launch {
            try {
                if (config.format == ExportFormat.BURNED_IN_MP4) {
                    val file = exportEngine.exportBurnedInVideo(
                        sourceVideoUri = Uri.parse(project.videoUri),
                        segments = project.segments,
                        style = project.style,
                        config = config,
                        title = project.title
                    ) { progress, message ->
                        _uiState.update {
                            it.copy(exportProgress = progress, exportMessage = message)
                        }
                    }
                    val updated = project.copy(exportPath = file.absolutePath)
                    repository.updateProject(updated)
                    _uiState.update {
                        it.copy(
                            isExporting = false,
                            exportProgress = 1f,
                            exportedFile = file,
                            currentProject = updated
                        )
                    }
                } else {
                    val file = exportEngine.exportSubtitles(
                        segments = project.segments,
                        useTranslatedText = config.useTranslatedText,
                        format = config.format,
                        title = project.title
                    )
                    _uiState.update {
                        it.copy(
                            isExporting = false,
                            exportProgress = 1f,
                            exportedFile = file
                        )
                    }
                }
            } catch (e: Exception) {
                if (e !is kotlinx.coroutines.CancellationException) {
                    _uiState.update {
                        it.copy(
                            isExporting = false,
                            exportError = e.message ?: "Export failed"
                        )
                    }
                }
            }
        }
    }

    fun cancelExport() {
        exportJob?.cancel()
        _uiState.update { it.copy(isExporting = false) }
    }

    fun duplicateProject(project: Project) {
        viewModelScope.launch {
            val dup = repository.duplicateProject(project)
            selectProject(dup)
        }
    }

    fun deleteProject(id: String) {
        viewModelScope.launch {
            repository.deleteProject(id)
            if (_uiState.value.currentProject?.id == id) {
                _uiState.update { it.copy(currentProject = null, currentScreen = Screen.HOME) }
            }
        }
    }
}
