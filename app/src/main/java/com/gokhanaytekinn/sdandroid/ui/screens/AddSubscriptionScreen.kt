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
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
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
    val nextBillingDate by viewModel.nextBillingDate.collectAsState()
    val isReminderEnabled by viewModel.isReminderEnabled.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isSuccess by viewModel.isSuccess.collectAsState()
    val isEditMode by viewModel.isEditMode.collectAsState()
    
    // Load subscription if editing
    LaunchedEffect(subscriptionId) {
        if (subscriptionId != null) {
            viewModel.loadSubscription(subscriptionId)
        } else {
            viewModel.resetState()
        }
    }
    
    // Date Picker Context
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
            val formattedDate = "$selectedYear-${(selectedMonth + 1).toString().padStart(2, '0')}-${selectedDay.toString().padStart(2, '0')}"
            viewModel.updateNextBillingDate(formattedDate)
        }, year, month, day
    )

    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            onBackClick()
            viewModel.resetState()
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
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                
                Text(
                    text = if (isEditMode) "Aboneliği Düzenle" else "Abonelik Ekle",
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
                Text(
                    text = "Hizmet Adı",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                OutlinedTextField(
                    value = name,
                    onValueChange = viewModel::updateName,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    placeholder = { Text("örn. Netflix", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                        focusedContainerColor = Color.White.copy(alpha = 0.05f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.05f)
                    ),
                    trailingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                    },
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Quick Suggestions
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val suggestions = listOf(
                        Triple("Netflix", NetflixRed, "N"),
                        Triple("Spotify", SpotifyGreen, "S"),
                        Triple("YouTube", Color(0xFFFF0000), "Y"),
                        Triple("Amazon", Color(0xFF00A8E1), "A")
                    )
                    
                    items(suggestions) { (appName, color, initial) ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { viewModel.updateName(appName) }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(color.copy(alpha = 0.2f))
                                    .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = initial,
                                    color = color,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = appName,
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
                            text = "Tutar",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = amount,
                            onValueChange = viewModel::updateAmount,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            placeholder = { Text("0.00", color = Color.Gray) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                                focusedContainerColor = Color.White.copy(alpha = 0.05f),
                                unfocusedContainerColor = Color.White.copy(alpha = 0.05f)
                            ),
                            singleLine = true
                        )
                    }
                    
                    // Currency
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Döviz",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        // Simple Dropdown implementation (or clickable text for now)
                        var expanded by remember { mutableStateOf(false) }
                        Box {
                            OutlinedTextField(
                                value = currency,
                                onValueChange = {},
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                readOnly = true,
                                trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryBlue,
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
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
                                listOf("TRY", "USD", "EUR", "GBP").forEach { curr ->
                                    DropdownMenuItem(
                                        text = { Text(curr) },
                                        onClick = {
                                            viewModel.updateCurrency(curr)
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Billing Cycle
                Text(
                    text = "Periyot",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                        .padding(4.dp)
                ) {
                    val cycles = listOf(BillingCycle.MONTHLY to "Aylık", BillingCycle.YEARLY to "Yıllık")
                    cycles.forEach { (cycle, label) ->
                        val isSelected = billingCycle == cycle
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(8.dp))
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
                
                // Next Payment Date
                Text(
                    text = "Sonraki Ödeme Tarihi",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Box {
                    OutlinedTextField(
                        value = nextBillingDate.ifEmpty { stringResource(R.string.date_placeholder) }, // Or "YYYY-MM-DD"
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth().clickable { datePickerDialog.show() },
                        shape = RoundedCornerShape(12.dp),
                        readOnly = true,
                        enabled = false, // To prevent keyboard but allow click
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onBackground,
                            disabledBorderColor = Color.White.copy(alpha = 0.1f),
                            disabledContainerColor = Color.White.copy(alpha = 0.05f),
                            disabledTrailingIconColor = Color.Gray
                        ),
                        trailingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                        singleLine = true
                    )
                    // Overlay clickable for datepicker needed because enabled=false blocks click on TF
                    Box(modifier = Modifier.matchParentSize().clickable { datePickerDialog.show() })
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
                            text = "Hatırlatıcı",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Ödemeden 1 gün önce bildir",
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
                
                if (error != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = error ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
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
                    .shadow(8.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    disabledContainerColor = PrimaryBlue.copy(alpha = 0.5f)
                )
            ) {
                 if (isLoading) {
                     CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                 } else {
                     Text(
                        text = if (isEditMode) "Güncelle" else "Kaydet",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                 }
            }
        }
    }
}
