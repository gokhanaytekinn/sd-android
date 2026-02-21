package com.gokhanaytekinn.sdandroid.data.model.request

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String? = null
)

data class SubscriptionRequest(
    val name: String,
    val icon: String?,
    val tier: String? = null,
    val amount: Double,
    val currency: String,
    val billingCycle: String,
    val startDate: String,
    val reminderEnabled: Boolean = false
)

data class SubscriptionUpdateRequest(
    val name: String? = null,
    val icon: String? = null,
    val tier: String? = null,
    val amount: Double? = null,
    val currency: String? = null,
    val billingCycle: String? = null,
    val startDate: String? = null,
    val reminderEnabled: Boolean? = null
)

data class FlagSuspiciousRequest(
    val reason: String
)

data class TransactionRequest(
    val subscriptionId: String?,
    val amount: Double,
    val currency: String,
    val type: String, // SUBSCRIPTION_PAYMENT, REFUND, UPGRADE, DOWNGRADE
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
    val currency: String,
    val billingCycle: String
)

data class FcmTokenRequest(
    val token: String
)

data class GoogleAuthRequest(
    val idToken: String
)
