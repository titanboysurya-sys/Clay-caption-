package com.example.model

data class Language(
    val code: String,          // ISO 639-1 or BCP 47 (e.g. "hi", "ta", "en")
    val name: String,          // English display name
    val nativeName: String,    // Native script name
    val script: String,        // Latin, Devanagari, Tamil, etc.
    val isRtl: Boolean = false,
    val supportsAsr: Boolean = true,
    val supportsTranslation: Boolean = true
)

object LanguageRegistry {
    val languages = listOf(
        Language("en", "English", "English", "Latin"),
        Language("hi", "Hindi", "हिन्दी", "Devanagari"),
        Language("ta", "Tamil", "தமிழ்", "Tamil"),
        Language("te", "Telugu", "తెలుగు", "Telugu"),
        Language("bn", "Bengali", "বাংলা", "Bengali"),
        Language("mr", "Marathi", "मराठी", "Devanagari"),
        Language("gu", "Gujarati", "ગુજરાતી", "Gujarati"),
        Language("kn", "Kannada", "ಕನ್ನಡ", "Kannada"),
        Language("ml", "Malayalam", "മലയാളം", "Malayalam"),
        Language("pa", "Punjabi", "ਪੰਜਾਬੀ", "Gurmukhi"),
        Language("ur", "Urdu", "اردو", "Arabic", isRtl = true),
        Language("es", "Spanish", "Español", "Latin"),
        Language("fr", "French", "Français", "Latin"),
        Language("de", "German", "Deutsch", "Latin"),
        Language("pt", "Portuguese", "Português", "Latin"),
        Language("it", "Italian", "Italiano", "Latin"),
        Language("nl", "Dutch", "Nederlands", "Latin"),
        Language("ru", "Russian", "Русский", "Cyrillic"),
        Language("ar", "Arabic", "العربية", "Arabic", isRtl = true),
        Language("tr", "Turkish", "Türkçe", "Latin"),
        Language("id", "Indonesian", "Bahasa Indonesia", "Latin"),
        Language("vi", "Vietnamese", "Tiếng Việt", "Latin"),
        Language("th", "Thai", "ไทย", "Thai"),
        Language("ja", "Japanese", "日本語", "Japanese"),
        Language("ko", "Korean", "한국어", "Hangul"),
        Language("zh", "Chinese (Simplified)", "简体中文", "Han")
    )

    fun getByCode(code: String): Language {
        return languages.firstOrNull { it.code.equals(code, ignoreCase = true) }
            ?: Language(code, code.uppercase(), code.uppercase(), "Default")
    }

    fun search(query: String): List<Language> {
        if (query.isBlank()) return languages
        val q = query.trim().lowercase()
        return languages.filter {
            it.name.lowercase().contains(q) ||
            it.nativeName.lowercase().contains(q) ||
            it.code.lowercase().contains(q)
        }
    }
}
