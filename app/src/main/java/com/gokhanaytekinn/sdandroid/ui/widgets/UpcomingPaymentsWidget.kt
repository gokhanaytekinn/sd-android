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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class UpcomingPaymentsWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = SubscriptionRepository(context)
        
        // Fetch data
        val result = repository.getSubscriptions()
        val subscriptions = result.getOrNull() ?: emptyList()
        
        // Filter for upcoming 7 days
        val upcoming = filterUpcoming(subscriptions)

        provideContent {
            WidgetTheme {
                WidgetContent(upcoming)
            }
        }
    }

    private fun filterUpcoming(subscriptions: List<Subscription>): List<Subscription> {
        val today = LocalDate.now()
        val sevenDaysLater = today.plusDays(7)

        return subscriptions.filter { sub ->
            sub.nextBillingDate?.let { dateStr ->
                try {
                    val renewalDate = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE)
                    !renewalDate.isBefore(today) && !renewalDate.isAfter(sevenDaysLater)
                } catch (e: Exception) {
                    false
                }
            } ?: false
        }.sortedBy { it.nextBillingDate }
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
                text = context.getString(R.string.widget_upcoming_title),
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
        val context = LocalContext.current
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = GlanceModifier.defaultWeight()) {
                Text(
                    text = sub.name, 
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        color = ColorProvider(Color.White),
                        fontSize = 14.sp
                    )
                )
                Text(
                    text = formatDateRelative(sub.nextBillingDate, context), 
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = ColorProvider(Color.White.copy(alpha = 0.7f))
                    )
                )
            }
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

    private fun formatDateRelative(dateStr: String?, context: Context): String {
        if (dateStr == null) return ""
        return try {
            val renewalDate = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE)
            val today = LocalDate.now()
            
            val days = ChronoUnit.DAYS.between(today, renewalDate).toInt()
            
            when {
                days == 0 -> context.getString(R.string.widget_today)
                days > 0 -> context.getString(R.string.widget_days_left, days)
                else -> dateStr
            }
        } catch (e: Exception) {
            dateStr
        }
    }
}

class UpcomingPaymentsWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = UpcomingPaymentsWidget()
}

@Composable
fun WidgetTheme(content: @Composable () -> Unit) {
    content()
}
