package com.gokhanaytekinn.sdandroid.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gokhanaytekinn.sdandroid.R
import com.gokhanaytekinn.sdandroid.data.model.BillingCycle
import com.gokhanaytekinn.sdandroid.data.model.Subscription
import com.gokhanaytekinn.sdandroid.ui.components.SDCard
import com.gokhanaytekinn.sdandroid.ui.components.SubscriptionCard

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel()
) {
    val subscriptions by viewModel.subscriptions.collectAsState()
    val stats by viewModel.stats.collectAsState()
    
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onAddSubscription() },
                containerColor = Color(0xFF2196F3),
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_subscription)
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Column {
                    Text(
                        text = stringResource(R.string.dashboard),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.my_subscriptions),
                        fontSize = 16.sp,
                        color = Color(0xFF757575)
                    )
                }
            }
            
            // Stats Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Total Monthly Cost Card
                    SDCard(
                        modifier = Modifier.weight(1f),
                        backgroundColor = Color(0xFF2196F3),
                        elevation = 4.dp
                    ) {
                        Text(
                            text = stringResource(R.string.total_monthly_cost),
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$${stats.totalMonthlyCost}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    
                    // Active Subscriptions Card
                    SDCard(
                        modifier = Modifier.weight(1f),
                        backgroundColor = Color(0xFF4CAF50),
                        elevation = 4.dp
                    ) {
                        Text(
                            text = stringResource(R.string.active_subscriptions),
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${stats.activeCount}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
            
            // Recent Subscriptions Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.recent_subscriptions),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                    TextButton(onClick = { viewModel.onViewAllClick() }) {
                        Text(
                            text = stringResource(R.string.view_all),
                            color = Color(0xFF2196F3)
                        )
                    }
                }
            }
            
            // Subscriptions List
            items(subscriptions) { subscription ->
                SubscriptionCard(
                    subscription = subscription,
                    onClick = { viewModel.onSubscriptionClick(subscription.id) }
                )
            }
            
            // Empty State
            if (subscriptions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "📭",
                                fontSize = 64.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No subscriptions yet",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF757575)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Tap the + button to add your first subscription",
                                fontSize = 14.sp,
                                color = Color(0xFF9E9E9E)
                            )
                        }
                    }
                }
            }
        }
    }
}
