package gr.pkcoding.notemarkapp.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder

/**
 * Extension functions for type-safe navigation with Navigation 3.0
 *
 * These extensions provide convenient methods for common navigation patterns
 * while maintaining type safety and proper back stack management.
 */

/**
 * Navigate to login screen with optional parameters
 */
fun NavController.navigateToLogin(
    fromRegister: Boolean = false,
    prefillEmail: String? = null,
    clearBackStack: Boolean = false
) {
    navigate(LoginRoute(fromRegister, prefillEmail)) {
        if (clearBackStack) {
            popUpTo(0) { inclusive = true }
        }
        // Avoid multiple copies of login screen
        launchSingleTop = true
    }
}

/**
 * Navigate to register screen with optional parameters
 */
fun NavController.navigateToRegister(
    fromLogin: Boolean = false,
    clearBackStack: Boolean = false
) {
    navigate(RegisterRoute(fromLogin)) {
        if (clearBackStack) {
            popUpTo(0) { inclusive = true }
        }
        // Avoid multiple copies of register screen
        launchSingleTop = true
    }
}

/**
 * Navigate to main app after successful authentication
 */
fun NavController.navigateToMain(clearAuthStack: Boolean = true) {
    navigate(HomeRoute) {
        if (clearAuthStack) {
            // Clear entire auth stack when entering main app
            popUpTo(AuthGraph) { inclusive = true }
        }
        // Prevent going back to auth flow
        launchSingleTop = true
    }
}

/**
 * Navigate to landing screen (welcome/onboarding)
 */
fun NavController.navigateToLanding(clearBackStack: Boolean = false) {
    navigate(LandingRoute) {
        if (clearBackStack) {
            popUpTo(0) { inclusive = true }
        }
        launchSingleTop = true
    }
}

/**
 * Navigate to profile screen
 */
fun NavController.navigateToProfile(userId: String? = null) {
    navigate(ProfileRoute(userId)) {
        launchSingleTop = true
    }
}

/**
 * Handle logout navigation - clear everything and go to landing
 */
fun NavController.handleLogout() {
    navigate(LandingRoute) {
        // Clear entire back stack on logout
        popUpTo(0) { inclusive = true }
        launchSingleTop = true
    }
}

/**
 * Navigate back with fallback to landing if no back stack
 */
fun NavController.navigateBackOrToLanding() {
    if (!popBackStack()) {
        navigateToLanding(clearBackStack = true)
    }
}

/**
 * Switch between login and register screens (maintains single instance)
 */
fun NavController.switchToLogin(prefillEmail: String? = null) {
    navigate(LoginRoute(fromRegister = true, prefillEmail = prefillEmail)) {
        // Remove register screen from back stack
        popUpTo<RegisterRoute> { inclusive = true }
        launchSingleTop = true
    }
}

/**
 * Switch between register and login screens (maintains single instance)
 */
fun NavController.switchToRegister() {
    navigate(RegisterRoute(fromLogin = true)) {
        // Remove login screen from back stack
        popUpTo<LoginRoute> { inclusive = true }
        launchSingleTop = true
    }
}

/**
 * Handle authentication success navigation based on state
 */
fun NavController.handleAuthSuccess(isNewUser: Boolean) {
    navigate(HomeRoute) {
        // Clear auth stack
        popUpTo(AuthGraph) { inclusive = true }
        launchSingleTop = true
    }
}

/**
 * Handle token expiration or forced logout
 */
fun NavController.handleSessionExpired() {
    navigate(LandingRoute) {
        // Clear everything and start fresh
        popUpTo(0) { inclusive = true }
        launchSingleTop = true
    }
}

/**
 * Check if we're currently in auth flow
 */
val NavController.isInAuthFlow: Boolean
    get() = currentDestination?.route?.let { route ->
        route.contains("Splash") ||
                route.contains("Landing") ||
                route.contains("Login") ||
                route.contains("Register")
    } ?: false

/**
 * Check if we're currently in main app
 */
val NavController.isInMainApp: Boolean
    get() = currentDestination?.route?.let { route ->
        route.contains("Home") ||
                route.contains("Profile")
    } ?: false

/**
 * Get current route name for analytics or debugging
 */
val NavController.currentRouteName: String?
    get() = currentDestination?.route