package com.gokhanaytekinn.sdandroid.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.foundation.text.ClickableText
import com.gokhanaytekinn.sdandroid.ui.theme.PrimaryBlue
import com.gokhanaytekinn.sdandroid.ui.viewmodel.AuthViewModel
import com.gokhanaytekinn.sdandroid.R

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onBackClick: () -> Unit = {},
    onRegisterSuccess: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    val context = LocalContext.current
    val authState by viewModel.authState.collectAsState()
    
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var termsAccepted by remember { mutableStateOf(false) }
    
    var showTermsDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    
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
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Headline
            Text(
                text = "Hesap Oluştur",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Aboneliklerini yönetmeye ve tasarruf etmeye başla.",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                lineHeight = 24.sp
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Full Name Field
            Column {
                Text(
                    text = "Ad Soyad",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { 
                        fullName = it
                        viewModel.clearError() 
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (authState.nameError != null) 76.dp else 56.dp),
                    placeholder = { Text("Örn: Ahmet Yılmaz") },
                    isError = authState.nameError != null,
                    supportingText = if (authState.nameError != null) {
                        { Text(stringResource(authState.nameError!!)) }
                    } else null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                        errorBorderColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(if (authState.nameError != null) 32.dp else 16.dp))
            
            // Email Field
            Column {
                Text(
                    text = "E-posta",
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
                        .height(if (authState.emailError != null) 76.dp else 56.dp),
                    placeholder = { Text("örnek@mail.com") },
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
                    text = "Şifre",
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
                        .height(if (authState.passwordError != null) 76.dp else 56.dp),
                    placeholder = { Text("••••••••") },
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
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
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
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Terms Checkbox
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = termsAccepted,
                    onCheckedChange = { termsAccepted = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = PrimaryBlue
                    ),
                    modifier = Modifier.padding(end = 8.dp)
                )
                val annotatedString = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = PrimaryBlue, fontWeight = FontWeight.Medium)) {
                        pushStringAnnotation(tag = "TERMS", annotation = "terms")
                        append("Kullanım Koşullarını")
                        pop()
                    }
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))) {
                        append(" ve ")
                    }
                    withStyle(style = SpanStyle(color = PrimaryBlue, fontWeight = FontWeight.Medium)) {
                        pushStringAnnotation(tag = "POLICY", annotation = "policy")
                        append("Gizlilik Politikası")
                        pop()
                    }
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))) {
                        append("'nı kabul ediyorum.")
                    }
                }

                ClickableText(
                    text = annotatedString,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    ),
                    modifier = Modifier.weight(1f),
                    onClick = { offset ->
                        annotatedString.getStringAnnotations(tag = "TERMS", start = offset, end = offset)
                            .firstOrNull()?.let {
                                showTermsDialog = true
                            }
                        annotatedString.getStringAnnotations(tag = "POLICY", start = offset, end = offset)
                            .firstOrNull()?.let {
                                showPrivacyDialog = true
                            }
                    }
                )
            }
        }
        
        // Sticky Footer
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // Register Button
                Button(
                    onClick = {
                        if (termsAccepted && email.isNotBlank() && password.isNotBlank() && fullName.isNotBlank()) {
                            viewModel.register(fullName, email, password, onRegisterSuccess)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue
                    ),
                    enabled = !authState.isLoading && termsAccepted && email.isNotBlank() && password.isNotBlank() && fullName.isNotBlank(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp
                    )
                ) {
                    if (authState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Kayıt Ol",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                // Error Message
                if (authState.error != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = authState.error!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Login Link
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Zaten bir hesabın var mı? ",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                    TextButton(
                        onClick = onNavigateToLogin,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "Giriş Yap",
                            color = PrimaryBlue,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Terms of Use Dialog
    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            title = { Text("Kullanım Koşulları", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "1. Kabul\nAbonelik Dedektifi'ni kullanarak bu koşulları kabul etmiş sayılırsınız.\n\n" +
                                "2. Hizmet Kapsamı\nUygulama, aboneliklerinizi takip etmenize yardımcı olan bir araçtır. Finansal tavsiye niteliği taşımaz.\n\n" +
                                "3. Kullanıcı Sorumluluğu\nHesap bilgilerinizin güvenliğinden ve eklediğiniz verilerin doğruluğundan siz sorumlusunuz.\n\n" +
                                "4. Değişiklikler\nUygulama özelliklerinde ve bu koşullarda dilediğimiz zaman değişiklik yapma hakkımız saklıdır.\n\n" +
                                "5. Sonlandırma\nKoşulların ihlali durumunda hizmete erişiminizi sonlandırabiliriz.",
                        lineHeight = 20.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showTermsDialog = false }) {
                    Text("Anladım", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    // Privacy Policy Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("Gizlilik Politikası", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "Son Güncelleme: 20 Şubat 2026\n\n" +
                                "1. Giriş\nVerilerinizin gizliliğine önem veriyoruz.\n\n" +
                                "2. Toplanan Veriler\nAbonelik bilgileri, kullanım tercihleri ve temel cihaz bilgileri işlenmektedir.\n\n" +
                                "3. Veri Kullanımı\nVerileriniz yalnızca hatırlatıcılar göndermek ve uygulama performansını artırmak için kullanılır.\n\n" +
                                "4. Veri Güvenliği\nVerileriniz endüstri standardı şifreleme yöntemleri ile korunmaktadır.\n\n" +
                                "5. İletişim\nSorularınız için: gokhanaytekinn@yandex.com",
                        lineHeight = 20.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("Kapat", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}
