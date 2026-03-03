package com.gokhanaytekinn.sdandroid.ui.screens

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gokhanaytekinn.sdandroid.R
import com.gokhanaytekinn.sdandroid.ui.theme.*

enum class SubscriptionPlanType {
    FREE, MONTHLY, YEARLY
}

@Composable
fun PremiumUpgradeScreen(
    initialPlan: SubscriptionPlanType = SubscriptionPlanType.FREE,
    onCloseClick: () -> Unit = {},
    onUpgradeClick: (SubscriptionPlanType) -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val premiumPreferences = remember { com.gokhanaytekinn.sdandroid.data.preferences.PremiumPreferences(context) }
    val billingManager = remember { com.gokhanaytekinn.sdandroid.util.BillingManager(context, premiumPreferences) }
    
    var selectedPlan by remember { mutableStateOf(initialPlan) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    
    val features = remember(selectedPlan) {
        when (selectedPlan) {
            SubscriptionPlanType.FREE -> listOf(
                FeatureData(Icons.Filled.TrackChanges, R.string.feature_auto_capture_title, R.string.feature_auto_capture_free_desc, false),
                FeatureData(Icons.Filled.AllInclusive, R.string.feature_unlimited_tracking_title, R.string.feature_unlimited_tracking_free_desc, false)
            )
            else -> listOf(
                FeatureData(Icons.Filled.TrackChanges, R.string.feature_auto_capture_title, R.string.feature_auto_capture_premium_desc, true),
                FeatureData(Icons.Filled.AllInclusive, R.string.feature_unlimited_tracking_title, R.string.feature_unlimited_tracking_premium_desc, true)
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onCloseClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.close),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Text(
                    text = stringResource(R.string.plans_header),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue,
                    letterSpacing = 2.sp
                )
                
                Spacer(modifier = Modifier.width(40.dp))
            }
            
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp)
            ) {
                // Hero Text
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.premium_hero_title),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center,
                            lineHeight = 40.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = stringResource(R.string.premium_hero_subtitle),
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                // Features List
                item {
                    Spacer(modifier = Modifier.height(40.dp))
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        features.forEach { feature ->
                            FeatureRow(feature)
                        }
                    }
                }
                
                // Pricing Cards
                item {
                    Spacer(modifier = Modifier.height(48.dp))
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Free Plan
                        PricingCard(
                            title = stringResource(R.string.plan_free),
                            price = "₺0",
                            period = "",
                            badge = null,
                            selected = selectedPlan == SubscriptionPlanType.FREE,
                            onClick = { selectedPlan = SubscriptionPlanType.FREE }
                        )

                        // Monthly Plan
                        PricingCard(
                            title = stringResource(R.string.plan_monthly_premium),
                            price = "₺99.99",
                            period = stringResource(R.string.per_month),
                            badge = null,
                            selected = selectedPlan == SubscriptionPlanType.MONTHLY,
                            onClick = { selectedPlan = SubscriptionPlanType.MONTHLY }
                        )

                        // Yearly Plan
                        PricingCard(
                            title = stringResource(R.string.plan_yearly_premium),
                            price = "₺799.99",
                            period = stringResource(R.string.per_year),
                            badge = stringResource(R.string.most_popular),
                            selected = selectedPlan == SubscriptionPlanType.YEARLY,
                            onClick = { selectedPlan = SubscriptionPlanType.YEARLY }
                        )
                    }
                }
                
                // Trust Badge
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.secure_payment_footer),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                    Spacer(modifier = Modifier.height(140.dp))
                }
            }
        }
        
        // Sticky Bottom CTA
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Button(
                    onClick = { 
                        val activity = context as? Activity
                        if (activity != null) {
                            val productId = if (selectedPlan == SubscriptionPlanType.MONTHLY) "premium_monthly" else "premium_yearly"
                            billingManager.launchBillingFlow(activity, productId)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedPlan == SubscriptionPlanType.FREE) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f) else PrimaryBlue,
                        contentColor = if (selectedPlan == SubscriptionPlanType.FREE) MaterialTheme.colorScheme.onSurface else Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    enabled = selectedPlan != SubscriptionPlanType.FREE
                ) {
                    Text(
                        text = if (selectedPlan == SubscriptionPlanType.FREE) stringResource(R.string.current_plan) else stringResource(R.string.upgrade_to_premium_btn),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    LegalText(stringResource(R.string.restore_purchase)) { /* Restore logic */ }
                    LegalText(stringResource(R.string.terms_of_use_title)) { showTermsDialog = true }
                    LegalText(stringResource(R.string.privacy_policy_title)) { showPrivacyDialog = true }
                }
            }
        }
    }

    // Terms of Use Dialog
    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            title = { Text(stringResource(R.string.terms_of_use_title), fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.terms_of_use_content),
                        lineHeight = 20.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showTermsDialog = false }) {
                    Text(stringResource(R.string.understood), color = PrimaryBlue, fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    // Privacy Policy Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text(stringResource(R.string.privacy_policy_title), fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.privacy_dialog_content),
                        lineHeight = 20.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text(stringResource(R.string.close), color = PrimaryBlue, fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

data class FeatureData(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: Int,
    val description: Int,
    val isEnabled: Boolean
)

@Composable
fun FeatureRow(feature: FeatureData) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .border(
                    width = 1.dp,
                    color = if (feature.isEnabled) PrimaryBlue.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = feature.icon,
                contentDescription = null,
                tint = if (feature.isEnabled) PrimaryBlue else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.size(22.dp)
            )
        }
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(feature.title),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (feature.isEnabled) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            Text(
                text = stringResource(feature.description),
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
        
        Icon(
            imageVector = if (feature.isEnabled) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
            contentDescription = null,
            tint = if (feature.isEnabled) PrimaryBlue else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun PricingCard(
    title: String,
    price: String,
    period: String,
    badge: String?,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent,
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) PrimaryBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )
    ) {
        Box(modifier = Modifier.padding(20.dp)) {
            if (badge != null) {
                Surface(
                    modifier = Modifier.align(Alignment.TopEnd),
                    shape = RoundedCornerShape(6.dp),
                    color = PrimaryBlue
                ) {
                    Text(
                        text = badge,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            Column {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selected) PrimaryBlue else MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = price,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    if (period.isNotEmpty()) {
                        Text(
                            text = period,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 4.dp, start = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LegalText(
    text: String,
    onClick: () -> Unit
) {
    Text(
        text = text,
        fontSize = 11.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
        textDecoration = TextDecoration.Underline,
        modifier = Modifier.clickable { onClick() }
    )
}
