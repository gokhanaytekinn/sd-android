package com.gokhanaytekinn.sdandroid.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gokhanaytekinn.sdandroid.R
import com.gokhanaytekinn.sdandroid.ui.theme.PrimaryBlue
import com.gokhanaytekinn.sdandroid.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    viewModel: AuthViewModel,
    onBackClick: () -> Unit,
    onCodeSent: () -> Unit
) {
    val authState by viewModel.authState.collectAsState()
    
    var email by remember { mutableStateOf("") }
    
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
                            imageVector = Icons.Default.LockReset,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Title & Description
                Text(
                    text = stringResource(R.string.forgot_password),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = stringResource(R.string.forgot_password_desc),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    lineHeight = 24.sp
                )
                
                Spacer(modifier = Modifier.height(40.dp))
                
                // Email Input
                Text(
                    text = stringResource(R.string.email_address_label),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )
                
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.email_placeholder), color = Color(0xFF94A3B8)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Mail,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8)
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.12f)
                    ),
                    singleLine = true
                )
                
                if (authState.emailError != null) {
                    Text(
                        text = stringResource(authState.emailError!!),
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Send Button
                Button(
                    onClick = {
                        viewModel.forgotPassword(email) {
                            onCodeSent()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    enabled = !authState.isLoading
                ) {
                    if (authState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                stringResource(R.string.send_code),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
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
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Back to Login Link
                TextButton(
                    onClick = onBackClick,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = stringResource(R.string.back_to_login),
                        color = PrimaryBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
