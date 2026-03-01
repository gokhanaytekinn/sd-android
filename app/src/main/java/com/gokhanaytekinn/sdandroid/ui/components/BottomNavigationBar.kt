package com.gokhanaytekinn.sdandroid.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.gokhanaytekinn.sdandroid.ui.navigation.Screen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = Icons.Default.Dashboard,
                label = stringResource(R.string.nav_dashboard),
                selected = currentRoute == Screen.Dashboard.route,
                onClick = { onNavigate(Screen.Dashboard.route) }
            )
            BottomNavItem(
                icon = Icons.Default.List,
                label = stringResource(R.string.subscriptions),
                selected = currentRoute == Screen.SubscriptionsList.route,
                onClick = { onNavigate(Screen.SubscriptionsList.route) }
            )
            BottomNavItem(
                icon = Icons.Default.Notifications,
                label = stringResource(R.string.nav_upcoming),
                selected = currentRoute == Screen.UpcomingSubscriptions.route,
                onClick = { onNavigate(Screen.UpcomingSubscriptions.route) }
            )
            BottomNavItem(
                icon = Icons.Default.Settings,
                label = stringResource(R.string.nav_settings),
                selected = currentRoute == Screen.AppSettings.route,
                onClick = { onNavigate(Screen.AppSettings.route) }
            )
        }
    }
}

@Composable
fun MainActionButton(
    isMenuExpanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (isMenuExpanded) 135f else 0f,
        animationSpec = tween(durationMillis = 300)
    )

    FloatingActionButton(
        onClick = onClick,
        shape = CircleShape,
        containerColor = PrimaryBlue,
        contentColor = Color.White,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 6.dp,
            pressedElevation = 12.dp
        ),
        modifier = modifier.size(56.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add/Scan",
            modifier = Modifier
                .size(28.dp)
                .rotate(rotation)
        )
    }
}

@Composable
fun RowScope.BottomNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable(
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) PrimaryBlue else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(26.dp)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            color = if (selected) PrimaryBlue else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
