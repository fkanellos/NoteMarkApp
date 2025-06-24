package gr.pkcoding.notemarkapp.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import gr.pkcoding.notemarkapp.features.auth.ui.landing.LandingScreen
import gr.pkcoding.notemarkapp.features.auth.ui.login.LoginScreen
import gr.pkcoding.notemarkapp.ui.components.RegisterScreen

/**
 * Main navigation graph for the NoteMark app using Navigation 3.0
 *
 * NOTE: This does NOT include a SplashRoute - we use Android's native splash screen
 * for initial app loading. This navigation graph starts with LandingRoute after
 * the native splash screen determines authentication status.
 *
 * Features:
 * - Type-safe navigation with @Serializable routes
 * - Nested navigation graphs for better organization
 * - Smooth animations between screens
 * - Proper back stack management
 * - ViewModel scoping per navigation graph
 *
 * @param navController Navigation controller for the app
 * @param modifier Modifier for the NavHost
 * @param startDestination Starting destination (usually LandingRoute or HomeRoute based on auth)
 */

@Composable
fun NoteMarkNavigationGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: Any = AuthGraph
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            )
        }
    ) {
        // Authentication Flow Graph
        navigation<AuthGraph>(
            startDestination = LandingRoute
        ) {
            // Landing Screen
            composable<LandingRoute>(
                enterTransition = {
                    fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(300))
                }
            ) {
                LandingScreen(
                    onGetStartedClick = {
                        navController.navigateToRegister()
                    },
                    onLoginClick = {
                        navController.navigateToLogin()
                    }
                )
            }

            composable<RegisterRoute> { backStackEntry ->
                val registerRoute = backStackEntry.toRoute<RegisterRoute>()

                RegisterScreen(
                    onRegisterClick = { username, email, password, confirmPassword ->
                        // TODO: Call AuthViewModel για registration
                        // For now, navigate to main (placeholder)
                        navController.navigateToMain(clearAuthStack = true)
                    },
                    onSignInClick = {
                        navController.switchToLogin()
                    },
                    onBackClick = {
                        navController.navigateBackOrToLanding()
                    }
                )
            }

// Update το existing login composable να χρησιμοποιεί το actual LoginScreen:
            composable<LoginRoute> { backStackEntry ->
                val loginRoute = backStackEntry.toRoute<LoginRoute>()

                LoginScreen(
                    onLoginClick = { email, password ->
                        // TODO: Call AuthViewModel για authentication
                        // For now, navigate to main (placeholder)
                        navController.navigateToMain(clearAuthStack = true)
                    },
                    onSignUpClick = {
                        navController.switchToRegister()
                    },
                    onBackClick = {
                        navController.navigateBackOrToLanding()
                    }
                )
            }
        }

        // Main Application Graph
        navigation<MainGraph>(
            startDestination = HomeRoute
        ) {
            // Home Screen
            composable<HomeRoute>(
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(300)
                    )
                }
            ) {
                HomeScreen(
                    onNavigateToProfile = {
                        navController.navigateToProfile()
                    },
                    onLogout = {
                        navController.handleLogout()
                    }
                )
            }

            // Profile Screen
            composable<ProfileRoute> { backStackEntry ->
                val profileRoute = backStackEntry.toRoute<ProfileRoute>()

                ProfileScreen(
                    userId = profileRoute.userId,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onLogout = {
                        navController.handleLogout()
                    }
                )
            }
        }
    }
}


/**
 * Placeholder screens (to be replaced with actual implementations)
 */
@Composable
private fun LandingScreenPlaceholder(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome to NoteMark",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = onNavigateToRegister) {
                Text("Get Started")
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onNavigateToLogin) {
                Text("Log In")
            }
        }
    }
}

@Composable
private fun LoginScreenPlaceholder(
    fromRegister: Boolean,
    prefillEmail: String?,
    onNavigateToRegister: () -> Unit,
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
                text = "Login Screen",
                style = MaterialTheme.typography.headlineMedium
            )
            if (fromRegister) {
                Text("(Coming from Register)")
            }
            prefillEmail?.let { email ->
                Text("Prefill: $email")
            }
            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = onNavigateToMain) {
                Text("Login (Demo)")
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onNavigateToRegister) {
                Text("Don't have an account?")
            }
        }
    }
}

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
                text = "Welcome to NoteMark!",
                style = MaterialTheme.typography.headlineMedium
            )
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
        Text(
            text = "Profile Screen (Coming Soon)",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}