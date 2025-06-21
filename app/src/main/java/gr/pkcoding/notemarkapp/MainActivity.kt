package gr.pkcoding.notemarkapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dagger.hilt.android.AndroidEntryPoint
import gr.pkcoding.notemarkapp.ui.theme.NoteMarkAppTheme
import io.ktor.client.HttpClient
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var httpClient: HttpClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // NO SPLASH SCREEN FOR NOW - we'll fix it later
        // The issue is in the splash_screen_view layout XML

        Log.d("MainActivity", "HttpClient injected: ${httpClient.javaClass.simpleName}")
        Log.d("MainActivity", "Hilt setup working perfectly! 🚀")

        setContent {
            NoteMarkAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("NoteMark with Hilt! 🚀\n\nNetwork module ready!\nReady for Authentication!")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NoteMarkAppTheme {
        Greeting("Android")
    }
}