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
            val rootDir = Environment.getExternalStorageDirectory()
            var fileCount = 0
            
            // Tüm cihazı tara (gizli klasörler ve sistem/veritabanı alanları hariç)
            try {
                rootDir.walkTopDown()
                    .onEnter { dir -> 
                        !dir.name.startsWith(".") && 
                        dir.name != "Android" && 
                        dir.name != "lost+found" 
                    }
                    .filter { it.isFile }
                    .filter { file ->
                        file.extension.equals("pdf", ignoreCase = true) ||
                        file.extension.equals("txt", ignoreCase = true) ||
                        file.extension.equals("csv", ignoreCase = true)
                    }
                    .forEach { file ->
                        fileCount++
                        if (fileCount % 5 == 0) { // Her 5 dosyada bir UI'ı güncelle
                            onProgress?.invoke(fileCount)
                        }
                        
                        try {
                            when (file.extension.lowercase()) {
                                "txt", "csv" -> scanTextFile(file, detectedSubscriptions)
                                "pdf" -> scanPdfFile(file, detectedSubscriptions)
                            }
                        } catch (e: Exception) {
                            // Dosya okuma hatası, devam et
                        }
                    }
                
                // Son durumu bildir
                onProgress?.invoke(fileCount)
                
            } catch (e: Exception) {
                e.printStackTrace()
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
        val charsetsToTry = listOf(
            Charsets.UTF_8,
            java.nio.charset.Charset.forName("windows-1254"),
            java.nio.charset.Charset.forName("ISO-8859-9")
        )
        
        var lines: List<String>? = null
        for (charset in charsetsToTry) {
            try {
                lines = file.readLines(charset)
                break
            } catch (e: Exception) {
                // Ignore and try next charset
            }
        }
        
        if (lines != null) {
            for (line in lines) {
                if (line.isNotBlank()) {
                    processFileContent(line, file.name, results)
                }
            }
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
        
        val amount = SubscriptionDetectionPatterns.detectAmount(content, fallbackToAnyNumber = true) 
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
