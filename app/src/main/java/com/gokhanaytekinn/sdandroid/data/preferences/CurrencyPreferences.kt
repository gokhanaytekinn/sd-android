package com.gokhanaytekinn.sdandroid.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CurrencyPreferences(private val context: Context) {
    
    companion object {
        private val CURRENCY_KEY = intPreferencesKey("selected_currency")
        const val DEFAULT_CURRENCY = 1
    }
    
    val selectedCurrency: Flow<Int> = context.settingsDataStore.data.map { preferences ->
        preferences[CURRENCY_KEY] ?: DEFAULT_CURRENCY
    }
    
    suspend fun setCurrency(currencyCode: Int) {
        context.settingsDataStore.edit { preferences ->
            preferences[CURRENCY_KEY] = currencyCode
        }
    }
}
