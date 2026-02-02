package com.gokhanaytekinn.sdandroid.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gokhanaytekinn.sdandroid.data.model.Subscription
import com.gokhanaytekinn.sdandroid.data.repository.SubscriptionRepository
import com.gokhanaytekinn.sdandroid.data.preferences.SearchPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SubscriptionRepository(application)
    private val searchPreferences = SearchPreferences(application)
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _searchResults = MutableStateFlow<List<Subscription>>(emptyList())
    val searchResults: StateFlow<List<Subscription>> = _searchResults.asStateFlow()
    
    private val _allSubscriptions = MutableStateFlow<List<Subscription>>(emptyList())
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    val recentSearches = searchPreferences.recentSearches
    
    init {
        loadSubscriptions()
    }
    
    private fun loadSubscriptions() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getSubscriptions()
            result.onSuccess { subscriptions ->
                _allSubscriptions.value = subscriptions
            }
            _isLoading.value = false
        }
    }
    
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        performSearch(query)
    }
    
    private fun performSearch(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        
        val filtered = _allSubscriptions.value.filter { subscription ->
            subscription.name.contains(query, ignoreCase = true) ||
            subscription.category?.contains(query, ignoreCase = true) == true
        }
        _searchResults.value = filtered
        
        // Save to recent searches
        viewModelScope.launch {
            searchPreferences.addRecentSearch(query)
        }
    }
    
    fun onRecentSearchClick(search: String) {
        _searchQuery.value = search
        performSearch(search)
    }
    
    fun clearSearch() {
        _searchQuery.value = ""
        _searchResults.value = emptyList()
    }
}
