package com.gokhanaytekinn.sdandroid.data.model

data class SubscriptionStats(
    val totalMonthlyCost: Double,
    val totalYearlyCost: Double,
    val activeCount: Int,
    val currency: String = "USD"
)
