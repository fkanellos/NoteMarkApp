package gr.pkcoding.notemarkapp.features.auth.usecase

import gr.pkcoding.notemarkapp.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * Use case for reactive authentication state monitoring
 *
 * Handles:
 * - Real-time authentication state changes
 * - Reactive UI updates when login/logout occurs
 * - Continuous monitoring of token validity
 *
 * This use case returns a Flow for reactive programming:
 * - UI can observe authentication state changes
 * - Automatic navigation when auth state changes
 * - No manual polling needed
 *
 * Unlike other use cases, this doesn't extend BaseUseCase because
 * it returns a Flow rather than a one-time result.
 *
 * @param repository Auth repository for state monitoring
 * @param dispatcher Coroutine dispatcher for background execution
 */
class CheckAuthStateUseCase @Inject constructor(
    private val repository: AuthRepository,
    private val dispatcher: CoroutineDispatcher
) {

    /**
     * Observe authentication state changes
     *
     * Returns a Flow that emits:
     * - true when user is authenticated (has valid tokens)
     * - false when user is not authenticated or tokens expired
     *
     * The Flow will emit new values whenever:
     * - User logs in successfully
     * - User logs out
     * - Tokens expire and refresh fails
     * - App starts and checks existing tokens
     *
     * Usage in ViewModel:
     * ```
     * checkAuthStateUseCase().collect { isAuthenticated ->
     *     if (isAuthenticated) {
     *         // Navigate to main app
     *     } else {
     *         // Navigate to login screen
     *     }
     * }
     * ```
     */
    operator fun invoke(): Flow<Boolean> {
        return repository.isLoggedIn()
            .flowOn(dispatcher) // Execute on background thread
    }
}