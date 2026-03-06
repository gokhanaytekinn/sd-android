package com.gokhanaytekinn.sdandroid.ui.screens

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gokhanaytekinn.sdandroid.R
import com.gokhanaytekinn.sdandroid.data.model.BillingCycle
import com.gokhanaytekinn.sdandroid.ui.theme.*
import com.gokhanaytekinn.sdandroid.ui.viewmodel.AddSubscriptionViewModel
import com.gokhanaytekinn.sdandroid.util.CurrencyFormatter
import com.gokhanaytekinn.sdandroid.util.CurrencyVisualTransformation
import com.gokhanaytekinn.sdandroid.ui.components.ErrorDialog
import com.gokhanaytekinn.sdandroid.ui.components.InterstitialAdManager
import kotlinx.coroutines.flow.collectLatest
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AddSubscriptionScreen(
    subscriptionId: String? = null,
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val application = context.applicationContext as android.app.Application
    val viewModel: AddSubscriptionViewModel = viewModel { AddSubscriptionViewModel(application) }
    
    val name by viewModel.name.collectAsState()
    val amount by viewModel.amount.collectAsState()
    val currency by viewModel.currency.collectAsState()
    val billingCycle by viewModel.billingCycle.collectAsState()
    val isReminderEnabled by viewModel.isReminderEnabled.collectAsState()
    val jointEmails by viewModel.jointEmails.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isSuccess by viewModel.isSuccess.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val isEditMode by viewModel.isEditMode.collectAsState()
    val participants by viewModel.participants.collectAsState()
    val selectedCategory by viewModel.category.collectAsState()
    
    val nameError by viewModel.nameError.collectAsState()
    val amountError by viewModel.amountError.collectAsState()
    val currencyError by viewModel.currencyError.collectAsState()
    
    val adManager = remember { InterstitialAdManager(context) }
    
    val focusRequesterName = remember { FocusRequester() }
    val focusRequesterAmount = remember { FocusRequester() }
    val bringIntoViewRequesterName = remember { BringIntoViewRequester() }
    val bringIntoViewRequesterAmount = remember { BringIntoViewRequester() }
    
    LaunchedEffect(Unit) {
        adManager.loadAd()
    }
    
    LaunchedEffect(Unit) {
        viewModel.focusEvent.collectLatest { field ->
            when (field) {
                "name" -> {
                    bringIntoViewRequesterName.bringIntoView()
                    focusRequesterName.requestFocus()
                }
                "amount" -> {
                    bringIntoViewRequesterAmount.bringIntoView()
                    focusRequesterAmount.requestFocus()
                }
            }
        }
    }

    LaunchedEffect(subscriptionId) {
        if (subscriptionId != null) {
            viewModel.loadSubscription(subscriptionId)
        } else {
            viewModel.resetState()
            val calendar = java.util.Calendar.getInstance()
            viewModel.updateBillingDay(calendar.get(java.util.Calendar.DAY_OF_MONTH))
            viewModel.updateBillingMonth(calendar.get(java.util.Calendar.MONTH) + 1)
        }
    }
    
    // Day and Month Selection State
    val days = (1..31).toList()
    val months = listOf(
        stringResource(R.string.month_1),
        stringResource(R.string.month_2),
        stringResource(R.string.month_3),
        stringResource(R.string.month_4),
        stringResource(R.string.month_5),
        stringResource(R.string.month_6),
        stringResource(R.string.month_7),
        stringResource(R.string.month_8),
        stringResource(R.string.month_9),
        stringResource(R.string.month_10),
        stringResource(R.string.month_11),
        stringResource(R.string.month_12)
    )
    
    val billingDay by viewModel.billingDay.collectAsState()
    val billingMonth by viewModel.billingMonth.collectAsState()

    var pendingNavigateBack by remember { mutableStateOf(false) }

    LaunchedEffect(isSuccess, successMessage) {
        if (successMessage != null) {
            android.widget.Toast.makeText(context, successMessage, android.widget.Toast.LENGTH_LONG).show()
            pendingNavigateBack = true
            viewModel.clearSuccessMessage()
        } else if (isSuccess) {
            pendingNavigateBack = true
        }
    }

    LaunchedEffect(Unit) {
        viewModel.showInterstitialAd.collectLatest { shouldShow ->
            if (shouldShow) {
                // Reklamı göster, kapanınca geri dön
                adManager.showAd(context as android.app.Activity) {
                    if (pendingNavigateBack) {
                        onBackClick()
                        viewModel.resetState()
                        pendingNavigateBack = false
                    }
                }
            }
        }
    }

    // Eğer başarılı olduysa ve reklam tetiklenmediyse (hemen dön)
    // LaunchedEffect flow emit gecikmesi nedeniyle küçük bir delay ile kontrol etmek faydalı olabilir,
    // ancak viewModel tarafında checkInterstitialAdCondition() reklamEmit edilecekse hemen ediyor.
    // Bu nedenle pendingNavigateBack duruma göre işlenecektir. Ek olarak 500ms sonra reklam açılmadıysa dönülür:
    LaunchedEffect(pendingNavigateBack) {
        if (pendingNavigateBack) {
            kotlinx.coroutines.delay(500)
            if (pendingNavigateBack) {
                onBackClick()
                viewModel.resetState()
                pendingNavigateBack = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp) // Space for bottom button
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.05f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                
                Text(
                    text = if (isEditMode) stringResource(R.string.edit_subscription) else stringResource(R.string.add_subscription),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                
                Spacer(modifier = Modifier.size(40.dp)) // Balance the header
            }
            
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                // Service Name Input
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.service_name),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = name,
                        onValueChange = viewModel::updateName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .bringIntoViewRequester(bringIntoViewRequesterName)
                            .focusRequester(focusRequesterName),
                        shape = RoundedCornerShape(12.dp),
                        placeholder = { Text(stringResource(R.string.service_name_placeholder), color = Color.Gray) },
                        isError = nameError != null,
                        supportingText = if (nameError != null) {
                            { Text(stringResource(nameError!!)) }
                        } else null,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                            errorBorderColor = MaterialTheme.colorScheme.error,
                            focusedContainerColor = Color.White.copy(alpha = 0.05f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                            cursorColor = MaterialTheme.colorScheme.onBackground,
                            focusedTextColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                        ),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Categories (Scrollable Chips)
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.category),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    val getCategoryIcon: (String?) -> androidx.compose.ui.graphics.vector.ImageVector = { cat ->
                        when (cat) {
                            "category_streaming" -> Icons.Default.PlayArrow
                            "category_gaming" -> Icons.Default.Star
                            "category_software" -> Icons.Default.Build
                            "category_other" -> Icons.Default.List
                            else -> Icons.Default.List
                        }
                    }
                    
                    val categories = listOf(
                        "category_streaming", "category_gaming", "category_software", "category_shopping", "category_other"
                    )
                    
                    // Kategori seçimi kullanıcının kendisine bırakılmalı


                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categories) { catKey ->
                            val resId = context.resources.getIdentifier(catKey, "string", context.packageName)
                            val catName = if (resId != 0) stringResource(resId) else catKey
                            val isSelected = selectedCategory == catKey
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable { viewModel.updateCategory(catKey) }
                                    .background(if (isSelected) PrimaryBlue else Color.White.copy(alpha = 0.05f))
                                    .border(
                                        1.dp,
                                        if (isSelected) PrimaryBlue else Color.White.copy(alpha = 0.1f),
                                        RoundedCornerShape(20.dp)
                                    )
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = getCategoryIcon(catKey),
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else PrimaryBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = catName,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Quick Suggestions
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    data class QuickShortcut(val name: String, val color: Color, val initial: String, val iconRes: Int?, val catKey: String)
                    val suggestions = listOf(
                        QuickShortcut("Google", Color(0xFF4285F4), "G", R.drawable.ic_google, "category_software"),
                        QuickShortcut("Cursor", Color(0xFF1A1A1A), "C", R.drawable.ic_cursor, "category_software"),
                        QuickShortcut("Claude", Color(0xFFD97757), "C", R.drawable.ic_claude, "category_software"),
                        QuickShortcut("Netflix", NetflixRed, "N", R.drawable.ic_netflix, "category_streaming"),
                        QuickShortcut("Spotify", SpotifyGreen, "S", R.drawable.ic_spotify, "category_streaming"),
                        QuickShortcut("YouTube", Color(0xFFFF0000), "Y", R.drawable.ic_youtube, "category_streaming"),
                        QuickShortcut("Amazon", Color(0xFF00A8E1), "A", R.drawable.ic_amazon, "category_shopping"),
                        QuickShortcut("HBO Max", Color(0xFF5A2E81), "H", R.drawable.ic_hbomax, "category_streaming")
                    )
                    
                    items(suggestions) { shortcut ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { 
                                viewModel.updateName(shortcut.name)
                                viewModel.updateCategory(shortcut.catKey)
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(shortcut.color.copy(alpha = 0.2f))
                                    .border(1.dp, shortcut.color.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (shortcut.iconRes != null) {
                                     androidx.compose.foundation.Image(
                                         painter = androidx.compose.ui.res.painterResource(id = shortcut.iconRes),
                                         contentDescription = shortcut.name,
                                         modifier = Modifier.size(24.dp)
                                     )
                                } else {
                                    Text(
                                        text = shortcut.initial,
                                        color = shortcut.color,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = shortcut.name,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Amount and Currency
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Amount
                    Column(modifier = Modifier.weight(2f)) {
                        Text(
                            text = stringResource(R.string.amount),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = amount,
                            onValueChange = viewModel::updateAmount,
                            modifier = Modifier
                                .fillMaxWidth()
                                .bringIntoViewRequester(bringIntoViewRequesterAmount)
                                .focusRequester(focusRequesterAmount),
                            shape = RoundedCornerShape(12.dp),
                            placeholder = { Text("0,00", color = Color.Gray) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            visualTransformation = CurrencyVisualTransformation(CurrencyFormatter.getCurrencySymbol(currency)),
                            isError = amountError != null,
                            supportingText = if (amountError != null) {
                                { Text(stringResource(amountError!!)) }
                            } else null,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                                errorBorderColor = MaterialTheme.colorScheme.error,
                                focusedContainerColor = Color.White.copy(alpha = 0.05f),
                                unfocusedContainerColor = Color.White.copy(alpha = 0.05f)
                            ),
                            singleLine = true
                        )
                    }
                    
                    // Currency
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.currency),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        // Simple Dropdown implementation (or clickable text for now)
                        var expanded by remember { mutableStateOf(false) }
                        Box {
                            OutlinedTextField(
                                value = CurrencyFormatter.getCurrencySymbol(currency),
                                onValueChange = {},
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                readOnly = true,
                                isError = currencyError != null,
                                trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryBlue,
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                                    errorBorderColor = MaterialTheme.colorScheme.error,
                                    focusedContainerColor = Color.White.copy(alpha = 0.05f),
                                    unfocusedContainerColor = Color.White.copy(alpha = 0.05f)
                                )
                            )
                            // Invisible clickable box
                            Box(modifier = Modifier.matchParentSize().clickable { expanded = true })
                            
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                listOf(1, 2, 3, 4, 5, 6, 7).forEach { currCode ->
                                    DropdownMenuItem(
                                        text = { Text(CurrencyFormatter.getCurrencySymbol(currCode)) },
                                        onClick = {
                                            viewModel.updateCurrency(currCode)
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                        if (currencyError != null) {
                            Text(
                                text = stringResource(currencyError!!),
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Billing Cycle
                Text(
                    text = stringResource(R.string.period),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(Color.Transparent, RoundedCornerShape(12.dp))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 1.0f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(4.dp)
                ) {
                    val cycles = listOf(BillingCycle.MONTHLY to stringResource(R.string.monthly), BillingCycle.YEARLY to stringResource(R.string.yearly))
                    cycles.forEach { (cycle, label) ->
                        val isSelected = billingCycle == cycle
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) PrimaryBlue else Color.Transparent)
                                .clickable { viewModel.updateBillingCycle(cycle) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color.Gray
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Next Payment Date (Simplified to Day/Month Selection)
                val nextPaymentLabel = if (billingCycle == BillingCycle.YEARLY) {
                    stringResource(R.string.payment_recurrence_day_month)
                } else {
                    stringResource(R.string.payment_recurrence_day)
                }
                Text(
                    text = nextPaymentLabel,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Day Selector (1-31)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(days) { day ->
                        val isSelected = billingDay == day
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) PrimaryBlue else Color.White.copy(alpha = 0.05f))
                                .border(1.dp, if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.outline.copy(alpha = 1.0f), CircleShape)
                                .clickable { viewModel.updateBillingDay(day) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.toString(),
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
                
                if (billingCycle == BillingCycle.YEARLY) {
                    Spacer(modifier = Modifier.height(16.dp))
                    // Month Selector
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items((1..12).toList()) { month ->
                            val isSelected = billingMonth == month
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) PrimaryBlue else Color.White.copy(alpha = 0.05f))
                                    .border(1.dp, if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.outline.copy(alpha = 1.0f), RoundedCornerShape(12.dp))
                                    .clickable { viewModel.updateBillingMonth(month) }
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = months[month - 1],
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Reminder Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.reminder_desc),
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    
                    Switch(
                        checked = isReminderEnabled,
                        onCheckedChange = viewModel::updateIsReminderEnabled,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = PrimaryBlue,
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color.White.copy(alpha = 0.1f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Joint Subscriptions (New Section)
                Text(
                    text = stringResource(R.string.joint_users),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                var emailInput by remember { mutableStateOf("") }

                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    placeholder = { Text(stringResource(R.string.add_email_placeholder), color = Color.Gray) },
                    trailingIcon = {
                        IconButton(onClick = {
                            if (emailInput.isNotBlank()) {
                                viewModel.addJointEmail(emailInput)
                                emailInput = ""
                            }
                        }) {
                            Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add))
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                        focusedContainerColor = Color.White.copy(alpha = 0.05f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.05f)
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(8.dp))

                val combinedDisplayList = remember(jointEmails, participants) {
                    val list = mutableListOf<Triple<String, String?, String?>>() // email, status, name
                    participants?.forEach { list.add(Triple(it.email, it.status, it.name)) }
                    jointEmails.forEach { email ->
                        if (participants?.none { it.email == email } != false) {
                            list.add(Triple(email, null, null))
                        }
                    }
                    list
                }

                combinedDisplayList.forEach { (email, status, name) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = name ?: email,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 14.sp
                            )
                            
                            if (status != null) {
                                val (icon, tint) = when (status) {
                                    "ACCEPTED" -> Icons.Default.CheckCircle to Color(0xFF4CAF50)
                                    "REJECTED" -> Icons.Default.Cancel to MaterialTheme.colorScheme.error
                                    else -> Icons.Default.Pending to Color.Gray
                                }
                                Icon(icon, contentDescription = status, tint = tint, modifier = Modifier.size(16.dp))
                            }
                        }
                        
                        IconButton(onClick = { viewModel.removeJointEmail(email) }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = stringResource(R.string.remove), tint = Color.Gray, modifier = Modifier.size(16.dp))
                        }
                    }
                }
                
                if (error != null) {
                    ErrorDialog(
                        errorMessage = error!!,
                        onDismiss = { viewModel.clearError() }
                    )
                }
            }
        }
        
        // Save Button (Fixed at bottom)
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.background.copy(alpha = 0.9f)
        ) {
            Button(
                onClick = viewModel::saveSubscription,
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .height(56.dp)
                    .shadow(8.dp, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    disabledContainerColor = PrimaryBlue.copy(alpha = 0.5f)
                )
            ) {
                 if (isLoading) {
                     CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                 } else {
                     Text(
                        text = if (isEditMode) stringResource(R.string.update) else stringResource(R.string.save),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                 }
            }
        }
    }
}
