package com.gokhanaytekinn.sdandroid.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gokhanaytekinn.sdandroid.R
import com.gokhanaytekinn.sdandroid.data.model.BillingCycle
import com.gokhanaytekinn.sdandroid.data.model.Subscription
import com.gokhanaytekinn.sdandroid.ui.theme.*
import com.gokhanaytekinn.sdandroid.util.CurrencyFormatter
import com.gokhanaytekinn.sdandroid.util.DateUtils

@Composable
fun SubscriptionCard(
    subscription: Subscription,
    modifier: Modifier = Modifier,
    currency: String = "TRY",
    showDate: Boolean = false,
    showCountdown: Boolean = false,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp, 
            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon with category-based symbol and brand-based colors
                val iconBg = when (subscription.name) {
                    "MacFit Gym" -> Color(0xFF10FFFFFF)
                    "Netflix", "Netflix Premium" -> NetflixRed.copy(alpha = 0.2f)
                    "Spotify", "Spotify Duo" -> SpotifyGreen.copy(alpha = 0.2f)
                    "Adobe Creative Cloud", "Adobe" -> AdobeRed.copy(alpha = 0.2f)
                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                }
                
                val iconColor = when (subscription.name) {
                    "MacFit Gym" -> MaterialTheme.colorScheme.onSurface
                    "Netflix", "Netflix Premium" -> NetflixRed
                    "Spotify", "Spotify Duo" -> SpotifyGreen
                    "Adobe Creative Cloud", "Adobe" -> AdobeRed
                    else -> MaterialTheme.colorScheme.onSurface
                }
                
                val icon = when (subscription.category) {
                    "Spor & Sağlık", "Sports & Health" -> Icons.Default.FitnessCenter
                    "Eğlence", "Entertainment" -> Icons.Default.Movie
                    "Müzik", "Music" -> Icons.Default.MusicNote
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
                    
                    val isOther = subscription.category == null || 
                                 subscription.category == "Other" || 
                                 subscription.category == "Diğer"
                    
                    val localizedBillingCycle = when (subscription.billingCycle) {
                        BillingCycle.MONTHLY -> stringResource(R.string.monthly)
                        BillingCycle.YEARLY -> stringResource(R.string.yearly)
                        else -> subscription.billingCycle.name
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    val displayText = if (isOther) {
                        localizedBillingCycle
                    } else {
                        val localizedCategory = when (subscription.category) {
                            "Spor & Sağlık", "Sports & Health" -> stringResource(R.string.category_sports)
                            "Eğlence", "Entertainment" -> stringResource(R.string.category_entertainment)
                            "Müzik", "Music" -> stringResource(R.string.category_music)
                            else -> subscription.category
                        }
                        "$localizedCategory • $localizedBillingCycle"
                    }
                    
                    Text(
                        text = displayText,
                        fontSize = 12.sp,
                        color = Color(0xFF9CA3AF),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = CurrencyFormatter.formatAmount(subscription.cost, currency),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                if (showDate && subscription.nextBillingDate != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = DateUtils.formatDate(subscription.nextBillingDate),
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280),
                        fontWeight = FontWeight.Medium
                    )
                }
                
                if (showCountdown && subscription.nextBillingDate != null) {
                    val daysRemaining = DateUtils.calculateDaysRemaining(subscription.nextBillingDate)
                    val dayText = when {
                        daysRemaining == 0L -> stringResource(R.string.today)
                        daysRemaining == 1L -> stringResource(R.string.tomorrow)
                        else -> "$daysRemaining ${stringResource(R.string.days_left)}"
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = dayText,
                        fontSize = 12.sp,
                        color = if (daysRemaining <= 3) ErrorColor else Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
