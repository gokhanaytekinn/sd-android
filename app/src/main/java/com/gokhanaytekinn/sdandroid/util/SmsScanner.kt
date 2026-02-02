package com.gokhanaytekinn.sdandroid.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.Telephony
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

/**
 * SMS tarayıcı sınıfı
 * Cihazın SMS mesajlarını tarar ve abonelik bilgisi tespit eder
 */
class SmsScanner(private val context: Context) {
    
    data class DetectedSubscription(
        val serviceName: String,
        val amount: Double,
        val billingCycle: String,
        val detectedDate: String,
        val sourceMessage: String
    )
    
    /**
     * SMS mesajlarını tarar ve abonelik tespit eder
     * @param maxMessages Taranacak maksimum mesaj sayısı (varsayılan: 500)
     * @param onProgress İlerleme callback'i (taranan mesaj sayısı)
     */
    suspend fun scanMessages(
        maxMessages: Int = 500,
        onProgress: ((Int) -> Unit)? = null
    ): List<DetectedSubscription> = withContext(Dispatchers.IO) {
        val detectedSubscriptions = mutableListOf<DetectedSubscription>()
        
        try {
            val contentResolver: ContentResolver = context.contentResolver
            val uri = Telephony.Sms.CONTENT_URI
            
            // SMS'leri tarihe göre sırala (en yeni önce)
            val cursor = contentResolver.query(
                uri,
                arrayOf(
                    Telephony.Sms._ID,
                    Telephony.Sms.ADDRESS,
                    Telephony.Sms.BODY,
                    Telephony.Sms.DATE
                ),
                null,
                null,
                "${Telephony.Sms.DATE} DESC"
            )
            
            cursor?.use {
                var count = 0
                val bodyIndex = it.getColumnIndex(Telephony.Sms.BODY)
                val dateIndex = it.getColumnIndex(Telephony.Sms.DATE)
                
                while (it.moveToNext() && count < maxMessages) {
                    count++
                    onProgress?.invoke(count)
                    
                    val body = it.getString(bodyIndex) ?: continue
                    val date = it.getLong(dateIndex)
                    
                    // Mesaj abonelik ile ilgili mi kontrol et
                    if (!SubscriptionDetectionPatterns.isSubscriptionRelated(body)) {
                        continue
                    }
                    
                    // Servis adını tespit et
                    val serviceName = SubscriptionDetectionPatterns.detectServiceName(body) 
                        ?: continue
                    
                    // Tutarı tespit et
                    val amount = SubscriptionDetectionPatterns.detectAmount(body) 
                        ?: continue
                    
                    // Fatura döngüsünü tespit et
                    val billingCycle = SubscriptionDetectionPatterns.detectBillingCycle(body)
                        ?: "MONTHLY"
                    
                    // Tarihi formatla
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val detectedDate = dateFormat.format(Date(date))
                    
                    // Tespit edilen aboneliği ekle
                    detectedSubscriptions.add(
                        DetectedSubscription(
                            serviceName = serviceName,
                            amount = amount,
                            billingCycle = billingCycle,
                            detectedDate = detectedDate,
                            sourceMessage = body.take(100) // İlk 100 karakter
                        )
                    )
                }
            }
            
        } catch (e: SecurityException) {
            // İzin verilmemiş
            throw e
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        // Aynı servisleri birleştir (en yüksek tutarlı olanı al)
        return@withContext detectedSubscriptions
            .groupBy { it.serviceName }
            .map { (_, subscriptions) ->
                subscriptions.maxByOrNull { it.amount } ?: subscriptions.first()
            }
    }
}
