package gr.pkcoding.notemarkapp.adaptive

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp

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

// Quick helpers
@Composable
fun Dp.adaptive(mediumMultiplier: Float = 1.2f, expandedMultiplier: Float = 1.5f): Dp {
    return adaptiveValue(
        compact = this,
        medium = this * mediumMultiplier,
        expanded = this * expandedMultiplier
    )
}