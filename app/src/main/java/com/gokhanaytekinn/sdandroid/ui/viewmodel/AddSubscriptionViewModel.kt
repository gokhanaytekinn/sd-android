package com.gokhanaytekinn.sdandroid.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gokhanaytekinn.sdandroid.data.model.BillingCycle
import com.gokhanaytekinn.sdandroid.data.model.Subscription
import com.gokhanaytekinn.sdandroid.data.repository.SubscriptionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AddSubscriptionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SubscriptionRepository(application)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess.asStateFlow()
    
    private var _subscriptionId: String? = null
    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode.asStateFlow()

    // Form State
    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()
    
    private val _amount = MutableStateFlow("")
    val amount: StateFlow<String> = _amount.asStateFlow()
    
    private val _currency = MutableStateFlow("TRY")
    val currency: StateFlow<String> = _currency.asStateFlow()
    
    private val _billingCycle = MutableStateFlow(BillingCycle.MONTHLY)
    val billingCycle: StateFlow<BillingCycle> = _billingCycle.asStateFlow()
    
    // YYYY-MM-DD
    private val _nextBillingDate = MutableStateFlow("")
    val nextBillingDate: StateFlow<String> = _nextBillingDate.asStateFlow()
    
    private val _isReminderEnabled = MutableStateFlow(false)
    val isReminderEnabled: StateFlow<Boolean> = _isReminderEnabled.asStateFlow()

    fun updateName(value: String) {
        _name.value = value
    }
    
    fun updateAmount(value: String) {
        _amount.value = value
    }
    
    fun updateCurrency(value: String) {
        _currency.value = value
    }
    
    fun updateBillingCycle(value: BillingCycle) {
        _billingCycle.value = value
    }
    
    fun updateNextBillingDate(value: String) {
        _nextBillingDate.value = value
    }
    
    fun updateIsReminderEnabled(value: Boolean) {
        _isReminderEnabled.value = value
    }

    fun loadSubscription(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _subscriptionId = id
            _isEditMode.value = true
            
            val result = repository.getSubscription(id)
            if (result.isSuccess) {
                val sub = result.getOrNull()
                sub?.let {
                    _name.value = it.name
                    _amount.value = it.cost.toString()
                    _currency.value = it.currency
                    _billingCycle.value = it.billingCycle
                    val rawDate = it.nextBillingDate ?: it.startDate ?: ""
                    _nextBillingDate.value = formatDateForUi(rawDate)
                    _isReminderEnabled.value = it.reminderEnabled
                }
            } else {
                _error.value = "Abonelik bilgileri yüklenemedi: ${result.exceptionOrNull()?.message}"
            }
            _isLoading.value = false
        }
    }

    fun saveSubscription() {
        if (_name.value.isBlank() || _amount.value.isBlank()) {
            _error.value = "Lütfen alanları doldurunuz."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val apiDate = formatDateForApi(_nextBillingDate.value)
                val subscription = Subscription(
                    id = _subscriptionId ?: UUID.randomUUID().toString(),
                    name = _name.value,
                    cost = _amount.value.toDoubleOrNull() ?: 0.0,
                    currency = _currency.value,
                    billingCycle = _billingCycle.value,
                    nextBillingDate = apiDate,
                    isActive = true,
                    startDate = if (_isEditMode.value) null else apiDate,
                    reminderEnabled = _isReminderEnabled.value
                )

                val result = if (_isEditMode.value && _subscriptionId != null) {
                    repository.updateSubscription(_subscriptionId!!, subscription)
                } else {
                    repository.createSubscription(subscription)
                }
                
                if (result.isSuccess) {
                    // TODO: Handle reminder creation/update if enabled
                    _isSuccess.value = true
                } else {
                    _error.value = result.exceptionOrNull()?.message ?: "Bir hata oluştu"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    

    
    fun resetState() {
        _isSuccess.value = false
        _error.value = null
        _name.value = ""
        _amount.value = ""
        _name.value = ""
        _amount.value = ""
        _nextBillingDate.value = ""
        _subscriptionId = null
        _isEditMode.value = false
        _isReminderEnabled.value = false
    }

    private fun formatDateForApi(uiDate: String): String? {
        if (uiDate.isBlank()) return null
        return try {
            val parts = uiDate.split(".")
            if (parts.size == 3) {
                "${parts[2]}-${parts[1]}-${parts[0]}"
            } else {
                uiDate
            }
        } catch (e: Exception) {
            uiDate
        }
    }

    private fun formatDateForUi(apiDate: String): String {
        if (apiDate.isBlank()) return ""
        return try {
            val parts = apiDate.split("-")
            if (parts.size == 3) {
                "${parts[2]}.${parts[1]}.${parts[0]}"
            } else {
                apiDate
            }
        } catch (e: Exception) {
            apiDate
        }
    }
}
