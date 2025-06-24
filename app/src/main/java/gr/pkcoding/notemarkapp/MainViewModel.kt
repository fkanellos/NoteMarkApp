package gr.pkcoding.notemarkapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated = _isAuthenticated.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        checkAuthenticationStatus()
    }

    private fun checkAuthenticationStatus() {
        viewModelScope.launch {
            // Simulate splash screen delay
            delay(2000L) // 2 seconds

            // TODO: Check if user has valid tokens in local storage
            // For now, assume not authenticated
            _isAuthenticated.value = false
            _isLoading.value = false
        }
    }

    fun logout() {
        viewModelScope.launch {
            // TODO: Clear tokens, navigate to landing
            _isAuthenticated.value = false
        }
    }
}