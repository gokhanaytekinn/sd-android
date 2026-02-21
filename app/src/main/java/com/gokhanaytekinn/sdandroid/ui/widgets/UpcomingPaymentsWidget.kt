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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import androidx.glance.LocalContext
import androidx.glance.color.ColorProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items

class UpcomingPaymentsWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = SubscriptionRepository(context)
        
        // Fetch data
        val result = repository.getSubscriptions()
        val subscriptions = result.getOrNull() ?: emptyList()
        
        // Filter for upcoming 7 days
        val upcoming = filterUpcoming(subscriptions)

        provideContent {
            GlanceTheme {
                WidgetContent(upcoming)
            }
        }
    }

    private fun filterUpcoming(subscriptions: List<Subscription>): List<Subscription> {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val now = Calendar.getInstance()
        val sevenDaysLater = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 7) }

        return subscriptions.filter { sub ->
            sub.nextBillingDate?.let { dateStr ->
                try {
                    val date = sdf.parse(dateStr)
                    val cal = Calendar.getInstance().apply { time = date }
                    cal.after(now) && cal.before(sevenDaysLater) || isSameDay(cal, now)
                } catch (e: Exception) {
                    false
                }
            } ?: false
        }.sortedBy { it.nextBillingDate }
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    @Composable
    private fun WidgetContent(subscriptions: List<Subscription>) {
        val context = LocalContext.current
        
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color.White) // Adjust for dark mode if needed
                .padding(8.dp)
        ) {
            Text(
                text = context.getString(R.string.widget_upcoming_title),
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            )
            
            Spacer(modifier = GlanceModifier.height(4.dp))
            
            if (subscriptions.isEmpty()) {
                Box(modifier = GlanceModifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = context.getString(R.string.widget_no_data))
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
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = GlanceModifier.defaultWeight()) {
                Text(text = sub.name, style = TextStyle(fontWeight = FontWeight.Medium))
                Text(text = formatDate(sub.nextBillingDate, context), style = TextStyle(fontSize = 12.sp))
            }
            Text(text = "${sub.cost} ${sub.currency}", style = TextStyle(fontWeight = FontWeight.Bold))
        }
    }

    private fun formatDate(dateStr: String?, context: Context): String {
        if (dateStr == null) return ""
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return try {
            val date = sdf.parse(dateStr)
            val cal = Calendar.getInstance().apply { time = date }
            val now = Calendar.getInstance()
            
            if (isSameDay(cal, now)) {
                context.getString(R.string.widget_today)
            } else {
                val diff = cal.timeInMillis - now.timeInMillis
                val days = (diff / (1000 * 60 * 60 * 24)).toInt() + 1
                context.getString(R.string.widget_days_left, days)
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
fun GlanceTheme(content: @Composable () -> Unit) {
    // Basic theme wrapper
    content()
}
