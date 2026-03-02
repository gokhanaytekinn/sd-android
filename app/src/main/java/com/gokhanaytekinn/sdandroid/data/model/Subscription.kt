package com.gokhanaytekinn.sdandroid.data.model

data class InvitationParticipant(
    val email: String,
    val name: String?,
    val status: String
)

data class Subscription(
    val id: String,
    val name: String,
    val cost: Double,
    val currency: String = "TRY",
    val billingCycle: BillingCycle,
    val nextBillingDate: String? = null,
    val startDate: String? = null,
    val category: String? = null,
    val iconUrl: String? = null,
    val isActive: Boolean = true,
    val isSuspicious: Boolean = false,
    val icon: String? = null,
    val status: String? = null,
    val tier: String? = null,
    val reminderEnabled: Boolean = false,
    val jointEmails: List<String>? = null,
    val isOwner: Boolean = true,
    val participants: List<InvitationParticipant>? = null
)

enum class BillingCycle {
    MONTHLY,
    YEARLY,
    WEEKLY,
    QUARTERLY
}
