package gr.pkcoding.notemarkapp.feature.auth.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import gr.pkcoding.notemarkapp.feature.auth.presentation.model.AuthEffect
import gr.pkcoding.notemarkapp.feature.auth.presentation.model.HapticFeedbackType
import gr.pkcoding.notemarkapp.navigation.handleAuthSuccess
import gr.pkcoding.notemarkapp.navigation.handleLogout
import gr.pkcoding.notemarkapp.navigation.handleSessionExpired
import gr.pkcoding.notemarkapp.navigation.isInMainApp
import gr.pkcoding.notemarkapp.navigation.navigateToLanding
import gr.pkcoding.notemarkapp.navigation.navigateToLogin
import gr.pkcoding.notemarkapp.navigation.navigateToMain
import gr.pkcoding.notemarkapp.navigation.navigateToRegister
import kotlinx.coroutines.flow.Flow

/**
 * Integration between AuthViewModel effects and Navigation 3.0
 *
 * This composable handles all navigation-related effects from the AuthViewModel
 * and translates them into proper Navigation 3.0 actions.
 *
 * Benefits:
 * - Centralized navigation logic
 * - Type-safe navigation with proper back stack management
 * - Clean separation between business logic and navigation
 * - Consistent error handling and recovery
 *
 * @param navController Navigation controller for the app
 * @param effects Flow of AuthEffects from AuthViewModel
 * @param onShowSnackbar Callback for showing snackbar messages
 * @param onShowDialog Callback for showing error dialogs
 * @param onTriggerHapticFeedback Callback for haptic feedback
 */
@Composable
fun AuthNavigationEffectHandler(
    navController: NavController,
    effects: Flow<AuthEffect>,
    onShowSnackbar: (String, String?, Boolean) -> Unit = { _, _, _ -> },
    onShowDialog: (String, String, Boolean) -> Unit = { _, _, _ -> },
    onTriggerHapticFeedback: (HapticFeedbackType) -> Unit = { _ -> }
) {
    LaunchedEffect(navController) {
        effects.collect { effect ->
            when (effect) {
                // Navigation Effects
                is AuthEffect.NavigateToMain -> {
                    navController.navigateToMain(clearAuthStack = effect.clearBackStack)
                }

                is AuthEffect.NavigateToLogin -> {
                    navController.navigateToLogin()
                }

                is AuthEffect.NavigateToRegister -> {
                    navController.navigateToRegister()
                }

                is AuthEffect.NavigateToLanding -> {
                    navController.navigateToLanding(clearBackStack = true)
                }

                is AuthEffect.NavigateBack -> {
                    if (!navController.popBackStack()) {
                        // If no back stack, go to landing
                        navController.navigateToLanding(clearBackStack = true)
                    }
                }

                // UI Effects
                is AuthEffect.ShowSnackbar -> {
                    onShowSnackbar(effect.message, effect.actionLabel, effect.isError)
                }

                is AuthEffect.ShowErrorDialog -> {
                    onShowDialog(effect.title, effect.message, effect.retryable)
                }

                is AuthEffect.ShowSuccessMessage -> {
                    onShowSnackbar(effect.message, null, false)
                }

                // Form Effects (handled by UI directly, no navigation needed)
                is AuthEffect.FocusField -> {
                    // This will be handled by the UI screens directly
                    // No navigation action needed
                }

                is AuthEffect.ClearForm -> {
                    // This will be handled by the UI screens directly
                    // No navigation action needed
                }

                // Haptic Effects
                is AuthEffect.TriggerHapticFeedback -> {
                    onTriggerHapticFeedback(effect.type)
                }
            }
        }
    }
}

/**
 * Helper composable for handling session-related navigation
 *
 * This handles authentication state changes from external sources
 * (token expiration, logout from other screens, etc.)
 *
 * @param navController Navigation controller
 * @param isAuthenticated Current authentication state
 * @param onSessionExpired Callback when session expires
 */
@Composable
fun SessionNavigationHandler(
    navController: NavController,
    isAuthenticated: Boolean,
    onSessionExpired: () -> Unit = {}
) {
    LaunchedEffect(isAuthenticated) {
        if (!isAuthenticated && navController.isInMainApp) {
            // User was in main app but session expired
            onSessionExpired()
            navController.handleSessionExpired()
        }
    }
}

/**
 * Navigation effect handler specifically for splash screen
 *
 * @param navController Navigation controller
 * @param checkAuthResult Result of authentication check
 */
@Composable
fun SplashNavigationHandler(
    navController: NavController,
    checkAuthResult: AuthCheckResult?
) {
    LaunchedEffect(checkAuthResult) {
        when (checkAuthResult) {
            is AuthCheckResult.Authenticated -> {
                navController.navigateToMain(clearAuthStack = true)
            }
            is AuthCheckResult.NotAuthenticated -> {
                navController.navigateToLanding(clearBackStack = true)
            }
            is AuthCheckResult.Error -> {
                // On error, default to not authenticated
                navController.navigateToLanding(clearBackStack = true)
            }
            null -> {
                // Still checking, do nothing
            }
        }
    }
}

/**
 * Result types for authentication check
 */
sealed class AuthCheckResult {
    object Authenticated : AuthCheckResult()
    object NotAuthenticated : AuthCheckResult()
    data class Error(val message: String) : AuthCheckResult()
}