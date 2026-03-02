package com.gokhanaytekinn.sdandroid.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gokhanaytekinn.sdandroid.ui.theme.*

data class Transaction(
    val id: String,
    val name: String,
    val category: String,
    val amount: String,
    val isIncome: Boolean = false,
    val isRecurring: Boolean = false,
    val isCancelled: Boolean = false,
    val icon: String = "💳",
    val iconColor: Color = PrimaryBlue,
    val isJoint: Boolean = false
)

@Composable
fun TransactionHistoryScreen(
    onBackClick: () -> Unit = {},
    onAddTransaction: () -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme
    var selectedFilter by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    
    val transactions = remember {
        mapOf(
            "Today" to listOf(
                Transaction(
                    "1",
                    "Spotify Premium",
                    "Entertainment • Monthly",
                    "-$16.99",
                    isRecurring = true,
                    icon = "🎵",
                    iconColor = Color(0xFF1DB954)
                ),
                Transaction(
                    "2",
                    "Whole Foods Market",
                    "Groceries",
                    "-$84.20",
                    icon = "🛍️",
                    iconColor = Color(0xFFFF9500)
                )
            ),
            "Yesterday" to listOf(
                Transaction(
                    "3",
                    "Freelance Payment",
                    "Income • Wire Transfer",
                    "+$1,250.00",
                    isIncome = true,
                    icon = "📈",
                    iconColor = PrimaryBlue
                ),
                Transaction(
                    "4",
                    "Netflix",
                    "Entertainment • Monthly",
                    "-$19.99",
                    isRecurring = true,
                    icon = "N",
                    iconColor = Color(0xFFE50914)
                )
            ),
            "Dec 12" to listOf(
                Transaction(
                    "5",
                    "City Water",
                    "Utilities",
                    "-$45.00",
                    icon = "💧",
                    iconColor = Color(0xFF2196F3)
                ),
                Transaction(
                    "6",
                    "PlayStation Plus",
                    "Subscription Cancelled",
                    "-$0.00",
                    isCancelled = true,
                    icon = "🎮",
                    iconColor = Color(0xFF9C27B0)
                )
            )
        )
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
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
                    text = "Transaction History",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onBackground,
                    modifier = Modifier.weight(1f).padding(end = 40.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
            
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = {
                    Text(
                        text = "Search for Netflix, Spotify...",
                        color = colorScheme.onSurfaceVariant
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = colorScheme.onSurfaceVariant
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colorScheme.surface,
                    unfocusedContainerColor = colorScheme.surface,
                    focusedIndicatorColor = colorScheme.primary,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = colorScheme.onSurface,
                    unfocusedTextColor = colorScheme.onSurface
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Filter Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(listOf("All", "Subscriptions", "Income", "Recurring")) { filter ->
                    val index = listOf("All", "Subscriptions", "Income", "Recurring").indexOf(filter)
                    AppFilterChip(
                        text = filter,
                        selected = selectedFilter == index,
                        onClick = { selectedFilter = index }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Transaction List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                transactions.forEach { (group, groupTransactions) ->
                    item {
                        Text(
                            text = group.uppercase(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }
                    
                    items(groupTransactions) { transaction ->
                        TransactionItem(transaction)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
        
        // Floating Action Button
        FloatingActionButton(
            onClick = onAddTransaction,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = colorScheme.primary,
            contentColor = colorScheme.onPrimary
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add Transaction",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    com.gokhanaytekinn.sdandroid.ui.components.SubscriptionCard(
        name = transaction.name,
        category = transaction.category,
        cost = transaction.amount.replace("$", "").replace("-", "").replace("+", "").replace(",", "").toDoubleOrNull() ?: 0.0,
        currency = if (transaction.amount.contains("$")) "USD" else "TRY",
        icon = transaction.icon,
        isJoint = transaction.isJoint,
        onClick = {},
        bottomContent = null
    )
}
