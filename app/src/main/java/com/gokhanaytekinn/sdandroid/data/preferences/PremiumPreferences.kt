package com.gokhanaytekinn.sdandroid.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.premiumDataStore: DataStore<Preferences> by preferencesDataStore(name = "premium_prefs")

class PremiumPreferences(private val context: Context) {

    companion object {
        private val IS_PREMIUM_KEY = booleanPreferencesKey("is_premium")
        private val ADS_COUNTER_KEY = intPreferencesKey("ads_counter")
    }

    val isPremium: Flow<Boolean> = context.premiumDataStore.data.map { preferences ->
        preferences[IS_PREMIUM_KEY] ?: false
    }

    suspend fun setPremiumStatus(isPremium: Boolean) {
        context.premiumDataStore.edit { preferences ->
            preferences[IS_PREMIUM_KEY] = isPremium
        }
    }

    val adsCounter: Flow<Int> = context.premiumDataStore.data.map { preferences ->
        preferences[ADS_COUNTER_KEY] ?: 0
    }

    suspend fun incrementAdsCounter() {
        context.premiumDataStore.edit { preferences ->
            val currentCounter = preferences[ADS_COUNTER_KEY] ?: 0
            preferences[ADS_COUNTER_KEY] = currentCounter + 1
        }
    }

    suspend fun resetAdsCounter() {
        context.premiumDataStore.edit { preferences ->
            preferences[ADS_COUNTER_KEY] = 0
        }
    }
}
