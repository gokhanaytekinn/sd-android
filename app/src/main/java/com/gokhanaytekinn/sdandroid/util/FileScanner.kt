package com.gokhanaytekinn.sdandroid.util

import android.content.Context
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Dosya tarayıcı sınıfı
 * Downloads ve Documents klasörlerindeki dosyaları tarar
 */
class FileScanner(private val context: Context) {
    
    data class DetectedSubscription(
        val serviceName: String,
        val amount: Double,
        val billingCycle: String,
        val sourceFile: String
    )
    
    /**
     * Dosyaları tarar ve abonelik tespit eder
     * @param onProgress İlerleme callback'i (taranan dosya sayısı)
     */
    suspend fun scanFiles(
        onProgress: ((Int) -> Unit)? = null
    ): List<DetectedSubscription> = withContext(Dispatchers.IO) {
        val detectedSubscriptions = mutableListOf<DetectedSubscription>()
        
        try {
            val foldersToScan = listOf(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
            )
            
            var fileCount = 0
            
            for (folder in foldersToScan) {
                if (!folder.exists() || !folder.isDirectory) continue
                
                // PDF ve TXT dosyalarını tara
                val files = folder.listFiles { file ->
                    file.isFile && (
                        file.extension.equals("pdf", ignoreCase = true) ||
                        file.extension.equals("txt", ignoreCase = true)
                    )
                } ?: continue
                
                for (file in files.take(50)) { // İlk 50 dosya
                    fileCount++
                    onProgress?.invoke(fileCount)
                    
                    try {
                        when (file.extension.lowercase()) {
                            "txt" -> scanTextFile(file, detectedSubscriptions)
                            "pdf" -> scanPdfFile(file, detectedSubscriptions)
                        }
                    } catch (e: Exception) {
                        // Dosya okuma hatası, devam et
                        e.printStackTrace()
                    }
                }
            }
            
        } catch (e: SecurityException) {
            // İzin verilmemiş
            throw e
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return@withContext detectedSubscriptions.distinctBy { it.serviceName }
    }
    
    private fun scanTextFile(file: File, results: MutableList<DetectedSubscription>) {
        try {
            val content = file.readText()
            processFileContent(content, file.name, results)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun scanPdfFile(file: File, results: MutableList<DetectedSubscription>) {
        // PDF parsing için Apache PDFBox veya benzer kütüphane gerekli
        // Şimdilik basit versiyon - sadece dosya adını kontrol et
        try {
            val fileName = file.name
            processFileContent(fileName, file.name, results)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun processFileContent(
        content: String,
        fileName: String,
        results: MutableList<DetectedSubscription>
    ) {
        if (!SubscriptionDetectionPatterns.isSubscriptionRelated(content)) {
            return
        }
        
        val serviceName = SubscriptionDetectionPatterns.detectServiceName(content) 
            ?: return
        
        val amount = SubscriptionDetectionPatterns.detectAmount(content) 
            ?: return
        
        val billingCycle = SubscriptionDetectionPatterns.detectBillingCycle(content)
            ?: "MONTHLY"
        
        results.add(
            DetectedSubscription(
                serviceName = serviceName,
                amount = amount,
                billingCycle = billingCycle,
                sourceFile = fileName
            )
        )
    }
}
