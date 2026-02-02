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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gokhanaytekinn.sdandroid.ui.theme.*
import com.gokhanaytekinn.sdandroid.ui.viewmodel.SearchViewModel
import com.gokhanaytekinn.sdandroid.data.preferences.CurrencyPreferences
import com.gokhanaytekinn.sdandroid.util.CurrencyFormatter

@Composable
fun SearchScreen(
    onBackClick: () -> Unit = {},
    onFilterClick: () -> Unit = {},
    onResultClick: (String) -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val application = context.applicationContext as android.app.Application
    val viewModel: SearchViewModel = remember { SearchViewModel(application) }
    val currencyPreferences = remember { CurrencyPreferences(context) }
    
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedCurrency by currencyPreferences.selectedCurrency.collectAsState(initial = "TRY")
    
    var selectedTab by remember { mutableStateOf(0) } // 0=Subscriptions, 1=Transactions
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Text(
                    text = "Ara",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                
                IconButton(onClick = onFilterClick) {
                    Icon(
                        imageVector = Icons.Filled.Tune,
                        contentDescription = "Filter",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            
            // Search Bar
            
        }
        
        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChange(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            placeholder = {
                Text("Abonelik veya işlem ara")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = Color(0xFF94A3B8)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.clearSearch() }) {
                        Icon(
                            imageVector = Icons.Filled.Cancel,
                            contentDescription = "Clear",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                cursorColor = PrimaryBlue,
                focusedPlaceholderColor = Color(0xFF94A3B8),
                unfocusedPlaceholderColor = Color(0xFF94A3B8)
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )
        
        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color(0xFF0d151b),
            contentColor = PrimaryBlue,
            divider = {
                Divider(color = Color(0xFF334155))
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                Text(
                    text = "Subscriptions",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedTab == 0) Color.White else Color(0xFF94A3B8)
                )
            }
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                Text(
                    text = "Transactions",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedTab == 1) Color.White else Color(0xFF94A3B8)
                )
            }
        }
        
        // Content
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // Recent Searches - Only show when search query is empty
            if (searchQuery.isEmpty()) {
            item {
                Text(
                    text = "SON ARAMALAR",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
                )
            }
            
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(recentSearches) { search ->
                        Surface(
                            onClick = { viewModel.onRecentSearchClick(search) },
                            shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Text(
                                text = search,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
            }
            
            // Results Header - Only show when there are results
            if (searchResults.isNotEmpty()) {
            item {
                Text(
                    text = "SONUÇLAR (${searchResults.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(horizontal = 16.dp).padding(top = 24.dp, bottom = 8.dp)
                )
            }
            
            // Subscription Results
            items(searchResults) { subscription ->
                Surface(
                    onClick = { onResultClick(subscription.id) },
                    color = Color.Transparent
                ) {
                    SubscriptionListItemDetailed(
                        subscription = subscription,
                        currency = selectedCurrency
                    )
                }
            }
            }
            
            // No Results Message
            if (searchQuery.isNotEmpty() && searchResults.isEmpty() && !isLoading) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(PrimaryBlue.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.SearchOff,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Sonuç bulunamadı",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        
                        Text(
                            text = "Başka bir arama terimi deneyin",
                            fontSize = 14.sp,
                            color = Color(0xFF94A3B8),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

        }
    }
}
