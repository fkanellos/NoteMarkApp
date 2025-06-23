package gr.pkcoding.notemarkapp.core.domain.util

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Interface for providing coroutine dispatchers
 *
 * This abstraction allows for:
 * - Easy testing with fake dispatchers
 * - Centralized dispatcher management
 * - Platform-specific dispatcher implementations
 * - Consistent threading behavior across the app
 *
 * Benefits:
 * - Unit tests can use TestDispatchers for deterministic testing
 * - Production code uses real dispatchers for proper threading
 * - Easy to mock in tests
 * - Single source of truth for dispatcher configuration
 */
interface DispatcherProvider {

    /**
     * Main dispatcher for UI updates
     *
     * Use for:
     * - UI updates and state changes
     * - Quick, non-blocking operations
     * - Operations that must run on the main thread
     */
    val main: CoroutineDispatcher

    /**
     * IO dispatcher for blocking operations
     *
     * Use for:
     * - Network requests
     * - File/database operations
     * - Any blocking I/O operations
     * - Reading/writing preferences
     */
    val io: CoroutineDispatcher

    /**
     * Default dispatcher for CPU-intensive work
     *
     * Use for:
     * - Heavy computations
     * - Data processing
     * - Image/video processing
     * - Parsing large datasets
     */
    val default: CoroutineDispatcher

    /**
     * Unconfined dispatcher for testing
     *
     * Use for:
     * - Unit tests that need immediate execution
     * - Operations that don't need specific threading
     * - Debugging and testing scenarios
     */
    val unconfined: CoroutineDispatcher
}