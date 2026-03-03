package com.gokhanaytekinn.sdandroid.data.model

enum class SubscriptionStatus(val value: Int) {
    ACTIVE(1),
    SUSPENDED(2),
    CANCELLED(3),
    PENDING_APPROVAL(4);

    companion object {
        fun fromValue(value: Int): SubscriptionStatus {
            return entries.find { it.value == value } ?: ACTIVE
        }
    }
}

data class InvitationParticipant(
    val email: String,
    val name: String?,
    val status: String
)

data class Subscription(
    val id: String,
    val suspiciousReason: String? = null,
    val name: String,
    val cost: Double,
    val currency: Int,
    val billingCycle: BillingCycle,
    val nextBillingDate: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val category: String? = null,
    val icon: String? = null,
    val status: Int = 1, // ACTIVE = 1, SUSPENDED = 2, CANCELLED = 3, PENDING_APPROVAL = 4
    val isActive: Boolean = status == 1 || status == 4, // 1: ACTIVE, 4: PENDING_APPROVAL are considered active in UI
    val isSuspicious: Boolean = false,
    val tier: Int? = null,
    val reminderEnabled: Boolean = false,
    val jointEmails: List<String>? = null,
    val isOwner: Boolean = true,
    val participants: List<InvitationParticipant>? = null
)

enum class BillingCycle(val value: Int) {
    MONTHLY(1),
    YEARLY(2),
    WEEKLY(3),
    QUARTERLY(4);

    companion object {
        fun fromValue(value: Int): BillingCycle {
            return entries.find { it.value == value } ?: MONTHLY
        }

        fun fromString(value: String): BillingCycle {
            return try {
                valueOf(value.uppercase())
            } catch (e: Exception) {
                MONTHLY
            }
        }
    }
}
