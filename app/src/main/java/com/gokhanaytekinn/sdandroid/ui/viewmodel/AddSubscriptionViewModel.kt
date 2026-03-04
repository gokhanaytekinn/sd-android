package com.gokhanaytekinn.sdandroid.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gokhanaytekinn.sdandroid.data.model.BillingCycle
import com.gokhanaytekinn.sdandroid.data.model.InvitationParticipant
import com.gokhanaytekinn.sdandroid.data.model.Subscription
import com.gokhanaytekinn.sdandroid.data.repository.SubscriptionRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.launch
import java.util.UUID

class AddSubscriptionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SubscriptionRepository(application)


    private val premiumPreferences = com.gokhanaytekinn.sdandroid.data.preferences.PremiumPreferences(application)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private val _showInterstitialAd = MutableSharedFlow<Boolean>()
    val showInterstitialAd: SharedFlow<Boolean> = _showInterstitialAd.asSharedFlow()
    
    private var _subscriptionId: String? = null
    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode.asStateFlow()

    // Form State
    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()
    
    private val _amount = MutableStateFlow("")
    val amount: StateFlow<String> = _amount.asStateFlow()
    
    private val _currency = MutableStateFlow(1)
    val currency: StateFlow<Int> = _currency.asStateFlow()
    
    private val _billingCycle = MutableStateFlow(BillingCycle.MONTHLY)
    val billingCycle: StateFlow<BillingCycle> = _billingCycle.asStateFlow()
    
    private val _billingDay = MutableStateFlow(1)
    val billingDay: StateFlow<Int> = _billingDay.asStateFlow()

    private val _billingMonth = MutableStateFlow<Int?>(null)
    val billingMonth: StateFlow<Int?> = _billingMonth.asStateFlow()
    
    private val _isReminderEnabled = MutableStateFlow(false)
    val isReminderEnabled: StateFlow<Boolean> = _isReminderEnabled.asStateFlow()

    private val _jointEmails = MutableStateFlow<List<String>>(emptyList())
    val jointEmails: StateFlow<List<String>> = _jointEmails.asStateFlow()

    private val _participants = MutableStateFlow<List<InvitationParticipant>?>(null)
    val participants: StateFlow<List<InvitationParticipant>?> = _participants.asStateFlow()

    private val _category = MutableStateFlow("")
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

    private val focusChannel = Channel<String>()
    val focusEvent = focusChannel.receiveAsFlow()

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
    
    fun updateBillingCycle(value: BillingCycle) {
        _billingCycle.value = value
    }

    fun updateCurrency(value: Int) {
        _currency.value = value
        _currencyError.value = null
    }
    
    fun updateBillingDay(value: Int) {
        _billingDay.value = value
        _dateError.value = null
    }
    
    fun updateBillingMonth(value: Int?) {
        _billingMonth.value = value
        _dateError.value = null
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
        var firstErrorField: String? = null
        
        if (_name.value.isBlank()) {
            _nameError.value = com.gokhanaytekinn.sdandroid.R.string.error_name_required
            isValid = false
            if (firstErrorField == null) firstErrorField = "name"
        }
        
        if (_amount.value.isBlank()) {
            _amountError.value = com.gokhanaytekinn.sdandroid.R.string.error_amount_required
            isValid = false
            if (firstErrorField == null) firstErrorField = "amount"
        } else {
            val amountValue = _amount.value.replace(',', '.').toDoubleOrNull() ?: 0.0
            if (amountValue <= 0.0) {
                _amountError.value = com.gokhanaytekinn.sdandroid.R.string.error_amount_invalid
                isValid = false
                if (firstErrorField == null) firstErrorField = "amount"
            }
        }
        
        // Removed currency string empty check because it is an Int now.
        
        if (_billingDay.value == 0) {
            _dateError.value = com.gokhanaytekinn.sdandroid.R.string.error_date_required
            isValid = false
            // Date is not a text input, so no explicit focus requester string for now
        }
        
        if (_billingCycle.value == BillingCycle.YEARLY && _billingMonth.value == null) {
            _dateError.value = com.gokhanaytekinn.sdandroid.R.string.error_date_required
            isValid = false
        }
        
        if (_category.value.isBlank()) {
            _error.value = "Lütfen bir kategori seçin" // Fallback string
            isValid = false
        }
        
        if (firstErrorField != null) {
            viewModelScope.launch {
                focusChannel.send(firstErrorField!!)
            }
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
                    _billingDay.value = it.billingDay ?: 1
                    _billingMonth.value = it.billingMonth
                    _isReminderEnabled.value = it.reminderEnabled
                    _category.value = it.category ?: ""
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
                // Check Premium Limit
                val isPremiumValue = premiumPreferences.isPremium.first()
                if (!isPremiumValue && !_isEditMode.value) {
                    val currentSubsResult = repository.getSubscriptions()
                    if (currentSubsResult.isSuccess) {
                        val activeCount = currentSubsResult.getOrNull()?.filter { it.isActive }?.size ?: 0
                        if (activeCount >= 4) {
                            _error.value = "Ücretsiz planda en fazla 4 aktif abonelik ekleyebilirsiniz. Lütfen Premium'a geçin."
                            _isLoading.value = false
                            return@launch
                        }
                    }
                }

                val subscription = Subscription(
                    id = _subscriptionId ?: UUID.randomUUID().toString(),
                    name = _name.value,
                    cost = _amount.value.replace(',', '.').toDoubleOrNull() ?: 0.0,
                    currency = _currency.value,
                    billingCycle = _billingCycle.value,
                    billingDay = _billingDay.value,
                    billingMonth = _billingMonth.value,
                    isActive = true,
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
                    val savedSub = result.getOrNull()
                    if (savedSub?.responseMessage != null) {
                        _successMessage.value = savedSub.responseMessage
                    } else {
                        _isSuccess.value = true
                    }
                    checkInterstitialAdCondition()
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
    
    fun clearError() {
        _error.value = null
    }

    fun clearSuccessMessage() {
        _successMessage.value = null
        _isSuccess.value = false
    }

    private suspend fun checkInterstitialAdCondition() {
        val isPremium = premiumPreferences.isPremium.first()
        if (!isPremium) {
            premiumPreferences.incrementAdsCounter()
            val counter = premiumPreferences.adsCounter.first()
            if (counter > 0 && counter % 3 == 0) {
                _showInterstitialAd.emit(true)
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
        _billingDay.value = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH)
        _billingMonth.value = null
        _subscriptionId = null
        _isEditMode.value = false
        _currency.value = 1
        _category.value = ""
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
