package com.gokhanaytekinn.sdandroid.util

import android.content.Context
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Cihaz abonelik tarayıcı koordinatörü
 * SMS ve dosya taramalarını koordine eder
 */
class DeviceSubscriptionScanner(private val context: Context) {
    
    data class ScanProgress(
        val isScanning: Boolean = false,
        val smsScanned: Int = 0,
        val filesScanned: Int = 0,
        val totalDetected: Int = 0
    )
    
    data class DetectedSubscription(
        val serviceName: String,
        val amount: Double,
        val billingCycle: String,
        val source: String, // "SMS" veya "Dosya"
        val details: String // Kaynak mesaj veya dosya adı
    )
    
    private val _progress = MutableStateFlow(ScanProgress())
    val progress: StateFlow<ScanProgress> = _progress.asStateFlow()
    
    /**
     * Cihazı tara (hem SMS hem dosyalar)
     */
    suspend fun scanDevice(): List<DetectedSubscription> = coroutineScope {
        _progress.value = ScanProgress(isScanning = true)
        
        val allDetected = mutableListOf<DetectedSubscription>()
        
        try {
            // SMS ve dosya taramalarını paralel olarak başlat
            val smsJob = async {
                scanSmsMessages()
            }
            
            val filesJob = async {
                scanFiles()
            }
            
            // Her iki sonucu bekle
            val smsResults = try { smsJob.await() } catch (e: Exception) { emptyList() }
            val fileResults = try { filesJob.await() } catch (e: Exception) { emptyList() }
            
            allDetected.addAll(smsResults)
            allDetected.addAll(fileResults)
            
            // Tekrar edenleri temizle (aynı servis için en yüksek tutarlı olanı al)
            val uniqueResults = allDetected
                .groupBy { it.serviceName }
                .map { (_, subscriptions) ->
                    subscriptions.maxByOrNull { it.amount } ?: subscriptions.first()
                }
            
            _progress.value = ScanProgress(
                isScanning = false,
                smsScanned = _progress.value.smsScanned,
                filesScanned = _progress.value.filesScanned,
                totalDetected = uniqueResults.size
            )
            
            return@coroutineScope uniqueResults
            
        } catch (e: Exception) {
            _progress.value = ScanProgress(isScanning = false)
            throw e
        }
    }
    
    private suspend fun scanSmsMessages(): List<DetectedSubscription> {
        val smsScanner = SmsScanner(context)
        val results = smsScanner.scanMessages(
            maxMessages = 500,
            onProgress = { count ->
                _progress.value = _progress.value.copy(smsScanned = count)
            }
        )
        
        return results.map { sms ->
            DetectedSubscription(
                serviceName = sms.serviceName,
                amount = sms.amount,
                billingCycle = sms.billingCycle,
                source = "SMS",
                details = "Tespit tarihi: ${sms.detectedDate}"
            )
        }
    }
    
    private suspend fun scanFiles(): List<DetectedSubscription> {
        val fileScanner = FileScanner(context)
        val results = fileScanner.scanFiles(
            onProgress = { count ->
                _progress.value = _progress.value.copy(filesScanned = count)
            }
        )
        
        return results.map { file ->
            DetectedSubscription(
                serviceName = file.serviceName,
                amount = file.amount,
                billingCycle = file.billingCycle,
                source = "Dosya",
                details = "Dosya: ${file.sourceFile}"
            )
        }
    }
}
