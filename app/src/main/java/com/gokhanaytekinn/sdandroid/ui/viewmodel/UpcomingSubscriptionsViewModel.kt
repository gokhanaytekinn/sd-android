package com.gokhanaytekinn.sdandroid.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gokhanaytekinn.sdandroid.data.model.Subscription
import com.gokhanaytekinn.sdandroid.data.repository.SubscriptionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UpcomingSubscriptionsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SubscriptionRepository(application)

    private val _uiState = MutableStateFlow<UpcomingUiState>(UpcomingUiState.Loading)
    val uiState: StateFlow<UpcomingUiState> = _uiState.asStateFlow()

    init {
        loadUpcomingSubscriptions()
    }

    fun loadUpcomingSubscriptions() {
        viewModelScope.launch {
            _uiState.value = UpcomingUiState.Loading
            val result = repository.getUpcomingSubscriptions()
            if (result.isSuccess) {
                val subscriptions = result.getOrDefault(emptyList())
                if (subscriptions.isEmpty()) {
                    _uiState.value = UpcomingUiState.Empty
                } else {
                    _uiState.value = UpcomingUiState.Success(subscriptions)
                }
            } else {
                _uiState.value = UpcomingUiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }
}

sealed class UpcomingUiState {
    object Loading : UpcomingUiState()
    data class Success(val subscriptions: List<Subscription>) : UpcomingUiState()
    data class Error(val message: String) : UpcomingUiState()
    object Empty : UpcomingUiState()
}
