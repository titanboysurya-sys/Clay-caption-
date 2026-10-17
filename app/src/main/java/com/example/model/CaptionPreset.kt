package com.example.model

data class CaptionPreset(
    val id: String,
    val name: String,
    val category: String,
    val description: String,
    val style: SubtitleStyle
)

object CaptionPresetRegistry {
    val presets: List<CaptionPreset> = listOf(
        // 1. Classic White
        CaptionPreset(
            id = "classic_white",
            name = "Classic White",
            category = "Standard",
            description = "Crisp white text with subtle shadow, universally readable",
            style = SubtitleStyle(
                presetId = "classic_white",
                presetName = "Classic White",
                fontFamilyName = "Outfit",
                fontSizeSp = 22f,
                textColor = 0xFFFFFFFF,
                hasOutline = true,
                outlineColor = 0xFF000000,
                outlineWidthDp = 1.5f,
                hasShadow = true,
                shadowColor = 0x99000000,
                shadowRadiusDp = 3f,
                hasBackgroundBox = false
            )
        ),
        // 2. Bold Yellow
        CaptionPreset(
            id = "bold_yellow",
            name = "Bold Yellow",
            category = "Social",
            description = "High-energy YouTube & TikTok viral video yellow",
            style = SubtitleStyle(
                presetId = "bold_yellow",
                presetName = "Bold Yellow",
                fontFamilyName = "Rubik",
                fontSizeSp = 24f,
                isBold = true,
                textColor = 0xFFFFD700,
                hasOutline = true,
                outlineColor = 0xFF000000,
                outlineWidthDp = 3.5f,
                hasShadow = true,
                shadowColor = 0xDD000000,
                shadowRadiusDp = 4f,
                hasBackgroundBox = false,
                animationType = CaptionAnimationType.POP
            )
        ),
        // 3. Clean Minimal
        CaptionPreset(
            id = "clean_minimal",
            name = "Clean Minimal",
            category = "Minimal",
            description = "Thin geometric typography with pure simplicity",
            style = SubtitleStyle(
                presetId = "clean_minimal",
                presetName = "Clean Minimal",
                fontFamilyName = "Inter",
                fontSizeSp = 19f,
                isBold = false,
                textColor = 0xFFF8FAFC,
                hasOutline = false,
                hasShadow = true,
                shadowColor = 0x88000000,
                shadowRadiusDp = 2f,
                hasBackgroundBox = false,
                letterSpacingSp = 1.0f
            )
        ),
        // 4. Modern Social
        CaptionPreset(
            id = "modern_social",
            name = "Modern Social",
            category = "Social",
            description = "Translucent dark pill with high-contrast bold white",
            style = SubtitleStyle(
                presetId = "modern_social",
                presetName = "Modern Social",
                fontFamilyName = "Outfit",
                fontSizeSp = 21f,
                isBold = true,
                textColor = 0xFFFFFFFF,
                hasOutline = false,
                hasShadow = false,
                hasBackgroundBox = true,
                backgroundColor = 0xCC111827,
                cornerRadiusDp = 14f,
                paddingHorizontalDp = 16f,
                paddingVerticalDp = 8f,
                animationType = CaptionAnimationType.SLIDE_UP
            )
        ),
        // 5. Cinematic
        CaptionPreset(
            id = "cinematic",
            name = "Cinematic",
            category = "Film",
            description = "Wide-tracked serif font, movie theater letterbox style",
            style = SubtitleStyle(
                presetId = "cinematic",
                presetName = "Cinematic",
                fontFamilyName = "Lora",
                fontSizeSp = 20f,
                isBold = false,
                textColor = 0xFFFFFBEB,
                hasOutline = true,
                outlineColor = 0xAA000000,
                outlineWidthDp = 1.0f,
                hasShadow = true,
                shadowColor = 0xAA000000,
                shadowRadiusDp = 4f,
                hasBackgroundBox = false,
                letterSpacingSp = 1.5f,
                verticalOffsetPercent = 0.90f
            )
        ),
        // 6. Soft Shadow
        CaptionPreset(
            id = "soft_shadow",
            name = "Soft Shadow",
            category = "Creative",
            description = "Dreamy glowing shadow with lavender tint",
            style = SubtitleStyle(
                presetId = "soft_shadow",
                presetName = "Soft Shadow",
                fontFamilyName = "Outfit",
                fontSizeSp = 22f,
                isBold = true,
                textColor = 0xFFFFFFFF,
                hasOutline = false,
                hasShadow = true,
                shadowColor = 0xCC7567E8,
                shadowRadiusDp = 8f,
                shadowOffsetXDp = 0f,
                shadowOffsetYDp = 3f,
                hasBackgroundBox = false
            )
        ),
        // 7. Strong Outline
        CaptionPreset(
            id = "strong_outline",
            name = "Strong Outline",
            category = "Accessibility",
            description = "Heavy 4dp black outline, readable over any bright video",
            style = SubtitleStyle(
                presetId = "strong_outline",
                presetName = "Strong Outline",
                fontFamilyName = "Rubik",
                fontSizeSp = 23f,
                isBold = true,
                textColor = 0xFFFFFFFF,
                hasOutline = true,
                outlineColor = 0xFF000000,
                outlineWidthDp = 4.0f,
                hasShadow = false,
                hasBackgroundBox = false
            )
        ),
        // 8. Rounded Label
        CaptionPreset(
            id = "rounded_label",
            name = "Rounded Label",
            category = "Claymorphic",
            description = "Curved clay badge with soft dark purple background",
            style = SubtitleStyle(
                presetId = "rounded_label",
                presetName = "Rounded Label",
                fontFamilyName = "Outfit",
                fontSizeSp = 20f,
                isBold = true,
                textColor = 0xFFF5F3FF,
                hasOutline = false,
                hasShadow = true,
                shadowColor = 0x66000000,
                shadowRadiusDp = 4f,
                hasBackgroundBox = true,
                backgroundColor = 0xD925243A,
                cornerRadiusDp = 20f,
                paddingHorizontalDp = 18f,
                paddingVerticalDp = 9f
            )
        ),
        // 9. Transparent Glass
        CaptionPreset(
            id = "transparent_glass",
            name = "Transparent Glass",
            category = "Modern",
            description = "Frosted glass container with crisp white text",
            style = SubtitleStyle(
                presetId = "transparent_glass",
                presetName = "Transparent Glass",
                fontFamilyName = "Inter",
                fontSizeSp = 20f,
                isBold = true,
                textColor = 0xFFFFFFFF,
                hasOutline = false,
                hasShadow = false,
                hasBackgroundBox = true,
                backgroundColor = 0x55FFFFFF,
                cornerRadiusDp = 12f,
                paddingHorizontalDp = 14f,
                paddingVerticalDp = 6f
            )
        ),
        // 10. Gradient Accent
        CaptionPreset(
            id = "gradient_accent",
            name = "Gradient Accent",
            category = "Creative",
            description = "Teal accent text with sleek dark backing",
            style = SubtitleStyle(
                presetId = "gradient_accent",
                presetName = "Gradient Accent",
                fontFamilyName = "Outfit",
                fontSizeSp = 22f,
                isBold = true,
                textColor = 0xFF45C9C2,
                hasOutline = false,
                hasShadow = true,
                shadowColor = 0x99000000,
                shadowRadiusDp = 4f,
                hasBackgroundBox = true,
                backgroundColor = 0xDD1E1B4B,
                cornerRadiusDp = 16f,
                paddingHorizontalDp = 16f,
                paddingVerticalDp = 8f
            )
        ),
        // 11. Karaoke Highlight
        CaptionPreset(
            id = "karaoke_highlight",
            name = "Karaoke Highlight",
            category = "Interactive",
            description = "Lively active speech highlight with warm gold accent",
            style = SubtitleStyle(
                presetId = "karaoke_highlight",
                presetName = "Karaoke Highlight",
                fontFamilyName = "Rubik",
                fontSizeSp = 24f,
                isBold = true,
                textColor = 0xFFFDE047,
                hasOutline = true,
                outlineColor = 0xFF1E293B,
                outlineWidthDp = 2.5f,
                hasShadow = true,
                shadowColor = 0x88000000,
                shadowRadiusDp = 4f,
                hasBackgroundBox = false,
                animationType = CaptionAnimationType.KARAOKE_HIGHLIGHT
            )
        ),
        // 12. Word Highlight
        CaptionPreset(
            id = "word_highlight",
            name = "Word Highlight",
            category = "Social",
            description = "Social shorts dynamic word pop highlight",
            style = SubtitleStyle(
                presetId = "word_highlight",
                presetName = "Word Highlight",
                fontFamilyName = "Outfit",
                fontSizeSp = 23f,
                isBold = true,
                textColor = 0xFF38BDF8,
                hasOutline = true,
                outlineColor = 0xFF0F172A,
                outlineWidthDp = 3.0f,
                hasShadow = true,
                shadowColor = 0xAA000000,
                shadowRadiusDp = 4f,
                hasBackgroundBox = false,
                animationType = CaptionAnimationType.WORD_REVEAL
            )
        ),
        // 13. Two-Line Cinema
        CaptionPreset(
            id = "two_line_cinema",
            name = "Two-Line Cinema",
            category = "Film",
            description = "Balanced two-line film dialog with subtle yellow tint",
            style = SubtitleStyle(
                presetId = "two_line_cinema",
                presetName = "Two-Line Cinema",
                fontFamilyName = "Inter",
                fontSizeSp = 21f,
                isBold = false,
                textColor = 0xFFFEF08A,
                hasOutline = true,
                outlineColor = 0xFF000000,
                outlineWidthDp = 1.8f,
                hasShadow = true,
                shadowColor = 0xAA000000,
                shadowRadiusDp = 3f,
                hasBackgroundBox = false,
                maxLines = 2,
                lineSpacingDp = 6f
            )
        ),
        // 14. Compact News
        CaptionPreset(
            id = "compact_news",
            name = "Compact News",
            category = "Broadcast",
            description = "High-readability uppercase broadcast banner style",
            style = SubtitleStyle(
                presetId = "compact_news",
                presetName = "Compact News",
                fontFamilyName = "Inter",
                fontSizeSp = 18f,
                isBold = true,
                textColor = 0xFFFFFFFF,
                hasOutline = false,
                hasShadow = false,
                hasBackgroundBox = true,
                backgroundColor = 0xEEB91C1C,
                cornerRadiusDp = 4f,
                paddingHorizontalDp = 14f,
                paddingVerticalDp = 6f
            )
        ),
        // 15. Podcast Style
        CaptionPreset(
            id = "podcast_style",
            name = "Podcast Style",
            category = "Social",
            description = "Centered conversational pill with clean neutral slate",
            style = SubtitleStyle(
                presetId = "podcast_style",
                presetName = "Podcast Style",
                fontFamilyName = "Outfit",
                fontSizeSp = 22f,
                isBold = true,
                textColor = 0xFFF1F5F9,
                hasOutline = false,
                hasShadow = true,
                shadowColor = 0x55000000,
                shadowRadiusDp = 4f,
                hasBackgroundBox = true,
                backgroundColor = 0xCC334155,
                cornerRadiusDp = 18f,
                paddingHorizontalDp = 18f,
                paddingVerticalDp = 8f
            )
        ),
        // 16. Documentary
        CaptionPreset(
            id = "documentary",
            name = "Documentary",
            category = "Film",
            description = "Understated elegant subtitle with fine serif detail",
            style = SubtitleStyle(
                presetId = "documentary",
                presetName = "Documentary",
                fontFamilyName = "Lora",
                fontSizeSp = 19f,
                isBold = false,
                isItalic = true,
                textColor = 0xFFF8FAFC,
                hasOutline = true,
                outlineColor = 0x88000000,
                outlineWidthDp = 1.0f,
                hasShadow = true,
                shadowColor = 0x88000000,
                shadowRadiusDp = 2f,
                hasBackgroundBox = false
            )
        ),
        // 17. Elegant Serif
        CaptionPreset(
            id = "elegant_serif",
            name = "Elegant Serif",
            category = "Editorial",
            description = "Luxury brand serif with rich cream hue",
            style = SubtitleStyle(
                presetId = "elegant_serif",
                presetName = "Elegant Serif",
                fontFamilyName = "Lora",
                fontSizeSp = 21f,
                isBold = true,
                textColor = 0xFFFEF3C7,
                hasOutline = false,
                hasShadow = true,
                shadowColor = 0xAA000000,
                shadowRadiusDp = 4f,
                hasBackgroundBox = false,
                letterSpacingSp = 1.0f
            )
        ),
        // 18. High Contrast
        CaptionPreset(
            id = "high_contrast",
            name = "High Contrast",
            category = "Accessibility",
            description = "Solid black banner with neon yellow for maximum visibility",
            style = SubtitleStyle(
                presetId = "high_contrast",
                presetName = "High Contrast",
                fontFamilyName = "Rubik",
                fontSizeSp = 22f,
                isBold = true,
                textColor = 0xFFCCFF00,
                hasOutline = false,
                hasShadow = false,
                hasBackgroundBox = true,
                backgroundColor = 0xFF000000,
                cornerRadiusDp = 6f,
                paddingHorizontalDp = 16f,
                paddingVerticalDp = 8f
            )
        ),
        // 19. Color Accent
        CaptionPreset(
            id = "color_accent",
            name = "Color Accent",
            category = "Creative",
            description = "Vibrant coral pink text with dark shadow",
            style = SubtitleStyle(
                presetId = "color_accent",
                presetName = "Color Accent",
                fontFamilyName = "Outfit",
                fontSizeSp = 22f,
                isBold = true,
                textColor = 0xFFF472B6,
                hasOutline = true,
                outlineColor = 0xFF18181B,
                outlineWidthDp = 2.0f,
                hasShadow = true,
                shadowColor = 0x99000000,
                shadowRadiusDp = 4f,
                hasBackgroundBox = false
            )
        ),
        // 20. Minimal Bottom Bar
        CaptionPreset(
            id = "minimal_bottom_bar",
            name = "Minimal Bottom Bar",
            category = "Modern",
            description = "Semi-translucent bottom cinematic strip",
            style = SubtitleStyle(
                presetId = "minimal_bottom_bar",
                presetName = "Minimal Bottom Bar",
                fontFamilyName = "Inter",
                fontSizeSp = 19f,
                isBold = false,
                textColor = 0xFFFFFFFF,
                hasOutline = false,
                hasShadow = false,
                hasBackgroundBox = true,
                backgroundColor = 0x99000000,
                cornerRadiusDp = 0f,
                paddingHorizontalDp = 24f,
                paddingVerticalDp = 10f,
                verticalOffsetPercent = 0.92f
            )
        ),
        // 21. Clay Pastel
        CaptionPreset(
            id = "clay_pastel",
            name = "Clay Pastel",
            category = "Claymorphic",
            description = "Signature ClayCaption soft purple card with inner glow",
            style = SubtitleStyle(
                presetId = "clay_pastel",
                presetName = "Clay Pastel",
                fontFamilyName = "Outfit",
                fontSizeSp = 21f,
                isBold = true,
                textColor = 0xFF25243A,
                hasOutline = false,
                hasShadow = true,
                shadowColor = 0x407567E8,
                shadowRadiusDp = 6f,
                hasBackgroundBox = true,
                backgroundColor = 0xF2F5F3FF,
                cornerRadiusDp = 22f,
                paddingHorizontalDp = 20f,
                paddingVerticalDp = 9f
            )
        ),
        // 22. Cyber Glow
        CaptionPreset(
            id = "cyber_glow",
            name = "Cyber Glow",
            category = "Creative",
            description = "Futuristic neon cyan glow on sleek dark surface",
            style = SubtitleStyle(
                presetId = "cyber_glow",
                presetName = "Cyber Glow",
                fontFamilyName = "Rubik",
                fontSizeSp = 22f,
                isBold = true,
                textColor = 0xFF38BDF8,
                hasOutline = true,
                outlineColor = 0xFF0284C7,
                outlineWidthDp = 2.0f,
                hasShadow = true,
                shadowColor = 0xFF0284C7,
                shadowRadiusDp = 8f,
                hasBackgroundBox = false,
                animationType = CaptionAnimationType.POP
            )
        )
    )

    fun getById(id: String): CaptionPreset {
        return presets.firstOrNull { it.id == id } ?: presets.first()
    }
}
