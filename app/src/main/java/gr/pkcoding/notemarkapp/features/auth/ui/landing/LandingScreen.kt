package gr.pkcoding.notemarkapp.features.auth.ui.landing

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import gr.pkcoding.notemarkapp.R
import gr.pkcoding.notemarkapp.adaptive.adaptiveValue
import gr.pkcoding.notemarkapp.adaptive.rememberWindowInfo
import gr.pkcoding.notemarkapp.ui.adaptive.AdaptiveText
import gr.pkcoding.notemarkapp.ui.adaptive.MaterialTextStyle
import gr.pkcoding.notemarkapp.ui.theme.BlueBase
import gr.pkcoding.notemarkapp.ui.theme.NoteMarkAppTheme
import gr.pkcoding.notemarkapp.ui.theme.SurfaceLowest

@Composable
fun LandingScreen(
    onGetStartedClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    val windowInfo = rememberWindowInfo()

    if (windowInfo.isLandscape) {
        LandingScreenLandscape(
            onGetStartedClick = onGetStartedClick,
            onLoginClick = onLoginClick
        )
    } else {
        LandingScreenPortrait(
            onGetStartedClick = onGetStartedClick,
            onLoginClick = onLoginClick
        )
    }
}

@Composable
private fun LandingScreenPortrait(
    onGetStartedClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top section με το graphic
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(BlueBase),
            contentAlignment = Alignment.Center
        ) {
            LandingGraphic()
        }

        // Bottom section με UI component
        LandingUIComponent(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(SurfaceLowest)
                .padding(
                    horizontal = adaptiveValue(24.dp, 32.dp, 40.dp),
                    vertical = adaptiveValue(32.dp, 40.dp, 48.dp)
                ),
            onGetStartedClick = onGetStartedClick,
            onLoginClick = onLoginClick
        )
    }
}

@Composable
private fun LandingScreenLandscape(
    onGetStartedClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        // Left section με το graphic
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .background(BlueBase),
            contentAlignment = Alignment.Center
        ) {
            LandingGraphic()
        }

        // Right section με UI component
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(400.dp)
                .clip(RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp))
                .background(SurfaceLowest),
            contentAlignment = Alignment.Center
        ) {
            LandingUIComponent(
                modifier = Modifier.padding(
                    horizontal = adaptiveValue(24.dp, 32.dp, 40.dp),
                    vertical = adaptiveValue(32.dp, 40.dp, 48.dp)
                ),
                onGetStartedClick = onGetStartedClick,
                onLoginClick = onLoginClick
            )
        }
    }
}

@Composable
private fun LandingGraphic() {
    // Προσωρινά χρησιμοποιούμε το N logo
    // TODO: Replace με το actual graphic από τα mockups
    Image(
        painter = painterResource(id = R.drawable.n_logo),
        contentDescription = "NoteMark Graphic",
        modifier = Modifier.size(
            adaptiveValue(120.dp, 140.dp, 160.dp)
        ),
        contentScale = ContentScale.Fit
    )
}

@Composable
private fun LandingUIComponent(
    modifier: Modifier = Modifier,
    onGetStartedClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(
            adaptiveValue(24.dp, 28.dp, 32.dp)
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title και Subtitle
        Column(
            verticalArrangement = Arrangement.spacedBy(
                adaptiveValue(8.dp, 12.dp, 16.dp)
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AdaptiveText(
                text = "Your Own Collection of Notes",
                style = MaterialTextStyle.DisplayLarge,
                textAlign = TextAlign.Center
            )

            AdaptiveText(
                text = "Capture your thoughts and ideas",
                style = MaterialTextStyle.BodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        // Buttons
        Column(
            verticalArrangement = Arrangement.spacedBy(
                adaptiveValue(16.dp, 20.dp, 24.dp)
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Get Started Button (Filled)
            Button(
                onClick = onGetStartedClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(adaptiveValue(48.dp, 56.dp, 64.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BlueBase,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(
                    adaptiveValue(8.dp, 12.dp, 16.dp)
                )
            ) {
                AdaptiveText(
                    text = "Get Started",
                    style = MaterialTextStyle.BodyLarge,
                    color = Color.White
                )
            }

            // Log In Button (Outlined)
            OutlinedButton(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(adaptiveValue(48.dp, 56.dp, 64.dp)),
                border = BorderStroke(
                    width = 1.dp,
                    color = BlueBase
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = BlueBase
                ),
                shape = RoundedCornerShape(
                    adaptiveValue(8.dp, 12.dp, 16.dp)
                )
            ) {
                AdaptiveText(
                    text = "Log In",
                    style = MaterialTextStyle.BodyLarge,
                    color = BlueBase
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LandingScreenPreview() {
    NoteMarkAppTheme {
        LandingScreen()
    }
}