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
    val billingDay: Int? = null,
    val billingMonth: Int? = null,
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
) {
    fun getNextRenewalDate(): java.time.LocalDate? {
        if (billingDay == null) return null
        val now = java.time.LocalDate.now()
        return if (billingCycle == BillingCycle.YEARLY && billingMonth != null) {
            var target = java.time.LocalDate.of(now.year, billingMonth, minOf(billingDay, java.time.LocalDate.of(now.year, billingMonth, 1).lengthOfMonth()))
            if (target.isBefore(now)) {
                target = target.plusYears(1)
            }
            target
        } else {
            var target = now.withDayOfMonth(minOf(billingDay, now.lengthOfMonth()))
            if (target.isBefore(now)) {
                target = target.plusMonths(1)
                target = target.withDayOfMonth(minOf(billingDay, target.lengthOfMonth()))
            }
            target
        }
    }
}

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
