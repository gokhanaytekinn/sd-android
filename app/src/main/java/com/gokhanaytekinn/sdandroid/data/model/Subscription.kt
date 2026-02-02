package com.gokhanaytekinn.sdandroid.data.model

data class Subscription(
    val id: String,
    val name: String,
    val description: String? = null,
    val cost: Double,
    val currency: String = "TRY",
    val billingCycle: BillingCycle,
    val nextBillingDate: String? = null,
    val category: String? = null,
    val iconUrl: String? = null,
    val isActive: Boolean = true,
    val isSuspicious: Boolean = false,
    val icon: String? = null, // Icon letter or emoji
    val backgroundColor: String? = null // Hex color for icon background
)

enum class BillingCycle {
    MONTHLY,
    YEARLY,
    WEEKLY,
    QUARTERLY
}
