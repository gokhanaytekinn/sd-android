package com.gokhanaytekinn.sdandroid.ui.screens

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
    val backgroundColor: Color
)

@Composable
fun SuspiciousPaymentsScreen(
    onBackClick: () -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val viewModel: com.gokhanaytekinn.sdandroid.ui.viewmodel.SuspiciousPaymentsViewModel = remember { com.gokhanaytekinn.sdandroid.ui.viewmodel.SuspiciousPaymentsViewModel(context) }
    
    val suspiciousTransactions by viewModel.suspiciousTransactions.collectAsState()
    val currentStep by viewModel.currentStep.collectAsState()
    val totalSteps = viewModel.totalSteps
    
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
                    tint = Color.White
                )
            }
            
            Text(
                text = "Şüpheli Ödemeler",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
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
                    color = Color.White
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
            color = Color(0xFFD1D5DB),
            modifier = Modifier.padding(16.dp),
            lineHeight = 20.sp
        )
        
        // Transaction Cards
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(suspiciousTransactions.take(currentStep)) { index, transaction ->
                if (index == currentStep - 1) {
                    SuspiciousTransactionCard(
                        transaction = transaction,
                        onConfirm = {
                            viewModel.onConfirmTransaction(transaction)
                        },
                        onReject = {
                            viewModel.onRejectTransaction()
                        }
                    )
                } else {
                    // Already confirmed transactions
                    SuspiciousTransactionCard(
                        transaction = transaction,
                        onConfirm = {},
                        onReject = {},
                        disabled = true
                    )
                }
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
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Transaction Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(transaction.backgroundColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = transaction.icon,
                            fontSize = if (transaction.icon.length == 1) 20.sp else 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (transaction.icon.length == 1) Color.White else Color.Black
                        )
                    }
                    
                    Column {
                        Text(
                            text = transaction.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${transaction.date} • ${transaction.category}",
                            fontSize = 12.sp,
                            color = Color(0xFF9CA3AF),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                Text(
                    text = "-${String.format("%.2f", transaction.amount)} ₺",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            Divider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = Color.White.copy(alpha = 0.05f),
                thickness = 1.dp
            )
            
            // Action Area
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
                
                if (!disabled) {
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
        }
    }
}
