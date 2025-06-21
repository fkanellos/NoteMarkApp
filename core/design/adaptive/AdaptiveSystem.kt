package gr.pkcoding.notemarkapp.adaptive

import androidx.compose.runtime.Composable

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