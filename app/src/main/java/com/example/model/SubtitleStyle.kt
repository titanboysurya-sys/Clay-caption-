package com.example.model

enum class SubtitleAlignment {
    START, CENTER, END
}

enum class SubtitlePosition {
    TOP, CENTER, BOTTOM
}

enum class CaptionAnimationType {
    NONE,
    FADE_IN,
    POP,
    SLIDE_UP,
    KARAOKE_HIGHLIGHT,
    WORD_REVEAL
}

data class SubtitleStyle(
    val presetId: String = "classic_white",
    val presetName: String = "Classic White",
    val fontFamilyName: String = "Outfit",
    val fontSizeSp: Float = 22f,
    val isBold: Boolean = true,
    val isItalic: Boolean = false,
    val textColor: Long = 0xFFFFFFFF,
    val textAlpha: Float = 1.0f,
    val outlineColor: Long = 0xFF000000,
    val outlineWidthDp: Float = 2.0f,
    val hasOutline: Boolean = true,
    val shadowColor: Long = 0xAA000000,
    val shadowRadiusDp: Float = 3.0f,
    val shadowOffsetXDp: Float = 1.5f,
    val shadowOffsetYDp: Float = 2.0f,
    val hasShadow: Boolean = true,
    val backgroundColor: Long = 0x80000000,
    val backgroundAlpha: Float = 0.6f,
    val hasBackgroundBox: Boolean = false,
    val cornerRadiusDp: Float = 12.0f,
    val paddingHorizontalDp: Float = 12.0f,
    val paddingVerticalDp: Float = 6.0f,
    val alignment: SubtitleAlignment = SubtitleAlignment.CENTER,
    val verticalPosition: SubtitlePosition = SubtitlePosition.BOTTOM,
    val verticalOffsetPercent: Float = 0.85f, // 0.0 (top) to 1.0 (bottom)
    val animationType: CaptionAnimationType = CaptionAnimationType.NONE,
    val maxLines: Int = 2,
    val letterSpacingSp: Float = 0.5f,
    val lineSpacingDp: Float = 4.0f
)
