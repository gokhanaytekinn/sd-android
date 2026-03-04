package com.gokhanaytekinn.sdandroid.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gokhanaytekinn.sdandroid.data.model.BillingCycle
import com.gokhanaytekinn.sdandroid.data.model.Subscription
import com.gokhanaytekinn.sdandroid.data.model.SubscriptionStats
import com.gokhanaytekinn.sdandroid.data.repository.SubscriptionRepository
import com.gokhanaytekinn.sdandroid.util.DateUtils
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
    
    // Upcoming subscriptions for preview
    private val _upcomingSubscriptions = MutableStateFlow<List<Subscription>>(emptyList())
    val upcomingSubscriptions: StateFlow<List<Subscription>> = _upcomingSubscriptions.asStateFlow()
    
    private val _pendingInvitations = MutableStateFlow<List<com.gokhanaytekinn.sdandroid.data.model.SubscriptionInvitation>>(emptyList())
    val pendingInvitations: StateFlow<List<com.gokhanaytekinn.sdandroid.data.model.SubscriptionInvitation>> = _pendingInvitations.asStateFlow()
    
    init {
        loadSubscriptions()
        loadSuspiciousCount()
        loadUpcomingSubscriptions()
        loadPendingInvitations()
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
            
            loadPendingInvitations()
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
    
    fun onSubscriptionClick() {
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

    private fun loadUpcomingSubscriptions() {
        viewModelScope.launch {
            val result = repository.getUpcomingSubscriptions()
            if (result.isSuccess) {
                // Filter locally for safety (max 10 days) and take only first 3 for preview
                val allSubscriptions = result.getOrNull() ?: emptyList()
                val upcomingSubs = allSubscriptions.filter { 
                    it.getNextRenewalDate() != null && DateUtils.isWithinNextDays(it.getNextRenewalDate()?.toString(), 10)
                }.sortedBy { it.getNextRenewalDate() }.take(3)
                _upcomingSubscriptions.value = upcomingSubs
            }
        }
    }

    fun loadPendingInvitations() {
        viewModelScope.launch {
            val result = repository.getPendingInvitations()
            if (result.isSuccess) {
                _pendingInvitations.value = result.getOrNull() ?: emptyList()
                _suspiciousCount.value = (_subscriptions.value.count { it.isSuspicious }) + _pendingInvitations.value.size
            }
        }
    }

    fun acceptInvitation(invitationId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.acceptInvitation(invitationId)
            if (result.isSuccess) {
                loadSubscriptions()
            } else {
                _error.value = "Davetiye kabul edilemedi"
            }
            _isLoading.value = false
        }
    }

    fun rejectInvitation(invitationId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.rejectInvitation(invitationId)
            if (result.isSuccess) {
                loadPendingInvitations()
            } else {
                _error.value = "Davetiye reddedilemedi"
            }
            _isLoading.value = false
        }
    }
}

