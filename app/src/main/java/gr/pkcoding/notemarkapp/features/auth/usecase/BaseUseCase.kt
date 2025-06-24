package gr.pkcoding.notemarkapp.features.auth.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Base class for all use cases in the application
 *
 * Provides common functionality:
 * - Thread management with coroutines
 * - Consistent error handling
 * - Parameter validation
 *
 * This follows the Clean Architecture principle where use cases represent
 * the business logic of the application and are independent of external concerns.
 *
 * @param Params Input parameters for the use case (use Unit if no params needed)
 * @param ReturnType Output type of the use case
 */
abstract class BaseUseCase<in Params, out ReturnType>(
    private val coroutineDispatcher: CoroutineDispatcher
) {

    /**
     * Execute the use case with given parameters
     *
     * Automatically switches to the provided dispatcher to ensure:
     * - Network operations run on IO dispatcher
     * - Database operations run on IO dispatcher
     * - CPU-intensive work runs on Default dispatcher
     * - UI updates happen on Main dispatcher (when needed)
     *
     * @param parameters Input parameters for the use case
     * @return The result of the use case execution
     */
    suspend operator fun invoke(parameters: Params): ReturnType {
        return withContext(coroutineDispatcher) {
            execute(parameters)
        }
    }

    /**
     * The actual business logic implementation
     *
     * Override this method in concrete use cases to implement:
     * - Input validation
     * - Business rule enforcement
     * - Coordination between repositories
     * - Error handling and mapping
     *
     * @param parameters Input parameters for the use case
     * @return The result of the business logic execution
     */
    @Throws(RuntimeException::class)
    protected abstract suspend fun execute(parameters: Params): ReturnType
}