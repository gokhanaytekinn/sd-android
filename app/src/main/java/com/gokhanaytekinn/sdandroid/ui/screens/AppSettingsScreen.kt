package com.gokhanaytekinn.sdandroid.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.gokhanaytekinn.sdandroid.ui.components.BottomNavigationBar

@Composable
fun AppSettingsScreen(
    onBackClick: () -> Unit = {},
    onUpgradeClick: (String?) -> Unit = {},
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
    
    // Currency state - load from preferences
    val selectedCurrency by currencyPreferences.selectedCurrency.collectAsState(initial = "TRY")
    
    // Dialog states
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showCurrencyDialog by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
        // Top App Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
            tonalElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                
                Text(
                    text = stringResource(R.string.settings),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.width(48.dp))
            }
        }
        
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            
            // Section: Hesap
            item {
                Text(
                    text = stringResource(R.string.account),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
            
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                ) {
                    Column {
                        // Profile Item
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryBlue.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = (authState.userName?.take(1) ?: "G").uppercase(),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryBlue
                                    )
                                }
                                
                                Column {
                                    Text(
                                        text = authState.userName ?: stringResource(R.string.guest_user),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = authState.userEmail ?: stringResource(R.string.not_logged_in),
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            
                            Icon(
                                imageVector = Icons.Filled.ChevronRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        Divider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(horizontal = 16.dp))
                        
                        // Membership Status
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = { onUpgradeClick(authState.tier) })
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(PrimaryBlue.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.WorkspacePremium,
                                        contentDescription = null,
                                        tint = PrimaryBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                
                                Text(
                                    text = stringResource(R.string.membership_type),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = PrimaryBlue.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(50)
                                    )
                            ) {
                                Text(
                                    text = if (authState.tier == "PREMIUM") stringResource(R.string.premium_plan) else stringResource(R.string.free_plan),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PrimaryBlue,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
            
            // Section: Uygulama
            item {
                Text(
                    text = stringResource(R.string.application),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(horizontal = 16.dp).padding(top = 24.dp, bottom = 12.dp)
                )
            }
            
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                ) {
                    Column {
                        // Notifications Toggle
                        SettingsToggleItem(
                            icon = Icons.Filled.Notifications,
                            title = stringResource(R.string.notifications),
                            checked = authState.notificationsEnabled,
                            onCheckedChange = { 
                                scope.launch {
                                    notificationPreferences.setNotificationsEnabled(it)
                                    authViewModel.updateNotificationSettings(it)
                                }
                            }
                        )
                        
                        Divider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(horizontal = 16.dp))
                        
                        // Currency Selection
                        SettingsNavigationItem(
                            icon = Icons.Filled.Payments,
                            title = stringResource(R.string.currency),
                            value = "$selectedCurrency (${getCurrencySymbol(selectedCurrency)})",
                            onClick = { showCurrencyDialog = true }
                        )
                        
                        Divider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(horizontal = 16.dp))
                        
                        // Dark Mode Toggle
                        SettingsToggleItem(
                            icon = Icons.Filled.DarkMode,
                            title = stringResource(R.string.dark_mode),
                            checked = darkModeEnabled,
                            onCheckedChange = { 
                                scope.launch {
                                    themePreferences.toggleDarkMode()
                                }
                            }
                        )
                        
                        Divider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(horizontal = 16.dp))
                        
                        // Language Selection
                        SettingsNavigationItem(
                            icon = Icons.Filled.Language,
                            title = stringResource(R.string.language),
                            value = getLanguageName(currentLanguage),
                            onClick = { showLanguageDialog = true }
                        )
                    }
                }
            }
            
            // Section: Destek
            item {
                Text(
                    text = stringResource(R.string.support),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,

                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(horizontal = 16.dp).padding(top = 24.dp, bottom = 12.dp)
                )
            }
            
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                ) {
                    Column {

                        // Help Center
                        SettingsNavigationItem(
                            icon = Icons.Filled.Help,
                            title = stringResource(R.string.help_center),
                            onClick = onHelpClick
                        )
                        
                        Divider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(horizontal = 16.dp))
                        
                        // Privacy Policy
                        SettingsNavigationItem(
                            icon = Icons.Filled.Policy,
                            title = stringResource(R.string.privacy_policy),
                            onClick = onPrivacyClick
                        )
                    }
                }
            }
            
            // Logout Button
            item {
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        scope.launch {
                            authViewModel.logout()
                            onLogoutClick()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFEF2F2),
                        contentColor = Color(0xFFEF4444)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Logout,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.logout),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                Text(
                    text = stringResource(R.string.version_info, "2.4.0", 892),
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
                    CurrencyOption("TRY", "₺", selectedCurrency) {
                        scope.launch {
                            currencyPreferences.setCurrency("TRY")
                            showCurrencyDialog = false
                        }
                    }
                    CurrencyOption("USD", "$", selectedCurrency) {
                         scope.launch {
                            currencyPreferences.setCurrency("USD")
                            showCurrencyDialog = false
                        }
                    }
                    CurrencyOption("EUR", "€", selectedCurrency) {
                         scope.launch {
                            currencyPreferences.setCurrency("EUR")
                            showCurrencyDialog = false
                        }
                    }
                    CurrencyOption("GBP", "£", selectedCurrency) {
                         scope.launch {
                            currencyPreferences.setCurrency("GBP")
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
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = PrimaryBlue
            )
        }
    }
}

@Composable
fun CurrencyOption(
    code: String,
    symbol: String,
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
        Text("$code ($symbol)", fontSize = 16.sp)
        if (code == selectedCode) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = PrimaryBlue
            )
        }
    }
}

fun getCurrencySymbol(currency: String): String {
    return when(currency) {
        "TRY" -> "₺"
        "USD" -> "$"
        "EUR" -> "€"
        "GBP" -> "£"
        else -> currency
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
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(PrimaryBlue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = PrimaryBlue,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFE2E8F0)
            )
        )
    }
}

@Composable
fun SettingsNavigationItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String? = null,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryBlue.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (value != null) {
                    Text(
                        text = value,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
