package com.gokhanaytekinn.sdandroid.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gokhanaytekinn.sdandroid.R
import com.gokhanaytekinn.sdandroid.ui.theme.PrimaryBlue

@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    selectedTab: Int = 0,
    onDashboardClick: () -> Unit = {},
    onSubscriptionsClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            ),
        color = Color.Transparent,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BottomNavItem(
                icon = Icons.Default.Dashboard,
                label = stringResource(R.string.nav_dashboard),
                selected = selectedTab == 0,
                onClick = onDashboardClick
            )
            BottomNavItem(
                icon = Icons.Default.List,
                label = "Abonelikler", // Hardcoded for now if string resource doesn't exist, or we can add it
                selected = selectedTab == 1,
                onClick = onSubscriptionsClick
            )
            BottomNavItem(
                icon = Icons.Default.Search,
                label = stringResource(R.string.nav_search),
                selected = selectedTab == 2,
                onClick = onSearchClick
            )
            BottomNavItem(
                icon = Icons.Default.Settings,
                label = stringResource(R.string.nav_settings),
                selected = selectedTab == 3,
                onClick = onSettingsClick
            )
        }
    }
}

@Composable
fun BottomNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) PrimaryBlue else Color(0xFF6B7280),
            modifier = Modifier.size(26.dp)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            color = if (selected) PrimaryBlue else Color(0xFF6B7280)
        )
    }
}
