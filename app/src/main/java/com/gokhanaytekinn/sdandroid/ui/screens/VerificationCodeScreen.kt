package com.gokhanaytekinn.sdandroid.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gokhanaytekinn.sdandroid.R
import com.gokhanaytekinn.sdandroid.ui.theme.PrimaryBlue
import com.gokhanaytekinn.sdandroid.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationCodeScreen(
    viewModel: AuthViewModel,
    onBackClick: () -> Unit,
    onCodeVerified: (String) -> Unit
) {
    val authState by viewModel.authState.collectAsState()
    
    // Timer for resend button (15 minutes = 900 seconds)
    var timeLeft by remember { mutableStateOf(900) }
    val isTimerActive = timeLeft > 0
    
    LaunchedEffect(key1 = timeLeft) {
        if (timeLeft > 0) {
            kotlinx.coroutines.delay(1000)
            timeLeft--
        }
    }
    
    val codeDigits = remember { mutableStateListOf("", "", "", "", "", "") }
    val focusRequesters = remember { List(6) { FocusRequester() } }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                
                // Icon
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = PrimaryBlue.copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.MarkEmailRead,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Title & Description
                Text(
                    text = stringResource(R.string.verify_code_title),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = stringResource(R.string.verify_code_desc, authState.resetEmail ?: stringResource(R.string.email)),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    lineHeight = 24.sp
                )
                
                Spacer(modifier = Modifier.height(48.dp))
                
                // 6-Digit Code Input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    codeDigits.forEachIndexed { index, digit ->
                        OutlinedTextField(
                            value = digit,
                            onValueChange = { newValue ->
                                if (newValue.length <= 1) {
                                    codeDigits[index] = newValue
                                    if (newValue.isNotEmpty() && index < 5) {
                                        focusRequesters[index + 1].requestFocus()
                                    }
                                }
                            },
                            modifier = Modifier
                                .width(50.dp)
                                .focusRequester(focusRequesters[index]),
                            textStyle = LocalTextStyle.current.copy(
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.12f)
                            )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(48.dp))
                
                // Verify Button
                val fullCode = codeDigits.joinToString("")
                Button(
                    onClick = {
                        viewModel.verifyCode(fullCode) {
                            onCodeVerified(fullCode)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    enabled = fullCode.length == 6 && !authState.isLoading
                ) {
                    if (authState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            stringResource(R.string.verify_and_continue),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                if (authState.error != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = authState.error!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Resend Link
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isTimerActive) stringResource(R.string.wait_for_code) else stringResource(R.string.code_not_received),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                    
                    if (isTimerActive) {
                        val minutes = timeLeft / 60
                        val seconds = timeLeft % 60
                        Text(
                            text = String.format(" %02d:%02d", minutes, seconds),
                            color = PrimaryBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    } else {
                        TextButton(onClick = { 
                            authState.resetEmail?.let { 
                                viewModel.forgotPassword(it) {
                                    timeLeft = 900 // Reset timer
                                }
                            }
                        }) {
                            Text(
                                text = stringResource(R.string.resend_code),
                                color = PrimaryBlue,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
    
    // Auto focus first field
    LaunchedEffect(Unit) {
        focusRequesters[0].requestFocus()
    }
}
