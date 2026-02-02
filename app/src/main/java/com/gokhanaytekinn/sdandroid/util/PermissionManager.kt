package com.gokhanaytekinn.sdandroid.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat

/**
 * İzin yönetim sınıfı
 * SMS ve depolama izinlerini yönetir
 */
class PermissionManager(private val context: Context) {
    
    /**
     * SMS okuma izni var mı kontrol eder
     */
    fun hasSmsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_SMS
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * Depolama okuma izni var mı kontrol eder
     */
    fun hasStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_VIDEO
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // Android 12 ve altı
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
    
    /**
     * Tüm gerekli izinler verilmiş mi kontrol eder
     */
    fun hasAllPermissions(): Boolean {
        return hasSmsPermission() && hasStoragePermission()
    }
    
    /**
     * İstenmesi gereken izinlerin listesini döndürür
     */
    fun getRequiredPermissions(): Array<String> {
        val permissions = mutableListOf(Manifest.permission.READ_SMS)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
            permissions.add(Manifest.permission.READ_MEDIA_VIDEO)
        } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        
        return permissions.toTypedArray()
    }
    
    /**
     * Eksik izinlerin listesini döndürür
     */
    fun getMissingPermissions(): Array<String> {
        return getRequiredPermissions().filter { permission ->
            ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()
    }
    
    /**
     * Ayarlar ekranına yönlendirme intent'i oluşturur
     */
    fun getSettingsIntent(): Intent {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        return intent
    }
    
    companion object {
        /**
         * İzin açıklama mesajları
         */
        val SMS_PERMISSION_RATIONALE = """
            SMS izni, cihazınızdaki abonelik bildirimlerini otomatik olarak tespit etmek için gereklidir.
            Bu sayede aboneliklerinizi manuel olarak girmek zorunda kalmazsınız.
        """.trimIndent()
        
        val STORAGE_PERMISSION_RATIONALE = """
            Depolama izni, cihazınızdaki fatura ve abonelik belgelerini taramak için gereklidir.
            Bu sayede PDF ve diğer dosyalardan abonelik bilgisi otomatik olarak tespit edilebilir.
        """.trimIndent()
        
        val PERMISSION_DENIED_MESSAGE = """
            İzin verilmeden cihaz taraması yapılamaz.
            İsterseniz aboneliklerinizi manuel olarak ekleyebilirsiniz.
        """.trimIndent()
        
        val PERMISSION_PERMANENTLY_DENIED_MESSAGE = """
            İzin verilmedi. Lütfen uygulama ayarlarından izinleri etkinleştirin.
        """.trimIndent()
    }
}
