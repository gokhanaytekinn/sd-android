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
    onNavigate: (String) -> Unit,
    onScanClick: () -> Unit = {},
    onAddClick: () -> Unit = {}
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    Box {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
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
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
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
                
                // Placeholder space for FAB
                Spacer(modifier = Modifier.weight(1f))
                
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

        // Central FAB over the bar
        // We use a Box with the same height as the navigation bar to ensure the FAB is positioned relative to the same baseline
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp) // Approximate height of the bottom bar
                .align(Alignment.BottomCenter)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-20).dp),
                contentAlignment = Alignment.Center
            ) {
                // Hide the background FAB when expanded to avoid duplication and layering issues
                val alpha by animateFloatAsState(
                    targetValue = if (isMenuExpanded) 0f else 1f,
                    animationSpec = tween(durationMillis = 300)
                )

                Box(modifier = Modifier.alpha(alpha)) {
                    MainActionButton(
                        isMenuExpanded = isMenuExpanded,
                        onClick = { isMenuExpanded = !isMenuExpanded }
                    )
                }
            }
        }
    }

    val popupTransitionState = remember { MutableTransitionState(false) }
    LaunchedEffect(isMenuExpanded) {
        popupTransitionState.targetState = isMenuExpanded
    }

    if (popupTransitionState.currentState || popupTransitionState.targetState) {
        Popup(
            alignment = Alignment.BottomCenter,
            onDismissRequest = { isMenuExpanded = false },
            properties = PopupProperties(focusable = true)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { isMenuExpanded = false }
            ) {
                AnimatedVisibility(
                    visibleState = popupTransitionState,
                    enter = fadeIn(animationSpec = tween(300)),
                    exit = fadeOut(animationSpec = tween(300))
                ) {
                    // Full screen container to allow children to align themselves
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 100.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Scan Subscription
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 40.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    modifier = Modifier.padding(end = 16.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.menu_scan),
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                FloatingActionButton(
                                    onClick = {
                                        isMenuExpanded = false
                                        onScanClick()
                                    },
                                    shape = CircleShape,
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    contentColor = PrimaryBlue,
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Icon(Icons.Default.DocumentScanner, contentDescription = "Scan")
                                }
                            }

                            // Add Manually
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 40.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    modifier = Modifier.padding(end = 16.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.menu_add_manually),
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                FloatingActionButton(
                                    onClick = {
                                        isMenuExpanded = false
                                        onAddClick()
                                    },
                                    shape = CircleShape,
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    contentColor = PrimaryBlue,
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Add Manually")
                                }
                            }
                        }

                        // IMPORTANT: Replicate the FAB here so it's ABOVE the dimming background
                        // We use the exact same Box structure as above to ensure identical positioning
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .align(Alignment.BottomCenter)
                        ) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .offset(y = (-20).dp),
                                contentAlignment = Alignment.Center
                            ) {
                                MainActionButton(
                                    isMenuExpanded = isMenuExpanded,
                                    onClick = { isMenuExpanded = false }
                                )
                            }
                        }
                    }
                }
            }
        }
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
            .clickable(onClick = onClick)
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
