package com.gokhanaytekinn.sdandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.gokhanaytekinn.sdandroid.data.preferences.ThemePreferences
import com.gokhanaytekinn.sdandroid.ui.navigation.NavGraph
import com.gokhanaytekinn.sdandroid.ui.theme.SDAndroidTheme
import com.gokhanaytekinn.sdandroid.data.preferences.LanguagePreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val themePreferences = ThemePreferences(this)
        val onboardingPreferences = com.gokhanaytekinn.sdandroid.data.preferences.OnboardingPreferences(this)
        val tokenManager = com.gokhanaytekinn.sdandroid.data.local.TokenManager(this)
        val languagePreferences = LanguagePreferences(this)
        
        // Restore saved language preference on app startup
        lifecycleScope.launch {
            val savedLanguage = languagePreferences.selectedLanguage.first()
            val appLocale = LocaleListCompat.forLanguageTags(savedLanguage)
            AppCompatDelegate.setApplicationLocales(appLocale)
        }
        
        setContent {
            val isDarkMode by themePreferences.isDarkMode.collectAsState(initial = true)
            
            // Determine initial route based on state
            var startDestination by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<String?>(null) }
            
            androidx.compose.runtime.LaunchedEffect(Unit) {
                val isOnboardingComplete = onboardingPreferences.isOnboardingComplete()
                val isLoggedIn = tokenManager.isLoggedIn()
                
                startDestination = when {
                    !isOnboardingComplete -> com.gokhanaytekinn.sdandroid.ui.navigation.Screen.Onboarding.route
                    isLoggedIn -> com.gokhanaytekinn.sdandroid.ui.navigation.Screen.Dashboard.route
                    else -> com.gokhanaytekinn.sdandroid.ui.navigation.Screen.Login.route
                }
            }
            
            SDAndroidTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (startDestination != null) {
                        val navController = rememberNavController()
                        NavGraph(navController = navController, startDestination = startDestination!!)
                    }
                }
            }
        }
    }
}
