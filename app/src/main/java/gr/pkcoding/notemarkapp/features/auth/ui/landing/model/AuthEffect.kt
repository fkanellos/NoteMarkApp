package gr.pkcoding.notemarkapp.feature.auth.presentation.model

/**
 * Represents one-time side effects in the authentication flow
 *
 * Effects are events that should happen only once and are not part of the persistent state.
 * They are consumed by the UI and should not be replayed when the UI recomposes.
 *
 * Examples: Navigation, showing snackbars, showing dialogs, triggering haptic feedback
 *
 * Used by AuthViewModel to communicate one-time events to the Compose UI.
 */
sealed class AuthEffect {

    /**
     * Navigate to the main app after successful authentication
     *
     * UI Behavior:
     * - Navigate to main app screen
     * - Clear authentication back stack
     * - Show welcome message if needed
     *
     * @param clearBackStack Whether to clear the entire back stack
     */
    data class NavigateToMain(
        val clearBackStack: Boolean = true
    ) : AuthEffect()

    /**
     * Navigate to the login screen
     *
     * UI Behavior:
     * - Navigate to login screen
     * - Clear any form data from register screen
     * - Reset form states
     */
    object NavigateToLogin : AuthEffect()

    /**
     * Navigate to the register screen
     *
     * UI Behavior:
     * - Navigate to register screen
     * - Clear any form data from login screen
     * - Reset form states
     */
    object NavigateToRegister : AuthEffect()

    /**
     * Navigate to the landing/welcome screen
     *
     * UI Behavior:
     * - Navigate to landing screen
     * - Clear any authentication states
     * - Show welcome/onboarding content
     */
    object NavigateToLanding : AuthEffect()

    /**
     * Show a snackbar message to the user
     *
     * UI Behavior:
     * - Display snackbar with message
     * - Auto-dismiss after timeout
     * - Optional action button
     *
     * @param message The message to display
     * @param actionLabel Optional action button text
     * @param isError Whether this is an error message (affects styling)
     */
    data class ShowSnackbar(
        val message: String,
        val actionLabel: String? = null,
        val isError: Boolean = false
    ) : AuthEffect()

    /**
     * Show an error dialog for critical errors
     *
     * UI Behavior:
     * - Display modal error dialog
     * - Block user interaction until dismissed
     * - Provide retry or dismiss options
     *
     * @param title Dialog title
     * @param message Error message
     * @param retryable Whether to show retry button
     */
    data class ShowErrorDialog(
        val title: String,
        val message: String,
        val retryable: Boolean = true
    ) : AuthEffect()

    /**
     * Show a success message after account creation
     *
     * UI Behavior:
     * - Display success snackbar or dialog
     * - Welcome the new user
     * - Possibly show onboarding hints
     *
     * @param message Success message to show
     */
    data class ShowSuccessMessage(
        val message: String
    ) : AuthEffect()

    /**
     * Focus on a specific input field
     *
     * UI Behavior:
     * - Move focus to the specified field
     * - Show keyboard if needed
     * - Scroll to field if needed
     *
     * @param fieldName Name of the field to focus
     */
    data class FocusField(
        val fieldName: String
    ) : AuthEffect()

    /**
     * Clear form data and reset field states
     *
     * UI Behavior:
     * - Clear all form fields
     * - Reset validation states
     * - Hide any field errors
     */
    object ClearForm : AuthEffect()

    /**
     * Trigger haptic feedback for user actions
     *
     * UI Behavior:
     * - Provide tactile feedback
     * - Different types for different actions
     *
     * @param type Type of haptic feedback (success, error, warning)
     */
    data class TriggerHapticFeedback(
        val type: HapticFeedbackType
    ) : AuthEffect()

    /**
     * Close the current screen (used for back navigation)
     *
     * UI Behavior:
     * - Navigate back to previous screen
     * - Handle back stack appropriately
     */
    object NavigateBack : AuthEffect()
}

/**
 * Types of haptic feedback for different user actions
 */
enum class HapticFeedbackType {
    /**
     * Light feedback for successful actions
     */
    Success,

    /**
     * Strong feedback for errors
     */
    Error,

    /**
     * Medium feedback for warnings
     */
    Warning,

    /**
     * Light feedback for general interactions
     */
    Click
}