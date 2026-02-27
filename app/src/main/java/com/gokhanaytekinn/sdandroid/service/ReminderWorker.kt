package com.gokhanaytekinn.sdandroid.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gokhanaytekinn.sdandroid.MainActivity
import com.gokhanaytekinn.sdandroid.R
import com.gokhanaytekinn.sdandroid.data.preferences.NotificationPreferences
import com.gokhanaytekinn.sdandroid.data.repository.SubscriptionRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ReminderWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val notificationPreferences = NotificationPreferences(applicationContext)
        val isGlobalEnabled = notificationPreferences.notificationsEnabled.first()
        
        if (!isGlobalEnabled) {
            return Result.success()
        }

        val repository = SubscriptionRepository(applicationContext)
        val result = repository.getSubscriptions(status = "ACTIVE")

        if (result.isSuccess) {
            val tomorrow = LocalDate.now().plusDays(1)
            val formatter = DateTimeFormatter.ISO_DATE_TIME

            val subscriptionsToRemind = result.getOrNull()?.filter { sub ->
                sub.reminderEnabled && sub.nextBillingDate != null && try {
                    val renewalDate = LocalDate.parse(sub.nextBillingDate, formatter)
                    renewalDate.isEqual(tomorrow)
                } catch (e: Exception) {
                    false
                }
            } ?: emptyList()

            subscriptionsToRemind.forEach { sub ->
                sendNotification(
                    sub.name,
                    "Aboneliğiniz yarın yenilenecek: ${sub.cost} ${sub.currency}"
                )
            }
        }

        return Result.success()
    }

    private fun sendNotification(title: String, messageBody: String) {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = "reminders_channel"
        val notificationBuilder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
}
