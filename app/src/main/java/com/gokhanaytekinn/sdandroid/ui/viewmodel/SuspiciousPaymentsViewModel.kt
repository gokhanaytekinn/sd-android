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
    private val _suspiciousTransactions = MutableStateFlow<List<SuspiciousTransaction>>(
        listOf(
            SuspiciousTransaction("Netflix", "24 Ekim", "Eğlence", 129.99, "N", NetflixRed),
            SuspiciousTransaction("Spotify", "22 Ekim", "Müzik", 39.99, "🎵", SpotifyGreen),
            SuspiciousTransaction("Adobe Inc.", "15 Ekim", "Yazılım", 350.00, "Ae", AdobeRed)
        )
    )
    val suspiciousTransactions: StateFlow<List<SuspiciousTransaction>> = _suspiciousTransactions.asStateFlow()
    
    private val _currentStep = MutableStateFlow(1)
    val currentStep: StateFlow<Int> = _currentStep.asStateFlow()
    
    val totalSteps = 3
    
    fun onConfirmTransaction(transaction: SuspiciousTransaction) {
        viewModelScope.launch {
            // Add to subscriptions
            val newSubscription = Subscription(
                id = UUID.randomUUID().toString(),
                name = transaction.name,
                cost = transaction.amount,
                currency = "TRY",
                billingCycle = com.gokhanaytekinn.sdandroid.data.model.BillingCycle.MONTHLY,
                nextBillingDate = transaction.date,
                icon = transaction.icon
            )
            repository.createSubscription(newSubscription)
            
            advanceStep()
        }
    }
    
    fun onRejectTransaction() {
        advanceStep()
    }
    
    private fun advanceStep() {
        if (_currentStep.value < totalSteps + 1) {
            _currentStep.value += 1
        }
    }
}
