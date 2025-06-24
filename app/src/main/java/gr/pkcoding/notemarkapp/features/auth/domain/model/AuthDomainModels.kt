package gr.pkcoding.notemarkapp.features.auth.domain.model

import gr.pkcoding.notemarkapp.core.network.model.ApiResult
import gr.pkcoding.notemarkapp.core.network.model.AuthResponse
import gr.pkcoding.notemarkapp.core.network.storage.AuthTokens

/**
 * Domain User model - represents authenticated user in the app
 * This is what the UI layer works with (not the API response models)
 */
data class User(
    val id: String? = null,
    val username: String,
    val email: String
)

/**
 * Authentication state for the domain
 */
sealed class AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error(val message: String, val cause: Throwable? = null) : AuthResult<Nothing>()
    data class NetworkError(val message: String = "No internet connection") : AuthResult<Nothing>()
    data class ValidationError(val field: String, val message: String) : AuthResult<Nothing>()
}

/**
 * Login credentials domain model
 */
data class LoginCredentials(
    val email: String,
    val password: String
) {
    fun isValid(): Boolean {
        return email.isNotBlank() &&
                email.contains("@") &&
                password.length >= 8
    }
}

/**
 * Registration data domain model
 */
data class RegistrationData(
    val username: String,
    val email: String,
    val password: String,
    val confirmPassword: String
) {
    fun validate(): List<AuthResult.ValidationError> {
        val errors = mutableListOf<AuthResult.ValidationError>()

        if (username.length < 3 || username.length > 20) {
            errors.add(
                AuthResult.ValidationError(
                    "username",
                    "Username must be between 3 and 20 characters"
                )
            )
        }

        if (!email.contains("@") || email.isBlank()) {
            errors.add(AuthResult.ValidationError("email", "Invalid email provided"))
        }

        if (password.length < 8 || !password.containsNumberOrSymbol()) {
            errors.add(
                AuthResult.ValidationError(
                    "password",
                    "Password must be at least 8 characters and include a number or symbol"
                )
            )
        }

        if (password != confirmPassword) {
            errors.add(AuthResult.ValidationError("confirmPassword", "Passwords do not match"))
        }

        return errors
    }

    private fun String.containsNumberOrSymbol(): Boolean {
        return any { it.isDigit() } || any { !it.isLetterOrDigit() }
    }
}

/**
 * Extension functions for domain conversions
 */

// Convert API models to Domain models
fun AuthResponse.toUser(): User {
    // TODO: In a real app, make API call to get user profile after login
    // For now, return a temporary user since we have valid tokens
    return User(
        id = "temp_user_id", // Will be populated from getUserProfile() API later
        username = "Current User", // Will be populated from getUserProfile() API later
        email = "user@example.com" // Will be populated from getUserProfile() API later
    )
}

fun AuthResponse.toTokens(): AuthTokens {
    return AuthTokens(
        accessToken = accessToken,
        refreshToken = refreshToken
    )
}

// Convert API errors to Domain errors
fun ApiResult.Error.toDomainError(): AuthResult.Error {
    return AuthResult.Error(
        message = exception.message ?: "Unknown error occurred",
        cause = exception
    )
}

fun ApiResult.HttpError.toDomainError(): AuthResult<Nothing> {
    return when (code) {
        401 -> AuthResult.Error("Invalid login credentials")
        409 -> AuthResult.Error("Email already exists")
        400 -> AuthResult.ValidationError("form", message)
        in 500..599 -> AuthResult.Error("Server error. Please try again later.")
        else -> AuthResult.Error(message)
    }
}