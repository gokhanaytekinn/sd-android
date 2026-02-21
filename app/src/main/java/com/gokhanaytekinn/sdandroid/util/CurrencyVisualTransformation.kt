package com.gokhanaytekinn.sdandroid.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class CurrencyVisualTransformation(
    private val currencySymbol: String
) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        if (originalText.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        // Clean input: only digits
        val rawInput = originalText.filter { it.isDigit() }
        if (rawInput.isEmpty()) {
            return TransformedText(AnnotatedString(""), OffsetMapping.Identity)
        }

        // Parse as cents (Long)
        val value = rawInput.toLongOrNull() ?: 0L
        val amount = value.toDouble() / 100.0

        // Format
        val symbols = DecimalFormatSymbols().apply {
            groupingSeparator = '.'
            decimalSeparator = ','
        }
        val decimalFormat = DecimalFormat("#,##0.00", symbols)
        val formatted = "${decimalFormat.format(amount)} $currencySymbol"

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                // This is a bit tricky for currency formatting where length changes a lot
                // For simplicity in a fixed-decimal input, we usually put cursor at the end
                return formatted.length
            }

            override fun transformedToOriginal(offset: Int): Int {
                return originalText.length
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}
