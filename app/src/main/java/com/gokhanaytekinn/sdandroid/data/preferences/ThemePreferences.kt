package com.gokhanaytekinn.sdandroid.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// private val Context.dataStore: DataStore<Preferences>
//     get() = settingsDataStore

private val Context.dataStore: DataStore<Preferences>
    get() = settingsDataStore

class ThemePreferences(private val context: Context) {
    
    companion object {
        private val IS_DARK_MODE_KEY = booleanPreferencesKey("is_dark_mode")
        const val DEFAULT_DARK_MODE = true // Default to dark mode
    }
    
    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_DARK_MODE_KEY] ?: DEFAULT_DARK_MODE
    }
    
    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_DARK_MODE_KEY] = enabled
        }
    }
    
    suspend fun toggleDarkMode() {
        context.dataStore.edit { preferences ->
            val current = preferences[IS_DARK_MODE_KEY] ?: DEFAULT_DARK_MODE
            preferences[IS_DARK_MODE_KEY] = !current
        }
    }
}
