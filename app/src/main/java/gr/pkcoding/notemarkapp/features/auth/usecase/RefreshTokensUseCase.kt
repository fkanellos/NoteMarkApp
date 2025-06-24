package gr.pkcoding.notemarkapp.features.auth.usecase

import gr.pkcoding.notemarkapp.features.auth.usecase.BaseUseCase
import gr.pkcoding.notemarkapp.features.auth.domain.model.AuthResult
import gr.pkcoding.notemarkapp.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Use case for refreshing authentication tokens
 *
 * Handles:
 * - Automatic token refresh when access token expires
 * - Secure token replacement in local storage
 * - Session termination if refresh token is invalid
 * - Error handling for network issues during refresh
 *
 * This use case is typically called:
 * - Automatically by network layer when 401 responses occur
 * - Proactively before token expiration
 * - During app startup to ensure tokens are fresh
 *
 * @param repository Auth repository for token management
 * @param dispatcher Coroutine dispatcher for background execution
 */
class RefreshTokensUseCase @Inject constructor(
    private val repository: AuthRepository,
    dispatcher: CoroutineDispatcher
) : BaseUseCase<Unit, AuthResult<Unit>>(dispatcher) {

    /**
     * Execute token refresh process
     *
     * Flow:
     * 1. Check if refresh token exists in storage
     * 2. Make API call to refresh endpoint with current refresh token
     * 3. If successful, replace both access and refresh tokens
     * 4. If failed (401), clear all tokens (force re-login)
     * 5. Return success or error result
     *
     * Important notes:
     * - Both access and refresh tokens are replaced after successful refresh
     * - Old refresh token becomes invalid after use
     * - If refresh fails with 401, user must login again
     * - Network errors during refresh should be retried
     */
    override suspend fun execute(parameters: Unit): AuthResult<Unit> {
        // Delegate to repository to handle:
        // - Refresh token validation
        // - API call to /auth/refresh endpoint
        // - Token replacement in secure storage
        // - Error handling and session cleanup if needed
        return repository.refreshTokens()
    }
}