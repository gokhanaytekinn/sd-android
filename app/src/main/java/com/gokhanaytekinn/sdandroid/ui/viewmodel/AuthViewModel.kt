package com.gokhanaytekinn.sdandroid.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gokhanaytekinn.sdandroid.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false,
    val userName: String? = null,
    val userEmail: String? = null
)

class AuthViewModel(context: Context) : ViewModel() {
    
    private val repository = AuthRepository(context)
    
    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        restoreSession()
    }

    private fun restoreSession() {
        viewModelScope.launch {
            if (repository.isLoggedIn()) {
                _authState.value = _authState.value.copy(isLoading = true)
                val result = repository.getCurrentUser()
                if (result.isSuccess) {
                    val user = result.getOrNull()
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        userName = user?.name,
                        userEmail = user?.email
                    )
                } else {
                    // Token invalid or network error, but we treat it as not authenticated for safety
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isAuthenticated = false
                    )
                }
            }
        }
    }
    
    fun login(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)
            
            val result = repository.login(email, password)
            
            if (result.isSuccess) {
                val authResponse = result.getOrNull()
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    isAuthenticated = true,
                    userName = authResponse?.user?.name,
                    userEmail = authResponse?.user?.email
                )
                onSuccess()
            } else {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Login failed"
                )
            }
        }
    }
    
    fun register(name: String, email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)
            
            val result = repository.register(email, password, name)
            
            if (result.isSuccess) {
                val authResponse = result.getOrNull()
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    isAuthenticated = true,
                    userName = authResponse?.user?.name,
                    userEmail = authResponse?.user?.email
                )
                onSuccess()
            } else {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Registration failed"
                )
            }
        }
    }
    
    fun clearError() {
        _authState.value = _authState.value.copy(error = null)
    }
    
    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _authState.value = AuthState()
        }
    }
}
