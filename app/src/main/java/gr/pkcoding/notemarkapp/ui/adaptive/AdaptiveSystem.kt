package gr.pkcoding.notemarkapp.ui.adaptive

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gr.pkcoding.notemarkapp.adaptive.WindowInfo
import gr.pkcoding.notemarkapp.adaptive.rememberWindowInfo

/**
 * Generic adaptive value selector
 * Usage: val textSize = adaptiveValue(14.sp, 16.sp, 18.sp)
 */
@Composable
fun <T> adaptiveValue(
    compact: T,
    medium: T,
    expanded: T
): T {
    val windowInfo = rememberWindowInfo()
    return when (windowInfo.screenWidthInfo) {
        WindowInfo.WindowType.Compact -> compact
        WindowInfo.WindowType.Medium -> medium
        WindowInfo.WindowType.Expanded -> expanded
    }
}

/**
 * Adaptive value with orientation support
 * Usage: val columns = adaptiveValue(compact = 1, medium = 2, expanded = 3, landscapeMultiplier = 1.5f)
 */
@Composable
fun <T> adaptiveValue(
    compact: T,
    medium: T,
    expanded: T,
    landscapeModifier: (T) -> T = { it }
): T {
    val windowInfo = rememberWindowInfo()
    val baseValue = when (windowInfo.screenWidthInfo) {
        WindowInfo.WindowType.Compact -> compact
        WindowInfo.WindowType.Medium -> medium
        WindowInfo.WindowType.Expanded -> expanded
    }

    return if (windowInfo.isLandscape) {
        landscapeModifier(baseValue)
    } else {
        baseValue
    }
}

/**
 * Conditional adaptive value - shows different content based on screen size
 * Usage: adaptiveContent(compact = { PhoneLayout() }, expanded = { TabletLayout() })
 */
@Composable
fun <T> adaptiveContent(
    compact: @Composable () -> T,
    medium: @Composable () -> T = compact,
    expanded: @Composable () -> T = medium
): T {
    val windowInfo = rememberWindowInfo()
    return when (windowInfo.screenWidthInfo) {
        WindowInfo.WindowType.Compact -> compact()
        WindowInfo.WindowType.Medium -> medium()
        WindowInfo.WindowType.Expanded -> expanded()
    }
}

/**
 * Adaptive value based on condition
 * Usage: val shouldShow = adaptiveCondition { it.isTablet }
 */
@Composable
fun adaptiveCondition(
    condition: (WindowInfo) -> Boolean
): Boolean {
    val windowInfo = rememberWindowInfo()
    return condition(windowInfo)
}

// ============================================================================
// ADAPTIVE DIMENSIONS (merged από AdaptiveDimensions.kt)
// ============================================================================

@Immutable
data class AdaptiveDp(
    val compact: Dp,
    val medium: Dp,
    val expanded: Dp
) {
    @Composable
    fun value(): Dp = adaptiveValue(compact, medium, expanded)
}

// Extension for easy creation
fun adaptiveDp(compact: Dp, medium: Dp, expanded: Dp) = AdaptiveDp(compact, medium, expanded)

// Quick helpers for Dp
@Composable
fun Dp.adaptive(mediumMultiplier: Float = 1.2f, expandedMultiplier: Float = 1.5f): Dp {
    return adaptiveValue(
        compact = this,
        medium = this * mediumMultiplier,
        expanded = this * expandedMultiplier
    )
}

// ============================================================================
// QUICK ACCESS EXTENSIONS (merged από AdaptiveExtensions.kt)
// ============================================================================

// Quick access extensions
@Composable
fun Int.toDp(): Dp = adaptiveValue(
    compact = this.dp,
    medium = (this * 1.2f).dp,
    expanded = (this * 1.5f).dp
)

@Composable
fun Int.toSp(): TextUnit = adaptiveValue(
    compact = this.sp,
    medium = (this * 1.15f).sp,
    expanded = (this * 1.3f).sp
)

// Grid helpers
@Composable
fun adaptiveColumns(baseColumns: Int = 1): Int = adaptiveValue(
    compact = baseColumns,
    medium = baseColumns + 1,
    expanded = baseColumns + 2
)

@Composable
fun adaptiveRows(baseRows: Int = 1): Int = adaptiveValue(
    compact = baseRows,
    medium = baseRows,
    expanded = baseRows + 1
)