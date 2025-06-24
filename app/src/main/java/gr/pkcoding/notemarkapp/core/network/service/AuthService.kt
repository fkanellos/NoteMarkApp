package gr.pkcoding.notemarkapp.core.network.service

import gr.pkcoding.notemarkapp.core.network.model.ApiResult
import gr.pkcoding.notemarkapp.core.network.model.AuthResponse
import gr.pkcoding.notemarkapp.core.network.model.LoginRequest
import gr.pkcoding.notemarkapp.core.network.model.RefreshTokenRequest
import gr.pkcoding.notemarkapp.core.network.model.RegisterRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import javax.inject.Inject
import javax.inject.Singleton

interface AuthService {
    suspend fun register(username: String, email: String, password: String): ApiResult<Unit>
    suspend fun login(email: String, password: String): ApiResult<AuthResponse>
    suspend fun refreshToken(refreshToken: String): ApiResult<AuthResponse>
}

@Singleton
class AuthServiceImpl @Inject constructor(
    private val httpClient: HttpClient
) : AuthService {

    override suspend fun register(
        username: String,
        email: String,
        password: String
    ): ApiResult<Unit> {
        return try {
            httpClient.post("auth/register") {
                setBody(RegisterRequest(username, email, password))
            }
            ApiResult.Success(Unit)
        } catch (e: ClientRequestException) {
            when (e.response.status) {
                HttpStatusCode.Conflict -> ApiResult.HttpError(409, "Email already exists")
                HttpStatusCode.BadRequest -> ApiResult.HttpError(400, "Invalid registration data")
                else -> ApiResult.HttpError(e.response.status.value, e.message)
            }
        } catch (e: ServerResponseException) {
            ApiResult.HttpError(e.response.status.value, "Server error")
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }

    override suspend fun login(email: String, password: String): ApiResult<AuthResponse> {
        return try {
            val response: AuthResponse = httpClient.post("auth/login") {
                setBody(LoginRequest(email, password))
            }.body()

            ApiResult.Success(response)
        } catch (e: ClientRequestException) {
            when (e.response.status) {
                HttpStatusCode.Unauthorized -> ApiResult.HttpError(401, "Invalid login credentials")
                HttpStatusCode.BadRequest -> ApiResult.HttpError(400, "Invalid login data")
                else -> ApiResult.HttpError(e.response.status.value, e.message)
            }
        } catch (e: ServerResponseException) {
            ApiResult.HttpError(e.response.status.value, "Server error")
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }

    override suspend fun refreshToken(refreshToken: String): ApiResult<AuthResponse> {
        return try {
            val response: AuthResponse = httpClient.post("auth/refresh") {
                setBody(RefreshTokenRequest(refreshToken))
            }.body()

            ApiResult.Success(response)
        } catch (e: ClientRequestException) {
            when (e.response.status) {
                HttpStatusCode.Unauthorized -> ApiResult.HttpError(401, "Invalid refresh token")
                else -> ApiResult.HttpError(e.response.status.value, e.message)
            }
        } catch (e: ServerResponseException) {
            ApiResult.HttpError(e.response.status.value, "Server error")
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
}