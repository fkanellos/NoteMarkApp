package gr.pkcoding.notemarkapp.core.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

/**
 * Base class for use cases that return a Flow (reactive streams)
 *
 * Used for:
 * - Real-time data updates (authentication state, user preferences)
 * - Observing database changes
 * - Continuous monitoring of system state
 * - Reactive UI updates
 *
 * Unlike BaseUseCase which returns a single result, FlowUseCase returns
 * a stream of values that can change over time.
 *
 * @param Params Input parameters for the use case (use Unit if no params needed)
 * @param ReturnType Output type of each emission from the Flow
 */
abstract class FlowUseCase<in Params, out ReturnType>(
    private val coroutineDispatcher: CoroutineDispatcher
) {

    /**
     * Execute the use case and return a Flow
     *
     * The Flow will automatically run on the provided dispatcher:
     * - IO dispatcher for database/network observations
     * - Default dispatcher for CPU-intensive transformations
     * - Main dispatcher for UI-related streams (rarely used)
     *
     * @param parameters Input parameters for the use case
     * @return A Flow that emits values of ReturnType
     */
    operator fun invoke(parameters: Params): Flow<ReturnType> {
        return execute(parameters)
            .flowOn(coroutineDispatcher)
    }

    /**
     * The actual reactive business logic implementation
     *
     * Override this method in concrete use cases to implement:
     * - Reactive data observations
     * - Stream transformations and filtering
     * - Error handling in reactive streams
     * - Combining multiple data sources
     *
     * @param parameters Input parameters for the use case
     * @return A Flow that represents the reactive business logic
     */
    protected abstract fun execute(parameters: Params): Flow<ReturnType>
}