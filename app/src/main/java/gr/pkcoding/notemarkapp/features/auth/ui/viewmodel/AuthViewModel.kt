package gr.pkcoding.notemarkapp.features.auth.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import gr.pkcoding.notemarkapp.features.auth.domain.model.AuthResult
import gr.pkcoding.notemarkapp.features.auth.usecase.CheckAuthStateUseCase
import gr.pkcoding.notemarkapp.features.auth.usecase.GetCurrentUserUseCase
import gr.pkcoding.notemarkapp.features.auth.usecase.LoginUseCase
import gr.pkcoding.notemarkapp.features.auth.usecase.LogoutUseCase
import gr.pkcoding.notemarkapp.features.auth.usecase.RegisterUseCase
import gr.pkcoding.notemarkapp.feature.auth.presentation.model.AuthEffect
import gr.pkcoding.notemarkapp.feature.auth.presentation.model.AuthFields
import gr.pkcoding.notemarkapp.feature.auth.presentation.model.AuthIntent
import gr.pkcoding.notemarkapp.feature.auth.presentation.model.AuthState
import gr.pkcoding.notemarkapp.feature.auth.presentation.model.AuthUiState
import gr.pkcoding.notemarkapp.feature.auth.presentation.model.clearFieldErrors
import gr.pkcoding.notemarkapp.feature.auth.presentation.model.clearForm
import gr.pkcoding.notemarkapp.feature.auth.presentation.model.setFieldErrors
import gr.pkcoding.notemarkapp.feature.auth.presentation.model.setFieldsEnabled
import gr.pkcoding.notemarkapp.feature.auth.presentation.model.setFocusedField
import gr.pkcoding.notemarkapp.feature.auth.presentation.model.togglePasswordVisibility
import gr.pkcoding.notemarkapp.feature.auth.presentation.model.updateField
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for authentication screens using MVI (Model-View-Intent) pattern
 *
 * Responsibilities:
 * - Manage authentication state and UI state
 * - Process user intents and coordinate with use cases
 * - Emit one-time effects for navigation and UI feedback
 * - Handle authentication flow coordination
 *
 * MVI Pattern Components:
 * - Model: AuthState (what the user sees) + AuthUiState (form states)
 * - View: Compose UI screens that observe state
 * - Intent: AuthIntent (what the user wants to do)
 *
 * @param loginUseCase Use case for user authentication
 * @param registerUseCase Use case for account creation
 * @param logoutUseCase Use case for session termination
 * @param getCurrentUserUseCase Use case for retrieving current user
 * @param checkAuthStateUseCase Use case for monitoring auth state changes
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val checkAuthStateUseCase: CheckAuthStateUseCase
) : ViewModel() {

    // Main authentication state (what the user sees)
    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state.asStateFlow()

    // UI-specific state for form management
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // One-time effects for navigation and UI feedback
    private val _effects = Channel<AuthEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        // Monitor authentication state changes for reactive navigation
        observeAuthState()

        // Check authentication status on ViewModel creation (app startup)
        processIntent(AuthIntent.CheckAuthStatus)
    }

    /**
     * Process user intents and update state accordingly
     *
     * This is the main entry point for all user actions in the authentication flow.
     * Each intent triggers specific business logic and state updates.
     *
     * @param intent The user's intention/action
     */
    fun processIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.Login -> handleLogin(intent.email, intent.password)
            is AuthIntent.Register -> handleRegister(intent.username, intent.email, intent.password, intent.confirmPassword)
            is AuthIntent.Logout -> handleLogout()
            is AuthIntent.NavigateToLogin -> handleNavigateToLogin()
            is AuthIntent.NavigateToRegister -> handleNavigateToRegister()
            is AuthIntent.RetryLastOperation -> handleRetryLastOperation()
            is AuthIntent.ClearError -> handleClearError()
            is AuthIntent.CheckAuthStatus -> handleCheckAuthStatus()
            is AuthIntent.UpdateField -> handleUpdateField(intent.field, intent.value)
            is AuthIntent.TogglePasswordVisibility -> handleTogglePasswordVisibility(intent.field)
        }
    }

    /**
     * Handle user login attempt
     */
    private fun handleLogin(email: String, password: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading("Logging in...")
            _uiState.value = _uiState.value.setFieldsEnabled(false)

            // Execute login use case
            val result = loginUseCase(LoginUseCase.Params(email, password))

            when (result) {
                is AuthResult.Success -> {
                    _state.value = AuthState.Success(result.data, isNewUser = false)
                    _effects.trySend(AuthEffect.NavigateToMain())
                    _effects.trySend(AuthEffect.ShowSuccessMessage("Welcome back!"))
                }
                is AuthResult.ValidationError -> {
                    _state.value = AuthState.ValidationError(result.field, result.message)
                    _uiState.value = _uiState.value
                        .setFieldErrors(mapOf(result.field to result.message))
                        .setFieldsEnabled(true)
                    _effects.trySend(AuthEffect.FocusField(result.field))
                }
                is AuthResult.NetworkError -> {
                    _state.value = AuthState.NetworkError(result.message)
                    _uiState.value = _uiState.value.setFieldsEnabled(true)
                    _effects.trySend(AuthEffect.ShowSnackbar(result.message, "Retry", isError = true))
                }
                is AuthResult.Error -> {
                    _state.value = AuthState.Error(result.message)
                    _uiState.value = _uiState.value.setFieldsEnabled(true)
                    _effects.trySend(AuthEffect.ShowSnackbar(result.message, isError = true))
                }
            }
        }
    }

    /**
     * Handle user registration attempt
     */
    private fun handleRegister(username: String, email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading("Creating account...")
            _uiState.value = _uiState.value.setFieldsEnabled(false)

            // Execute register use case
            val result = registerUseCase(
                RegisterUseCase.Params(username, email, password, confirmPassword)
            )

            when (result) {
                is AuthResult.Success -> {
                    // Registration successful - now log in automatically
                    handleAutoLoginAfterRegistration(email, password)
                }
                is AuthResult.ValidationError -> {
                    _state.value = AuthState.ValidationError(result.field, result.message)
                    _uiState.value = _uiState.value
                        .setFieldErrors(mapOf(result.field to result.message))
                        .setFieldsEnabled(true)
                    _effects.trySend(AuthEffect.FocusField(result.field))
                }
                is AuthResult.NetworkError -> {
                    _state.value = AuthState.NetworkError(result.message)
                    _uiState.value = _uiState.value.setFieldsEnabled(true)
                    _effects.trySend(AuthEffect.ShowSnackbar(result.message, "Retry", isError = true))
                }
                is AuthResult.Error -> {
                    _state.value = AuthState.Error(result.message)
                    _uiState.value = _uiState.value.setFieldsEnabled(true)
                    _effects.trySend(AuthEffect.ShowSnackbar(result.message, isError = true))
                }
            }
        }
    }

    /**
     * Auto-login after successful registration
     */
    private suspend fun handleAutoLoginAfterRegistration(email: String, password: String) {
        _state.value = AuthState.Loading("Logging you in...")

        val loginResult = loginUseCase(LoginUseCase.Params(email, password))

        when (loginResult) {
            is AuthResult.Success -> {
                _state.value = AuthState.Success(loginResult.data, isNewUser = true)
                _effects.trySend(AuthEffect.NavigateToMain())
                _effects.trySend(AuthEffect.ShowSuccessMessage("Account created successfully! Welcome to NoteMark!"))
            }
            is AuthResult.Error -> {
                // Registration worked but auto-login failed - redirect to login
                _state.value = AuthState.Idle
                _effects.trySend(AuthEffect.NavigateToLogin)
                _effects.trySend(AuthEffect.ShowSuccessMessage("Account created! Please log in."))
            }
            else -> {
                // Fallback - go to login screen
                _state.value = AuthState.Idle
                _effects.trySend(AuthEffect.NavigateToLogin)
            }
        }

        _uiState.value = _uiState.value.setFieldsEnabled(true)
    }

    /**
     * Handle user logout
     */
    private fun handleLogout() {
        viewModelScope.launch {
            _state.value = AuthState.Loading("Logging out...")

            val result = logoutUseCase(Unit)

            when (result) {
                is AuthResult.Success -> {
                    _state.value = AuthState.Idle
                    _uiState.value = _uiState.value.clearForm()
                    _effects.trySend(AuthEffect.NavigateToLanding)
                    _effects.trySend(AuthEffect.ShowSuccessMessage("Logged out successfully"))
                }
                is AuthResult.Error -> {
                    // Even if logout fails, clear local state and navigate away
                    _state.value = AuthState.Idle
                    _uiState.value = _uiState.value.clearForm()
                    _effects.trySend(AuthEffect.NavigateToLanding)
                    _effects.trySend(AuthEffect.ShowSnackbar("Logged out", isError = false))
                }
                else -> {
                    // Fallback - always allow logout from UI perspective
                    _state.value = AuthState.Idle
                    _effects.trySend(AuthEffect.NavigateToLanding)
                }
            }
        }
    }

    /**
     * Handle navigation to login screen
     */
    private fun handleNavigateToLogin() {
        _state.value = AuthState.Idle
        _uiState.value = _uiState.value.clearFieldErrors()
        _effects.trySend(AuthEffect.NavigateToLogin)
    }

    /**
     * Handle navigation to register screen
     */
    private fun handleNavigateToRegister() {
        _state.value = AuthState.Idle
        _uiState.value = _uiState.value.clearFieldErrors()
        _effects.trySend(AuthEffect.NavigateToRegister)
    }

    /**
     * Handle retry of last failed operation
     */
    private fun handleRetryLastOperation() {
        _state.value = AuthState.Idle
        _uiState.value = _uiState.value.clearFieldErrors().setFieldsEnabled(true)
    }

    /**
     * Handle clearing current error state
     */
    private fun handleClearError() {
        _state.value = AuthState.Idle
        _uiState.value = _uiState.value.clearFieldErrors()
    }

    /**
     * Handle checking authentication status (app startup)
     */
    private fun handleCheckAuthStatus() {
        viewModelScope.launch {
            _state.value = AuthState.Loading("Checking authentication...")

            val result = getCurrentUserUseCase(Unit)

            when (result) {
                is AuthResult.Success -> {
                    if (result.data != null) {
                        // User is already authenticated
                        _state.value = AuthState.Success(result.data, isNewUser = false)
                        _effects.trySend(AuthEffect.NavigateToMain(clearBackStack = true))
                    } else {
                        // No authenticated user
                        _state.value = AuthState.Idle
                        _effects.trySend(AuthEffect.NavigateToLanding)
                    }
                }
                is AuthResult.Error -> {
                    // Authentication check failed - assume not logged in
                    _state.value = AuthState.Idle
                    _effects.trySend(AuthEffect.NavigateToLanding)
                }
                else -> {
                    // Fallback - go to landing
                    _state.value = AuthState.Idle
                    _effects.trySend(AuthEffect.NavigateToLanding)
                }
            }
        }
    }

    /**
     * Handle form field updates
     */
    private fun handleUpdateField(field: String, value: String) {
        _uiState.value = _uiState.value.updateField(field, value)

        // Real-time validation for specific fields after first submission attempt
        if (_uiState.value.showRealTimeValidation) {
            validateFieldRealTime(field, value)
        }
    }

    /**
     * Handle password visibility toggle
     */
    private fun handleTogglePasswordVisibility(field: String) {
        _uiState.value = _uiState.value.togglePasswordVisibility(field)
    }

    /**
     * Perform real-time validation on a specific field
     */
    private fun validateFieldRealTime(field: String, value: String) {
        val currentErrors = _uiState.value.fieldErrors.toMutableMap()

        when (field) {
            AuthFields.EMAIL -> {
                if (value.isNotBlank() && !value.contains("@")) {
                    currentErrors[field] = "Invalid email format"
                } else {
                    currentErrors.remove(field)
                }
            }
            AuthFields.USERNAME -> {
                when {
                    value.isNotBlank() && value.length < 3 -> {
                        currentErrors[field] = "Username must be at least 3 characters"
                    }
                    value.length > 20 -> {
                        currentErrors[field] = "Username can't be longer than 20 characters"
                    }
                    else -> {
                        currentErrors.remove(field)
                    }
                }
            }
            AuthFields.PASSWORD -> {
                if (value.isNotBlank() && (value.length < 8 || !value.containsNumberOrSymbol())) {
                    currentErrors[field] = "Password must be at least 8 characters with a number or symbol"
                } else {
                    currentErrors.remove(field)
                }
            }
            AuthFields.CONFIRM_PASSWORD -> {
                val password = _uiState.value.getFieldValue(AuthFields.PASSWORD)
                if (value.isNotBlank() && value != password) {
                    currentErrors[field] = "Passwords do not match"
                } else {
                    currentErrors.remove(field)
                }
            }
        }

        _uiState.value = _uiState.value.copy(fieldErrors = currentErrors)
    }

    /**
     * Observe authentication state changes for reactive behavior
     */
    private fun observeAuthState() {
        checkAuthStateUseCase()
            .onEach { isAuthenticated ->
                // Handle authentication state changes from external sources
                // (e.g., token expiration, logout from another screen)
                if (!isAuthenticated && _state.value is AuthState.Success) {
                    // User was logged in but is now logged out (token expired, etc.)
                    _state.value = AuthState.Idle
                    _uiState.value = _uiState.value.clearForm()
                    _effects.trySend(AuthEffect.NavigateToLanding)
                    _effects.trySend(AuthEffect.ShowSnackbar("Session expired. Please log in again.", isError = true))
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Handle field focus changes (for validation timing)
     */
    fun onFieldFocusChanged(field: String, hasFocus: Boolean) {
        _uiState.value = _uiState.value.setFocusedField(if (hasFocus) field else null)

        // Validate field when it loses focus (if we've attempted submission before)
        if (!hasFocus && _uiState.value.showRealTimeValidation) {
            val value = _uiState.value.getFieldValue(field)
            validateFieldRealTime(field, value)
        }
    }

    /**
     * Get current form data for a specific screen
     */
    fun getLoginFormData(): Pair<String, String> {
        return Pair(
            _uiState.value.getFieldValue(AuthFields.EMAIL),
            _uiState.value.getFieldValue(AuthFields.PASSWORD)
        )
    }

    /**
     * Get current form data for register screen
     */
    fun getRegisterFormData(): RegisterFormData {
        return RegisterFormData(
            username = _uiState.value.getFieldValue(AuthFields.USERNAME),
            email = _uiState.value.getFieldValue(AuthFields.EMAIL),
            password = _uiState.value.getFieldValue(AuthFields.PASSWORD),
            confirmPassword = _uiState.value.getFieldValue(AuthFields.CONFIRM_PASSWORD)
        )
    }

    /**
     * Clear all form data (used when switching between screens)
     */
    fun clearFormData() {
        _uiState.value = _uiState.value.clearForm()
    }
}

/**
 * Data class for register form data
 */
data class RegisterFormData(
    val username: String,
    val email: String,
    val password: String,
    val confirmPassword: String
)

/**
 * Extension function for password validation
 */
private fun String.containsNumberOrSymbol(): Boolean {
    return any { it.isDigit() } || any { !it.isLetterOrDigit() }
}