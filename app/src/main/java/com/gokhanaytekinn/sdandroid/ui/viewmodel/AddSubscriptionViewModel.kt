package com.gokhanaytekinn.sdandroid.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gokhanaytekinn.sdandroid.data.model.BillingCycle
import com.gokhanaytekinn.sdandroid.data.model.InvitationParticipant
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

    private val _jointEmails = MutableStateFlow<List<String>>(emptyList())
    val jointEmails: StateFlow<List<String>> = _jointEmails.asStateFlow()

    private val _participants = MutableStateFlow<List<InvitationParticipant>?>(null)
    val participants: StateFlow<List<InvitationParticipant>?> = _participants.asStateFlow()

    private val _category = MutableStateFlow("category_other")
    val category: StateFlow<String> = _category.asStateFlow()

    // Validation States
    private val _nameError = MutableStateFlow<Int?>(null)
    val nameError: StateFlow<Int?> = _nameError.asStateFlow()
    
    private val _amountError = MutableStateFlow<Int?>(null)
    val amountError: StateFlow<Int?> = _amountError.asStateFlow()
    
    private val _currencyError = MutableStateFlow<Int?>(null)
    val currencyError: StateFlow<Int?> = _currencyError.asStateFlow()
    
    private val _dateError = MutableStateFlow<Int?>(null)
    val dateError: StateFlow<Int?> = _dateError.asStateFlow()

    fun updateName(value: String) {
        _name.value = value
        if (value.isNotBlank()) _nameError.value = null
    }
    
    fun updateAmount(value: String) {
        // Keep digits and at most one comma
        val cleanValue = value.filter { it.isDigit() || it == ',' }
        val parts = cleanValue.split(",")
        val finalValue = if (parts.size > 2) {
            // If more than one comma, keep only the first one
            parts[0] + "," + parts.drop(1).joinToString("")
        } else {
            cleanValue
        }
        
        _amount.value = finalValue
        if (finalValue.isNotBlank()) _amountError.value = null
    }
    
    fun updateCurrency(value: String) {
        _currency.value = value
        _currencyError.value = null
    }
    
    fun updateBillingCycle(value: BillingCycle) {
        _billingCycle.value = value
    }
    
    fun updateNextBillingDate(value: String) {
        _nextBillingDate.value = value
        if (value.isNotBlank()) _dateError.value = null
    }
    
    fun updateIsReminderEnabled(value: Boolean) {
        _isReminderEnabled.value = value
    }

    fun updateCategory(value: String) {
        _category.value = value
    }

    fun addJointEmail(email: String) {
        if (email.isNotBlank() && !_jointEmails.value.contains(email)) {
            _jointEmails.value = _jointEmails.value + email
        }
    }

    fun removeJointEmail(email: String) {
        _jointEmails.value = _jointEmails.value - email
    }

    private fun validate(): Boolean {
        var isValid = true
        
        if (_name.value.isBlank()) {
            _nameError.value = com.gokhanaytekinn.sdandroid.R.string.error_name_required
            isValid = false
        }
        
        if (_amount.value.isBlank()) {
            _amountError.value = com.gokhanaytekinn.sdandroid.R.string.error_amount_required
            isValid = false
        } else {
            val amountValue = _amount.value.toDoubleOrNull() ?: 0.0
            if (amountValue <= 0.0) {
                _amountError.value = com.gokhanaytekinn.sdandroid.R.string.error_amount_invalid
                isValid = false
            }
        }
        
        if (_currency.value.isBlank()) {
            _currencyError.value = com.gokhanaytekinn.sdandroid.R.string.error_currency_required
            isValid = false
        }
        
        if (_nextBillingDate.value.isBlank()) {
            _dateError.value = com.gokhanaytekinn.sdandroid.R.string.error_date_required
            isValid = false
        }
        
        return isValid
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
                    // Convert Double cost to string with comma (e.g. 12.5 -> "12,5")
                    val amountStr = it.cost.toString().replace('.', ',')
                        .removeSuffix(",0") // Clean up .0 case
                    _amount.value = amountStr
                    _currency.value = it.currency
                    _billingCycle.value = it.billingCycle
                    val rawDate = it.nextBillingDate ?: it.startDate ?: ""
                    _nextBillingDate.value = formatDateForUi(rawDate)
                    _isReminderEnabled.value = it.reminderEnabled
                    _category.value = it.category ?: "category_other"
                    _jointEmails.value = it.jointEmails ?: emptyList()
                    _participants.value = it.participants
                }
            } else {
                _error.value = "Abonelik bilgileri yüklenemedi: ${result.exceptionOrNull()?.message}"
            }
            _isLoading.value = false
        }
    }

    fun saveSubscription() {
        if (!validate()) return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val apiDate = formatDateForApi(_nextBillingDate.value)
                val subscription = Subscription(
                    id = _subscriptionId ?: UUID.randomUUID().toString(),
                    name = _name.value,
                    cost = _amount.value.replace(',', '.').toDoubleOrNull() ?: 0.0,
                    currency = _currency.value,
                    billingCycle = _billingCycle.value,
                    nextBillingDate = apiDate,
                    isActive = true,
                    startDate = if (_isEditMode.value) null else apiDate,
                    category = _category.value,
                    reminderEnabled = _isReminderEnabled.value,
                    jointEmails = _jointEmails.value.ifEmpty { null }
                )

                val result = if (_isEditMode.value && _subscriptionId != null) {
                    repository.updateSubscription(_subscriptionId!!, subscription)
                } else {
                    repository.createSubscription(subscription)
                }
                
                if (result.isSuccess) {
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
        _nameError.value = null
        _amountError.value = null
        _currencyError.value = null
        _dateError.value = null
        _name.value = ""
        _amount.value = ""
        _nextBillingDate.value = ""
        _subscriptionId = null
        _isEditMode.value = false
        _isReminderEnabled.value = false
        _category.value = "category_other"
        _jointEmails.value = emptyList()
        _participants.value = null
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
