package com.gokhanaytekinn.sdandroid.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CurrencyPreferences(private val context: Context) {
    
    companion object {
        private val CURRENCY_KEY = stringPreferencesKey("selected_currency")
        const val DEFAULT_CURRENCY = "TRY"
    }
    
    val selectedCurrency: Flow<String> = context.settingsDataStore.data.map { preferences ->
        preferences[CURRENCY_KEY] ?: DEFAULT_CURRENCY
    }
    
    suspend fun setCurrency(currencyCode: String) {
        context.settingsDataStore.edit { preferences ->
            preferences[CURRENCY_KEY] = currencyCode
        }
    }
}
