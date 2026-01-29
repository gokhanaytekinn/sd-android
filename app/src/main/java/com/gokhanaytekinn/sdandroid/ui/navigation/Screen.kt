package com.gokhanaytekinn.sdandroid.ui.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Dashboard : Screen("dashboard")
}
