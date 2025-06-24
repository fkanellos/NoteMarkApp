package gr.pkcoding.notemarkapp.feature.auth.presentation.model

import gr.pkcoding.notemarkapp.features.auth.domain.model.User

/**
 * Represents all possible states in the authentication flow
 *
 * This sealed class provides type-safe state management for the authentication UI.
 * Each state represents what the user sees and how the UI should behave.
 *
 * Used by AuthViewModel to manage UI state in a predictable way.
 */
sealed class AuthState {

    /**
     * Initial state when no authentication action is happening
     *
     * UI Behavior:
     * - Show login/register forms in normal state
     * - All buttons are enabled
     * - No loading indicators
     * - No error messages
     */
    object Idle : AuthState()

    /**
     * Loading state during authentication operations
     *
     * UI Behavior:
     * - Show loading indicators on buttons
     * - Disable form inputs to prevent duplicate submissions
     * - Show progress indicators where appropriate
     *
     * @param operation Optional description of what's loading (login, register, etc.)
     */
    data class Loading(val operation: String? = null) : AuthState()

    /**
     * Successful authentication state
     *
     * UI Behavior:
     * - Trigger navigation to main app
     * - Show success messages if needed
     * - Clear any error states
     *
     * @param user The authenticated user data
     * @param isNewUser True if this was a registration, false for login
     */
    data class Success(
        val user: User,
        val isNewUser: Boolean = false
    ) : AuthState()

    /**
     * General error state for non-validation errors
     *
     * UI Behavior:
     * - Show error snackbar or dialog
     * - Return forms to normal state
     * - Allow user to retry the operation
     *
     * @param message User-friendly error message
     * @param retryable Whether the user can retry the failed operation
     */
    data class Error(
        val message: String,
        val retryable: Boolean = true
    ) : AuthState()

    /**
     * Network error state for connectivity issues
     *
     * UI Behavior:
     * - Show network-specific error message
     * - Provide retry option
     * - Possibly show offline mode if available
     *
     * @param message Network error message
     */
    data class NetworkError(
        val message: String = "No internet connection"
    ) : AuthState()

    /**
     * Validation error state for form field errors
     *
     * UI Behavior:
     * - Highlight specific fields with errors
     * - Show field-specific error messages
     * - Keep form in editable state
     * - Focus on first error field
     *
     * @param fieldErrors Map of field names to error messages
     */
    data class ValidationError(
        val fieldErrors: Map<String, String>
    ) : AuthState() {

        /**
         * Convenience constructor for single field error
         */
        constructor(field: String, message: String) : this(mapOf(field to message))

        /**
         * Check if a specific field has an error
         */
        fun hasFieldError(field: String): Boolean = fieldErrors.containsKey(field)

        /**
         * Get error message for a specific field
         */
        fun getFieldError(field: String): String? = fieldErrors[field]
    }
}

/**
 * Extension functions for convenient state checking
 */

/**
 * Check if the current state represents a loading operation
 */
val AuthState.isLoading: Boolean
    get() = this is AuthState.Loading

/**
 * Check if the current state represents any kind of error
 */
val AuthState.isError: Boolean
    get() = this is AuthState.Error || this is AuthState.NetworkError || this is AuthState.ValidationError

/**
 * Check if the current state allows user interaction
 */
val AuthState.isInteractive: Boolean
    get() = this is AuthState.Idle || this is AuthState.ValidationError

/**
 * Get user data if state is success, null otherwise
 */
val AuthState.user: User?
    get() = (this as? AuthState.Success)?.user