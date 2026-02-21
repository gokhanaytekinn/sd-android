package com.gokhanaytekinn.sdandroid.ui.screens

import com.gokhanaytekinn.sdandroid.R
import androidx.compose.ui.res.stringResource

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PrivacyPolicyScreen(
    onBackClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Text(
                text = stringResource(R.string.privacy_policy_title),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Spacer(modifier = Modifier.size(40.dp)) // Balance header
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = stringResource(R.string.privacy_last_updated),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            PolicySection(
                title = stringResource(R.string.privacy_title_1),
                content = stringResource(R.string.privacy_content_1)
            )
            
            PolicySection(
                title = stringResource(R.string.privacy_title_2),
                content = stringResource(R.string.privacy_content_2)
            )
            
            PolicySection(
                title = stringResource(R.string.privacy_title_3),
                content = stringResource(R.string.privacy_content_3)
            )
            
            PolicySection(
                title = stringResource(R.string.privacy_title_4),
                content = stringResource(R.string.privacy_content_4)
            )
            
            PolicySection(
                title = stringResource(R.string.privacy_title_5),
                content = stringResource(R.string.privacy_content_5)
            )
            
            PolicySection(
                title = stringResource(R.string.privacy_title_6),
                content = stringResource(R.string.privacy_content_6)
            )
            
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun PolicySection(
    title: String,
    content: String
) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = content,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 20.sp
        )
    }
}
