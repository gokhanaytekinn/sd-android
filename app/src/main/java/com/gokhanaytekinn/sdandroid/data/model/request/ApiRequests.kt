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
    val startDate: String
)

data class FlagSuspiciousRequest(
    val isSuspicious: Boolean,
    val reason: String? = null
)

data class TransactionRequest(
    val subscriptionId: String?,
    val amount: Double,
    val type: String, // SUBSCRIPTION_PAYMENT, REFUND, UPGRADE, DOWNGRADE
    val status: String = "COMPLETED", // COMPLETED, PENDING, FAILED, REFUNDED
    val description: String? = null
)

data class ReminderRequest(
    val subscriptionId: String,
    val type: String, // SUBSCRIPTION_RENEWAL, PAYMENT_DUE, TRIAL_ENDING, SUSPICIOUS_ACTIVITY
    val message: String,
    val scheduledDate: String
)

data class ReminderUpdateRequest(
    val message: String? = null,
    val scheduledDate: String? = null,
    val isRead: Boolean? = null
)

data class ConversionRequest(
    val planType: String, // PREMIUM_MONTHLY, PREMIUM_YEARLY
    val paymentMethod: String? = null
)
