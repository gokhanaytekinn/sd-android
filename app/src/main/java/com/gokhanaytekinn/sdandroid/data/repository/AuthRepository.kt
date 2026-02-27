package com.gokhanaytekinn.sdandroid.data.repository

import android.content.Context
import com.gokhanaytekinn.sdandroid.data.api.ApiService
import com.gokhanaytekinn.sdandroid.data.api.RetrofitClient
import com.gokhanaytekinn.sdandroid.data.local.TokenManager
import com.gokhanaytekinn.sdandroid.data.model.request.*
import com.gokhanaytekinn.sdandroid.data.model.response.AuthResponse
import com.gokhanaytekinn.sdandroid.data.model.response.UserResponse

class AuthRepository(context: Context) {
    
    private val tokenManager = TokenManager(context)
    private val apiService: ApiService = RetrofitClient.createApiService(tokenManager)
    
    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                // Save token if login successful
                authResponse.token?.let { tokenManager.saveToken(it) }
                authResponse.user?.email?.let { tokenManager.saveUserEmail(it) }
                Result.success(authResponse)
            } else {
                // Parse error response
                val errorMessage = try {
                    val errorBody = response.errorBody()?.string()
                    if (!errorBody.isNullOrEmpty()) {
                        val gson = com.google.gson.Gson()
                        val errorResponse = gson.fromJson(errorBody, com.gokhanaytekinn.sdandroid.data.model.response.ErrorResponse::class.java)
                        errorResponse.message
                    } else {
                        "Login failed: ${response.message()}"
                    }
                } catch (e: Exception) {
                    "Login failed: ${response.message()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun register(email: String, password: String, name: String? = null): Result<AuthResponse> {
        return try {
            val response = apiService.register(RegisterRequest(email, password, name))
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                // Save token if registration successful
                authResponse.token?.let { tokenManager.saveToken(it) }
                authResponse.user?.email?.let { tokenManager.saveUserEmail(it) }
                Result.success(authResponse)
            } else {
                // Parse error response
                val errorMessage = try {
                    val errorBody = response.errorBody()?.string()
                    if (!errorBody.isNullOrEmpty()) {
                        val gson = com.google.gson.Gson()
                        val errorResponse = gson.fromJson(errorBody, com.gokhanaytekinn.sdandroid.data.model.response.ErrorResponse::class.java)
                        errorResponse.message
                    } else {
                        "Registration failed: ${response.message()}"
                    }
                } catch (e: Exception) {
                    "Registration failed: ${response.message()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getCurrentUser(): Result<UserResponse> {
        return try {
            val response = apiService.getCurrentUser()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                // Parse error response
                val errorMessage = try {
                    val errorBody = response.errorBody()?.string()
                    if (!errorBody.isNullOrEmpty()) {
                        val gson = com.google.gson.Gson()
                        val errorResponse = gson.fromJson(errorBody, com.gokhanaytekinn.sdandroid.data.model.response.ErrorResponse::class.java)
                        errorResponse.message
                    } else {
                        "Failed to get user: ${response.message()}"
                    }
                } catch (e: Exception) {
                    "Failed to get user: ${response.message()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun logout() {
        tokenManager.clearToken()
    }

    suspend fun updateFcmToken(token: String): Result<Unit> {
        return try {
            val response = apiService.updateFcmToken(FcmTokenRequest(token))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to update FCM token"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateNotificationSettings(enabled: Boolean, language: String? = null): Result<Unit> {
        return try {
            val response = apiService.updateNotificationSettings(NotificationSettingsRequest(enabled, language))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to update notification settings"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun isLoggedIn(): Boolean {
        return tokenManager.isLoggedIn()
    }

    suspend fun loginWithGoogle(idToken: String): Result<AuthResponse> {
        return try {
            val response = apiService.loginWithGoogle(
                com.gokhanaytekinn.sdandroid.data.model.request.GoogleAuthRequest(idToken)
            )
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                authResponse.token?.let { tokenManager.saveToken(it) }
                authResponse.user?.email?.let { tokenManager.saveUserEmail(it) }
                Result.success(authResponse)
            } else {
                val errorMessage = try {
                    val errorBody = response.errorBody()?.string()
                    if (!errorBody.isNullOrEmpty()) {
                        val gson = com.google.gson.Gson()
                        val errorResponse = gson.fromJson(errorBody, com.gokhanaytekinn.sdandroid.data.model.response.ErrorResponse::class.java)
                        errorResponse.message
                    } else "Google login failed: ${response.message()}"
                } catch (e: Exception) { "Google login failed: ${response.message()}" }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun forgotPassword(email: String): Result<Unit> {
        return try {
            val response = apiService.forgotPassword(ForgotPasswordRequest(email))
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Failed to send reset code"))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun verifyCode(email: String, code: String): Result<Unit> {
        return try {
            val response = apiService.verifyCode(VerifyCodeRequest(email, code))
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Invalid verification code"))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun resetPassword(email: String, code: String, newPassword: String): Result<Unit> {
        return try {
            val response = apiService.resetPassword(ResetPasswordRequest(email, code, newPassword))
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Failed to reset password"))
        } catch (e: Exception) { Result.failure(e) }
    }
}
