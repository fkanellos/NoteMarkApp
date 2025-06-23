package gr.pkcoding.notemarkapp.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation routes using Navigation 3.0
 *
 * Benefits of using @Serializable routes:
 * - Compile-time safety for navigation
 * - Type-safe arguments passing
 * - Better IDE support with autocomplete
 * - Automatic argument validation
 * - No string-based route errors
 */

/**
 * Root navigation graphs
 */
@Serializable
object AuthGraph

@Serializable
object MainGraph

/**
 * Authentication flow routes
 */
@Serializable
object SplashRoute {
    /**
     * Initial splash screen that checks authentication status
     *
     * Flow:
     * - Show app logo and loading
     * - Check if user has valid tokens
     * - Navigate to Main if authenticated
     * - Navigate to Landing if not authenticated
     */
}

@Serializable
object LandingRoute {
    /**
     * Welcome/onboarding screen with main actions
     *
     * Contains:
     * - App introduction/hero graphic
     * - "Get Started" button (navigate to Register)
     * - "Log In" button (navigate to Login)
     */
}

@Serializable
data class LoginRoute(
    /**
     * Whether user came from registration screen
     * Used to show appropriate messaging or prefill email
     */
    val fromRegister: Boolean = false,

    /**
     * Optional email to prefill (e.g., after registration)
     */
    val prefillEmail: String? = null
) {
    /**
     * Login screen for existing users
     *
     * Contains:
     * - Email input field
     * - Password input field with visibility toggle
     * - Login button with loading states
     * - "Don't have an account?" link to register
     * - Error handling and validation
     */
}

@Serializable
data class RegisterRoute(
    /**
     * Whether user came from login screen
     * Used for analytics or user flow tracking
     */
    val fromLogin: Boolean = false
) {
    /**
     * Registration screen for new users
     *
     * Contains:
     * - Username input field (3-20 characters)
     * - Email input field
     * - Password input field with strength indicator
     * - Confirm password field
     * - Register button with loading states
     * - "Already have an account?" link to login
     * - Comprehensive validation and error handling
     */
}

/**
 * Main application routes (after authentication)
 */
@Serializable
object HomeRoute {
    /**
     * Main home screen after successful authentication
     *
     * This is where users land after login/register
     * Will contain the main app functionality in future milestones
     */
}

@Serializable
data class ProfileRoute(
    /**
     * User ID to show profile for
     * Defaults to current user if not specified
     */
    val userId: String? = null
) {
    /**
     * User profile screen
     *
     * Contains:
     * - User information display
     * - Settings and preferences
     * - Logout functionality
     */
}

/**
 * Navigation argument keys for consistent parameter passing
 */
object NavigationArgs {
    const val FROM_REGISTER = "fromRegister"
    const val FROM_LOGIN = "fromLogin"
    const val PREFILL_EMAIL = "prefillEmail"
    const val USER_ID = "userId"
    const val CLEAR_BACK_STACK = "clearBackStack"
}

/**
 * Navigation constants for route management
 */
object NavigationConstants {
    /**
     * Animation duration for screen transitions
     */
    const val TRANSITION_DURATION_MS = 300

    /**
     * Delay before automatic navigation (e.g., splash screen)
     */
    const val AUTO_NAVIGATION_DELAY_MS = 2000L

    /**
     * Back press handling
     */
    const val BACK_PRESS_TIMEOUT_MS = 2000L
}