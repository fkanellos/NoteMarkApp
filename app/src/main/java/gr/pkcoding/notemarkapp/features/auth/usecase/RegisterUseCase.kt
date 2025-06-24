package gr.pkcoding.notemarkapp.features.auth.usecase

import gr.pkcoding.notemarkapp.features.auth.domain.model.AuthResult
import gr.pkcoding.notemarkapp.features.auth.domain.model.RegistrationData
import gr.pkcoding.notemarkapp.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Use case for user registration functionality
 *
 * Handles:
 * - Comprehensive input validation (username, email, password, confirmation)
 * - Business rule enforcement (password strength, username length, etc.)
 * - Account creation through repository
 * - Detailed error reporting for each field
 *
 * @param repository Auth repository for API communication
 * @param dispatcher Coroutine dispatcher for background execution
 */
class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository,
    dispatcher: CoroutineDispatcher
) : BaseUseCase<RegisterUseCase.Params, AuthResult<Unit>>(dispatcher) {

    /**
     * Input parameters for registration
     *
     * @param username Desired username (3-20 characters)
     * @param email User's email address
     * @param password User's password (8+ chars with number/symbol)
     * @param confirmPassword Password confirmation (must match)
     */
    data class Params(
        val username: String,
        val email: String,
        val password: String,
        val confirmPassword: String
    )

    /**
     * Execute registration process
     *
     * Flow:
     * 1. Basic input validation (empty checks)
     * 2. Create registration data object with business rules
     * 3. Validate all business rules and collect errors
     * 4. If valid, delegate to repository for account creation
     * 5. Return result (success or first validation error)
     */
    override suspend fun execute(parameters: Params): AuthResult<Unit> {
        // 1. Basic input validation
        if (parameters.username.isBlank()) {
            return AuthResult.ValidationError("username", "Username cannot be empty")
        }

        if (parameters.email.isBlank()) {
            return AuthResult.ValidationError("email", "Email cannot be empty")
        }

        if (parameters.password.isBlank()) {
            return AuthResult.ValidationError("password", "Password cannot be empty")
        }

        if (parameters.confirmPassword.isBlank()) {
            return AuthResult.ValidationError("confirmPassword", "Please confirm your password")
        }

        // 2. Create registration data with trimmed inputs
        val registrationData = RegistrationData(
            username = parameters.username.trim(),
            email = parameters.email.trim(),
            password = parameters.password,
            confirmPassword = parameters.confirmPassword
        )

        // 3. Comprehensive business rule validation
        val validationErrors = registrationData.validate()
        if (validationErrors.isNotEmpty()) {
            // Return the first validation error found
            return validationErrors.first()
        }

        // 4. All validation passed - delegate to repository
        return repository.register(registrationData)
    }
}