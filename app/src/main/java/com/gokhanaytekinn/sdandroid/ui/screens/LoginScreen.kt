package com.gokhanaytekinn.sdandroid.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.gokhanaytekinn.sdandroid.ui.theme.PrimaryBlue
import com.gokhanaytekinn.sdandroid.ui.components.ErrorDialog
import com.gokhanaytekinn.sdandroid.ui.viewmodel.AuthViewModel
import com.gokhanaytekinn.sdandroid.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
    onForgotPassword: () -> Unit = {}
) {
    val context = LocalContext.current
    val authState by viewModel.authState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    var googleSignInError by remember { mutableStateOf<String?>(null) }

    // Google Sign-In akışı
    fun launchGoogleSignIn() {
        val googleSignInOption = GetSignInWithGoogleOption.Builder(
            serverClientId = "511347263387-cv901jhvhfoap5ibn4rndu7irlgspkn7.apps.googleusercontent.com"
        ).build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleSignInOption)
            .build()
        coroutineScope.launch {
            try {
                val result = credentialManager.getCredential(context = context, request = request)
                val credential = result.credential
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken
                viewModel.signInWithGoogle(idToken, onLoginSuccess)
            } catch (e: GetCredentialException) {
                googleSignInError = e.message ?: context.getString(R.string.google_sign_in_failed, e.type)
            } catch (e: Exception) {
                android.util.Log.e("GoogleSignIn", "Exception: ${e.message}")
                googleSignInError = e.message ?: context.getString(R.string.unknown_error)
            }
        }
    }
    
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    // Show error if exists
    LaunchedEffect(authState.error) {
        authState.error?.let {
            // Error will be shown in Snackbar
        }
    }
    
    val focusRequesterEmail = remember { FocusRequester() }
    val focusRequesterPassword = remember { FocusRequester() }
    val bringIntoViewRequesterEmail = remember { BringIntoViewRequester() }
    val bringIntoViewRequesterPassword = remember { BringIntoViewRequester() }

    LaunchedEffect(Unit) {
        viewModel.focusEvent.collectLatest { field ->
            when (field) {
                "email" -> {
                    bringIntoViewRequesterEmail.bringIntoView()
                    focusRequesterEmail.requestFocus()
                }
                "password" -> {
                    bringIntoViewRequesterPassword.bringIntoView()
                    focusRequesterPassword.requestFocus()
                }
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            Column {
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Headline
                Text(
                    text = stringResource(R.string.login),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = stringResource(R.string.login_desc),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    lineHeight = 24.sp
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Email Field
                Column {
                    Text(
                        text = stringResource(R.string.email),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { 
                            email = it
                            viewModel.clearError()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .bringIntoViewRequester(bringIntoViewRequesterEmail)
                            .focusRequester(focusRequesterEmail)
                            .height(if (authState.emailError != null) 76.dp else 56.dp),
                        placeholder = { Text(stringResource(R.string.email_placeholder)) },
                        isError = authState.emailError != null,
                        supportingText = if (authState.emailError != null) {
                            { Text(stringResource(authState.emailError!!)) }
                        } else null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                            errorBorderColor = MaterialTheme.colorScheme.error
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(if (authState.emailError != null) 32.dp else 16.dp))
                
                // Password Field
                Column {
                    Text(
                        text = stringResource(R.string.password),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { 
                            password = it
                            viewModel.clearError()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .bringIntoViewRequester(bringIntoViewRequesterPassword)
                            .focusRequester(focusRequesterPassword)
                            .height(if (authState.passwordError != null) 76.dp else 56.dp),
                        placeholder = { Text(stringResource(R.string.password_placeholder)) },
                        isError = authState.passwordError != null,
                        supportingText = if (authState.passwordError != null) {
                            { Text(stringResource(authState.passwordError!!)) }
                        } else null,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                    contentDescription = if (passwordVisible) stringResource(R.string.hide_password) else stringResource(R.string.show_password),
                                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                            errorBorderColor = MaterialTheme.colorScheme.error
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    // Forgot Password
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = onForgotPassword,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.forgot_password),
                                color = PrimaryBlue,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Login Button
                Button(
                    onClick = {
                        if (email.isNotBlank() && password.isNotBlank()) {
                            viewModel.login(email, password, onLoginSuccess)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp
                    ),
                    enabled = !authState.isLoading && email.isNotBlank() && password.isNotBlank()
                ) {
                    if (authState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.login),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                // Error Message Dialog
                if (authState.error != null) {
                    ErrorDialog(
                        errorMessage = authState.error!!,
                        onDismiss = { viewModel.clearGeneralError() }
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Divider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f))
                    Text(
                        text = stringResource(R.string.or),
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        fontSize = 14.sp
                    )
                    Divider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f))
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Social Login Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedButton(
                        onClick = { launchGoogleSignIn() },
                        modifier = Modifier
                            .size(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(0.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 1.dp,
                            brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
                        )
                    ) {
                        Icon(
                            painter = androidx.compose.ui.res.painterResource(id = com.gokhanaytekinn.sdandroid.R.drawable.ic_google),
                            contentDescription = stringResource(R.string.language_english), // English/Google placeholder
                            modifier = Modifier.size(24.dp),
                            tint = Color.Unspecified
                        )
                    }
                }

                // Google Sign-In hata mesajı
                if (googleSignInError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = googleSignInError!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

            }
            
            // Sign Up Link
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.no_account_prompt),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
                TextButton(
                    onClick = onNavigateToRegister,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = stringResource(R.string.register),
                        color = PrimaryBlue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
