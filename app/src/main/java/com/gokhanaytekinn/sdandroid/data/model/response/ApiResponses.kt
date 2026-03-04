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
    val tier: Int?, // FREE, PREMIUM
    val notificationsEnabled: Boolean = true,
    val language: String? = "tr",
    val createdAt: String?
)

data class InvitationParticipant(
    val email: String,
    val name: String?,
    val status: String // PENDING, ACCEPTED, REJECTED
)

data class SubscriptionResponse(
    val id: String,
    val name: String,
    val icon: String?,
    val tier: Int?,
    val amount: Double,
    val currency: Int,
    val billingCycle: Int,
    val endDate: String?,
    val billingDay: Int?,
    val billingMonth: Int?,
    val status: Int, // ACTIVE = 1, SUSPENDED = 2, CANCELLED = 3, PENDING_APPROVAL = 4
    val isSuspicious: Boolean = false,
    val suspiciousReason: String?,
    val isApproved: Boolean = false,
    val reminderEnabled: Boolean = false,
    val approvedAt: String?,
    val approvedBy: String?,
    val userId: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val jointEmails: List<String>? = null,
    val owner: Boolean? = true,
    val participants: List<InvitationParticipant>? = null
)

data class TransactionResponse(
    val id: String,
    val subscriptionId: String?,
    val userId: String?,
    val amount: Double,
    val currency: Int?,
    val type: Int,
    val status: Int,
    val description: String?,
    val metadata: Map<String, Any>?,
    val createdAt: String,
    val updatedAt: String?
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
    val userId: String?,
    val title: String?,
    val type: String,
    val message: String,
    val scheduledAt: String,
    val sentAt: String?,
    val isRead: Boolean,
    val metadata: Map<String, Any>?,
    val createdAt: String,
    val updatedAt: String?
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
