package gr.pkcoding.notemarkapp.feature.auth.presentation.model

/**
 * Represents all possible user intentions/actions in the authentication flow
 *
 * This sealed class defines what the user wants to do in the auth screens.
 * Each intent triggers a specific action in the AuthViewModel which may
 * result in state changes and use case executions.
 *
 * Used by Compose UI to communicate user actions to the ViewModel.
 */
sealed class AuthIntent {

    /**
     * User wants to log in with email and password
     *
     * Triggers:
     * - Input validation
     * - LoginUseCase execution
     * - Navigation to main app on success
     * - Error handling on failure
     *
     * @param email User's email address
     * @param password User's password
     */
    data class Login(
        val email: String,
        val password: String
    ) : AuthIntent()

    /**
     * User wants to register a new account
     *
     * Triggers:
     * - Comprehensive input validation
     * - RegisterUseCase execution
     * - Automatic login after successful registration
     * - Error handling for validation or API failures
     *
     * @param username Desired username (3-20 characters)
     * @param email User's email address
     * @param password User's password (8+ chars with number/symbol)
     * @param confirmPassword Password confirmation (must match)
     */
    data class Register(
        val username: String,
        val email: String,
        val password: String,
        val confirmPassword: String
    ) : AuthIntent()

    /**
     * User wants to log out of the current session
     *
     * Triggers:
     * - LogoutUseCase execution
     * - Token cleanup
     * - Navigation to login screen
     * - Session state reset
     */
    object Logout : AuthIntent()

    /**
     * User wants to navigate to the register screen
     *
     * Triggers:
     * - Navigation to register screen
     * - State reset to clear any existing errors
     */
    object NavigateToRegister : AuthIntent()

    /**
     * User wants to navigate to the login screen
     *
     * Triggers:
     * - Navigation to login screen
     * - State reset to clear any existing errors
     */
    object NavigateToLogin : AuthIntent()

    /**
     * User wants to retry a failed operation
     *
     * Triggers:
     * - Clear current error state
     * - Return to idle state
     * - Allow user to retry their last action
     */
    object RetryLastOperation : AuthIntent()

    /**
     * Clear any current error state and return to idle
     *
     * Triggers:
     * - Reset state to Idle
     * - Clear any displayed errors
     * - Reset form validation states
     */
    object ClearError : AuthIntent()

    /**
     * Check current authentication status (used on app startup)
     *
     * Triggers:
     * - GetCurrentUserUseCase execution
     * - Navigation based on authentication status
     * - Auto-login if valid tokens exist
     */
    object CheckAuthStatus : AuthIntent()

    /**
     * Update form field values (for real-time validation)
     *
     * Triggers:
     * - Clear field-specific validation errors
     * - Real-time validation if enabled
     * - UI state updates
     *
     * @param field Name of the field being updated
     * @param value New value of the field
     */
    data class UpdateField(
        val field: String,
        val value: String
    ) : AuthIntent()

    /**
     * Toggle password visibility in password fields
     *
     * Triggers:
     * - UI state update for password visibility
     * - Icon change in password field
     *
     * @param field The password field to toggle ("password" or "confirmPassword")
     */
    data class TogglePasswordVisibility(
        val field: String
    ) : AuthIntent()
}

/**
 * Constants for field names used in UpdateField and TogglePasswordVisibility
 */
object AuthFields {
    const val USERNAME = "username"
    const val EMAIL = "email"
    const val PASSWORD = "password"
    const val CONFIRM_PASSWORD = "confirmPassword"
}