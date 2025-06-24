package gr.pkcoding.notemarkapp.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
private fun RegisterScreenPlaceholder(
    fromLogin: Boolean,
    onNavigateToLogin: (String?) -> Unit,
    onNavigateToMain: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Register Screen",
                style = MaterialTheme.typography.headlineMedium
            )
            if (fromLogin) {
                Text("(Coming from Login)")
            }
            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = onNavigateToMain) {
                Text("Register (Demo)")
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { onNavigateToLogin("demo@example.com") }) {
                Text("Already have an account?")
            }
        }
    }
}

@Composable
private fun HomeScreen(
    onNavigateToProfile: () -> Unit,
    onLogout: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🎉 Welcome to NoteMark!",
                style = MaterialTheme.typography.headlineMedium
            )
            Text("You successfully logged in!")
            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = onNavigateToProfile) {
                Text("Go to Profile")
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onLogout) {
                Text("Logout")
            }
        }
    }
}

@Composable
private fun ProfileScreen(
    userId: String?,
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Profile Screen",
                style = MaterialTheme.typography.headlineMedium
            )
            Text("Coming Soon in next milestones!")
            userId?.let {
                Text("User ID: $it")
            }
            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = onLogout) {
                Text("Logout")
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onNavigateBack) {
                Text("Back to Home")
            }
        }
    }
}