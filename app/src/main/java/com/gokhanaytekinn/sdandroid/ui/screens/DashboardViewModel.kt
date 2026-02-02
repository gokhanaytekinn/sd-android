package com.gokhanaytekinn.sdandroid.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gokhanaytekinn.sdandroid.data.model.BillingCycle
import com.gokhanaytekinn.sdandroid.data.model.Subscription
import com.gokhanaytekinn.sdandroid.data.model.SubscriptionStats
import com.gokhanaytekinn.sdandroid.data.repository.SubscriptionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = SubscriptionRepository(application.applicationContext)
    
    private val _subscriptions = MutableStateFlow<List<Subscription>>(emptyList())
    val subscriptions: StateFlow<List<Subscription>> = _subscriptions.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _stats = MutableStateFlow(
        SubscriptionStats(
            totalMonthlyCost = 0.0,
            totalYearlyCost = 0.0,
            activeCount = 0
        )
    )
    val stats: StateFlow<SubscriptionStats> = _stats.asStateFlow()
    
    // Suspicious subscriptions count - load from API
    private val _suspiciousCount = MutableStateFlow(0)
    val suspiciousCount: StateFlow<Int> = _suspiciousCount.asStateFlow()
    
    init {
        loadSubscriptions()
        loadSuspiciousCount()
    }
    
    fun loadSubscriptions() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = repository.getSubscriptions()
            if (result.isSuccess) {
                _subscriptions.value = result.getOrNull() ?: emptyList()
                calculateStats()
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Failed to load subscriptions"
            }
            
            _isLoading.value = false
        }
    }
    
    private fun calculateStats() {
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
    
    fun onAddSubscription() {
        // Navigate to add subscription screen (placeholder)
    }
    
    fun onSubscriptionClick(id: String) {
        // Navigate to subscription detail screen (placeholder)
    }
    
    fun onViewAllClick() {
        // Navigate to all subscriptions screen (placeholder)
    }
    
    private fun loadSuspiciousCount() {
        viewModelScope.launch {
            val result = repository.getSuspiciousSubscriptions()
            if (result.isSuccess) {
                _suspiciousCount.value = result.getOrNull()?.size ?: 0
            }
        }
    }
}

