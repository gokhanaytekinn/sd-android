package com.gokhanaytekinn.sdandroid.util

object CurrencyFormatter {
    
    fun formatAmount(amount: Double, currencyCode: String): String {
        val symbol = getCurrencySymbol(currencyCode)
        
        return when (currencyCode) {
            "TRY" -> "₺%.2f".format(amount)
            "USD" -> "$%.2f".format(amount)
            "EUR" -> "€%.2f".format(amount)
            "GBP" -> "£%.2f".format(amount)
            else -> "$symbol%.2f".format(amount)
        }
    }
    
    fun getCurrencySymbol(currencyCode: String): String {
        return when (currencyCode) {
            "TRY" -> "₺"
            "USD" -> "$"
            "EUR" -> "€"
            "GBP" -> "£"
            else -> currencyCode
        }
    }
    
    fun formatAmountWithoutSymbol(amount: Double): String {
        return "%.2f".format(amount)
    }
}
