package gr.pkcoding.notemarkapp.feature.auth.presentation.model

/**
 * Represents the UI-specific state for authentication screens
 *
 * This class manages form field states, visibility settings, and UI-specific
 * configurations that are separate from the business logic state (AuthState).
 *
 * Used by AuthViewModel to manage form interactions and UI behavior.
 */
data class AuthUiState(
    /**
     * Current values of form fields
     */
    val formFields: Map<String, String> = emptyMap(),

    /**
     * Password visibility states for password fields
     */
    val passwordVisibility: Map<String, Boolean> = mapOf(
        AuthFields.PASSWORD to false,
        AuthFields.CONFIRM_PASSWORD to false
    ),

    /**
     * Whether form fields should be enabled for interaction
     * (disabled during loading states)
     */
    val fieldsEnabled: Boolean = true,

    /**
     * Field-specific error messages for validation
     */
    val fieldErrors: Map<String, String> = emptyMap(),

    /**
     * Whether to show real-time validation (after first submission attempt)
     */
    val showRealTimeValidation: Boolean = false,

    /**
     * Which field currently has focus (for validation timing)
     */
    val focusedField: String? = null,

    /**
     * Whether the form has been submitted at least once
     * (affects when to show validation errors)
     */
    val hasAttemptedSubmission: Boolean = false
) {

    /**
     * Get the current value of a specific field
     */
    fun getFieldValue(field: String): String = formFields[field] ?: ""

    /**
     * Check if a specific field has an error
     */
    fun hasFieldError(field: String): Boolean = fieldErrors.containsKey(field)

    /**
     * Get the error message for a specific field
     */
    fun getFieldError(field: String): String? = fieldErrors[field]

    /**
     * Check if a password field is currently visible
     */
    fun isPasswordVisible(field: String): Boolean = passwordVisibility[field] ?: false

    /**
     * Check if any field has an error
     */
    val hasAnyFieldError: Boolean
        get() = fieldErrors.isNotEmpty()

    /**
     * Check if the form is valid (no field errors and all required fields filled)
     */
    fun isFormValid(requiredFields: List<String>): Boolean {
        return fieldErrors.isEmpty() &&
                requiredFields.all { field -> getFieldValue(field).isNotBlank() }
    }

    /**
     * Get all current form data as a map
     */
    val allFormData: Map<String, String>
        get() = formFields.toMap()

    /**
     * Check if any field has been modified from its default state
     */
    val hasModifiedFields: Boolean
        get() = formFields.values.any { it.isNotBlank() }
}

/**
 * Helper functions for creating updated UI states
 */

/**
 * Update the value of a specific field
 */
fun AuthUiState.updateField(field: String, value: String): AuthUiState {
    return copy(
        formFields = formFields + (field to value),
        // Clear field error when user starts typing
        fieldErrors = if (hasFieldError(field)) {
            fieldErrors - field
        } else {
            fieldErrors
        }
    )
}

/**
 * Toggle password visibility for a specific field
 */
fun AuthUiState.togglePasswordVisibility(field: String): AuthUiState {
    return copy(
        passwordVisibility = passwordVisibility +
                (field to !isPasswordVisible(field))
    )
}

/**
 * Set field errors from validation results
 */
fun AuthUiState.setFieldErrors(errors: Map<String, String>): AuthUiState {
    return copy(
        fieldErrors = errors,
        hasAttemptedSubmission = true,
        showRealTimeValidation = true
    )
}

/**
 * Clear all field errors
 */
fun AuthUiState.clearFieldErrors(): AuthUiState {
    return copy(fieldErrors = emptyMap())
}

/**
 * Clear all form data and reset to initial state
 */
fun AuthUiState.clearForm(): AuthUiState {
    return copy(
        formFields = emptyMap(),
        fieldErrors = emptyMap(),
        hasAttemptedSubmission = false,
        showRealTimeValidation = false,
        focusedField = null
    )
}

/**
 * Set fields enabled/disabled state
 */
fun AuthUiState.setFieldsEnabled(enabled: Boolean): AuthUiState {
    return copy(fieldsEnabled = enabled)
}

/**
 * Set the currently focused field
 */
fun AuthUiState.setFocusedField(field: String?): AuthUiState {
    return copy(focusedField = field)
}