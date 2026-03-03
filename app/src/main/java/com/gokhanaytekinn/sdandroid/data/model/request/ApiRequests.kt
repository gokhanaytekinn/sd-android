package com.gokhanaytekinn.sdandroid.data.model.request

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String? = null,
    val language: String? = "tr"
)

data class SubscriptionRequest(
    val name: String,
    val icon: String?,
    val tier: Int? = null,
    val amount: Double,
    val currency: Int,
    val billingCycle: Int,
    val billingDay: Int,
    val billingMonth: Int? = null,
    val reminderEnabled: Boolean = false,
    val jointEmails: List<String>? = null
)

data class SubscriptionUpdateRequest(
    val name: String? = null,
    val icon: String? = null,
    val tier: Int? = null,
    val amount: Double? = null,
    val currency: Int? = null,
    val billingCycle: Int? = null,
    val billingDay: Int? = null,
    val billingMonth: Int? = null,
    val reminderEnabled: Boolean? = null,
    val jointEmails: List<String>? = null
)

data class FlagSuspiciousRequest(
    val reason: String
)

data class TransactionRequest(
    val subscriptionId: String?,
    val amount: Double,
    val currency: Int,
    val type: Int, // 1: PAYMENT, 2: REFUND, 3: UPGRADE, 4: DOWNGRADE
    val description: String? = null
)

data class ReminderRequest(
    val title: String,
    val type: String, // SUBSCRIPTION_RENEWAL, PAYMENT_DUE, TRIAL_ENDING, SUSPICIOUS_ACTIVITY
    val message: String,
    val scheduledAt: String
)

data class ReminderUpdateRequest(
    val message: String? = null,
    val scheduledAt: String? = null,
    val isRead: Boolean? = null
)

data class ConversionRequest(
    val amount: Double,
    val currency: Int,
    val billingCycle: Int
)

data class FcmTokenRequest(
    val token: String
)

data class GoogleAuthRequest(
    val idToken: String
)

data class NotificationSettingsRequest(
    val enabled: Boolean,
    val language: String? = null
)

data class ForgotPasswordRequest(val email: String)

data class VerifyCodeRequest(val email: String, val code: String)

data class ResetPasswordRequest(val email: String, val code: String, val newPassword: String)
