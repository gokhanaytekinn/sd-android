package com.gokhanaytekinn.sdandroid.ui.screens

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
import com.gokhanaytekinn.sdandroid.ui.theme.*
import com.gokhanaytekinn.sdandroid.data.preferences.CurrencyPreferences
import com.gokhanaytekinn.sdandroid.util.CurrencyFormatter

@Composable
fun DashboardScreen(
    onNavigateToSuspicious: () -> Unit = {},
    onNavigateToAllSubscriptions: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val application = context.applicationContext as android.app.Application
    val viewModel: DashboardViewModel = remember { DashboardViewModel(application) }
    val authViewModel: com.gokhanaytekinn.sdandroid.ui.viewmodel.AuthViewModel = remember { com.gokhanaytekinn.sdandroid.ui.viewmodel.AuthViewModel(context) }
    val currencyPreferences = remember { CurrencyPreferences(context) }
    
    val subscriptions by viewModel.subscriptions.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val authState by authViewModel.authState.collectAsState()
    val suspiciousCount by viewModel.suspiciousCount.collectAsState()
    val selectedCurrency by currencyPreferences.selectedCurrency.collectAsState(initial = "TRY")
    
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
                        text = "Merhaba, ${authState.userName ?: "Kullanıcı"}",
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
            
            // Scrollable Content
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Hero Card - Total Spend
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = Color.White.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(16.dp)
                            ),
                        shape = RoundedCornerShape(16.dp),
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
                
                
                // Suspicious Activity Alert - Only show if there are suspicious subscriptions
                if (suspiciousCount > 0) {
    item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToSuspicious() },
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1E1510)
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
                                        text = "Şüpheli Abonelikler",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFE7D6)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${suspiciousCount} işlem inceleme bekliyor",
                                        fontSize = 12.sp,
                                        color = Color(0xFFFFE7D6).copy(alpha = 0.6f),
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "İçinde En Pahalı Abonelikler",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        TextButton(onClick = onNavigateToAllSubscriptions) {
                            Text(
                                text = "Tümü",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryBlue
                            )
                        }
                    }
                }
                
                // Subscription items
                items(subscriptions.take(3)) { subscription ->
                    SubscriptionListItem(
                        subscription = subscription,
                        currency = selectedCurrency
                    )
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
                            contentColor = BackgroundDark
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Tüm Abonelikleri Gör",
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
                
                // Bottom spacing for nav bar
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
        
        // Bottom Navigation
        BottomNavigationBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            selectedTab = 0,
            onSubscriptionsClick = onNavigateToAllSubscriptions,
            onSearchClick = onNavigateToSearch,
            onSettingsClick = onNavigateToSettings
        )
    }
}

@Composable
fun SubscriptionListItem(
    subscription: Subscription,
    currency: String = "TRY",
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
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
                // Icon
                val iconBg = when (subscription.name) {
                    "MacFit Gym" -> Color(0xFF10FFFFFF)
                    "Netflix" -> NetflixRed.copy(alpha = 0.2f)
                    "Spotify" -> SpotifyGreen.copy(alpha = 0.2f)
                    else -> Color.White.copy(alpha = 0.1f)
                }
                
                val iconColor = when (subscription.name) {
                    "MacFit Gym" -> Color.White
                    "Netflix" -> NetflixRed
                    "Spotify" -> SpotifyGreen
                    else -> Color.White
                }
                
                val icon = when (subscription.category) {
                    "Spor & Sağlık" -> Icons.Default.FitnessCenter
                    "Eğlence" -> Icons.Default.Movie
                    "Müzik" -> Icons.Default.MusicNote
                    else -> Icons.Default.Star
                }
                
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
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
                    Text(
                        text = subscription.category ?: "",
                        fontSize = 12.sp,
                        color = Color(0xFF9CA3AF),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Text(
                text = CurrencyFormatter.formatAmount(subscription.cost, currency),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}


