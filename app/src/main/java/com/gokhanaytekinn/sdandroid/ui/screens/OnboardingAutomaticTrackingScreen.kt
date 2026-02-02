package com.gokhanaytekinn.sdandroid.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gokhanaytekinn.sdandroid.ui.theme.*

// Content-only version for use in pager (no progress indicators, no button)
@Composable
fun OnboardingAutomaticTrackingContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Illustration Area
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            // Decorative background
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .blur(80.dp)
                    .background(PrimaryBlue.copy(alpha = 0.05f), CircleShape)
            )
            
            // Card with scanning effect
            Surface(
                modifier = Modifier
                    .width(256.dp)
                    .height(200.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 12.dp,
                tonalElevation = 1.dp
            ) {
                Box {
                    // Scanning line
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(PrimaryBlue.copy(alpha = 0.4f))
                            .align(Alignment.TopCenter)
                    )
                    
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Notification icon row
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(PrimaryBlue.copy(alpha = 0.1f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.NotificationsActive,
                                    contentDescription = null,
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            
                            Column {
                                Box(
                                    modifier = Modifier
                                        .width(80.dp)
                                        .height(8.dp)
                                        .background(Color(0xFFE5E7EB), RoundedCornerShape(4.dp))
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .width(48.dp)
                                        .height(8.dp)
                                        .background(Color(0xFFF3F4F6), RoundedCornerShape(4.dp))
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Items
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF6F7F8), RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(16.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(8.dp)
                                    .background(Color(0xFFE5E7EB), RoundedCornerShape(4.dp))
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF6F7F8), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                                .alpha(0.6f),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Sms,
                                contentDescription = null,
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier.size(16.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(8.dp)
                                    .background(Color(0xFFE5E7EB), RoundedCornerShape(4.dp))
                            )
                        }
                    }
                    
                    // Shield badge
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .offset(x = (-8).dp, y = (-8).dp)
                            .background(PrimaryBlue, CircleShape)
                            .align(Alignment.BottomEnd),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.VerifiedUser,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Content
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Ödemelerini Otomatik Yakalar",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                lineHeight = 34.sp
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "SMS ve bildirimlerini tarayarak aboneliklerini saniyeler içinde listeler.",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Security badge
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = null,
                tint = Color(0xFF617689),
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "UÇTAN UCA ŞİFRELİ VE GÜVENLİ",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF617689),
                letterSpacing = 1.sp
            )
        }
    }
}

// Original screen with button for standalone use
@Composable
fun OnboardingAutomaticTrackingScreen(
    onContinueClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Progress Indicators
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(
                modifier = Modifier
                    .width(48.dp)
                    .height(6.dp)
                    .background(Color(0xFFdbe1e6), RoundedCornerShape(50))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Spacer(
                modifier = Modifier
                    .width(48.dp)
                    .height(6.dp)
                    .background(PrimaryBlue, RoundedCornerShape(50))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Spacer(
                modifier = Modifier
                    .width(48.dp)
                    .height(6.dp)
                    .background(Color(0xFFdbe1e6), RoundedCornerShape(50))
            )
        }
        
        Box(modifier = Modifier.weight(1f)) {
            OnboardingAutomaticTrackingContent()
        }
        
        // CTA Button
        Button(
            onClick = onContinueClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryBlue
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 8.dp
            )
        ) {
            Text(
                text = "Devam Et",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}
