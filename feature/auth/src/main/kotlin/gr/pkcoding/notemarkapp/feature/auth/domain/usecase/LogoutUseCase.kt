package gr.pkcoding.notemarkapp.feature.auth.domain.usecase

import gr.pkcoding.notemarkapp.core.domain.usecase.BaseUseCase
import gr.pkcoding.notemarkapp.feature.auth.domain.model.AuthResult
import gr.pkcoding.notemarkapp.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Use case for user logout functionality
 *
 * Handles:
 * - Secure token removal from local storage
 * - Session cleanup
 * - Error handling during logout process
 *
 * This use case takes no parameters since logout is a simple operation
 * that clears the current user's session.
 *
 * @param repository Auth repository for session management
 * @param dispatcher Coroutine dispatcher for background execution
 */
class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository,
    dispatcher: CoroutineDispatcher
) : BaseUseCase<Unit, AuthResult<Unit>>(dispatcher) {

    /**
     * Execute logout process
     *
     * Flow:
     * 1. Clear stored authentication tokens
     * 2. Clear any cached user data
     * 3. Return success or error result
     *
     * Note: Logout should almost always succeed since it's mainly
     * a local operation (clearing stored tokens).
     */
    override suspend fun execute(parameters: Unit): AuthResult<Unit> {
        // Delegate to repository to handle:
        // - Token removal from secure storage
        // - Any additional cleanup needed
        return repository.logout()
    }
}