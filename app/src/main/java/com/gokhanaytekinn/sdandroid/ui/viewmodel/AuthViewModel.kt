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
    val nameError: Int? = null,
    val emailError: Int? = null,
    val passwordError: Int? = null,
    val confirmPasswordError: Int? = null,
    val isAuthenticated: Boolean = false,
    val userName: String? = null,
    val userEmail: String? = null,
    val notificationsEnabled: Boolean = true,
    val language: String? = "tr",
    val resetEmail: String? = null,
    val isResetCodeVerified: Boolean = false
)

class AuthViewModel(context: Context) : ViewModel() {
    
    private val repository = AuthRepository(context)
    private val languagePreferences = com.gokhanaytekinn.sdandroid.data.preferences.LanguagePreferences(context)
    private val appContext = context.applicationContext
    
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
                        userEmail = user?.email,
                        notificationsEnabled = user?.notificationsEnabled ?: true,
                        language = user?.language ?: "tr"
                    )
                } else {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isAuthenticated = false
                    )
                }
            }
        }
    }
    
    fun login(email: String, password: String, onSuccess: () -> Unit) {
        if (!validateLogin(email, password)) return
        
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)
            
            val result = repository.login(email, password)
            
            if (result.isSuccess) {
                val authResponse = result.getOrNull()
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    isAuthenticated = true,
                    userName = authResponse?.user?.name,
                    userEmail = authResponse?.user?.email,
                    notificationsEnabled = authResponse?.user?.notificationsEnabled ?: true
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
    
    fun register(name: String, email: String, password: String, confirmPassword: String, onSuccess: () -> Unit) {
        if (!validateRegister(name, email, password, confirmPassword)) return
        
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)
            
            val result = repository.register(email, password, name)
            
            if (result.isSuccess) {
                val authResponse = result.getOrNull()
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    isAuthenticated = true,
                    userName = authResponse?.user?.name,
                    userEmail = authResponse?.user?.email,
                    notificationsEnabled = authResponse?.user?.notificationsEnabled ?: true
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
    
    private fun validateLogin(email: String, password: String): Boolean {
        var isValid = true
        var emailErr: Int? = null
        var passErr: Int? = null
        
        if (email.isBlank()) {
            emailErr = com.gokhanaytekinn.sdandroid.R.string.error_email_required
            isValid = false
        }
        
        if (password.isBlank()) {
            passErr = com.gokhanaytekinn.sdandroid.R.string.error_password_required
            isValid = false
        }
        
        _authState.value = _authState.value.copy(emailError = emailErr, passwordError = passErr)
        return isValid
    }
    
    private fun validateRegister(name: String, email: String, password: String, confirmPassword: String): Boolean {
        var isValid = true
        var nameErr: Int? = null
        var emailErr: Int? = null
        var passErr: Int? = null
        var confirmPassErr: Int? = null
        
        if (name.isBlank()) {
            nameErr = com.gokhanaytekinn.sdandroid.R.string.error_name_required
            isValid = false
        }
        
        if (email.isBlank()) {
            emailErr = com.gokhanaytekinn.sdandroid.R.string.error_email_required
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailErr = com.gokhanaytekinn.sdandroid.R.string.error_email_invalid
            isValid = false
        }
        
        if (password.isBlank()) {
            passErr = com.gokhanaytekinn.sdandroid.R.string.error_password_required
            isValid = false
        } else if (password.length < 6) {
            passErr = com.gokhanaytekinn.sdandroid.R.string.error_password_short
            isValid = false
        }
        
        if (confirmPassword.isBlank()) {
            confirmPassErr = com.gokhanaytekinn.sdandroid.R.string.error_password_required
            isValid = false
        } else if (password != confirmPassword) {
            confirmPassErr = com.gokhanaytekinn.sdandroid.R.string.error_passwords_do_not_match
            isValid = false
        }
        
        _authState.value = _authState.value.copy(
            nameError = nameErr,
            emailError = emailErr,
            passwordError = passErr,
            confirmPasswordError = confirmPassErr
        )
        return isValid
    }
    
    fun clearError() {
        _authState.value = _authState.value.copy(
            error = null,
            nameError = null,
            emailError = null,
            passwordError = null,
            confirmPasswordError = null
        )
    }
    
    fun signInWithGoogle(idToken: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)
            val result = repository.loginWithGoogle(idToken)
            if (result.isSuccess) {
                val user = result.getOrNull()?.user
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    isAuthenticated = true,
                    userName = user?.name,
                    userEmail = user?.email,
                    notificationsEnabled = user?.notificationsEnabled ?: true,
                    language = user?.language ?: "tr"
                )
                onSuccess()
            } else {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Google ile giriş başarısız"
                )
            }
        }
    }

    fun forgotPassword(email: String, onSuccess: () -> Unit) {
        if (email.isBlank()) {
            _authState.value = _authState.value.copy(emailError = com.gokhanaytekinn.sdandroid.R.string.error_email_required)
            return
        }

        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)
            val result = repository.forgotPassword(email)
            if (result.isSuccess) {
                _authState.value = _authState.value.copy(isLoading = false, resetEmail = email)
                onSuccess()
            } else {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Sıfırlama kodu gönderilemedi"
                )
            }
        }
    }

    fun verifyCode(code: String, onSuccess: () -> Unit) {
        val email = _authState.value.resetEmail ?: return
        if (code.length != 6) return

        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)
            val result = repository.verifyCode(email, code)
            if (result.isSuccess) {
                _authState.value = _authState.value.copy(isLoading = false, isResetCodeVerified = true)
                onSuccess()
            } else {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Geçersiz doğrulama kodu"
                )
            }
        }
    }

    fun resetPassword(code: String, newPassword: String, onSuccess: () -> Unit) {
        val email = _authState.value.resetEmail ?: return
        
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)
            val result = repository.resetPassword(email, code, newPassword)
            if (result.isSuccess) {
                _authState.value = _authState.value.copy(
                    isLoading = false, 
                    resetEmail = null, 
                    isResetCodeVerified = false
                )
                onSuccess()
            } else {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Şifre güncellenemedi"
                )
            }
        }
    }

    fun updateNotificationSettings(enabled: Boolean) {
        viewModelScope.launch {
            val language = com.gokhanaytekinn.sdandroid.data.preferences.LanguagePreferences(appContext).selectedLanguage.kotlinx.coroutines.flow.first()
            repository.updateNotificationSettings(enabled, language)
            _authState.value = _authState.value.copy(notificationsEnabled = enabled, language = language)
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _authState.value = AuthState()
        }
    }
}
