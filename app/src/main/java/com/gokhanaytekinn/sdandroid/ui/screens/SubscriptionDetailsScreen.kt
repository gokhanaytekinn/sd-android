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
import androidx.compose.ui.res.stringResource
import com.gokhanaytekinn.sdandroid.R
import com.gokhanaytekinn.sdandroid.data.model.Subscription
import com.gokhanaytekinn.sdandroid.data.model.SubscriptionStatus
import com.gokhanaytekinn.sdandroid.data.model.BillingCycle
import com.gokhanaytekinn.sdandroid.data.repository.SubscriptionRepository
import com.gokhanaytekinn.sdandroid.ui.theme.*
import com.gokhanaytekinn.sdandroid.util.CurrencyFormatter
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun SubscriptionDetailsScreen(
    subscriptionId: String = "",
    onBackClick: () -> Unit = {},
    onEditPlanClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { SubscriptionRepository(context) }

    var subscription by remember { mutableStateOf<Subscription?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var participantToRemove by remember { mutableStateOf<com.gokhanaytekinn.sdandroid.data.model.InvitationParticipant?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

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
            error = context.getString(R.string.subscription_id_not_found)
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
            com.gokhanaytekinn.sdandroid.ui.components.SubscriptionDetailsSkeleton()
        } else if (error != null && subscription == null) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = (error ?: stringResource(R.string.an_error_occurred)).toString(),
                    color = colorScheme.error,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onBackClick) {
                    Text(stringResource(R.string.go_back))
                }

            }
        } else if (subscription != null) {
            val sub = subscription!!

            // Calculate remaining days
            val daysRemaining = calculateDaysRemaining(sub.nextBillingDate)
            val progressValue = calculateProgress(sub.billingCycle.name, daysRemaining)
            val formattedStartDate = formatDateLocalized(context, sub.startDate)


            // Period text
            val periodText = when (sub.billingCycle.name) {
                "MONTHLY" -> stringResource(R.string.period_monthly).lowercase()
                "YEARLY" -> stringResource(R.string.period_yearly).lowercase()
                "WEEKLY" -> stringResource(R.string.period_weekly).lowercase()
                else -> stringResource(R.string.period_monthly).lowercase()
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
                                contentDescription = stringResource(R.string.back),
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
                                .clip(RoundedCornerShape(12.dp))
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
                                shape = RoundedCornerShape(12.dp)
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
                                        text = stringResource(R.string.reminder),
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
                        // Main Renewal Card - Using unified SubscriptionCard
                        com.gokhanaytekinn.sdandroid.ui.components.SubscriptionCard(
                            subscription = sub,
                            showDate = true,
                            isJoint = !sub.participants.isNullOrEmpty(),
                            bottomContent = {
                                Text(
                                    text = stringResource(R.string.next_payment),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = colorScheme.onSurfaceVariant
                                )

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
                        )

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
                                        text = stringResource(R.string.total_spent),
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
                                        text = stringResource(R.string.start_date_label),
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
                    if (sub.isOwner) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (!isLoading) {
                                        scope.launch {
                                            isLoading = true
                                            val newStatus = !sub.reminderEnabled
                                            val updatedSubscription = sub.copy(reminderEnabled = newStatus)
                                            val result = repository.updateSubscription(subscriptionId, updatedSubscription)
                                            if (result.isSuccess) {
                                                subscription = result.getOrNull()
                                            } else {
                                                error = result.exceptionOrNull()?.message
                                            }
                                            isLoading = false
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (sub.reminderEnabled) MaterialTheme.colorScheme.surfaceVariant else colorScheme.primary,
                                    contentColor = if (sub.reminderEnabled) MaterialTheme.colorScheme.onSurfaceVariant else colorScheme.onPrimary
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = if (sub.reminderEnabled) Icons.Default.NotificationsOff else Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (sub.reminderEnabled) stringResource(R.string.turn_off_reminder) else stringResource(R.string.set_reminder),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
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
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.edit_plan),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )

                            }
                        }
                    }
                    
                    if (sub.isOwner) {
                        Spacer(modifier = Modifier.height(12.dp))
                        if (sub.status == SubscriptionStatus.CANCELLED.value) {
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
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.reactivate_subscription),
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
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Cancel,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.cancel_subscription_btn),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )

                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            TextButton(
                                onClick = { showDeleteDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = colorScheme.error
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteForever,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.delete_subscription),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Participants Section
                item {
                    val participants = sub.participants
                    if (!participants.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(32.dp))
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.People,
                                contentDescription = null,
                                tint = colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = stringResource(R.string.participants),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onBackground
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        participants.forEach { participant ->
                            ParticipantRow(
                                participant = participant,
                                colorScheme = colorScheme,
                                canRemove = sub.isOwner,
                                onRemove = {
                                    participantToRemove = participant
                                }
                            )
                        }
                    }
                }

                // Confirmation Dialog for Participant Removal
                item {
                    if (participantToRemove != null) {
                        AlertDialog(
                            onDismissRequest = { participantToRemove = null },
                            title = { Text(stringResource(R.string.remove_participant_title)) },
                            text = { 
                                Text(
                                    stringResource(
                                        R.string.remove_participant_confirm, 
                                        participantToRemove!!.name ?: participantToRemove!!.email
                                    )
                                ) 
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        val participant = participantToRemove!!
                                        participantToRemove = null
                                        scope.launch {
                                            isLoading = true
                                            val result = repository.removeParticipant(sub.id, participant.email)
                                            if (result.isSuccess) {
                                                subscription = result.getOrNull()
                                            } else {
                                                error = result.exceptionOrNull()?.message
                                            }
                                            isLoading = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.error)
                                ) {
                                    Text(stringResource(R.string.remove_participant_btn))
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { participantToRemove = null }) {
                                    Text(stringResource(R.string.cancel))
                                }
                            }
                        )
                    }
                }

            }
        }
    }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_subscription)) },
            text = { Text(stringResource(R.string.delete_subscription_confirm_desc)) },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        scope.launch {
                            isLoading = true
                            val result = repository.deleteSubscription(subscriptionId)
                            if (result.isSuccess) {
                                onBackClick()
                            } else {
                                error = result.exceptionOrNull()?.message
                                isLoading = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.error)
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
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

private fun formatDateLocalized(context: android.content.Context, dateStr: String?): String {
    if (dateStr.isNullOrBlank()) return "-"
    return try {
        val date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE)
        val locale = context.resources.configuration.locales[0]
        val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", locale)
        date.format(formatter)
    } catch (e: Exception) {
        dateStr
    }
}

@Composable
fun ParticipantRow(
    participant: com.gokhanaytekinn.sdandroid.data.model.InvitationParticipant,
    colorScheme: ColorScheme,
    canRemove: Boolean = false,
    onRemove: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(colorScheme.surfaceVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = colorScheme.onSurfaceVariant
                )
            }
            Column {
                Text(
                    text = participant.name ?: participant.email,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorScheme.onBackground
                )
                if (participant.name != null) {
                    Text(
                        text = participant.email,
                        fontSize = 11.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val (icon, tint) = when (participant.status) {
                "ACCEPTED" -> Icons.Default.CheckCircle to Color(0xFF4CAF50)
                "REJECTED" -> Icons.Default.Cancel to colorScheme.error
                else -> Icons.Default.Pending to colorScheme.onSurfaceVariant
            }

            Icon(
                imageVector = icon,
                contentDescription = participant.status,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )

            if (canRemove) {
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Remove",
                        tint = colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
