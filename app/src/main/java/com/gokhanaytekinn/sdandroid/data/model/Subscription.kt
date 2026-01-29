package com.gokhanaytekinn.sdandroid.data.model

data class Subscription(
    val id: String,
    val name: String,
    val description: String? = null,
    val cost: Double,
    val currency: String = "USD",
    val billingCycle: BillingCycle,
    val nextBillingDate: String? = null,
    val category: String? = null,
    val iconUrl: String? = null,
    val isActive: Boolean = true
)

enum class BillingCycle {
    MONTHLY,
    YEARLY,
    WEEKLY,
    QUARTERLY
}
