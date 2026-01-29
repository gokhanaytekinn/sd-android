package com.gokhanaytekinn.sdandroid.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gokhanaytekinn.sdandroid.data.model.BillingCycle
import com.gokhanaytekinn.sdandroid.data.model.Subscription

@Composable
fun SubscriptionCard(
    subscription: Subscription,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    SDCard(
        modifier = modifier,
        elevation = 2.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subscription.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = getBillingCycleText(subscription.billingCycle),
                    fontSize = 14.sp,
                    color = Color(0xFF757575)
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$${subscription.cost}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2196F3)
                )
                if (subscription.nextBillingDate != null) {
                    Text(
                        text = "Next: ${subscription.nextBillingDate}",
                        fontSize = 12.sp,
                        color = Color(0xFF757575)
                    )
                }
            }
        }
        
        if (subscription.category != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .background(
                        color = Color(0xFFE3F2FD),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = subscription.category,
                    fontSize = 12.sp,
                    color = Color(0xFF2196F3),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

private fun getBillingCycleText(cycle: BillingCycle): String {
    return when (cycle) {
        BillingCycle.MONTHLY -> "Monthly"
        BillingCycle.YEARLY -> "Yearly"
        BillingCycle.WEEKLY -> "Weekly"
        BillingCycle.QUARTERLY -> "Quarterly"
    }
}
