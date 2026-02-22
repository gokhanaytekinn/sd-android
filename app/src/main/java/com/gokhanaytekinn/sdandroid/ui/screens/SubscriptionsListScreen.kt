package com.gokhanaytekinn.sdandroid.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gokhanaytekinn.sdandroid.data.model.Subscription
import com.gokhanaytekinn.sdandroid.ui.components.BottomNavigationBar
import com.gokhanaytekinn.sdandroid.ui.components.ScanningDialog
import com.gokhanaytekinn.sdandroid.ui.components.DetectedSubscriptionsDialog
import com.gokhanaytekinn.sdandroid.ui.theme.*
import com.gokhanaytekinn.sdandroid.data.preferences.CurrencyPreferences
import com.gokhanaytekinn.sdandroid.ui.screens.DashboardViewModel
import com.gokhanaytekinn.sdandroid.util.CurrencyFormatter
import com.gokhanaytekinn.sdandroid.util.DateUtils
import com.gokhanaytekinn.sdandroid.util.PermissionManager
import com.gokhanaytekinn.sdandroid.R
import com.gokhanaytekinn.sdandroid.util.DeviceSubscriptionScanner

@Composable
fun SubscriptionsListScreen(
    onSubscriptionClick: (String) -> Unit = {},
    onNavigateToDashboard: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToAddSubscription: () -> Unit = {},
    onNavigateToAnalytics: () -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val application = context.applicationContext as android.app.Application
    val viewModel: DashboardViewModel = remember { DashboardViewModel(application) }
    val currencyPreferences = remember { CurrencyPreferences(context) }
    
    val subscriptions by viewModel.subscriptions.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val selectedCurrency by currencyPreferences.selectedCurrency.collectAsState(initial = "TRY")
    val isScanning by viewModel.isScanning.collectAsState()
    val scanProgress by viewModel.scanProgress.collectAsState()
    val detectedSubscriptions by viewModel.detectedSubscriptions.collectAsState()
    
    var selectedTab by remember { mutableStateOf(0) }
    var showScanDialog by remember { mutableStateOf(false) }
    var showResultsDialog by remember { mutableStateOf(false) }
    var showPermissionRationale by remember { mutableStateOf(false) }
    
    val permissionManager = remember { PermissionManager(context) }
    
    // İzin launcher'ı
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            viewModel.scanDeviceForSubscriptions()
            showScanDialog = true
        } else {
            // İzin verilmedi
            showPermissionRationale = true
        }
    }
    
    // Tarama tamamlandığında sonuç dialog'u göster
    LaunchedEffect(detectedSubscriptions) {
        if (detectedSubscriptions.isNotEmpty() && !isScanning) {
            showScanDialog = false
            showResultsDialog = true
        }
    }
    
    // Refresh subscriptions when screen is shown
    LaunchedEffect(Unit) {
        viewModel.loadSubscriptions()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
                tonalElevation = 1.dp
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.subscriptions),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Scan button
                            IconButton(
                                onClick = {
                                    val missingPermissions = permissionManager.getMissingPermissions()
                                    if (missingPermissions.isEmpty()) {
                                        viewModel.scanDeviceForSubscriptions()
                                        showScanDialog = true
                                    } else {
                                        permissionLauncher.launch(missingPermissions)
                                    }
                                },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(SuccessColor.copy(alpha = 0.1f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Scanner,
                                    contentDescription = stringResource(R.string.search_placeholder), // Or add a specific "Scan" string
                                    tint = SuccessColor
                                )
                            }
                            
                            // Add button
                            IconButton(
                                onClick = onNavigateToAddSubscription,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryBlue.copy(alpha = 0.1f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add",
                                    tint = PrimaryBlue
                                )
                            }
                        }
                    }
                    
                    // Tabs
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 16.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Transparent)
                            .padding(4.dp)
                    ) {
                        TabItem(
                            text = stringResource(R.string.active),
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            modifier = Modifier.weight(1f)
                        )
                        TabItem(
                            text = stringResource(R.string.suspicious),
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            modifier = Modifier.weight(1f)
                        )
                        TabItem(
                            text = stringResource(R.string.cancelled),
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            
            // Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Summary Card
                if (selectedTab == 0) {
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = stringResource(R.string.total_monthly).uppercase(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = CurrencyFormatter.formatAmount(stats.totalMonthlyCost, selectedCurrency),
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                                
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryBlue.copy(alpha = 0.2f))
                                        .clickable { onNavigateToAnalytics() }, // Add navigation here
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Analytics,
                                        contentDescription = null,
                                        tint = PrimaryBlue
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Section Header
                item {
                    Text(
                        text = stringResource(R.string.monthly).uppercase(), // "BU AY" -> "THIS MONTH" or use "monthly"
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF6B7280),
                        letterSpacing = 1.5.sp
                    )
                }
                
                // Filtered Content based on selectedTab
                when (selectedTab) {
                    0 -> { // Active
                        val activeSubs = subscriptions.filter { it.isActive && !it.isSuspicious }
                        if (activeSubs.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stringResource(R.string.no_active_subscriptions),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            items(activeSubs) { subscription ->
                                SubscriptionListItemDetailed(
                                    subscription = subscription,
                                    currency = selectedCurrency,
                                    onClick = { onSubscriptionClick(subscription.id) }
                                )
                            }
                        }
                    }
                    1 -> { // Suspicious
                        val suspiciousSubs = subscriptions.filter { it.isSuspicious }
                        // Show detected subscriptions that haven't been added yet
                        
                        if (suspiciousSubs.isEmpty() && detectedSubscriptions.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stringResource(R.string.no_suspicious_subscriptions),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            if (detectedSubscriptions.isNotEmpty()) {
                                item { 
                                    Text(
                                        text = stringResource(R.string.detected_count, detectedSubscriptions.size),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = WarningColor,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    ) 
                                }
                                items(detectedSubscriptions) { subscription ->
                                    com.gokhanaytekinn.sdandroid.ui.components.DetectedSubscriptionItem(
                                        subscription = subscription,
                                        currency = selectedCurrency,
                                        onConfirm = { viewModel.confirmDetectedSubscription(subscription) },
                                        onReject = { viewModel.rejectDetectedSubscription(subscription) }
                                    )
                                }
                                item { Spacer(modifier = Modifier.height(16.dp)) }
                            }
                            
                            if (suspiciousSubs.isNotEmpty()) {
                                item { 
                                    Text(
                                        text = stringResource(R.string.marked_count, suspiciousSubs.size),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    ) 
                                }
                                items(suspiciousSubs) { subscription ->
                                    SubscriptionListItemDetailed(
                                        subscription = subscription,
                                        currency = selectedCurrency,
                                        onClick = { onSubscriptionClick(subscription.id) }
                                    )
                                }
                            }
                        }
                    }
                    2 -> { // Cancelled
                        val cancelledSubs = subscriptions.filter { !it.isActive }
                        if (cancelledSubs.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stringResource(R.string.no_cancelled_subscriptions),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            items(cancelledSubs) { subscription ->
                                SubscriptionListItemDetailed(
                                    subscription = subscription,
                                    currency = selectedCurrency,
                                    onClick = { onSubscriptionClick(subscription.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Tarama Dialog'u
    if (showScanDialog) {
        ScanningDialog(
            isScanning = isScanning,
            progress = scanProgress,
            onDismiss = { 
                showScanDialog = false
                // Do not clear detected subscriptions here, they go to the Suspicious tab
            }
        )
    }
    
    // Sonuç Dialog'u
    if (showResultsDialog) {
        DetectedSubscriptionsDialog(
            detectedSubscriptions = detectedSubscriptions,
            onConfirm = { subscription ->
                viewModel.confirmDetectedSubscription(subscription)
            },
            onReject = { subscription ->
                viewModel.rejectDetectedSubscription(subscription)
            },
            onDismiss = {
                showResultsDialog = false
                // Do not clear detected subscriptions here, they go to the Suspicious tab
            },
            currency = selectedCurrency
        )
    }
    
    // İzin Açıklama Dialog'u
    if (showPermissionRationale) {
        AlertDialog(
            onDismissRequest = { showPermissionRationale = false },
            title = { Text(stringResource(R.string.error)) }, // Or add "Permission Required" to strings
            text = { 
                Text(
                    text = "${stringResource(R.string.sms_permission_rationale)}\n\n${stringResource(R.string.storage_permission_rationale)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(onClick = { showPermissionRationale = false }) {
                    Text(stringResource(R.string.done))
                }
            }
        )
    }
}

@Composable
fun TabItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (selected) SuccessColor else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (selected) MaterialTheme.colorScheme.background else Color(0xFF9CA3AF)
        )
    }
}

@Composable
fun SubscriptionListItemDetailed(
    subscription: Subscription,
    currency: String = "TRY",
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(0.dp),
        color = Color.Transparent
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Icon with gradient
                    val iconBg = when (subscription.name) {
                        "Netflix Premium" -> NetflixRed
                        "Spotify Duo" -> SpotifyGreen
                        "Adobe Creative Cloud" -> AdobeRed
                        "iCloud+ 200GB" -> Color(0xFF007AFF)
                        "Amazon Prime" -> Color(0xFF00A8E1)
                        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    }
                    
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(iconBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (subscription.name) {
                                "Netflix Premium" -> "N"
                                "Spotify Duo" -> "🎵"
                                "Adobe Creative Cloud" -> "Ae"
                                "iCloud+ 200GB" -> "☁️"
                                "Amazon Prime" -> "📦"
                                else -> subscription.name.take(1)
                            },
                            fontSize = if (subscription.name.contains("Spotify") || 
                                         subscription.name.contains("iCloud") ||
                                         subscription.name.contains("Amazon")) 24.sp else 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    
                    Column {
                        Text(
                            text = subscription.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        val localizedCategory = when (subscription.category) {
                            "Spor & Sağlık", "Sports & Health" -> stringResource(R.string.category_sports)
                            "Eğlence", "Entertainment" -> stringResource(R.string.category_entertainment)
                            "Müzik", "Music" -> stringResource(R.string.category_music)
                            else -> subscription.category ?: stringResource(R.string.category_other)
                        }
                        val localizedBillingCycle = when (subscription.billingCycle) {
                            com.gokhanaytekinn.sdandroid.data.model.BillingCycle.MONTHLY -> stringResource(R.string.monthly)
                            com.gokhanaytekinn.sdandroid.data.model.BillingCycle.YEARLY -> stringResource(R.string.yearly)
                            else -> subscription.billingCycle.name
                        }
                        Row {
                            Text(
                                text = "$localizedCategory • $localizedBillingCycle",
                                fontSize = 14.sp,
                                color = Color(0xFF9CA3AF)
                            )
                        }
                    }
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = CurrencyFormatter.formatAmount(subscription.cost, currency),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = DateUtils.formatDate(subscription.nextBillingDate),
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Divider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                thickness = 1.dp
            )
        }
    }
}
