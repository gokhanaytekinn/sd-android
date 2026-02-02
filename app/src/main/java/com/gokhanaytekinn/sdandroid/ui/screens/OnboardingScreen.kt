package com.gokhanaytekinn.sdandroid.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gokhanaytekinn.sdandroid.ui.theme.BackgroundDark
import com.gokhanaytekinn.sdandroid.ui.theme.PrimaryBlue

@Composable
fun OnboardingScreen(
    onGetStartedClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            
            // Badge at top
            Surface(
                modifier = Modifier,
                shape = RoundedCornerShape(50),
                color = PrimaryBlue.copy(alpha = 0.1f),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🔍",
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "ABONELİK DEDEKTİFİ",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF9db99d),
                        letterSpacing = 1.5.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Hero Visual with glow effect
            Box(
                modifier = Modifier
                    .size(280.dp),
                contentAlignment = Alignment.Center
            ) {
                // Glow effect
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .blur(60.dp)
                        .background(
                            PrimaryBlue.copy(alpha = 0.2f),
                            CircleShape
                        )
                )
                // Placeholder for illustration
                Text(
                    text = "🔍💳",
                    fontSize = 80.sp,
                    textAlign = TextAlign.Center
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Title with styled text
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 32.sp
                        )
                    ) {
                        append("Aboneliklerini ")
                    }
                    withStyle(
                        style = SpanStyle(
                            color = PrimaryBlue,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 32.sp
                        )
                    ) {
                        append("Keşfet")
                    }
                },
                textAlign = TextAlign.Center,
                lineHeight = 38.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Description
            Text(
                text = "Tekrarlayan ödemelerini bulur ve tasarruf etmeni sağlar.",
                fontSize = 16.sp,
                color = Color(0xFF9CA3AF),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Get Started Button
            Button(
                onClick = onGetStartedClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    contentColor = BackgroundDark
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Başla",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.15.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = null,
                    tint = BackgroundDark
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Pagination Indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(PrimaryBlue, CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(Color(0xFF374151), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(Color(0xFF374151), CircleShape)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
