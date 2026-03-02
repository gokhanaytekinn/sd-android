package com.gokhanaytekinn.sdandroid.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.draw.blur
import androidx.compose.foundation.border
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gokhanaytekinn.sdandroid.R
import com.gokhanaytekinn.sdandroid.data.model.Subscription
import com.gokhanaytekinn.sdandroid.ui.components.BottomNavigationBar
import com.gokhanaytekinn.sdandroid.ui.components.SubscriptionCard
import com.gokhanaytekinn.sdandroid.ui.components.SDCard
import com.gokhanaytekinn.sdandroid.ui.theme.*
import com.gokhanaytekinn.sdandroid.data.preferences.CurrencyPreferences
import com.gokhanaytekinn.sdandroid.util.CurrencyFormatter
import com.gokhanaytekinn.sdandroid.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToSuspicious: () -> Unit = {},
    onNavigateToAllSubscriptions: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    onNavigateToUpcoming: () -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val application = context.applicationContext as android.app.Application
    val viewModel: DashboardViewModel = remember { DashboardViewModel(application) }
    val authViewModel: com.gokhanaytekinn.sdandroid.ui.viewmodel.AuthViewModel = remember { com.gokhanaytekinn.sdandroid.ui.viewmodel.AuthViewModel(context) }
    val currencyPreferences = remember { CurrencyPreferences(context) }
    
    val subscriptions by viewModel.subscriptions.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val authState by authViewModel.authState.collectAsState()
    val suspiciousCount by viewModel.suspiciousCount.collectAsState()
    val upcomingSubs by viewModel.upcomingSubscriptions.collectAsState()
    val selectedCurrency by currencyPreferences.selectedCurrency.collectAsState(initial = "TRY")
    
    // Calculate expensive subscriptions outside of LazyColumn items
    val expensiveSubscriptions = remember(subscriptions) {
        subscriptions.sortedByDescending { it.cost }.take(3)
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .padding(top = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.hello_user, authState.userName ?: stringResource(R.string.guest_user)),
                        fontSize = 14.sp,
                        color = Color(0xFF9CA3AF),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.dashboard_title),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
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

                    // Profile picture
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFD4B8)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "👤", fontSize = 20.sp)
                    }
                }
            }
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Hero Card - Total Spend
                if (isLoading && stats.totalMonthlyCost == 0.0) {
                    item {
                        com.gokhanaytekinn.sdandroid.ui.components.DashboardSkeleton()
                    }
                } else {
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Transparent,
                            tonalElevation = 0.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.total_monthly),
                                    fontSize = 14.sp,
                                    color = Color(0xFF9CA3AF),
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = CurrencyFormatter.formatAmount(stats.totalMonthlyCost, selectedCurrency),
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Black,
                                    color = PrimaryBlue
                                )
                            }
                        }
                    }
                }

                // Upcoming Payments Section
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 0.dp), // Adjusted padding since LazyColumn has spacing
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.upcoming_payments),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        
                        Text(
                            text = stringResource(R.string.view_all),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = PrimaryBlue,
                            modifier = Modifier.clickable { onNavigateToUpcoming() }
                        )
                    }
                }
                
                if (upcomingSubs.isEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.no_upcoming_payments),
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                } else {
                    items(upcomingSubs) { sub ->
                        SubscriptionCard(
                            subscription = sub,
                            currency = sub.currency,
                            showDate = true,
                            isJoint = !sub.participants.isNullOrEmpty(),
                            onClick = { onNavigateToUpcoming() }
                        )
                    }
                }

                if (suspiciousCount > 0) {
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToSuspicious() },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSystemInDarkTheme()) Color(0xFF1E1510) else MaterialTheme.colorScheme.errorContainer,
                            border = if (!isSystemInDarkTheme()) BorderStroke(1.dp, MaterialTheme.colorScheme.error) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(WarningColor.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = WarningColor,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    
                                    Column {
                                        Text(
                                            text = stringResource(R.string.suspicious_subscriptions),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSystemInDarkTheme()) Color(0xFFFFE7D6) else MaterialTheme.colorScheme.onErrorContainer
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = stringResource(R.string.transactions_pending, suspiciousCount),
                                            fontSize = 12.sp,
                                            color = if (isSystemInDarkTheme()) Color(0xFFFFE7D6).copy(alpha = 0.6f) else MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                                
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = WarningColor.copy(alpha = 0.4f)
                                )
                            }
                        }
                    }
                }
                
                // Expensive Subscriptions List
                item {
                    Text(
                        text = stringResource(R.string.most_expensive),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
                
                if (expensiveSubscriptions.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.no_subscriptions_yet),
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    items(expensiveSubscriptions) { subscription ->
                        SubscriptionListItem(
                            subscription = subscription,
                            currency = selectedCurrency
                        )
                    }
                }
                
                // Main Action Button
                item {
                    Button(
                        onClick = onNavigateToAllSubscriptions,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.view_all_subscriptions),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null
                        )
                    }
                }
            }
            }
        }
    }

@Composable
fun SubscriptionListItem(
    subscription: Subscription,
    currency: String = "TRY",
    onClick: () -> Unit = {}
) {
    com.gokhanaytekinn.sdandroid.ui.components.SubscriptionCard(
        subscription = subscription,
        currency = currency,
        isJoint = !subscription.participants.isNullOrEmpty(),
        onClick = onClick
    )
}
