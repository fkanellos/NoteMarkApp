package gr.pkcoding.notemarkapp.core.domain.model

/**
 * Generic result wrapper for domain operations
 *
 * This sealed class provides type-safe error handling throughout the domain layer.
 * It's used by use cases to return either successful results or various error types.
 *
 * Benefits:
 * - Explicit error handling - forces developers to handle all cases
 * - Type safety - no runtime surprises from exceptions
 * - Composable - can be chained and transformed easily
 * - Consistent error handling across the entire application
 *
 * @param T The type of data on successful operations
 */
sealed class Result<out T> {

    /**
     * Represents a successful operation with data
     *
     * @param data The successful result data
     */
    data class Success<T>(val data: T) : Result<T>()

    /**
     * Represents a general error with message and optional cause
     *
     * Used for:
     * - Network errors
     * - Unexpected exceptions
     * - Business logic violations
     * - System errors
     *
     * @param message User-friendly error message
     * @param cause Optional underlying exception for debugging
     */
    data class Error(
        val message: String,
        val cause: Throwable? = null
    ) : Result<Nothing>()

    /**
     * Represents validation errors for specific fields
     *
     * Used for:
     * - Form field validation
     * - Input parameter validation
     * - Business rule violations with field context
     *
     * @param field The name of the field that failed validation
     * @param message User-friendly validation error message
     */
    data class ValidationError(
        val field: String,
        val message: String
    ) : Result<Nothing>()

    /**
     * Represents network-related errors
     *
     * Used for:
     * - No internet connection
     * - Timeout errors
     * - Server unavailable
     * - DNS resolution failures
     *
     * @param message User-friendly network error message
     */
    data class NetworkError(
        val message: String = "No internet connection"
    ) : Result<Nothing>()
}

/**
 * Extension functions for convenient Result handling
 */

/**
 * Execute action if result is successful
 *
 * @param action Lambda to execute with successful data
 * @return The original result for chaining
 */
inline fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.Success) action(data)
    return this
}

/**
 * Execute action if result is an error
 *
 * @param action Lambda to execute with error message
 * @return The original result for chaining
 */
inline fun <T> Result<T>.onError(action: (String) -> Unit): Result<T> {
    if (this is Result.Error) action(message)
    return this
}

/**
 * Execute action if result is a validation error
 *
 * @param action Lambda to execute with field and message
 * @return The original result for chaining
 */
inline fun <T> Result<T>.onValidationError(action: (String, String) -> Unit): Result<T> {
    if (this is Result.ValidationError) action(field, message)
    return this
}

/**
 * Execute action if result is a network error
 *
 * @param action Lambda to execute with network error message
 * @return The original result for chaining
 */
inline fun <T> Result<T>.onNetworkError(action: (String) -> Unit): Result<T> {
    if (this is Result.NetworkError) action(message)
    return this
}

/**
 * Get data if successful, null otherwise
 *
 * @return The data if result is Success, null for any error
 */
fun <T> Result<T>.getOrNull(): T? {
    return if (this is Result.Success) data else null
}

/**
 * Get data if successful, default value otherwise
 *
 * @param default Default value to return on error
 * @return The data if result is Success, default value for any error
 */
fun <T> Result<T>.getOrDefault(default: T): T {
    return if (this is Result.Success) data else default
}

/**
 * Transform successful result data
 *
 * @param transform Function to transform successful data
 * @return New Result with transformed data, or original error
 */
inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> {
    return when (this) {
        is Result.Success -> Result.Success(transform(data))
        is Result.Error -> this
        is Result.ValidationError -> this
        is Result.NetworkError -> this
    }
}