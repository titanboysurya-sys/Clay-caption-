package com.example

import com.example.data.ProjectRepository
import com.example.engine.OfflineTranslationEngine
import com.example.model.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SubtitleEngineTest {

    @Test
    fun testTimestampFormatting() {
        val ms = 65432L // 1 minute, 5 seconds, 432 millis
        val formatted = SubtitleSegment.formatTimestamp(ms)
        assertEquals("01:05.432", formatted)

        val shortFormatted = SubtitleSegment.formatTimestampShort(ms)
        assertEquals("01:05", shortFormatted)

        val hourMs = 3665000L // 1 hour, 1 minute, 5 seconds
        assertEquals("01:01:05", SubtitleSegment.formatTimestampShort(hourMs))

        val srtFormatted = SubtitleSegment.formatTimestampSrt(ms)
        assertEquals("00:01:05,432", srtFormatted)

        val parsed = SubtitleSegment.parseTimestamp("01:05.432")
        assertEquals(65432L, parsed)

        val project = Project(
            title = "Test Clip",
            videoUri = "content://media/1",
            videoDurationMs = ms
        )
        assertEquals("01:05", project.durationFormattedShort)
    }

    @Test
    fun testSubtitleSegmentValidation() {
        val seg = SubtitleSegment(
            startTimeMs = 1000L,
            endTimeMs = 4500L,
            sourceText = "Hello world",
            translatedText = "नमस्ते दुनिया"
        )
        assertEquals(3500L, seg.durationMs)
        assertEquals("Hello world", seg.displayText(showTranslated = false))
        assertEquals("नमस्ते दुनिया", seg.displayText(showTranslated = true))
    }

    @Test
    fun testLanguageRegistry() {
        val hindi = LanguageRegistry.getByCode("hi")
        assertEquals("Hindi", hindi.name)
        assertEquals("Devanagari", hindi.script)

        val tamil = LanguageRegistry.getByCode("ta")
        assertEquals("Tamil", tamil.name)

        val searchResult = LanguageRegistry.search("tamil")
        assertTrue(searchResult.any { it.code == "ta" })
    }

    @Test
    fun testCaptionPresetsCountAndIntegrity() {
        val presets = CaptionPresetRegistry.presets
        assertTrue("Must have at least 20 distinct caption presets", presets.size >= 20)

        val classic = CaptionPresetRegistry.getById("classic_white")
        assertEquals("Classic White", classic.name)
        assertTrue(classic.style.hasOutline)

        val boldYellow = CaptionPresetRegistry.getById("bold_yellow")
        assertEquals("Bold Yellow", boldYellow.name)
        assertEquals(0xFFFFD700, boldYellow.style.textColor)
    }

    @Test
    fun testSerializationAndDeserialization() {
        val segments = listOf(
            SubtitleSegment(
                id = "seg-1",
                startTimeMs = 0L,
                endTimeMs = 2000L,
                sourceText = "First subtitle",
                translatedText = "पहला उपशीर्षक"
            ),
            SubtitleSegment(
                id = "seg-2",
                startTimeMs = 2500L,
                endTimeMs = 5000L,
                sourceText = "Second subtitle",
                translatedText = "दूसरा उपशीर्षक"
            )
        )

        val json = ProjectRepository.serializeSegments(segments)
        val deserialized = ProjectRepository.deserializeSegments(json)

        assertEquals(2, deserialized.size)
        assertEquals("First subtitle", deserialized[0].sourceText)
        assertEquals("दूसरा उपशीर्षक", deserialized[1].translatedText)
        assertEquals(2500L, deserialized[1].startTimeMs)
    }

    @Test
    fun testStyleSerialization() {
        val style = SubtitleStyle(
            presetId = "bold_yellow",
            presetName = "Bold Yellow",
            fontSizeSp = 26f,
            isBold = true,
            textColor = 0xFFFFD700
        )

        val json = ProjectRepository.serializeStyle(style)
        val deserialized = ProjectRepository.deserializeStyle(json)

        assertEquals("bold_yellow", deserialized.presetId)
        assertEquals(26f, deserialized.fontSizeSp)
        assertTrue(deserialized.isBold)
        assertEquals(0xFFFFD700, deserialized.textColor)
    }

    @Test
    fun testOfflineTranslationEngine() = runBlocking {
        val engine = OfflineTranslationEngine()
        val trans = engine.translate("नमस्ते", "hi", "ta")
        assertTrue("Should contain Tamil translation for Hindi phrase", trans.isNotBlank())
    }
}
