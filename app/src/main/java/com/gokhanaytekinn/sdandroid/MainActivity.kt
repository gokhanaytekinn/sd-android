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
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.compose.rememberNavController
import com.gokhanaytekinn.sdandroid.data.preferences.ThemePreferences
import com.gokhanaytekinn.sdandroid.ui.navigation.NavGraph
import com.gokhanaytekinn.sdandroid.ui.theme.SDAndroidTheme
import com.gokhanaytekinn.sdandroid.data.preferences.LanguagePreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.google.firebase.messaging.FirebaseMessaging
import android.util.Log
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.first

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Kaydedilmiş dil tercihini uygulama açılmadan ÖNCE uygula
        val languagePreferences = LanguagePreferences(this)
        val savedLanguage = kotlinx.coroutines.runBlocking {
            languagePreferences.selectedLanguage.first()
        }
        val appLocale = LocaleListCompat.forLanguageTags(savedLanguage)
        AppCompatDelegate.setApplicationLocales(appLocale)

        super.onCreate(savedInstanceState)
        
        createNotificationChannel()
        checkNotificationPermission()
        getAndSendFCMToken()
        scheduleReminderWorker()
        
        val themePreferences = ThemePreferences(this)
        val onboardingPreferences = com.gokhanaytekinn.sdandroid.data.preferences.OnboardingPreferences(this)
        val tokenManager = com.gokhanaytekinn.sdandroid.data.local.TokenManager(this)
        
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

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val name = "Abonelik Hatırlatıcıları"
            val descriptionText = "Yenilenen abonelikleriniz için bildirimler"
            val importance = android.app.NotificationManager.IMPORTANCE_HIGH
            val channel = android.app.NotificationChannel("reminders_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: android.app.NotificationManager =
                getSystemService(android.content.Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun checkNotificationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                androidx.core.app.ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }
    }

    private fun getAndSendFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // 1. Token'ı al
            val token = task.result
            Log.d("FCM", "Token: $token")

            // 2. Token'ı Backend'e gönder
            sendTokenToBackend(token)
        }
    }

    private fun sendTokenToBackend(token: String) {
        val authRepository = com.gokhanaytekinn.sdandroid.data.repository.AuthRepository(this)
        
        // Launch in lifecycle scope to handle the suspend function
        lifecycleScope.launchWhenStarted {
            if (authRepository.isLoggedIn()) {
                val result = authRepository.updateFcmToken(token)
                if (result.isSuccess) {
                    Log.d("FCM", "Token successfully updated on backend")
                } else {
                    Log.e("FCM", "Failed to update token on backend: ${result.exceptionOrNull()?.message}")
                }
            } else {
                Log.d("FCM", "User not logged in, skipping token update")
            }
        }
    }

    private fun scheduleReminderWorker() {
        val workRequest = androidx.work.PeriodicWorkRequestBuilder<com.gokhanaytekinn.sdandroid.service.ReminderWorker>(
            1, java.util.concurrent.TimeUnit.DAYS
        ).setInitialDelay(calculateInitialDelay(), java.util.concurrent.TimeUnit.MILLISECONDS)
            .addTag("reminder_worker")
            .build()

        androidx.work.WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "subscription_reminders",
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    private fun calculateInitialDelay(): Long {
        val now = java.time.LocalDateTime.now()
        var scheduledTime = now.withHour(12).withMinute(20).withSecond(0).withNano(0)
        if (now.isAfter(scheduledTime)) {
            scheduledTime = scheduledTime.plusDays(1)
        }
        return java.time.Duration.between(now, scheduledTime).toMillis()
    }
}
