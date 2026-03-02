package com.gokhanaytekinn.sdandroid.data.api

import com.gokhanaytekinn.sdandroid.data.model.request.*
import com.gokhanaytekinn.sdandroid.data.model.response.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    // ============ AUTHENTICATION ============
    
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>
    
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/google")
    suspend fun loginWithGoogle(@Body request: GoogleAuthRequest): Response<AuthResponse>
    
    @GET("api/auth/me")
    suspend fun getCurrentUser(): Response<UserResponse>

    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<Unit>

    @POST("api/auth/verify-code")
    suspend fun verifyCode(@Body request: VerifyCodeRequest): Response<Unit>

    @POST("api/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<Unit>
    
    // ============ USERS ============
    
    @PATCH("api/users/fcm-token")
    suspend fun updateFcmToken(@Body request: FcmTokenRequest): Response<Unit>
    
    @PATCH("api/users/notifications")
    suspend fun updateNotificationSettings(@Body request: NotificationSettingsRequest): Response<Unit>
    
    // ============ SUBSCRIPTIONS ============
    
    @GET("api/subscriptions")
    suspend fun getSubscriptions(
        @Query("status") status: String? = null,
        @Query("isSuspicious") isSuspicious: Boolean? = null
    ): Response<List<SubscriptionResponse>>
    
    @POST("api/subscriptions")
    suspend fun createSubscription(@Body request: SubscriptionRequest): Response<SubscriptionResponse>
    
    @GET("api/subscriptions/{id}")
    suspend fun getSubscription(@Path("id") id: String): Response<SubscriptionResponse>
    
    @PUT("api/subscriptions/{id}")
    suspend fun updateSubscription(
        @Path("id") id: String,
        @Body request: SubscriptionUpdateRequest
    ): Response<SubscriptionResponse>
    
    @GET("api/subscriptions/suspicious")
    suspend fun getSuspiciousSubscriptions(): Response<List<SubscriptionResponse>>

    @GET("api/subscriptions/upcoming")
    suspend fun getUpcomingSubscriptions(): Response<List<SubscriptionResponse>>
    
    @PATCH("api/subscriptions/{id}/approve")
    suspend fun approveSubscription(@Path("id") id: String): Response<SubscriptionResponse>
    
    @PATCH("api/subscriptions/{id}/flag")
    suspend fun flagAsSuspicious(
        @Path("id") id: String,
        @Body request: FlagSuspiciousRequest
    ): Response<SubscriptionResponse>
    
    @PATCH("api/subscriptions/{id}/cancel")
    suspend fun cancelSubscription(@Path("id") id: String): Response<Unit>
    
    // ============ TRANSACTIONS ============
    
    @GET("api/transactions")
    suspend fun getTransactions(
        @Query("type") type: String? = null,
        @Query("status") status: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): Response<PageTransactionResponse>
    
    @POST("api/transactions")
    suspend fun createTransaction(@Body request: TransactionRequest): Response<TransactionResponse>
    
    @GET("api/transactions/{id}")
    suspend fun getTransaction(@Path("id") id: String): Response<TransactionResponse>
    
    // ============ REMINDERS ============
    
    @GET("api/reminders")
    suspend fun getReminders(
        @Query("type") type: String? = null,
        @Query("isRead") isRead: Boolean? = null
    ): Response<List<ReminderResponse>>
    
    @POST("api/reminders")
    suspend fun createReminder(@Body request: ReminderRequest): Response<ReminderResponse>
    
    @PATCH("api/reminders/{id}")
    suspend fun updateReminder(
        @Path("id") id: String,
        @Body request: ReminderUpdateRequest
    ): Response<ReminderResponse>
    
    @PATCH("api/reminders/{id}/read")
    suspend fun markReminderAsRead(@Path("id") id: String): Response<Unit>
    
    @DELETE("api/reminders/{id}")
    suspend fun deleteReminder(@Path("id") id: String): Response<Unit>
    
    // ============ ANALYTICS ============
    
    @GET("api/analytics/subscriptions")
    suspend fun getSubscriptionMetrics(): Response<Map<String, Any>>
    
    @GET("api/analytics/revenue")
    suspend fun getRevenueMetrics(): Response<Map<String, Any>>
    
    // ============ CONVERSIONS ============
    
    @POST("api/conversions/upgrade")
    suspend fun convertToPremium(@Body request: ConversionRequest): Response<SubscriptionResponse>
    
    @POST("api/conversions/downgrade")
    suspend fun downgradeToFree(): Response<Unit>

    // ============ INVITATIONS ============

    @GET("api/invitations/pending")
    suspend fun getPendingInvitations(): Response<List<com.gokhanaytekinn.sdandroid.data.model.SubscriptionInvitation>>

    @POST("api/invitations/{id}/accept")
    suspend fun acceptInvitation(@Path("id") id: String): Response<Unit>

    @POST("api/invitations/{id}/reject")
    suspend fun rejectInvitation(@Path("id") id: String): Response<Unit>
}
