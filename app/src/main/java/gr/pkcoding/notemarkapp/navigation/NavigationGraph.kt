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
import gr.pkcoding.notemarkapp.features.auth.ui.register.RegisterScreen

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
 * @param startDestination Starting destination (usually AuthGraph or MainGraph based on auth)
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
                    onNavigateToRegister = {
                        navController.navigateToRegister()
                    },
                    onNavigateToLogin = {
                        navController.navigateToLogin()
                    },
                    onNavigateToMain = {
                        navController.navigateToMain(clearAuthStack = true)
                    },
                    onShowSnackbar = { message, isError ->
                        // TODO: Implement snackbar in Phase 2
                    }
                )
            }

            // Register Screen
            composable<RegisterRoute> { backStackEntry ->
                val registerRoute = backStackEntry.toRoute<RegisterRoute>()

                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigateToMain(clearAuthStack = true)
                    },
                    onSignInClick = {
                        navController.switchToLogin()
                    },
                    onBackClick = {
                        navController.navigateBackOrToLanding()
                    },
                    onShowSnackbar = { message, isError ->
                        // TODO: Implement snackbar in Phase 2
                    }
                )
            }

            // Login Screen
            composable<LoginRoute> { backStackEntry ->
                val loginRoute = backStackEntry.toRoute<LoginRoute>()

                LoginScreen(
                    onLoginSuccess = {
                        navController.navigateToMain(clearAuthStack = true)
                    },
                    onSignUpClick = {
                        navController.switchToRegister()
                    },
                    onBackClick = {
                        navController.navigateBackOrToLanding()
                    },
                    onShowSnackbar = { message, isError ->
                        // TODO: Implement snackbar in Phase 2
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