package com.gokhanaytekinn.sdandroid.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.gokhanaytekinn.sdandroid.data.model.Subscription
import com.gokhanaytekinn.sdandroid.data.model.BillingCycle
import com.gokhanaytekinn.sdandroid.ui.theme.*
import com.gokhanaytekinn.sdandroid.util.CurrencyFormatter
import com.gokhanaytekinn.sdandroid.util.DateUtils

@Composable
fun getBrandColor(name: String): Color {
    return when {
        name.contains("Netflix", ignoreCase = true) -> NetflixRed
        name.contains("Spotify", ignoreCase = true) -> SpotifyGreen
        name.contains("Adobe", ignoreCase = true) -> AdobeRed
        else -> MaterialTheme.colorScheme.primary
    }
}

@Composable
fun SubscriptionCard(
    subscription: Subscription,
    currency: String = "TRY",
    showDate: Boolean = false,
    showCountdown: Boolean = false,
    isJoint: Boolean = false,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    bottomContent: @Composable (ColumnScope.() -> Unit)? = null
) {
    SubscriptionCard(
        name = subscription.name,
        category = subscription.category ?: "Other",
        cost = subscription.cost,
        currency = currency,
        billingCycle = subscription.billingCycle,
        icon = subscription.icon,
        nextBillingDate = subscription.nextBillingDate,
        isJoint = isJoint,
        showDate = showDate,
        showCountdown = showCountdown,
        onClick = onClick,
        modifier = modifier,
        bottomContent = bottomContent
    )
}

@Composable
fun SubscriptionCard(
    name: String,
    category: String,
    cost: Double,
    currency: String = "TRY",
    billingCycle: BillingCycle = BillingCycle.MONTHLY,
    icon: String? = null,
    nextBillingDate: String? = null,
    isJoint: Boolean = false,
    showDate: Boolean = false,
    showCountdown: Boolean = false,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    bottomContent: @Composable (ColumnScope.() -> Unit)? = null
) {
    val brandColor = getBrandColor(name)
    val daysRemaining = nextBillingDate?.let { DateUtils.calculateDaysRemaining(it) } ?: -1L

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(
            1.dp, 
            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Icon placeholder with brand color
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(brandColor.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = icon ?: name.take(1).uppercase(),
                            color = brandColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            if (isJoint) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Group,
                                    contentDescription = "Joint Subscription",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        
                        val context = androidx.compose.ui.platform.LocalContext.current
                        val categoryResId = context.resources.getIdentifier(category, "string", context.packageName)
                        val localizedCategory = if (categoryResId != 0) stringResource(categoryResId) else category

                        val cycleText = when (billingCycle) {
                            BillingCycle.MONTHLY -> stringResource(R.string.billing_monthly_label)
                            BillingCycle.YEARLY -> stringResource(R.string.billing_yearly_label)
                            BillingCycle.WEEKLY -> stringResource(R.string.billing_weekly_label)
                            else -> stringResource(R.string.period_monthly)
                        }

                        val categoryText = if (category == "Other" || category == "Diğer" || category == "category_other") {
                             cycleText
                        } else {
                            "$localizedCategory • $cycleText"
                        }
                        
                        Text(
                            text = categoryText,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = CurrencyFormatter.formatAmount(cost, currency),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    if (showDate && nextBillingDate != null) {
                        Text(
                            text = DateUtils.formatDate(nextBillingDate),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else if (showCountdown && nextBillingDate != null) {
                        val dayText = when {
                            daysRemaining == 0L -> stringResource(R.string.today)
                            daysRemaining == 1L -> stringResource(R.string.tomorrow)
                            else -> "$daysRemaining ${stringResource(R.string.days_left)}"
                        }
                        Text(
                            text = dayText,
                            fontSize = 12.sp,
                            color = if (daysRemaining <= 3 && daysRemaining >= 0) ErrorColor else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            if (bottomContent != null) {
                Divider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    thickness = 1.dp
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    bottomContent()
                }
            }
        }
    }
}
