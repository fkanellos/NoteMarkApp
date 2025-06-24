package gr.pkcoding.notemarkapp.features.auth.domain.repository

import gr.pkcoding.notemarkapp.features.auth.domain.model.AuthResult
import gr.pkcoding.notemarkapp.features.auth.domain.model.LoginCredentials
import gr.pkcoding.notemarkapp.features.auth.domain.model.RegistrationData
import gr.pkcoding.notemarkapp.features.auth.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Domain repository interface - defines what the auth feature can do
 *
 * This interface is owned by the Domain layer, but implemented in the Data layer
 * This follows Dependency Inversion Principle - high-level modules don't depend on low-level modules
 */
interface AuthRepository {

    /**
     * Register a new user account
     */
    suspend fun register(registrationData: RegistrationData): AuthResult<Unit>

    /**
     * Login with email and password
     */
    suspend fun login(credentials: LoginCredentials): AuthResult<User>

    /**
     * Logout current user and clear tokens
     */
    suspend fun logout(): AuthResult<Unit>

    /**
     * Get current authenticated user (if any)
     * This checks if we have valid tokens and returns user info
     */
    suspend fun getCurrentUser(): AuthResult<User?>

    /**
     * Check if user is currently logged in
     * Returns Flow so UI can reactively update
     */
    fun isLoggedIn(): Flow<Boolean>

    /**
     * Refresh authentication tokens
     * This is called automatically when access token expires
     */
    suspend fun refreshTokens(): AuthResult<Unit>

    /**
     * Check if we have valid authentication tokens
     */
    suspend fun hasValidTokens(): Boolean
}