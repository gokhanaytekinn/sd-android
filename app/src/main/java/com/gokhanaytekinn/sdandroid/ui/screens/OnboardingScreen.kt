package com.gokhanaytekinn.sdandroid.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gokhanaytekinn.sdandroid.R
import com.gokhanaytekinn.sdandroid.ui.components.SDButton

@Composable
fun OnboardingScreen(
    onGetStartedClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))
            
            // Illustration placeholder
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(Color(0xFFE3F2FD)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📱",
                    fontSize = 80.sp
                )
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Title
            Text(
                text = stringResource(R.string.onboarding_title),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Description
            Text(
                text = stringResource(R.string.onboarding_description),
                fontSize = 16.sp,
                color = Color(0xFF757575),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Get Started Button
            SDButton(
                text = stringResource(R.string.get_started),
                onClick = onGetStartedClick,
                backgroundColor = Color(0xFF2196F3),
                textColor = Color.White
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
