package com.gokhanaytekinn.sdandroid.ui.widgets

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.text.FontWeight
import androidx.glance.unit.ColorProvider
import com.gokhanaytekinn.sdandroid.R
import com.gokhanaytekinn.sdandroid.data.repository.SubscriptionRepository
import com.gokhanaytekinn.sdandroid.data.model.Subscription
import androidx.glance.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.appWidgetBackground

class TopExpensiveWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = SubscriptionRepository(context)
        
        // Fetch data
        val result = repository.getSubscriptions()
        val subscriptions = result.getOrNull() ?: emptyList()
        
        // Get top 3 expensive active subscriptions
        val topExpensive = subscriptions
            .filter { it.isActive }
            .sortedByDescending { it.cost }
            .take(3)

        provideContent {
            WidgetTheme {
                WidgetContent(topExpensive)
            }
        }
    }

    @Composable
    private fun WidgetContent(subscriptions: List<Subscription>) {
        val context = LocalContext.current
        
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .appWidgetBackground()
                .background(ColorProvider(Color(0xFF1E293B))) // SurfaceDark
                .cornerRadius(16.dp)
                .padding(16.dp)
        ) {
            Text(
                text = context.getString(R.string.widget_expensive_title),
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = ColorProvider(Color.White)
                )
            )
            
            Spacer(modifier = GlanceModifier.height(8.dp))
            
            if (subscriptions.isEmpty()) {
                Box(
                    modifier = GlanceModifier.fillMaxSize(), 
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = context.getString(R.string.widget_no_data),
                        style = TextStyle(color = ColorProvider(Color.White.copy(alpha = 0.6f)))
                    )
                }
            } else {
                LazyColumn(modifier = GlanceModifier.fillMaxSize()) {
                    items(subscriptions) { sub ->
                        SubscriptionItem(sub)
                    }
                }
            }
        }
    }

    @Composable
    private fun SubscriptionItem(sub: Subscription) {
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = sub.name, 
                modifier = GlanceModifier.defaultWeight(),
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    color = ColorProvider(Color.White),
                    fontSize = 14.sp
                )
            )
            Text(
                text = com.gokhanaytekinn.sdandroid.util.CurrencyFormatter.formatAmount(sub.cost, sub.currency), 
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    color = ColorProvider(Color(0xFF359EFF)), // PrimaryBlue
                    fontSize = 14.sp
                )
            )
        }
    }
}

class TopExpensiveWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TopExpensiveWidget()
}
