package gr.pkcoding.notemarkapp.adaptive

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.TextUnit

@Immutable
data class AdaptiveTextUnit(
    val compact: TextUnit,
    val medium: TextUnit,
    val expanded: TextUnit
) {
    @Composable
    fun value(): TextUnit = adaptiveValue(compact, medium, expanded)
}

fun adaptiveTextUnit(compact: TextUnit, medium: TextUnit, expanded: TextUnit) =
    AdaptiveTextUnit(compact, medium, expanded)

@Composable
fun TextUnit.adaptive(mediumMultiplier: Float = 1.15f, expandedMultiplier: Float = 1.3f): TextUnit {
    return adaptiveValue(
        compact = this,
        medium = this * mediumMultiplier,
        expanded = this * expandedMultiplier
    )
}