package com.gokhanaytekinn.sdandroid.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.gokhanaytekinn.sdandroid.ui.screens.*

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Onboarding.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
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
                onBackClick = {
                    navController.popBackStack()
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onForgotPassword = {
                    // TODO: Forgot password screen
                }
            )
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(
                onBackClick = {
                    navController.popBackStack()
                },
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
                onNavigateToSuspicious = {
                    navController.navigate(Screen.SuspiciousPayments.route)
                },
                onNavigateToAllSubscriptions = {
                    navController.navigate(Screen.SubscriptionsList.route)
                },
                onNavigateToSearch = {
                    navController.navigate(Screen.Search.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.AppSettings.route)
                }
            )
        }
        
        
        composable(Screen.SubscriptionsList.route) {
            SubscriptionsListScreen(
                onSubscriptionClick = { subscriptionId ->
                    navController.navigate(Screen.SubscriptionDetails.createRoute(subscriptionId))
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                },
                onNavigateToSearch = {
                    navController.navigate(Screen.Search.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.AppSettings.route)
                },
                onNavigateToAddSubscription = {
                    navController.navigate(Screen.AddSubscription.route)
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
        
        composable(Screen.PremiumAnalytics.route) {
            PremiumAnalyticsScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.PremiumUpgrade.route) {
            PremiumUpgradeScreen(
                onCloseClick = {
                    navController.popBackStack()
                },
                onUpgradeClick = { isYearly ->
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
                onBackClick = {
                    navController.popBackStack()
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
                onUpgradeClick = {
                    navController.navigate(Screen.PremiumUpgrade.route)
                },
                onLogoutClick = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.OnboardingAutomaticTracking.route) {
            OnboardingAutomaticTrackingScreen(
                onContinueClick = {
                    navController.navigate(Screen.OnboardingSavings.route)
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
                onBackClick = {
                    navController.popBackStack()
                },
                onResultClick = { id ->
                    // Navigate to subscription or transaction details
                    navController.navigate(Screen.SubscriptionDetails.createRoute(id))
                }
            )
        }
    }
}
