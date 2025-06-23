package gr.pkcoding.notemarkapp.core.network.model

import kotlinx.serialization.Serializable

// Request Models
@Serializable
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RefreshTokenRequest(
    val refreshToken: String
)

// Response Models
@Serializable
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String
)

// Result wrapper for API calls
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val exception: Throwable) : ApiResult<Nothing>()
    data class HttpError(val code: Int, val message: String) : ApiResult<Nothing>()
}

// Extension function for easy result handling
inline fun <T> ApiResult<T>.onSuccess(action: (T) -> Unit): ApiResult<T> {
    if (this is ApiResult.Success) action(data)
    return this
}

inline fun <T> ApiResult<T>.onError(action: (Throwable) -> Unit): ApiResult<T> {
    if (this is ApiResult.Error) action(exception)
    return this
}

inline fun <T> ApiResult<T>.onHttpError(action: (Int, String) -> Unit): ApiResult<T> {
    if (this is ApiResult.HttpError) action(code, message)
    return this
}