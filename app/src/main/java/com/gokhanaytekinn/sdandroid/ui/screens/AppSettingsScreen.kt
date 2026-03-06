package com.gokhanaytekinn.sdandroid.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gokhanaytekinn.sdandroid.R
import com.gokhanaytekinn.sdandroid.ui.theme.*
import com.gokhanaytekinn.sdandroid.ui.viewmodel.AuthViewModel
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import kotlinx.coroutines.launch
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.gokhanaytekinn.sdandroid.data.preferences.settingsDataStore
import com.gokhanaytekinn.sdandroid.BuildConfig
import com.gokhanaytekinn.sdandroid.ui.components.BottomNavigationBar
import com.gokhanaytekinn.sdandroid.util.CurrencyFormatter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign

@Composable
fun AppSettingsScreen(
    onBackClick: () -> Unit = {},
    onUpgradeClick: (Int?) -> Unit = {},
    onHelpClick: () -> Unit = {},
    onPrivacyClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    
    // ViewModels
    val authViewModel: AuthViewModel = remember { AuthViewModel(context) }
    val authState by authViewModel.authState.collectAsState()
    
    // Preferences
    val themePreferences = remember { com.gokhanaytekinn.sdandroid.data.preferences.ThemePreferences(context) }
    val languagePreferences = remember { com.gokhanaytekinn.sdandroid.data.preferences.LanguagePreferences(context) }
    val currencyPreferences = remember { com.gokhanaytekinn.sdandroid.data.preferences.CurrencyPreferences(context) }
    val notificationPreferences = remember { com.gokhanaytekinn.sdandroid.data.preferences.NotificationPreferences(context) }
    
    // States
    val darkModeEnabled by themePreferences.isDarkMode.collectAsState(initial = true)
    
    // Language state - read from saved preferences (not system locale)
    val currentLanguage by languagePreferences.selectedLanguage.collectAsState(initial = "tr")
    
    val premiumPreferences = remember { com.gokhanaytekinn.sdandroid.data.preferences.PremiumPreferences(context) }
    val isPremium by premiumPreferences.isPremium.collectAsState(initial = false)
    
    // Currency state - load from preferences
    val selectedCurrency by currencyPreferences.selectedCurrency.collectAsState(initial = 1)
    
    // Dialog states
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
        // iOS Style Header
        Text(
            text = stringResource(R.string.settings),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
        )
        
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            
            // Section: HESAP
            item {
                Text(
                    text = stringResource(R.string.account).uppercase(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    // Profile Item
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 80.dp)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2C3E50)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (authState.userName?.take(1) ?: "G").uppercase(),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column {
                            Text(
                                text = authState.userName ?: stringResource(R.string.guest_user),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = authState.userEmail ?: stringResource(R.string.not_logged_in),
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(12.dp)) }

            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF0B1621),
                    border = BorderStroke(0.5.dp, Color(0xFF1C2937))
                ) {
                    // Membership Status
                    SettingsNavigationItem(
                        icon = Icons.Outlined.Verified,
                        iconColor = Color(0xFFFF9500), // iOS Orange
                        title = stringResource(R.string.membership_type),
                        subtitle = if (isPremium) stringResource(R.string.premium_plan) else stringResource(R.string.free_plan),
                        onClick = { onUpgradeClick(authState.tier) }
                    )
                }
            }
            
            // Section: UYGULAMA
            item {
                Text(
                    text = stringResource(R.string.application).uppercase(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 16.dp).padding(top = 24.dp, bottom = 8.dp)
                )
            }
            
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF0B1621),
                    border = BorderStroke(0.5.dp, Color(0xFF1C2937))
                ) {
                    // Notifications Toggle
                    SettingsToggleItem(
                        icon = Icons.Outlined.Notifications,
                        iconColor = Color(0xFF007AFF), // iOS Blue
                        title = stringResource(R.string.notifications),
                        checked = authState.notificationsEnabled,
                        onCheckedChange = { enabled -> 
                            scope.launch {
                                notificationPreferences.setNotificationsEnabled(enabled)
                                authViewModel.updateNotificationSettings(enabled)
                            }
                        }
                    )
                }
            }
            
            item { Spacer(modifier = Modifier.height(12.dp)) }

            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF0B1621),
                    border = BorderStroke(0.5.dp, Color(0xFF1C2937))
                ) {
                    // Currency Selection
                    SettingsNavigationItem(
                        icon = Icons.Outlined.Payments,
                        iconColor = Color(0xFF34C759), // iOS Green
                        title = stringResource(R.string.currency),
                        subtitle = CurrencyFormatter.getCurrencySymbol(selectedCurrency),
                        onClick = { showCurrencyDialog = true }
                    )
                }
            }
            
            item { Spacer(modifier = Modifier.height(12.dp)) }

            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF0B1621),
                    border = BorderStroke(0.5.dp, Color(0xFF1C2937))
                ) {
                    // Dark Mode Toggle
                    SettingsToggleItem(
                        icon = Icons.Outlined.DarkMode,
                        iconColor = Color(0xFF5856D6), // iOS Purple
                        title = stringResource(R.string.dark_mode),
                        checked = darkModeEnabled,
                        onCheckedChange = { 
                            scope.launch {
                                themePreferences.toggleDarkMode()
                            }
                        }
                    )
                }
            }
            
            item { Spacer(modifier = Modifier.height(12.dp)) }

            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF0B1621),
                    border = BorderStroke(0.5.dp, Color(0xFF1C2937))
                ) {
                    // Language Selection
                    SettingsNavigationItem(
                        icon = Icons.Outlined.Language,
                        iconColor = Color(0xFF007AFF), // iOS Blue
                        title = stringResource(R.string.language),
                        subtitle = getLanguageName(currentLanguage),
                        onClick = { showLanguageDialog = true }
                    )
                }
            }
            
            // Section: DESTEK
            item {
                Text(
                    text = stringResource(R.string.support).uppercase(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 16.dp).padding(top = 24.dp, bottom = 8.dp)
                )
            }
            
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF0B1621),
                    border = BorderStroke(0.5.dp, Color(0xFF1C2937))
                ) {
                    // Help Center
                    SettingsNavigationItem(
                        icon = Icons.Outlined.HelpOutline,
                        iconColor = Color(0xFF007AFF),
                        title = stringResource(R.string.help_center),
                        onClick = onHelpClick
                    )
                }
            }
            
            item { Spacer(modifier = Modifier.height(12.dp)) }

            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF0B1621),
                    border = BorderStroke(0.5.dp, Color(0xFF1C2937))
                ) {
                    // Privacy Policy
                    SettingsNavigationItem(
                        icon = Icons.Outlined.Shield,
                        iconColor = Color(0xFF8E8E93),
                        title = stringResource(R.string.privacy_policy),
                        onClick = onPrivacyClick
                    )
                }
            }
            
            // Logout Button
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    SettingsNavigationItem(
                        icon = Icons.Outlined.Logout,
                        iconColor = Color(0xFFF97316), // iOS Orange
                        title = stringResource(R.string.logout),
                        titleColor = Color(0xFFF97316),
                        onClick = {
                            scope.launch {
                                authViewModel.logout()
                                onLogoutClick()
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Delete Account Button
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    SettingsNavigationItem(
                        icon = Icons.Outlined.Delete,
                        iconColor = MaterialTheme.colorScheme.error,
                        title = stringResource(R.string.delete_account),
                        titleColor = MaterialTheme.colorScheme.error,
                        onClick = { showDeleteAccountDialog = true }
                    )
                }

                Text(
                    text = stringResource(R.string.version_info, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
        }
    }
    
    // Language Selection Dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(stringResource(R.string.select_language)) },
            text = {
                Column {
                    LanguageOption("tr", "Türkçe", currentLanguage) { 
                        scope.launch {
                            languagePreferences.setLanguage("tr")
                            showLanguageDialog = false
                            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags("tr")
                            AppCompatDelegate.setApplicationLocales(appLocale)
                            authViewModel.updateNotificationSettings(authState.notificationsEnabled)
                        }
                    }
                    LanguageOption("az", "Azərbaycanca", currentLanguage) {
                        scope.launch {
                            languagePreferences.setLanguage("az")
                            showLanguageDialog = false
                            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags("az")
                            AppCompatDelegate.setApplicationLocales(appLocale)
                            authViewModel.updateNotificationSettings(authState.notificationsEnabled)
                        }
                    }
                    LanguageOption("kk", "Қазақша", currentLanguage) {
                        scope.launch {
                            languagePreferences.setLanguage("kk")
                            showLanguageDialog = false
                            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags("kk")
                            AppCompatDelegate.setApplicationLocales(appLocale)
                            authViewModel.updateNotificationSettings(authState.notificationsEnabled)
                        }
                    }
                    LanguageOption("uz", "Oʻzbekcha", currentLanguage) {
                        scope.launch {
                            languagePreferences.setLanguage("uz")
                            showLanguageDialog = false
                            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags("uz")
                            AppCompatDelegate.setApplicationLocales(appLocale)
                            authViewModel.updateNotificationSettings(authState.notificationsEnabled)
                        }
                    }
                    LanguageOption("ky", "Кыргызча", currentLanguage) {
                        scope.launch {
                            languagePreferences.setLanguage("ky")
                            showLanguageDialog = false
                            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags("ky")
                            AppCompatDelegate.setApplicationLocales(appLocale)
                            authViewModel.updateNotificationSettings(authState.notificationsEnabled)
                        }
                    }
                    LanguageOption("tk", "Türkmençe", currentLanguage) {
                        scope.launch {
                            languagePreferences.setLanguage("tk")
                            showLanguageDialog = false
                            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags("tk")
                            AppCompatDelegate.setApplicationLocales(appLocale)
                            authViewModel.updateNotificationSettings(authState.notificationsEnabled)
                        }
                    }
                    LanguageOption("en", "English", currentLanguage) {
                        scope.launch {
                            languagePreferences.setLanguage("en")
                            showLanguageDialog = false
                            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags("en")
                            AppCompatDelegate.setApplicationLocales(appLocale)
                            authViewModel.updateNotificationSettings(authState.notificationsEnabled)
                        }
                    }
                    LanguageOption("es", "Español", currentLanguage) {
                        scope.launch {
                            languagePreferences.setLanguage("es")
                            showLanguageDialog = false
                            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags("es")
                            AppCompatDelegate.setApplicationLocales(appLocale)
                            authViewModel.updateNotificationSettings(authState.notificationsEnabled)
                        }
                    }
                    LanguageOption("ru", "Русский", currentLanguage) {
                        scope.launch {
                            languagePreferences.setLanguage("ru")
                            showLanguageDialog = false
                            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags("ru")
                            AppCompatDelegate.setApplicationLocales(appLocale)
                            authViewModel.updateNotificationSettings(authState.notificationsEnabled)
                        }
                    }
                    LanguageOption("zh", "简体中文", currentLanguage) {
                        scope.launch {
                            languagePreferences.setLanguage("zh")
                            showLanguageDialog = false
                            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags("zh")
                            AppCompatDelegate.setApplicationLocales(appLocale)
                            authViewModel.updateNotificationSettings(authState.notificationsEnabled)
                        }
                    }
                    LanguageOption("fr", "Français", currentLanguage) {
                        scope.launch {
                            languagePreferences.setLanguage("fr")
                            showLanguageDialog = false
                            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags("fr")
                            AppCompatDelegate.setApplicationLocales(appLocale)
                            authViewModel.updateNotificationSettings(authState.notificationsEnabled)
                        }
                    }
                    LanguageOption("de", "Deutsch", currentLanguage) {
                        scope.launch {
                            languagePreferences.setLanguage("de")
                            showLanguageDialog = false
                            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags("de")
                            AppCompatDelegate.setApplicationLocales(appLocale)
                            authViewModel.updateNotificationSettings(authState.notificationsEnabled)
                        }
                    }



                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(stringResource(R.string.close))
                }
            }
        )
    }

     if (showCurrencyDialog) {
        AlertDialog(
            onDismissRequest = { showCurrencyDialog = false },
            title = { Text(stringResource(R.string.select_currency)) },
            text = {
                Column {
                    CurrencyOption(1, "TRY", selectedCurrency) {
                        scope.launch {
                            currencyPreferences.setCurrency(1)
                            showCurrencyDialog = false
                        }
                    }
                    CurrencyOption(2, "USD", selectedCurrency) {
                         scope.launch {
                            currencyPreferences.setCurrency(2)
                            showCurrencyDialog = false
                        }
                    }
                    CurrencyOption(3, "EUR", selectedCurrency) {
                         scope.launch {
                            currencyPreferences.setCurrency(3)
                            showCurrencyDialog = false
                        }
                    }
                    CurrencyOption(4, "GBP", selectedCurrency) {
                         scope.launch {
                            currencyPreferences.setCurrency(4)
                            showCurrencyDialog = false
                        }
                    }
                    CurrencyOption(5, "RUB", selectedCurrency) {
                         scope.launch {
                            currencyPreferences.setCurrency(5)
                            showCurrencyDialog = false
                        }
                    }
                    CurrencyOption(6, "AZN", selectedCurrency) {
                         scope.launch {
                            currencyPreferences.setCurrency(6)
                            showCurrencyDialog = false
                        }
                    }
                    CurrencyOption(7, "KZT", selectedCurrency) {
                         scope.launch {
                            currencyPreferences.setCurrency(7)
                            showCurrencyDialog = false
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCurrencyDialog = false }) {
                    Text(stringResource(R.string.close))
                }
            }
        )
    }

    // Delete Account Confirmation Dialog
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = {
                Text(
                    stringResource(R.string.delete_account_confirm_title),
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(stringResource(R.string.delete_account_confirm_desc))
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteAccountDialog = false
                        scope.launch {
                            authViewModel.deleteAccount()
                            onLogoutClick()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = Color.White
                    )
                ) {
                    Text(stringResource(R.string.delete_account))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
fun LanguageOption(
    code: String,
    name: String,
    selectedCode: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(name, fontSize = 16.sp)
        if (code == selectedCode) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = null,
                tint = PrimaryBlue
            )
        }
    }
}

@Composable
fun CurrencyOption(
    code: Int,
    name: String,
    selectedCode: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val symbol = com.gokhanaytekinn.sdandroid.util.CurrencyFormatter.getCurrencySymbol(code)
        Text("$name ($symbol)", fontSize = 16.sp)
        if (code == selectedCode) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = null,
                tint = PrimaryBlue
            )
        }
    }
}

fun getLanguageName(code: String): String {
    return when(code) {
        "tr" -> "Türkçe"
        "en" -> "English"
        "es" -> "Español"
        "ru" -> "Русский"
        "zh" -> "简体中文"
        "fr" -> "Français"
        "de" -> "Deutsch"
        "az" -> "Azərbaycanca"
        "tk" -> "Türkmençe"
        "kk" -> "Қазақша"
        "ky" -> "Кыргызча"
        "uz" -> "Oʻzbekcha"
        else -> code



    }
}

 @Composable
fun SettingsToggleItem(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            
            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF34C759), // iOS Green for switches
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFF39393D)
            )
        )
    }
}

@Composable
fun SettingsNavigationItem(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    titleColor: Color = Color.White,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
                
                Column {
                    Text(
                        text = title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Normal,
                        color = titleColor
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
            
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
