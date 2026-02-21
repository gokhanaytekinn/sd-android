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

        val parts = originalText.split(",")
        val integerPart = parts[0]
        val decimalPart = if (parts.size > 1) parts[1] else null

        // Format integer part with dots
        val symbols = DecimalFormatSymbols().apply {
            groupingSeparator = '.'
        }
        val decimalFormat = DecimalFormat("#,###", symbols)
        val formattedInteger = if (integerPart.isEmpty()) "" else {
            val value = integerPart.toLongOrNull() ?: 0L
            decimalFormat.format(value)
        }

        val formattedResult = StringBuilder(formattedInteger)
        if (decimalPart != null) {
            formattedResult.append(",").append(decimalPart)
        }
        
        val suffix = " $currencySymbol"
        val transformedText = formattedResult.toString() + suffix

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset == 0) return 0
                
                var transformedOffset = 0
                var originalProcessed = 0
                
                val transformedBase = formattedResult.toString()
                
                for (i in 0 until transformedBase.length) {
                    if (originalProcessed == offset) break
                    
                    val char = transformedBase[i]
                    if (char == '.') {
                        transformedOffset++
                    } else {
                        transformedOffset++
                        originalProcessed++
                    }
                }
                
                // If offset was at the end of original text (after all digits), 
                // it should still be before the suffix in transformed text
                return transformedOffset.coerceAtMost(transformedBase.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                val transformedBase = formattedResult.toString()
                val actualOffset = offset.coerceAtMost(transformedBase.length)
                
                var originalOffset = 0
                for (i in 0 until actualOffset) {
                    if (transformedBase[i] != '.') {
                        originalOffset++
                    }
                }
                return originalOffset
            }
        }

        return TransformedText(AnnotatedString(transformedText), offsetMapping)
    }
}
