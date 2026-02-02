package com.gokhanaytekinn.sdandroid.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "search_preferences")

class SearchPreferences(private val context: Context) {
    
    companion object {
        private val RECENT_SEARCHES_KEY = stringPreferencesKey("recent_searches")
        private const val MAX_RECENT_SEARCHES = 5
    }
    
    val recentSearches: Flow<List<String>> = context.dataStore.data.map { preferences ->
        val searchesString = preferences[RECENT_SEARCHES_KEY] ?: ""
        if (searchesString.isEmpty()) {
            emptyList()
        } else {
            searchesString.split(",").take(MAX_RECENT_SEARCHES)
        }
    }
    
    suspend fun addRecentSearch(query: String) {
        if (query.isBlank()) return
        
        context.dataStore.edit { preferences ->
            val currentSearches = preferences[RECENT_SEARCHES_KEY]?.split(",") ?: emptyList()
            val updatedSearches = mutableListOf(query)
            
            // Add previous searches, excluding the current query to avoid duplicates
            currentSearches.forEach { search ->
                if (search != query && updatedSearches.size < MAX_RECENT_SEARCHES) {
                    updatedSearches.add(search)
                }
            }
            
            preferences[RECENT_SEARCHES_KEY] = updatedSearches.joinToString(",")
        }
    }
    
    suspend fun clearRecentSearches() {
        context.dataStore.edit { preferences ->
            preferences.remove(RECENT_SEARCHES_KEY)
        }
    }
}
