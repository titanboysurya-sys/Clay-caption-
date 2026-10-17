package com.example.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * Standard Material 3 Shapes customized for Claymorphism.
 * Clay aesthetics feature smooth, exaggerated, pillowy rounded corners
 * that evoke physical clay and inflated surfaces.
 */
val ClayShapes = Shapes(
    // Badges, chips, small tags
    extraSmall = RoundedCornerShape(12.dp),
    // Small inputs, secondary buttons, chips
    small = RoundedCornerShape(16.dp),
    // Standard cards, dialogs, primary buttons
    medium = RoundedCornerShape(22.dp),
    // Large cards, hero banners, panels
    large = RoundedCornerShape(28.dp),
    // Bottom sheets, modal bottom panels, floating docks
    extraLarge = RoundedCornerShape(36.dp)
)

/**
 * Dedicated semantic shape tokens implementing the Claymorphism design system.
 * Use these tokens across components to maintain visual consistency.
 */
@Immutable
object ClayShapeTokens {
    /** Perfectly rounded circle for avatar, floating action buttons, and circular toggles */
    val pill: Shape = CircleShape

    /** Standard inflated clay card corner shape (24.dp) */
    val card: CornerBasedShape = RoundedCornerShape(24.dp)

    /** Compact clay card for lists, grid tiles, and sub-items (18.dp) */
    val cardSmall: CornerBasedShape = RoundedCornerShape(18.dp)

    /** Elevated hero card and prominent presentation containers (28.dp) */
    val cardLarge: CornerBasedShape = RoundedCornerShape(28.dp)

    /** Pill-shaped tactile action buttons (22.dp) */
    val button: CornerBasedShape = RoundedCornerShape(22.dp)

    /** Compact buttons, action chips, and segment indicators (16.dp) */
    val buttonSmall: CornerBasedShape = RoundedCornerShape(16.dp)

    /** Circular action icon buttons and state indicators */
    val iconButton: Shape = CircleShape

    /** Recessed or raised text input fields (18.dp) */
    val inputField: CornerBasedShape = RoundedCornerShape(18.dp)

    /** Interactive filter and category chips (16.dp) */
    val chip: CornerBasedShape = RoundedCornerShape(16.dp)

    /** Segmented control switches and track bars (20.dp) */
    val segmentedControl: CornerBasedShape = RoundedCornerShape(20.dp)

    /** Popup modals and confirmation dialogs (26.dp) */
    val dialog: CornerBasedShape = RoundedCornerShape(26.dp)

    /** Bottom sheet container with upper clay corners (32.dp) */
    val bottomSheet: CornerBasedShape = RoundedCornerShape(
        topStart = 32.dp,
        topEnd = 32.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )

    /** Floating navigation bar / dock container (32.dp) */
    val floatingDock: CornerBasedShape = RoundedCornerShape(32.dp)

    /** Small status badges, notification counters, and tag labels (12.dp) */
    val badge: CornerBasedShape = RoundedCornerShape(12.dp)
}
