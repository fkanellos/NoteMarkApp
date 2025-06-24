package gr.pkcoding.notemarkapp.features.auth.usecase

import gr.pkcoding.notemarkapp.features.auth.domain.model.AuthResult
import gr.pkcoding.notemarkapp.features.auth.domain.model.LoginCredentials
import gr.pkcoding.notemarkapp.features.auth.domain.model.User
import gr.pkcoding.notemarkapp.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Use case for user login functionality
 *
 * Handles:
 * - Input validation (email format, password requirements)
 * - Authentication through repository
 * - Automatic token storage
 * - Error mapping to UI-friendly messages
 *
 * @param repository Auth repository for API communication
 * @param dispatcher Coroutine dispatcher for background execution
 */
class LoginUseCase @Inject constructor(
    private val repository: AuthRepository,
    dispatcher: CoroutineDispatcher
) : BaseUseCase<LoginUseCase.Params, AuthResult<User>>(dispatcher) {

    /**
     * Input parameters for login
     *
     * @param email User's email address
     * @param password User's password
     */
    data class Params(
        val email: String,
        val password: String
    )

    /**
     * Execute login process
     *
     * Flow:
     * 1. Validate input parameters
     * 2. Create credentials object with additional validation
     * 3. Delegate to repository for API call
     * 4. Return result (success with user data or error)
     */
    override suspend fun execute(parameters: Params): AuthResult<User> {
        // 1. Basic input validation
        if (parameters.email.isBlank()) {
            return AuthResult.ValidationError("email", "Email cannot be empty")
        }

        if (parameters.password.isBlank()) {
            return AuthResult.ValidationError("password", "Password cannot be empty")
        }

        // 2. Create credentials with domain validation
        val credentials = LoginCredentials(
            email = parameters.email.trim(),
            password = parameters.password
        )

        // 3. Additional domain-level validation
        if (!credentials.isValid()) {
            return AuthResult.ValidationError("form", "Please check your email and password")
        }

        // 4. Delegate to repository for authentication
        return repository.login(credentials)
    }
}