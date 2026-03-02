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
    initialTab: Int = 0,
    onSubscriptionClick: (String) -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    onNavigateToAnalytics: () -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val application = context.applicationContext as android.app.Application
    val viewModel: DashboardViewModel = remember { DashboardViewModel(application) }
    val currencyPreferences = remember { CurrencyPreferences(context) }
    
    val subscriptions by viewModel.subscriptions.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedCurrency by currencyPreferences.selectedCurrency.collectAsState(initial = "TRY")
    val isScanning by viewModel.isScanning.collectAsState()
    val scanProgress by viewModel.scanProgress.collectAsState()
    val detectedSubscriptions by viewModel.detectedSubscriptions.collectAsState()
    val pendingInvitations by viewModel.pendingInvitations.collectAsState()
    
    var selectedTab by remember { mutableStateOf(initialTab) }
    var showScanDialog by remember { mutableStateOf(false) }
    var showResultsDialog by remember { mutableStateOf(false) }
    var showPermissionRationale by remember { mutableStateOf(false) }
    
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
                            // Search icon
                            IconButton(
                                onClick = onNavigateToSearch,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = stringResource(R.string.nav_search),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
            // Empty replacement because we removed the Scan button partially above and the rest of Add button here
                    
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
            if (isLoading && subscriptions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().weight(1f)) {
                    com.gokhanaytekinn.sdandroid.ui.components.SubscriptionListSkeleton()
                }
            } else {
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
                                            .clickable { onNavigateToAnalytics() },
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
                            text = stringResource(R.string.monthly).uppercase(),
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
                             
                             if (suspiciousSubs.isEmpty() && detectedSubscriptions.isEmpty() && pendingInvitations.isEmpty()) {
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
                                 // Show Pending Invitations First
                                 if (pendingInvitations.isNotEmpty()) {
                                     item {
                                         Text(
                                             text = "Bekleyen Davetiyeler (${pendingInvitations.size})",
                                             fontSize = 14.sp,
                                             fontWeight = FontWeight.Bold,
                                             color = PrimaryBlue,
                                             modifier = Modifier.padding(bottom = 8.dp)
                                         )
                                     }
                                     items(pendingInvitations) { invitation ->
                                         InvitationListItem(
                                             invitation = invitation,
                                             onAccept = { viewModel.acceptInvitation(invitation.id) },
                                             onReject = { viewModel.rejectInvitation(invitation.id) }
                                         )
                                     }
                                     item { Spacer(modifier = Modifier.height(16.dp)) }
                                 }

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
            scannedFileCount = scanProgress?.filesScanned ?: 0,
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
    com.gokhanaytekinn.sdandroid.ui.components.SubscriptionCard(
        subscription = subscription,
        currency = currency,
        showDate = true,
        isJoint = !subscription.participants.isNullOrEmpty(),
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
fun InvitationListItem(
    invitation: com.gokhanaytekinn.sdandroid.data.model.SubscriptionInvitation,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    com.gokhanaytekinn.sdandroid.ui.components.SubscriptionCard(
        name = invitation.subscriptionName ?: "Abonelik Daveti",
        category = "Ortak Abonelik",
        cost = 0.0,
        icon = "📩",
        nextBillingDate = invitation.createdAt.split("T")[0],
        isJoint = true,
        onClick = {},
        bottomContent = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.height(32.dp).padding(end = 8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("Reddet", fontSize = 12.sp)
                }
                Button(
                    onClick = onAccept,
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Kabul Et", fontSize = 12.sp)
                }
            }
        }
    )
}
