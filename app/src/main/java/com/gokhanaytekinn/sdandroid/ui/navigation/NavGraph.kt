package com.gokhanaytekinn.sdandroid.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.res.stringResource
import com.gokhanaytekinn.sdandroid.ui.components.BottomNavigationBar
import com.gokhanaytekinn.sdandroid.ui.components.BannerAdView
import com.gokhanaytekinn.sdandroid.ui.screens.DashboardViewModel
import com.gokhanaytekinn.sdandroid.data.preferences.CurrencyPreferences
import androidx.compose.runtime.collectAsState
import com.gokhanaytekinn.sdandroid.R
import com.gokhanaytekinn.sdandroid.ui.screens.*
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import com.gokhanaytekinn.sdandroid.ui.theme.PrimaryBlue
import com.gokhanaytekinn.sdandroid.ui.components.MainActionButton

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Onboarding.route
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val context = androidx.compose.ui.platform.LocalContext.current
    val authViewModel: com.gokhanaytekinn.sdandroid.ui.viewmodel.AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel {
        com.gokhanaytekinn.sdandroid.ui.viewmodel.AuthViewModel(context)
    }
    
    val premiumPreferences = remember { com.gokhanaytekinn.sdandroid.data.preferences.PremiumPreferences(context) }
    val isPremium by premiumPreferences.isPremium.collectAsState(initial = false)

    
    var isMenuExpanded by remember { mutableStateOf(false) }


    val showBottomBar = currentRoute in listOf(
        Screen.Dashboard.route,
        Screen.SubscriptionsList.route,
        Screen.Search.route,
        Screen.UpcomingSubscriptions.route,
        Screen.AppSettings.route
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.weight(1f),
            bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    currentRoute = currentRoute,
                    isDimmed = isMenuExpanded,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            if (showBottomBar) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (isMenuExpanded) {
                        // Add Manually
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
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
                                    navController.navigate(Screen.AddSubscription.route)
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

                    MainActionButton(
                        isMenuExpanded = isMenuExpanded,
                        onClick = { isMenuExpanded = !isMenuExpanded }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = androidx.compose.ui.Modifier.padding(innerPadding)
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingPagerScreen(
                    onComplete = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }
            
            composable(Screen.Login.route) {
                LoginScreen(
                    viewModel = authViewModel,
                    onLoginSuccess = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    },
                    onForgotPassword = {
                        navController.navigate(Screen.ForgotPassword.route)
                    }
                )
            }

            composable(Screen.ForgotPassword.route) {
                ForgotPasswordScreen(
                    viewModel = authViewModel,
                    onBackClick = { navController.popBackStack() },
                    onCodeSent = {
                        navController.navigate(Screen.VerificationCode.route)
                    }
                )
            }

            composable(Screen.VerificationCode.route) {
                VerificationCodeScreen(
                    viewModel = authViewModel,
                    onBackClick = { navController.popBackStack() },
                    onCodeVerified = { code ->
                        navController.navigate("reset_password/$code")
                    }
                )
            }

            composable(
                route = "reset_password/{code}",
                arguments = listOf(
                    androidx.navigation.navArgument("code") {
                        type = androidx.navigation.NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val code = backStackEntry.arguments?.getString("code") ?: ""
                ResetPasswordScreen(
                    viewModel = authViewModel,
                    verificationCode = code,
                    onBackClick = { navController.popBackStack() },
                    onPasswordReset = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }
            
            composable(Screen.Register.route) {
                RegisterScreen(
                    viewModel = authViewModel,
                    onRegisterSuccess = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.popBackStack()
                    }
                )
            }
            
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onNavigateToAllSubscriptions = {
                        navController.navigate(Screen.SubscriptionsList.route)
                    },
                    onNavigateToUpcoming = {
                        navController.navigate(Screen.UpcomingSubscriptions.route)
                    }
                )
            }
            
            
            composable(
                route = Screen.SubscriptionsList.route,
                arguments = listOf(
                    androidx.navigation.navArgument("tab") {
                        type = androidx.navigation.NavType.IntType
                        defaultValue = 0
                    }
                )
            ) { backStackEntry ->
                val initialTab = backStackEntry.arguments?.getInt("tab") ?: 0
                SubscriptionsListScreen(
                    initialTab = initialTab,
                    onSubscriptionClick = { subscriptionId ->
                        navController.navigate(Screen.SubscriptionDetails.createRoute(subscriptionId))
                    },
                    onNavigateToSearch = {
                        navController.navigate(Screen.Search.route)
                    }
                )
            }
            
            composable(
                route = Screen.AddSubscription.route,
                arguments = listOf(
                    androidx.navigation.navArgument("id") {
                        type = androidx.navigation.NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val subscriptionId = backStackEntry.arguments?.getString("id")
                AddSubscriptionScreen(
                    subscriptionId = subscriptionId,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
            
            composable(Screen.UpcomingSubscriptions.route) {
                UpcomingSubscriptionsScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onSubscriptionClick = { id ->
                        navController.navigate(Screen.SubscriptionDetails.createRoute(id))
                    }
                )
            }
            
            composable(Screen.SuspiciousPayments.route) {
                SuspiciousPaymentsScreen(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
            composable(
                route = Screen.PremiumUpgrade.route,
                arguments = listOf(
                    androidx.navigation.navArgument("plan") {
                        type = androidx.navigation.NavType.StringType
                        defaultValue = "FREE"
                        nullable = true
                    }
                )
            ) { backStackEntry ->
                val planStr = backStackEntry.arguments?.getString("plan") ?: "1"
                val initialPlan = if (planStr == "2") SubscriptionPlanType.MONTHLY else SubscriptionPlanType.FREE
                
                PremiumUpgradeScreen(
                    initialPlan = initialPlan,
                    onCloseClick = {
                        navController.popBackStack()
                    },
                    onUpgradeClick = { _ ->
                        // Handle premium upgrade
                        navController.popBackStack()
                    }
                )
            }
            
            composable(
                route = Screen.SubscriptionDetails.route,
                arguments = listOf(
                    androidx.navigation.navArgument("id") {
                        type = androidx.navigation.NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val subscriptionId = backStackEntry.arguments?.getString("id") ?: ""
                SubscriptionDetailsScreen(
                    subscriptionId = subscriptionId,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onEditPlanClick = {
                        navController.navigate(Screen.AddSubscription.createRoute(subscriptionId))
                    }
                )
            }
            
            composable(Screen.TransactionHistory.route) {
                TransactionHistoryScreen(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
            
            composable(Screen.AppSettings.route) {
                AppSettingsScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onUpgradeClick = { tier ->
                        navController.navigate(Screen.PremiumUpgrade.createRoute(tier))
                    },
                    onHelpClick = {
                        navController.navigate(Screen.HelpCenter.route)
                    },
                    onPrivacyClick = {
                        navController.navigate(Screen.PrivacyPolicy.route)
                    },
                    onLogoutClick = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.HelpCenter.route) {
                HelpCenterScreen(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
            
            composable(Screen.PrivacyPolicy.route) {
                PrivacyPolicyScreen(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
            
            composable(Screen.OnboardingSavings.route) {
                OnboardingSavingsScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onGetStartedClick = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }
            
            composable(Screen.Search.route) {
                SearchScreen(
                    onResultClick = { id ->
                        // Navigate to subscription or transaction details
                        navController.navigate(Screen.SubscriptionDetails.createRoute(id))
                    }
                )
            }
        }

        if (isMenuExpanded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(
                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                        indication = null,
                        onClick = { isMenuExpanded = false }
                    )
            )
        }
    }

    // Sabit Banner Reklam (Bottom Navigation'ın altında)
    if (showBottomBar && !isPremium) {
        Box(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)) {
            BannerAdView(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            )
        }
    }
}
}
