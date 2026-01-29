package com.gokhanaytekinn.sdandroid.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.gokhanaytekinn.sdandroid.ui.screens.DashboardScreen
import com.gokhanaytekinn.sdandroid.ui.screens.OnboardingScreen

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
            OnboardingScreen(
                onGetStartedClick = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Dashboard.route) {
            DashboardScreen()
        }
    }
}
