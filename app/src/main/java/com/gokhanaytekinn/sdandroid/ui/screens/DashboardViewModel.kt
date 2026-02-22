package com.gokhanaytekinn.sdandroid.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gokhanaytekinn.sdandroid.data.model.BillingCycle
import com.gokhanaytekinn.sdandroid.data.model.Subscription
import com.gokhanaytekinn.sdandroid.data.model.SubscriptionStats
import com.gokhanaytekinn.sdandroid.data.repository.SubscriptionRepository
import com.gokhanaytekinn.sdandroid.util.DeviceSubscriptionScanner
import com.gokhanaytekinn.sdandroid.util.PermissionManager
import com.gokhanaytekinn.sdandroid.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = SubscriptionRepository(application.applicationContext)
    private val deviceScanner = DeviceSubscriptionScanner(application.applicationContext)
    private val permissionManager = PermissionManager(application.applicationContext)
    
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
    
    // Scanning state
    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()
    
    private val _scanProgress = MutableStateFlow<DeviceSubscriptionScanner.ScanProgress?>(null)
    val scanProgress: StateFlow<DeviceSubscriptionScanner.ScanProgress?> = _scanProgress.asStateFlow()
    
    private val _detectedSubscriptions = MutableStateFlow<List<DeviceSubscriptionScanner.DetectedSubscription>>(emptyList())
    val detectedSubscriptions: StateFlow<List<DeviceSubscriptionScanner.DetectedSubscription>> = _detectedSubscriptions.asStateFlow()
    
    init {
        loadSubscriptions()
        loadSuspiciousCount()
        loadUpcomingSubscriptions()
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

    private fun loadUpcomingSubscriptions() {
        viewModelScope.launch {
            val result = repository.getUpcomingSubscriptions()
            if (result.isSuccess) {
                // Filter locally for safety (max 10 days) and take only first 3 for preview
                val filtered = result.getOrNull()?.filter { 
                    DateUtils.isWithinNextDays(it.nextBillingDate, 10)
                } ?: emptyList()
                _upcomingSubscriptions.value = filtered.take(3)
            }
        }
    }
    
    /**
     * Cihazı tara ve abonelikleri tespit et
     */
    fun scanDeviceForSubscriptions() {
        viewModelScope.launch {
            try {
                _isScanning.value = true
                _error.value = null
                
                // Progress'i gözlemle
                viewModelScope.launch {
                    deviceScanner.progress.collect { progress ->
                        _scanProgress.value = progress
                    }
                }
                
                // Taramayı başlat
                val detected = deviceScanner.scanDevice()
                _detectedSubscriptions.value = detected
                
            } catch (e: SecurityException) {
                _error.value = "İzin verilmedi. Lütfen gerekli izinleri verin."
            } catch (e: Exception) {
                _error.value = "Tarama sırasında hata oluştu: ${e.message}"
            } finally {
                _isScanning.value = false
            }
        }
    }
    
    /**
     * Tespit edilen aboneliği onayla ve ekle
     */
    fun confirmDetectedSubscription(detected: DeviceSubscriptionScanner.DetectedSubscription) {
        viewModelScope.launch {
            try {
                val billingCycle = when (detected.billingCycle) {
                    "MONTHLY" -> BillingCycle.MONTHLY
                    "YEARLY" -> BillingCycle.YEARLY
                    "WEEKLY" -> BillingCycle.WEEKLY
                    else -> BillingCycle.MONTHLY
                }
                
                // Subscription objesi oluştur
                val subscription = Subscription(
                    id = "",
                    name = detected.serviceName,
                    cost = detected.amount,
                    currency = "TRY",
                    billingCycle = billingCycle,
                    nextBillingDate = null,
                    isActive = true,
                    isSuspicious = false,
                    icon = null
                )
                
                // API'ye ekle
                val result = repository.createSubscription(subscription)
                
                if (result.isSuccess) {
                    // Başarılı, listeyi yenile
                    loadSubscriptions()
                    
                    // Tespit edilenler listesinden kaldır
                    _detectedSubscriptions.value = _detectedSubscriptions.value.filter {
                        it.serviceName != detected.serviceName
                    }
                } else {
                    _error.value = "Abonelik eklenemedi: ${result.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                _error.value = "Abonelik eklenemedi: ${e.message}"
            }
        }
    }
    
    /**
     * Tespit edilen aboneliği reddet
     */
    fun rejectDetectedSubscription(detected: DeviceSubscriptionScanner.DetectedSubscription) {
        _detectedSubscriptions.value = _detectedSubscriptions.value.filter {
            it.serviceName != detected.serviceName
        }
    }
    
    /**
     * Tüm tespit edilen abonelikleri temizle
     */
    fun clearDetectedSubscriptions() {
        _detectedSubscriptions.value = emptyList()
    }
    
    /**
     * İzinleri kontrol et
     */
    fun hasRequiredPermissions(): Boolean {
        return permissionManager.hasAllPermissions()
    }
}

