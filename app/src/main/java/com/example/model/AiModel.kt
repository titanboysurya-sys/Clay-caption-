package com.example.model

enum class ModelType {
    SPEECH_RECOGNITION,
    TRANSLATION
}

enum class ModelStatus {
    AVAILABLE_OFFLINE,
    DOWNLOADING,
    READY_TO_DOWNLOAD,
    INSTALLED
}

data class AiModelInfo(
    val id: String,
    val name: String,
    val type: ModelType,
    val description: String,
    val supportedLanguages: List<String>,
    val sizeMb: Int,
    val ramRequiredMb: Int,
    val license: String,
    val provider: String,
    val status: ModelStatus = ModelStatus.INSTALLED,
    val isDefault: Boolean = false,
    val isBundled: Boolean = true
)

object ModelCatalog {
    val speechModels = listOf(
        AiModelInfo(
            id = "whisper_tiny",
            name = "Whisper Tiny Mobile",
            type = ModelType.SPEECH_RECOGNITION,
            description = "Fastest on-device model, 99+ languages, optimized for mobile CPU/NNAPI",
            supportedLanguages = listOf("en", "hi", "ta", "te", "es", "fr", "de", "ar", "ja", "ru"),
            sizeMb = 39,
            ramRequiredMb = 250,
            license = "MIT License (Commercial Friendly)",
            provider = "OpenAI / whisper.cpp",
            status = ModelStatus.INSTALLED,
            isDefault = true,
            isBundled = true
        ),
        AiModelInfo(
            id = "whisper_base",
            name = "Whisper Base Multilingual",
            type = ModelType.SPEECH_RECOGNITION,
            description = "Higher accuracy for noisy speech and regional accents across 99+ languages",
            supportedLanguages = listOf("en", "hi", "ta", "te", "bn", "mr", "gu", "es", "fr", "de", "ar", "ja"),
            sizeMb = 73,
            ramRequiredMb = 480,
            license = "MIT License (Commercial Friendly)",
            provider = "OpenAI / whisper.cpp",
            status = ModelStatus.READY_TO_DOWNLOAD,
            isDefault = false,
            isBundled = false
        ),
        AiModelInfo(
            id = "android_system_asr",
            name = "Android On-Device Speech Recognizer",
            type = ModelType.SPEECH_RECOGNITION,
            description = "Uses Android OS native offline speech recognition engine without extra storage",
            supportedLanguages = listOf("en", "hi", "es", "fr", "de", "ja", "zh"),
            sizeMb = 0,
            ramRequiredMb = 120,
            license = "Android Open Source Project (AOSP)",
            provider = "Android System Framework",
            status = ModelStatus.INSTALLED,
            isDefault = false,
            isBundled = true
        )
    )

    val translationModels = listOf(
        AiModelInfo(
            id = "nllb_distilled",
            name = "NLLB-200 Distilled Mobile",
            type = ModelType.TRANSLATION,
            description = "High-quality direct neural translation across 200 international languages",
            supportedLanguages = listOf("en", "hi", "ta", "te", "bn", "mr", "gu", "kn", "ml", "pa", "ur", "es", "fr", "de", "pt", "it", "ar", "ja", "ko", "zh"),
            sizeMb = 128,
            ramRequiredMb = 420,
            license = "CC-BY-NC 4.0 / Meta Research",
            provider = "No Language Left Behind",
            status = ModelStatus.INSTALLED,
            isDefault = true,
            isBundled = true
        ),
        AiModelInfo(
            id = "fast_phrase_engine",
            name = "Compact Offline Linguistic Engine",
            type = ModelType.TRANSLATION,
            description = "Instant zero-RAM rule-based & phrase translation for common dialogue and vocabulary",
            supportedLanguages = listOf("en", "hi", "ta", "te", "es", "fr", "de", "ar", "ja", "ru"),
            sizeMb = 8,
            ramRequiredMb = 45,
            license = "Apache 2.0 (Open Source)",
            provider = "ClayCaption Embedded",
            status = ModelStatus.INSTALLED,
            isDefault = false,
            isBundled = true
        )
    )

    fun getAllModels(): List<AiModelInfo> = speechModels + translationModels
}
