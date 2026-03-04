package com.gokhanaytekinn.sdandroid.ui.viewmodel

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gokhanaytekinn.sdandroid.data.model.Subscription
import com.gokhanaytekinn.sdandroid.data.repository.SubscriptionRepository
import com.gokhanaytekinn.sdandroid.ui.screens.SuspiciousTransaction
import com.gokhanaytekinn.sdandroid.ui.theme.AdobeRed
import com.gokhanaytekinn.sdandroid.ui.theme.NetflixRed
import com.gokhanaytekinn.sdandroid.ui.theme.SpotifyGreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class SuspiciousPaymentsViewModel(context: Context) : ViewModel() {
    
    private val repository = SubscriptionRepository(context)
    
    // Initial mock data as per request, but managed in ViewModel
    private val _suspiciousTransactions = MutableStateFlow<List<SuspiciousTransaction>>(emptyList())
    val suspiciousTransactions: StateFlow<List<SuspiciousTransaction>> = _suspiciousTransactions.asStateFlow()

    private val _pendingInvitations = MutableStateFlow<List<com.gokhanaytekinn.sdandroid.data.model.SubscriptionInvitation>>(emptyList())
    val pendingInvitations: StateFlow<List<com.gokhanaytekinn.sdandroid.data.model.SubscriptionInvitation>> = _pendingInvitations.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            
            val transactionsResult = repository.getSuspiciousSubscriptions()
            if (transactionsResult.isSuccess) {
                _suspiciousTransactions.value = transactionsResult.getOrNull()?.map { it.toSuspiciousTransaction() } ?: emptyList()
            }

            val invitationsResult = repository.getPendingInvitations()
            if (invitationsResult.isSuccess) {
                _pendingInvitations.value = invitationsResult.getOrNull() ?: emptyList()
            }
            
            _isLoading.value = false
        }
    }

    private fun Subscription.toSuspiciousTransaction(): SuspiciousTransaction {
        return SuspiciousTransaction(
            name = name,
            date = this.getNextRenewalDate()?.toString() ?: "",
            category = category ?: "Eğlence",
            amount = cost,
            icon = icon ?: "N",
            backgroundColor = AdobeRed, // Default color or logic based on name
            subscriptionId = id,
            isInvitation = false
        )
    }
    
    private val _currentStep = MutableStateFlow(1)
    val currentStep: StateFlow<Int> = _currentStep.asStateFlow()
    
    val totalSteps = 3
    
    fun onConfirmTransaction(transaction: SuspiciousTransaction) {
        viewModelScope.launch {
            if (transaction.isInvitation) {
                transaction.invitationId?.let { id ->
                    repository.acceptInvitation(id)
                }
            } else {
                transaction.subscriptionId?.let { id ->
                    repository.approveSubscription(id)
                }
            }
            loadData()
        }
    }
    
    fun onRejectTransaction(transaction: SuspiciousTransaction) {
        viewModelScope.launch {
            if (transaction.isInvitation) {
                transaction.invitationId?.let { id ->
                    repository.rejectInvitation(id)
                }
            } else {
                // For direct suspicious transactions, rejection is currently advancing step
                advanceStep() 
            }
            loadData()
        }
    }
    
    private fun advanceStep() {
        if (_currentStep.value < totalSteps + 1) {
            _currentStep.value += 1
        }
    }
}
