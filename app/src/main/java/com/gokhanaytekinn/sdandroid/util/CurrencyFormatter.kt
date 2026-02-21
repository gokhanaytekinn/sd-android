package com.gokhanaytekinn.sdandroid.util

object CurrencyFormatter {
    
    fun formatAmount(amount: Double, currencyCode: String): String {
        val symbol = getCurrencySymbol(currencyCode)
        val formattedAmount = formatAmountLocalized(amount)
        
        return "$formattedAmount $symbol"
    }
    
    private fun formatAmountLocalized(amount: Double): String {
        val symbols = java.text.DecimalFormatSymbols().apply {
            groupingSeparator = '.'
            decimalSeparator = ','
        }
        val decimalFormat = java.text.DecimalFormat("#,##0.00", symbols)
        return decimalFormat.format(amount)
    }
    
    fun getCurrencySymbol(currencyCode: String): String {
        return when (currencyCode) {
            "TRY" -> "₺"
            "USD" -> "$"
            "EUR" -> "€"
            "GBP" -> "£"
            "RUB" -> "₽"
            "AZN" -> "₼"
            "KZT" -> "₸"
            else -> currencyCode
        }
    }
    
    fun formatAmountWithoutSymbol(amount: Double): String {
        return "%.2f".format(amount)
    }
}
