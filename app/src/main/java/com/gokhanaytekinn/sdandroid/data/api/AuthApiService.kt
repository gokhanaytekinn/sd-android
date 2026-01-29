package com.gokhanaytekinn.sdandroid.data.api

import com.gokhanaytekinn.sdandroid.data.model.AuthRequest
import com.gokhanaytekinn.sdandroid.data.model.AuthResponse
import com.gokhanaytekinn.sdandroid.data.model.User
import retrofit2.Response
import retrofit2.http.*

interface AuthApiService {
    
    @POST("/api/auth/login")
    suspend fun login(@Body request: AuthRequest): Response<AuthResponse>
    
    @POST("/api/auth/register")
    suspend fun register(@Body request: AuthRequest): Response<AuthResponse>
    
    @POST("/api/auth/logout")
    suspend fun logout(): Response<Unit>
    
    @GET("/api/auth/me")
    suspend fun getCurrentUser(): Response<User>
}
