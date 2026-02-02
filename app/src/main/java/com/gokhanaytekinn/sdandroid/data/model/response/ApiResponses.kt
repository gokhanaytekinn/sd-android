package com.gokhanaytekinn.sdandroid.data.model.response

data class AuthResponse(
    val token: String?,
    val user: UserResponse?,
    val message: String?,
    val success: Boolean = false
)

data class UserResponse(
    val id: String,
    val email: String,
    val name: String?,
    val planType: String?, // FREE, PREMIUM_MONTHLY, PREMIUM_YEARLY
    val createdAt: String?
)

data class SubscriptionResponse(
    val id: String,
    val name: String,
    val description: String?,
    val cost: Double,
    val currency: String,
    val billingCycle: String,
    val nextBillingDate: String?,
    val category: String?,
    val status: String, // ACTIVE, SUSPENDED, CANCELLED, PENDING_APPROVAL
    val isSuspicious: Boolean = false,
    val icon: String?,
    val backgroundColor: String?,
    val createdAt: String?,
    val userId: String?
)

data class TransactionResponse(
    val id: String,
    val subscriptionId: String?,
    val amount: Double,
    val type: String,
    val status: String,
    val description: String?,
    val createdAt: String
)

data class PageTransactionResponse(
    val content: List<TransactionResponse>,
    val totalElements: Long,
    val totalPages: Int,
    val size: Int,
    val number: Int
)

data class ReminderResponse(
    val id: String,
    val subscriptionId: String,
    val type: String,
    val message: String,
    val scheduledDate: String,
    val isRead: Boolean,
    val createdAt: String
)

data class AnalyticsResponse(
    val totalSubscriptions: Int,
    val activeSubscriptions: Int,
    val totalMonthlyCost: Double,
    val totalYearlyCost: Double,
    val categoryBreakdown: Map<String, Double>?,
    val monthlyTrend: List<MonthlyData>?
)

data class MonthlyData(
    val month: String,
    val amount: Double
)

data class ErrorResponse(
    val status: Int,
    val errorCode: String?,
    val message: String,
    val userMessage: String?,
    val path: String?,
    val timestamp: String?
)
