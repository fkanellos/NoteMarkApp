package gr.pkcoding.notemarkapp

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import gr.pkcoding.notemarkapp.navigation.AuthGraph
import gr.pkcoding.notemarkapp.navigation.MainGraph
import gr.pkcoding.notemarkapp.navigation.NoteMarkNavigationGraph
import gr.pkcoding.notemarkapp.ui.theme.NoteMarkAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashScreen = installSplashScreen()

        enableEdgeToEdge()

        setContent {
            val viewModel: MainViewModel = viewModel()
            val isLoading by viewModel.isLoading.collectAsState()
            val isAuthenticated by viewModel.isAuthenticated.collectAsState()

            // Keep splash screen while loading
            splashScreen.setKeepOnScreenCondition { isLoading }

            // Setup exit animation
            splashScreen.setOnExitAnimationListener { screen ->
                val zoomX = ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_X,
                    0.4f,
                    0.0f
                )
                zoomX.interpolator = OvershootInterpolator()
                zoomX.duration = 500L
                zoomX.doOnEnd { screen.remove() }

                val zoomY = ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_Y,
                    0.4f,
                    0.0f
                )
                zoomY.interpolator = OvershootInterpolator()
                zoomY.duration = 500L
                zoomY.doOnEnd { screen.remove() }

                zoomX.start()
                zoomY.start()
            }

            NoteMarkAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (!isLoading) {
                        val navController = rememberNavController()

                        // Navigation 3.0 με type-safe routes
                        NoteMarkNavigationGraph(
                            navController = navController,
                            startDestination = if (isAuthenticated) MainGraph else AuthGraph
                        )
                    }
                }
            }
        }
    }
}