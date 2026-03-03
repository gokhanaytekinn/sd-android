package com.gokhanaytekinn.sdandroid.util

object CurrencyFormatter {
    
    fun formatAmount(amount: Double, currencyCode: Int): String {
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
    
    fun getCurrencySymbol(currencyCode: Int): String {
        return when (currencyCode) {
            1 -> "₺"
            2 -> "$"
            3 -> "€"
            4 -> "£"
            5 -> "₽"
            6 -> "₼"
            7 -> "₸"
            else -> "₺"
        }
    }
    
    fun formatAmountWithoutSymbol(amount: Double): String {
        return "%.2f".format(amount)
    }
}
