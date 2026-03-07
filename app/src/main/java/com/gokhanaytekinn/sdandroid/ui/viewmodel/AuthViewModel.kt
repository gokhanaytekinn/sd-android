package com.gokhanaytekinn.sdandroid.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gokhanaytekinn.sdandroid.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.channels.Channel
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
    val isResetCodeVerified: Boolean = false,
    val tier: Int? = 1
)

class AuthViewModel(context: Context) : ViewModel() {
    
    private val repository = AuthRepository(context)
    private val languagePreferences = com.gokhanaytekinn.sdandroid.data.preferences.LanguagePreferences(context)
    private val premiumPreferences = com.gokhanaytekinn.sdandroid.data.preferences.PremiumPreferences(context)
    private val appContext = context.applicationContext
    
    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val focusChannel = Channel<String>()
    val focusEvent = focusChannel.receiveAsFlow()


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
                        language = user?.language ?: "tr",
                        tier = user?.tier ?: 1
                    )
                    premiumPreferences.setPremium((user?.tier ?: 1) >= 2)
                    // Sync local language to backend if they differ
                    syncLanguageIfNeeded()
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
                    notificationsEnabled = authResponse?.user?.notificationsEnabled ?: true,
                    language = authResponse?.user?.language ?: "tr",
                    tier = authResponse?.user?.tier ?: 1
                )
                syncLanguageIfNeeded()
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
            
            val localLanguage = languagePreferences.selectedLanguage.first()
            val result = repository.register(email, password, name, localLanguage)
            
            if (result.isSuccess) {
                val authResponse = result.getOrNull()
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    isAuthenticated = true,
                    userName = authResponse?.user?.name,
                    userEmail = authResponse?.user?.email,
                    notificationsEnabled = authResponse?.user?.notificationsEnabled ?: true,
                    language = authResponse?.user?.language ?: localLanguage,
                    tier = authResponse?.user?.tier ?: 1
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
        
        if (!isValid) {
            val firstError = if (emailErr != null) "email" else if (passErr != null) "password" else null
            firstError?.let {
                viewModelScope.launch { focusChannel.send(it) }
            }
        }
        
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
        
        if (!isValid) {
            val firstError = when {
                nameErr != null -> "name"
                emailErr != null -> "email"
                passErr != null -> "password"
                confirmPassErr != null -> "confirmPassword"
                else -> null
            }
            firstError?.let {
                viewModelScope.launch { focusChannel.send(it) }
            }
        }
        
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
    
    fun clearGeneralError() {
        _authState.value = _authState.value.copy(error = null)
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
                    language = user?.language ?: "tr",
                    tier = user?.tier ?: 1
                )
                premiumPreferences.setPremium((user?.tier ?: 1) >= 2)
                syncLanguageIfNeeded()
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
            viewModelScope.launch { focusChannel.send("email") }
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
        if (code.length != 6) {
             viewModelScope.launch { focusChannel.send("code") }
             // We can also set a specific error here if needed, but UI keeps handling it or we just focus.
             // Currently verification logic in viewmodel assumes 6 chars without setting field error.
             return
        }

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
        if (newPassword.isBlank() || newPassword.length < 6) {
             viewModelScope.launch { focusChannel.send("password") }
             // UI has its own validation but we ensure focus occurs here if called directly.
        }
        
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
            val language = com.gokhanaytekinn.sdandroid.data.preferences.LanguagePreferences(appContext).selectedLanguage.first()
            repository.updateNotificationSettings(enabled, language)
            _authState.value = _authState.value.copy(notificationsEnabled = enabled, language = language)
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            premiumPreferences.setPremium(false)
            _authState.value = AuthState()
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)
            val result = repository.deleteAccount()
            if (result.isSuccess) {
                premiumPreferences.setPremium(false)
                _authState.value = AuthState()
            } else {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Hesap silinemedi"
                )
            }
        }
    }

    private fun syncLanguageIfNeeded() {
        viewModelScope.launch {
            val localLanguage = languagePreferences.selectedLanguage.first()
            val backendLanguage = _authState.value.language
            
            if (localLanguage != backendLanguage) {
                repository.updateNotificationSettings(_authState.value.notificationsEnabled, localLanguage)
                _authState.value = _authState.value.copy(language = localLanguage)
            }
        }
    }
}
