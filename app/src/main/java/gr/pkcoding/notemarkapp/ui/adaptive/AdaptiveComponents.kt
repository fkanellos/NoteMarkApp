package gr.pkcoding.notemarkapp.ui.adaptive

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import gr.pkcoding.notemarkapp.adaptive.AdaptiveDefaults

@Composable
fun AdaptiveText(
    text: String,
    style: MaterialTextStyle,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE
) {
    val adaptiveStyle = when (style) {
        MaterialTextStyle.DisplayLarge -> AdaptiveDefaults.titleXLargeStyle
        MaterialTextStyle.HeadlineLarge -> AdaptiveDefaults.titleLargeStyle
        MaterialTextStyle.TitleLarge -> AdaptiveDefaults.titleSmallStyle
        MaterialTextStyle.BodyLarge -> AdaptiveDefaults.bodyLargeStyle
        MaterialTextStyle.BodyMedium -> AdaptiveDefaults.bodyMediumStyle
        MaterialTextStyle.BodySmall -> AdaptiveDefaults.bodySmallStyle
    }

    val materialBaseStyle = when (style) {
        MaterialTextStyle.DisplayLarge -> MaterialTheme.typography.displayLarge
        MaterialTextStyle.HeadlineLarge -> MaterialTheme.typography.headlineLarge
        MaterialTextStyle.TitleLarge -> MaterialTheme.typography.titleLarge
        MaterialTextStyle.BodyLarge -> MaterialTheme.typography.bodyLarge
        MaterialTextStyle.BodyMedium -> MaterialTheme.typography.bodyMedium
        MaterialTextStyle.BodySmall -> MaterialTheme.typography.bodySmall
    }

    Text(
        text = text,
        style = materialBaseStyle.copy(
            fontSize = adaptiveStyle.fontSize.value(),
            lineHeight = adaptiveStyle.lineHeight.value().value.sp,
            fontFamily = adaptiveStyle.fontFamily,
            fontWeight = adaptiveStyle.fontWeight
        ),
        modifier = modifier,
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis
    )
}

enum class MaterialTextStyle {
    DisplayLarge,
    HeadlineLarge,
    TitleLarge,
    BodyLarge,
    BodyMedium,
    BodySmall
}