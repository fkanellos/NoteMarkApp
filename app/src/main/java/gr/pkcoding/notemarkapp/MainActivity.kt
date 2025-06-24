package gr.pkcoding.notemarkapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import gr.pkcoding.notemarkapp.ui.theme.NoteMarkAppTheme
import io.ktor.client.HttpClient
import gr.pkcoding.notemarkapp.features.auth.ui.landing.LandingScreen
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var httpClient: HttpClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Log.d("MainActivity", "HttpClient injected: ${httpClient.javaClass.simpleName}")
        Log.d("MainActivity", "Ready for UI development! 🚀")

        setContent {
            NoteMarkAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NoteMarkApp()
                }
            }
        }
    }
}

@Composable
fun NoteMarkApp() {
    // Εδώ θα μπει το navigation με τα UI screens
    // Προς το παρόν δείχνουμε το Landing Screen
    LandingScreen()
}