package com.gokhanaytekinn.sdandroid.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gokhanaytekinn.sdandroid.data.model.Subscription
import com.gokhanaytekinn.sdandroid.data.repository.SubscriptionRepository
import com.gokhanaytekinn.sdandroid.ui.theme.*
import com.gokhanaytekinn.sdandroid.util.CurrencyFormatter
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

data class PaymentHistoryItem(
    val date: String,
    val amount: String,
    val isLatest: Boolean = false
)

@Composable
fun SubscriptionDetailsScreen(
    subscriptionId: String = "",
    onBackClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onSetReminderClick: () -> Unit = {},
    onEditPlanClick: () -> Unit = {},
    onCancelClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { SubscriptionRepository(context) }

    var subscription by remember { mutableStateOf<Subscription?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Load subscription data
    LaunchedEffect(subscriptionId) {
        if (subscriptionId.isNotBlank()) {
            isLoading = true
            val result = repository.getSubscription(subscriptionId)
            if (result.isSuccess) {
                subscription = result.getOrNull()
            } else {
                error = result.exceptionOrNull()?.message
            }
            isLoading = false
        } else {
            isLoading = false
            error = "Abonelik ID bulunamadı"
        }
    }

    val colorScheme = MaterialTheme.colorScheme
    val borderColor = colorScheme.onSurface.copy(alpha = 0.1f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = colorScheme.primary
            )
        } else if (error != null && subscription == null) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = error ?: "Bir hata oluştu",
                    color = colorScheme.error,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onBackClick) {
                    Text("Geri Dön")
                }
            }
        } else if (subscription != null) {
            val sub = subscription!!

            // Calculate remaining days
            val daysRemaining = calculateDaysRemaining(sub.nextBillingDate)
            val daysRemainingText = if (daysRemaining >= 0) "$daysRemaining gün kaldı" else ""
            val progressValue = calculateProgress(sub.billingCycle.name, daysRemaining)
            val formattedStartDate = formatDateTurkish(sub.startDate)
            val formattedRenewalDate = formatDateTurkish(sub.nextBillingDate)

            // Period text
            val periodText = when (sub.billingCycle.name) {
                "MONTHLY" -> "ay"
                "YEARLY" -> "yıl"
                "WEEKLY" -> "hafta"
                "QUARTERLY" -> "çeyrek"
                else -> "ay"
            }

            // Price text
            val priceText = CurrencyFormatter.formatAmount(sub.cost, sub.currency)

            // Icon first letter
            val iconText = sub.icon ?: sub.name.firstOrNull()?.toString() ?: "?"

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                // Top Bar
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = colorScheme.onBackground
                            )
                        }

                        Text(
                            text = sub.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colorScheme.onBackground.copy(alpha = 0f)
                        )


                    }
                }

                // Hero Section with Logo
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(112.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = iconText,
                                fontSize = 64.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = sub.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onBackground
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = priceText,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.primary
                            )
                            Text(
                                text = " / $periodText",
                                fontSize = 16.sp,
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Reminder Status
                item {
                    if (sub.reminderEnabled) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = colorScheme.primaryContainer,
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NotificationsActive,
                                        contentDescription = null,
                                        tint = colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Hatırlatıcı Açık",
                                        color = colorScheme.onPrimaryContainer,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                // Stats Cards
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Main Renewal Card - transparent with border
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = borderColor,
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Transparent
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp)
                            ) {
                                Text(
                                    text = "Bir Sonraki Ödeme",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = formattedRenewalDate,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colorScheme.onBackground
                                    )
                                    if (daysRemainingText.isNotEmpty()) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = colorScheme.primary.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = daysRemainingText,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = colorScheme.primary,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                LinearProgressIndicator(
                                    progress = progressValue,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(50)),
                                    color = colorScheme.primary,
                                    trackColor = colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                            }
                        }

                        // Secondary Stats Grid - transparent with border
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .border(
                                        width = 1.dp,
                                        color = borderColor,
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                shape = RoundedCornerShape(12.dp),
                                color = Color.Transparent
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "Toplam Harcama",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = priceText,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colorScheme.onBackground
                                    )
                                }
                            }

                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .border(
                                        width = 1.dp,
                                        color = borderColor,
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                shape = RoundedCornerShape(12.dp),
                                color = Color.Transparent
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "Başlangıç",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = formattedStartDate,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colorScheme.onBackground
                                    )
                                }
                            }
                        }
                    }
                }

                // Action Buttons
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onSetReminderClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorScheme.primary
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.NotificationsActive,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Hatırlatıcı Kur",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedButton(
                            onClick = onEditPlanClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = colorScheme.onBackground
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Planı Düzenle",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    if (sub.status == "CANCELLED") {
                        Button(
                            onClick = {
                                if (!isLoading) {
                                    scope.launch {
                                        isLoading = true
                                        val result = repository.approveSubscription(subscriptionId)
                                        if (result.isSuccess) {
                                            // Update local state with the new subscription data
                                            subscription = result.getOrNull()
                                        } else {
                                            error = result.exceptionOrNull()?.message
                                        }
                                        isLoading = false
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AccentColor
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Aboneliği Tekrar Aktif Et",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else {
                        OutlinedButton(
                            onClick = {
                                if (!isLoading) {
                                    scope.launch {
                                        isLoading = true
                                        val result = repository.cancelSubscription(subscriptionId)
                                        if (result.isSuccess) {
                                            onBackClick()
                                        } else {
                                            error = result.exceptionOrNull()?.message
                                            isLoading = false
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .height(48.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = colorScheme.error
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, colorScheme.error),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Cancel,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Aboneliği İptal Et",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Payment History Header
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.History,
                            contentDescription = null,
                            tint = colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Ödeme Geçmişi",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onBackground
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // TODO: Replace with real transaction data from API
                // For now show a placeholder based on subscription data
                item {
                    if (sub.nextBillingDate != null) {
                        PaymentHistoryRow(
                            PaymentHistoryItem(
                                date = formattedRenewalDate,
                                amount = priceText,
                                isLatest = true
                            ),
                            colorScheme
                        )
                    }
                }


            }
        }
    }
}

@Composable
fun PaymentHistoryRow(item: PaymentHistoryItem, colorScheme: ColorScheme) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Timeline dot
            Box(
                modifier = Modifier
                    .size(if (item.isLatest) 20.dp else 14.dp)
                    .background(
                        if (item.isLatest) colorScheme.primary else colorScheme.onSurfaceVariant,
                        CircleShape
                    )
            )

            Column {
                Text(
                    text = item.date,
                    fontSize = 14.sp,
                    fontWeight = if (item.isLatest) FontWeight.SemiBold else FontWeight.Medium,
                    color = colorScheme.onBackground
                )
            }
        }

        Text(
            text = item.amount,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (item.isLatest) colorScheme.onBackground else colorScheme.onSurfaceVariant
        )
    }
}

// Helper functions

private fun calculateDaysRemaining(renewalDate: String?): Int {
    if (renewalDate.isNullOrBlank()) return -1
    return try {
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
        val nextDate = LocalDate.parse(renewalDate, formatter)
        val today = LocalDate.now()
        ChronoUnit.DAYS.between(today, nextDate).toInt()
    } catch (e: Exception) {
        -1
    }
}

private fun calculateProgress(billingCycle: String, daysRemaining: Int): Float {
    if (daysRemaining < 0) return 0f
    val totalDays = when (billingCycle) {
        "MONTHLY" -> 30
        "YEARLY" -> 365
        "WEEKLY" -> 7
        "QUARTERLY" -> 90
        else -> 30
    }
    val elapsed = totalDays - daysRemaining
    return (elapsed.toFloat() / totalDays.toFloat()).coerceIn(0f, 1f)
}

private fun formatDateTurkish(dateStr: String?): String {
    if (dateStr.isNullOrBlank()) return "-"
    return try {
        val date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE)
        val months = listOf(
            "Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran",
            "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"
        )
        "${date.dayOfMonth} ${months[date.monthValue - 1]} ${date.year}"
    } catch (e: Exception) {
        dateStr
    }
}
