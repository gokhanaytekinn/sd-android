package com.gokhanaytekinn.sdandroid.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gokhanaytekinn.sdandroid.data.model.Subscription
import com.gokhanaytekinn.sdandroid.ui.theme.*

data class SuspiciousTransaction(
    val name: String,
    val date: String,
    val category: String,
    val amount: Double,
    val icon: String,
    val backgroundColor: Color,
    val subscriptionId: String? = null,
    val invitationId: String? = null,
    val isInvitation: Boolean = false
)

@Composable
fun SuspiciousPaymentsScreen(
    onBackClick: () -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val viewModel: com.gokhanaytekinn.sdandroid.ui.viewmodel.SuspiciousPaymentsViewModel = remember { com.gokhanaytekinn.sdandroid.ui.viewmodel.SuspiciousPaymentsViewModel(context) }
    
    val suspiciousTransactions by viewModel.suspiciousTransactions.collectAsState()
    val pendingInvitations by viewModel.pendingInvitations.collectAsState()
    
    val allItems = remember(suspiciousTransactions, pendingInvitations) {
        pendingInvitations.map { inv ->
            SuspiciousTransaction(
                name = inv.subscriptionName ?: "Abonelik Daveti",
                date = inv.createdAt.split("T")[0],
                category = "Ortak Abonelik",
                amount = 0.0,
                icon = "📩",
                backgroundColor = PrimaryBlue,
                invitationId = inv.id,
                isInvitation = true
            )
        } + suspiciousTransactions
    }

    val currentStep by viewModel.currentStep.collectAsState()
    val totalSteps = allItems.size
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            
            Text(
                text = "Şüpheli Ödemeler",
                fontSize = 18.sp,

                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Spacer(modifier = Modifier.size(40.dp))
        }
        
        // Progress Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${totalSteps - currentStep + 1} şüpheli işlem kaldı",
                    fontSize = 14.sp,

                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Adım $currentStep / $totalSteps",
                    fontSize = 12.sp,
                    color = Color(0xFF9db99d)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFF2c422c))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(currentStep.toFloat() / totalSteps)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(50))
                        .background(PrimaryBlue)
                )
            }
        }
        
        // Explainer Text
        Text(
            text = "Aşağıdaki işlemler düzenli abonelik ödemesi gibi görünüyor. Lütfen bu harcamaları doğrulayın.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.padding(16.dp),
            lineHeight = 20.sp
        )
        
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(allItems) { index, item ->
                SuspiciousTransactionCard(
                    transaction = item,
                    onConfirm = {
                        viewModel.onConfirmTransaction(item)
                    },
                    onReject = {
                        viewModel.onRejectTransaction(item)
                    }
                )
            }
            
            // Info Note
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E3A8A).copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF60A5FA),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Onaylanan abonelikler \"Aboneliklerim\" listesine eklenir ve gelecek ödemeler için hatırlatıcılar oluşturulur.",
                            fontSize = 12.sp,
                            color = Color(0xFF93C5FD),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SuspiciousTransactionCard(
    transaction: SuspiciousTransaction,
    onConfirm: () -> Unit,
    onReject: () -> Unit,
    disabled: Boolean = false
) {
    com.gokhanaytekinn.sdandroid.ui.components.SubscriptionCard(
        name = transaction.name,
        category = transaction.category,
        cost = transaction.amount,
        icon = transaction.icon,
        nextBillingDate = transaction.date, // This will be formatted by the card
        isJoint = transaction.isInvitation,
        onClick = {},
        bottomContent = if (!disabled) {
            {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Abonelik mi?",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF9db99d)
                    )
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onReject,
                            modifier = Modifier.height(36.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFFD1D5DB)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                Color(0xFF4B5563)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Hayır",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        
                        Button(
                            onClick = onConfirm,
                            modifier = Modifier.height(36.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryBlue,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Evet",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        } else null
    )
}
