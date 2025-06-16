package gr.pkcoding.notemarkapp.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = BlueBase,
    secondary = WhiteBase,
    tertiary = Blue10Opacity,

    background = Surface,
    surface = SurfaceLowest,

    onPrimary = WhiteBase,
    onSecondary = WhiteBase,
    onTertiary = WhiteBase,
    onBackground = OnSurface,
    onSurface = OnSurface,

    error = Error
)

@Composable
fun NoteMarkAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
