package com.gokhanaytekinn.sdandroid.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.onboardingDataStore: DataStore<Preferences> by preferencesDataStore(name = "onboarding_prefs")

class OnboardingPreferences(private val context: Context) {
    
    companion object {
        private val IS_ONBOARDING_COMPLETE_KEY = booleanPreferencesKey("is_onboarding_complete")
    }
    
    val isOnboardingCompleteFlow: Flow<Boolean> = context.onboardingDataStore.data.map { preferences ->
        preferences[IS_ONBOARDING_COMPLETE_KEY] ?: false
    }
    
    suspend fun isOnboardingComplete(): Boolean {
        return isOnboardingCompleteFlow.first()
    }
    
    suspend fun markOnboardingComplete() {
        context.onboardingDataStore.edit { preferences ->
            preferences[IS_ONBOARDING_COMPLETE_KEY] = true
        }
    }
}
