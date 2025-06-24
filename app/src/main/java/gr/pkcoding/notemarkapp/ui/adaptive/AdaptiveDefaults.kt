package gr.pkcoding.notemarkapp.adaptive

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gr.pkcoding.notemarkapp.ui.adaptive.adaptiveDp

object AdaptiveDefaults {

    // FONTS
    val spaceGroteskFont = FontFamily.Default // Replace with actual Space Grotesk font
    val interFont = FontFamily.Default // Replace with actual Inter font

    // TYPOGRAPHY - Based on your specs

    // TITLES (Space Grotesk)
    val titleXLarge = adaptiveTextUnit(
        compact = 32.sp,    // Μικρότερο για phones
        medium = 36.sp,     // Κανονικό για medium
        expanded = 40.sp    // Μεγαλύτερο για tablets
    )

    val titleLarge = adaptiveTextUnit(
        compact = 28.sp,    // Μικρότερο για phones
        medium = 32.sp,     // Κανονικό για medium
        expanded = 36.sp    // Μεγαλύτερο για tablets
    )

    val titleSmall = adaptiveTextUnit(
        compact = 15.sp,    // Μικρότερο για phones
        medium = 17.sp,     // Κανονικό για medium
        expanded = 24.sp    // Μεγαλύτερο για tablets
    )

    // BODY TEXT (Inter)
    val bodyLarge = adaptiveTextUnit(
        compact = 15.sp,    // Μικρότερο για phones
        medium = 17.sp,     // Κανονικό για medium
        expanded = 24.sp    // Μεγαλύτερο για tablets
    )

    val bodyMedium = adaptiveTextUnit(
        compact = 13.sp,    // Μικρότερο για phones
        medium = 15.sp,     // Κανονικό για medium
        expanded = 20.sp    // Μεγαλύτερο για tablets
    )

    val bodySmall = adaptiveTextUnit(
        compact = 13.sp,    // Μικρότερο για phones
        medium = 15.sp,     // Κανονικό για medium
        expanded = 20.sp    // Μεγαλύτερο για tablets
    )

    // LINE HEIGHTS - Based on your specs
    val titleXLargeLineHeight = adaptiveTextUnit(36.sp, 40.sp, 44.sp)
    val titleLargeLineHeight = adaptiveTextUnit(32.sp, 36.sp, 40.sp)
    val titleSmallLineHeight = adaptiveTextUnit(20.sp, 24.sp, 28.sp)

    val bodyLargeLineHeight = adaptiveTextUnit(20.sp, 24.sp, 28.sp)
    val bodyMediumLineHeight = adaptiveTextUnit(18.sp, 20.sp, 24.sp)
    val bodySmallLineHeight = adaptiveTextUnit(18.sp, 20.sp, 24.sp)

    // FONT WEIGHTS
    object FontWeights {
        val titleBold = FontWeight.Bold      // For X-Large & Large titles
        val titleMedium = FontWeight.Medium  // For Small titles
        val bodyRegular = FontWeight.Normal  // For body text
        val bodyMedium = FontWeight.Medium   // For medium body text
    }

    // COMPLETE TEXT STYLES - Ready to use
    data class AdaptiveTextStyle(
        val fontSize: AdaptiveTextUnit,
        val lineHeight: AdaptiveTextUnit,
        val fontFamily: FontFamily,
        val fontWeight: FontWeight
    )

    // TITLE STYLES
    val titleXLargeStyle = AdaptiveTextStyle(
        fontSize = titleXLarge,
        lineHeight = titleXLargeLineHeight,
        fontFamily = spaceGroteskFont,
        fontWeight = FontWeights.titleBold
    )

    val titleLargeStyle = AdaptiveTextStyle(
        fontSize = titleLarge,
        lineHeight = titleLargeLineHeight,
        fontFamily = spaceGroteskFont,
        fontWeight = FontWeights.titleBold
    )

    val titleSmallStyle = AdaptiveTextStyle(
        fontSize = titleSmall,
        lineHeight = titleSmallLineHeight,
        fontFamily = spaceGroteskFont,
        fontWeight = FontWeights.titleMedium
    )

    // BODY STYLES
    val bodyLargeStyle = AdaptiveTextStyle(
        fontSize = bodyLarge,
        lineHeight = bodyLargeLineHeight,
        fontFamily = interFont,
        fontWeight = FontWeights.bodyRegular
    )

    val bodyMediumStyle = AdaptiveTextStyle(
        fontSize = bodyMedium,
        lineHeight = bodyMediumLineHeight,
        fontFamily = interFont,
        fontWeight = FontWeights.bodyMedium
    )

    val bodySmallStyle = AdaptiveTextStyle(
        fontSize = bodySmall,
        lineHeight = bodySmallLineHeight,
        fontFamily = interFont,
        fontWeight = FontWeights.bodyRegular
    )

    // SPACING
    val spaceXs = adaptiveDp(4.dp, 6.dp, 8.dp)
    val spaceSm = adaptiveDp(8.dp, 12.dp, 16.dp)
    val spaceMd = adaptiveDp(16.dp, 20.dp, 24.dp)
    val spaceLg = adaptiveDp(24.dp, 32.dp, 40.dp)
    val spaceXl = adaptiveDp(32.dp, 48.dp, 64.dp)
    val space2xl = adaptiveDp(48.dp, 64.dp, 96.dp)

    // COMPONENT SIZES
    val buttonHeight = adaptiveDp(48.dp, 56.dp, 64.dp)
    val buttonMinWidth = adaptiveDp(120.dp, 140.dp, 160.dp)

    val iconSizeSmall = adaptiveDp(16.dp, 20.dp, 24.dp)
    val iconSizeMedium = adaptiveDp(24.dp, 28.dp, 32.dp)
    val iconSizeLarge = adaptiveDp(32.dp, 40.dp, 48.dp)

    val cornerRadiusSmall = adaptiveDp(4.dp, 6.dp, 8.dp)
    val cornerRadiusMedium = adaptiveDp(8.dp, 12.dp, 16.dp)
    val cornerRadiusLarge = adaptiveDp(16.dp, 20.dp, 24.dp)

    val elevationSmall = adaptiveDp(2.dp, 4.dp, 6.dp)
    val elevationMedium = adaptiveDp(4.dp, 6.dp, 8.dp)
    val elevationLarge = adaptiveDp(8.dp, 12.dp, 16.dp)
}