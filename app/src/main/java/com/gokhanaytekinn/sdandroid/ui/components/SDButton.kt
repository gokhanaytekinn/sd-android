package com.gokhanaytekinn.sdandroid.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SDButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    outlined: Boolean = false,
    enabled: Boolean = true,
    backgroundColor: Color = Color(0xFF2196F3),
    textColor: Color = Color.White,
    height: Dp = 56.dp
) {
    if (outlined) {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier
                .fillMaxWidth()
                .height(height),
            enabled = enabled,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(2.dp, backgroundColor),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = backgroundColor
            ),
            contentPadding = PaddingValues(16.dp)
        ) {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    } else {
        Button(
            onClick = onClick,
            modifier = modifier
                .fillMaxWidth()
                .height(height),
            enabled = enabled,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = backgroundColor,
                contentColor = textColor
            ),
            contentPadding = PaddingValues(16.dp)
        ) {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
