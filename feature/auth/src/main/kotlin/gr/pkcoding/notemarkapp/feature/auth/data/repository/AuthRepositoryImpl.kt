package gr.pkcoding.notemarkapp.feature.auth.data.repository

import gr.pkcoding.notemarkapp.core.network.model.ApiResult
import gr.pkcoding.notemarkapp.core.network.service.AuthService
import gr.pkcoding.notemarkapp.core.network.storage.TokenStorage
import gr.pkcoding.notemarkapp.feature.auth.domain.model.AuthResult
import gr.pkcoding.notemarkapp.feature.auth.domain.model.LoginCredentials
import gr.pkcoding.notemarkapp.feature.auth.domain.model.RegistrationData
import gr.pkcoding.notemarkapp.feature.auth.domain.model.User
import gr.pkcoding.notemarkapp.feature.auth.domain.model.toDomainError
import gr.pkcoding.notemarkapp.feature.auth.domain.model.toTokens
import gr.pkcoding.notemarkapp.feature.auth.domain.model.toUser
import gr.pkcoding.notemarkapp.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val tokenStorage: TokenStorage
) : AuthRepository {

    override suspend fun register(registrationData: RegistrationData): AuthResult<Unit> {
        // 1. Validate input data
        val validationErrors = registrationData.validate()
        if (validationErrors.isNotEmpty()) {
            return validationErrors.first() // Return first validation error
        }

        // 2. Make API call
        return when (val result = authService.register(
            username = registrationData.username,
            email = registrationData.email,
            password = registrationData.password
        )) {
            is ApiResult.Success -> AuthResult.Success(Unit)
            is ApiResult.Error -> result.toDomainError()
            is ApiResult.HttpError -> result.toDomainError()
        }
    }

    override suspend fun login(credentials: LoginCredentials): AuthResult<User> {
        // 1. Validate credentials
        if (!credentials.isValid()) {
            return AuthResult.ValidationError("email", "Invalid email or password")
        }

        // 2. Make API call
        return when (val result = authService.login(credentials.email, credentials.password)) {
            is ApiResult.Success -> {
                // 3. Save tokens securely
                tokenStorage.saveTokens(result.data.toTokens())

                // 4. Convert to domain model and return
                // Note: toUser() currently returns mock data - will be improved with user profile API
                AuthResult.Success(result.data.toUser())
            }
            is ApiResult.Error -> result.toDomainError()
            is ApiResult.HttpError -> result.toDomainError()
        }
    }

    override suspend fun logout(): AuthResult<Unit> {
        return try {
            // Clear tokens from secure storage
            tokenStorage.clearTokens()
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error("Failed to logout", e)
        }
    }

    override suspend fun getCurrentUser(): AuthResult<User?> {
        return try {
            val tokens = tokenStorage.getTokens()
            if (tokens != null) {
                // TODO: In a real app, make API call to get user profile
                // For now, return a mock user since we have valid tokens
                AuthResult.Success(
                    User(
                        id = "current_user_id",
                        username = "Current User",
                        email = "user@example.com"
                    )
                )
            } else {
                // No tokens = not logged in
                AuthResult.Success(null)
            }
        } catch (e: Exception) {
            AuthResult.Error("Failed to get current user", e)
        }
    }

    override fun isLoggedIn(): Flow<Boolean> = flow {
        try {
            val tokens = tokenStorage.getTokens()
            emit(tokens != null)
        } catch (e: Exception) {
            emit(false)
        }
    }

    override suspend fun refreshTokens(): AuthResult<Unit> {
        return try {
            val refreshToken = tokenStorage.getRefreshToken()
            if (refreshToken == null) {
                return AuthResult.Error("No refresh token available")
            }

            when (val result = authService.refreshToken(refreshToken)) {
                is ApiResult.Success -> {
                    // Save new tokens
                    tokenStorage.saveTokens(result.data.toTokens())
                    AuthResult.Success(Unit)
                }
                is ApiResult.Error -> result.toDomainError()
                is ApiResult.HttpError -> {
                    // If refresh fails, clear tokens (force re-login)
                    if (result.code == 401) {
                        tokenStorage.clearTokens()
                    }
                    result.toDomainError()
                }
            }
        } catch (e: Exception) {
            AuthResult.Error("Token refresh failed", e)
        }
    }

    override suspend fun hasValidTokens(): Boolean {
        return try {
            tokenStorage.getTokens() != null
        } catch (e: Exception) {
            false
        }
    }
}