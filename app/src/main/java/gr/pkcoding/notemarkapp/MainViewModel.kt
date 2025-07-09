package gr.pkcoding.notemarkapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import gr.pkcoding.notemarkapp.features.auth.domain.model.AuthResult
import gr.pkcoding.notemarkapp.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated = _isAuthenticated.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _authCheckResult = MutableStateFlow<AuthCheckResult?>(null)
    val authCheckResult = _authCheckResult.asStateFlow()

    init {
        // Start with reactive auth state monitoring
        observeAuthState()

        // Check initial authentication status
        checkAuthenticationStatus()
    }

    /**
     * Observe authentication state changes reactively
     * This handles automatic logout when tokens expire
     */
    private fun observeAuthState() {
        authRepository.isLoggedIn()
            .onEach { isLoggedIn ->
                _isAuthenticated.value = isLoggedIn

                // If user was authenticated but now isn't, handle session expiration
                if (!isLoggedIn && _authCheckResult.value is AuthCheckResult.Authenticated) {
                    _authCheckResult.value = AuthCheckResult.SessionExpired
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Check authentication status on app startup
     * This determines the initial navigation destination
     */
    private fun checkAuthenticationStatus() {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val result = authRepository.getCurrentUser()

                when (result) {
                    is AuthResult.Success -> {
                        if (result.data != null) {
                            // User is authenticated
                            _isAuthenticated.value = true
                            _authCheckResult.value = AuthCheckResult.Authenticated(result.data)
                        } else {
                            // No authenticated user
                            _isAuthenticated.value = false
                            _authCheckResult.value = AuthCheckResult.NotAuthenticated
                        }
                    }
                    is AuthResult.Error -> {
                        // Error checking auth status - assume not authenticated
                        _isAuthenticated.value = false
                        _authCheckResult.value = AuthCheckResult.Error(result.message)
                    }
                    is AuthResult.NetworkError -> {
                        // Network error - assume not authenticated for now
                        _isAuthenticated.value = false
                        _authCheckResult.value = AuthCheckResult.Error(result.message)
                    }
                    is AuthResult.ValidationError -> {
                        // Shouldn't happen for getCurrentUser, but handle gracefully
                        _isAuthenticated.value = false
                        _authCheckResult.value = AuthCheckResult.Error("Authentication validation error")
                    }
                }
            } catch (e: Exception) {
                // Unexpected error - assume not authenticated
                _isAuthenticated.value = false
                _authCheckResult.value = AuthCheckResult.Error(e.message ?: "Unknown error")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Force logout (called from UI when needed)
     */
    fun logout() {
        viewModelScope.launch {
            try {
                authRepository.logout()
                _isAuthenticated.value = false
                _authCheckResult.value = AuthCheckResult.NotAuthenticated
            } catch (e: Exception) {
                // Even if logout fails, clear local state
                _isAuthenticated.value = false
                _authCheckResult.value = AuthCheckResult.NotAuthenticated
            }
        }
    }

    /**
     * Refresh authentication status (useful for manual refresh)
     */
    fun refreshAuthStatus() {
        checkAuthenticationStatus()
    }

    /**
     * Clear auth check result after navigation is complete
     */
    fun clearAuthCheckResult() {
        _authCheckResult.value = null
    }
}

/**
 * Sealed class for authentication check results
 * Used by splash screen and navigation logic
 */
sealed class AuthCheckResult {
    data class Authenticated(val user: gr.pkcoding.notemarkapp.features.auth.domain.model.User) : AuthCheckResult()
    object NotAuthenticated : AuthCheckResult()
    object SessionExpired : AuthCheckResult()
    data class Error(val message: String) : AuthCheckResult()
}