package com.gokhanaytekinn.sdandroid.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.res.stringResource
import com.gokhanaytekinn.sdandroid.R
import com.gokhanaytekinn.sdandroid.ui.theme.*
import com.gokhanaytekinn.sdandroid.util.DeviceSubscriptionScanner
import com.gokhanaytekinn.sdandroid.util.CurrencyFormatter

@Composable
fun ScanningDialog(
    isScanning: Boolean,
    progress: DeviceSubscriptionScanner.ScanProgress?,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = { if (!isScanning) onDismiss() }) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment =Alignment.CenterHorizontally
            ) {
                // Loading icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (isScanning) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            color = PrimaryBlue
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = SuccessColor,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = if (isScanning) stringResource(R.string.scan_dialog_scanning) else stringResource(R.string.scan_dialog_completed),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                progress?.let {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.scan_dialog_sms_count, it.smsScanned),
                            fontSize = 14.sp,
                            color = Color(0xFF9CA3AF)
                        )
                        Text(
                            text = stringResource(R.string.scan_dialog_file_count, it.filesScanned),
                            fontSize = 14.sp,
                            color = Color(0xFF9CA3AF)
                        )
                        if (it.totalDetected > 0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.scan_dialog_detected_count, it.totalDetected),
                                fontSize = 14.sp,
                                color = SuccessColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
                
                if (!isScanning) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue
                        )
                    ) {
                        Text(stringResource(R.string.scan_dialog_ok))
                    }
                }
            }
        }
    }
}

@Composable
fun DetectedSubscriptionsDialog(
    detectedSubscriptions: List<DeviceSubscriptionScanner.DetectedSubscription>,
    scannedFileCount: Int = 0,
    onConfirm: (DeviceSubscriptionScanner.DetectedSubscription) -> Unit,
    onReject: (DeviceSubscriptionScanner.DetectedSubscription) -> Unit,
    onDismiss: () -> Unit,
    currency: String = "TRY"
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.detected_subs_title),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.detected_subs_found, detectedSubscriptions.size),
                            fontSize = 14.sp,
                            color = Color(0xFF9CA3AF)
                        )
                        if (scannedFileCount > 0) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Toplam $scannedFileCount dosya tarandı",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.detected_subs_close),
                            tint = Color.White
                        )
                    }
                }
                
                Divider(color = Color.White.copy(alpha = 0.1f))
                
                // List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(detectedSubscriptions) { subscription ->
                        DetectedSubscriptionItem(
                            subscription = subscription,
                            currency = currency,
                            onConfirm = { onConfirm(subscription) },
                            onReject = { onReject(subscription) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetectedSubscriptionItem(
    subscription: DeviceSubscriptionScanner.DetectedSubscription,
    currency: String,
    onConfirm: () -> Unit,
    onReject: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = subscription.serviceName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = CurrencyFormatter.formatAmount(subscription.amount, currency),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBlue
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = when (subscription.billingCycle) {
                            "MONTHLY" -> stringResource(R.string.billing_monthly_label)
                            "YEARLY" -> stringResource(R.string.billing_yearly_label)
                            "WEEKLY" -> stringResource(R.string.billing_weekly_label)
                            else -> subscription.billingCycle
                        },
                        fontSize = 12.sp,
                        color = Color(0xFF9CA3AF)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = when (subscription.source) {
                                "SMS" -> Icons.Default.Message
                                else -> Icons.Default.Folder
                            },
                            contentDescription = null,
                            tint = Color(0xFF6B7280),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${subscription.source}: ${subscription.details}",
                            fontSize = 11.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = WarningColor
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.detected_sub_reject))
                }
                
                Button(
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SuccessColor,
                        contentColor = MaterialTheme.colorScheme.background
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.detected_sub_add))
                }
            }
        }
    }
}
