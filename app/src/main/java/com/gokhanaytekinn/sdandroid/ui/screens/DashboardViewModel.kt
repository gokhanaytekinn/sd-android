package com.gokhanaytekinn.sdandroid.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gokhanaytekinn.sdandroid.data.model.BillingCycle
import com.gokhanaytekinn.sdandroid.data.model.Subscription
import com.gokhanaytekinn.sdandroid.data.model.SubscriptionStats
import com.gokhanaytekinn.sdandroid.data.repository.SubscriptionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {
    
    private val repository = SubscriptionRepository()
    
    private val _subscriptions = MutableStateFlow<List<Subscription>>(emptyList())
    val subscriptions: StateFlow<List<Subscription>> = _subscriptions.asStateFlow()
    
    private val _stats = MutableStateFlow(
        SubscriptionStats(
            totalMonthlyCost = 0.0,
            totalYearlyCost = 0.0,
            activeCount = 0
        )
    )
    val stats: StateFlow<SubscriptionStats> = _stats.asStateFlow()
    
    init {
        loadSubscriptions()
        loadStats()
    }
    
    private fun loadSubscriptions() {
        viewModelScope.launch {
            // Load from API (will fail in demo mode, so use mock data)
            val result = repository.getSubscriptions()
            if (result.isSuccess) {
                _subscriptions.value = result.getOrNull() ?: emptyList()
            } else {
                // Mock data for demonstration
                _subscriptions.value = getMockSubscriptions()
            }
        }
    }
    
    private fun loadStats() {
        viewModelScope.launch {
            // Calculate stats from subscriptions
            val subs = _subscriptions.value
            val monthlyCost = subs.filter { it.isActive }.sumOf { 
                when (it.billingCycle) {
                    BillingCycle.MONTHLY -> it.cost
                    BillingCycle.YEARLY -> it.cost / 12
                    BillingCycle.WEEKLY -> it.cost * 4
                    BillingCycle.QUARTERLY -> it.cost / 3
                }
            }
            
            _stats.value = SubscriptionStats(
                totalMonthlyCost = monthlyCost,
                totalYearlyCost = monthlyCost * 12,
                activeCount = subs.count { it.isActive }
            )
        }
    }
    
    fun onAddSubscription() {
        // Navigate to add subscription screen (placeholder)
    }
    
    fun onSubscriptionClick(id: String) {
        // Navigate to subscription detail screen (placeholder)
    }
    
    fun onViewAllClick() {
        // Navigate to all subscriptions screen (placeholder)
    }
    
    private fun getMockSubscriptions(): List<Subscription> {
        return listOf(
            Subscription(
                id = "1",
                name = "Netflix",
                description = "Streaming service",
                cost = 15.99,
                currency = "USD",
                billingCycle = BillingCycle.MONTHLY,
                nextBillingDate = "2024-02-15",
                category = "Entertainment",
                isActive = true
            ),
            Subscription(
                id = "2",
                name = "Spotify",
                description = "Music streaming",
                cost = 9.99,
                currency = "USD",
                billingCycle = BillingCycle.MONTHLY,
                nextBillingDate = "2024-02-10",
                category = "Music",
                isActive = true
            ),
            Subscription(
                id = "3",
                name = "Adobe Creative Cloud",
                description = "Design tools",
                cost = 54.99,
                currency = "USD",
                billingCycle = BillingCycle.MONTHLY,
                nextBillingDate = "2024-02-20",
                category = "Productivity",
                isActive = true
            )
        )
    }
}
