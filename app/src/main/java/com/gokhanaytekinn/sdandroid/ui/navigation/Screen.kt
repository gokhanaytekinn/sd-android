package com.gokhanaytekinn.sdandroid.ui.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    object SubscriptionsList : Screen("subscriptions_list")
    object SuspiciousPayments : Screen("suspicious_payments")
    object PremiumAnalytics : Screen("premium_analytics")
    object PremiumUpgrade : Screen("premium_upgrade")
    object SubscriptionDetails : Screen("subscription_details/{id}") {
        fun createRoute(id: String) = "subscription_details/$id"
    }
    object TransactionHistory : Screen("transaction_history")
    object HelpCenter : Screen("help_center")
    object PrivacyPolicy : Screen("privacy_policy")
    object AppSettings : Screen("app_settings")
    object OnboardingAutomaticTracking : Screen("onboarding_automatic_tracking")
    object OnboardingSavings : Screen("onboarding_savings")
    object Search : Screen("search")
    object AddSubscription : Screen("add_subscription?id={id}") {
        fun createRoute(id: String? = null): String {
            return if (id != null) "add_subscription?id=$id" else "add_subscription"
        }
    }
}
