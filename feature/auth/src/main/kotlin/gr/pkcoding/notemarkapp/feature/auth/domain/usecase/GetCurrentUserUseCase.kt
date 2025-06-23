package gr.pkcoding.notemarkapp.feature.auth.domain.usecase

import gr.pkcoding.notemarkapp.core.domain.usecase.BaseUseCase
import gr.pkcoding.notemarkapp.feature.auth.domain.model.AuthResult
import gr.pkcoding.notemarkapp.feature.auth.domain.model.User
import gr.pkcoding.notemarkapp.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Use case for retrieving current authenticated user
 *
 * Handles:
 * - Check if user has valid tokens
 * - Retrieve user information from tokens or API
 * - Handle expired tokens gracefully
 * - Return null if no user is authenticated
 *
 * This is commonly used for:
 * - App startup authentication check
 * - Profile screen data loading
 * - Permission checks throughout the app
 *
 * @param repository Auth repository for user data retrieval
 * @param dispatcher Coroutine dispatcher for background execution
 */
class GetCurrentUserUseCase @Inject constructor(
    private val repository: AuthRepository,
    dispatcher: CoroutineDispatcher
) : BaseUseCase<Unit, AuthResult<User?>>(dispatcher) {

    /**
     * Execute current user retrieval
     *
     * Flow:
     * 1. Check if valid tokens exist in storage
     * 2. If tokens exist, retrieve user information
     * 3. If no tokens or tokens invalid, return null (not logged in)
     * 4. Handle any errors during the process
     *
     * Returns:
     * - Success(User) if user is authenticated
     * - Success(null) if no user is logged in
     * - Error if something went wrong during check
     */
    override suspend fun execute(parameters: Unit): AuthResult<User?> {
        // Delegate to repository to handle:
        // - Token validation
        // - User data retrieval
        // - Error handling for expired/invalid tokens
        return repository.getCurrentUser()
    }
}