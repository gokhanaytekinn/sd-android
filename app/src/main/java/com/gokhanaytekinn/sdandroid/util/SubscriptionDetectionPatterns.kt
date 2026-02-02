package com.gokhanaytekinn.sdandroid.util

/**
 * Abonelik tespit pattern'lerini içeren sınıf
 * SMS ve dosyalardaki abonelik bilgilerini tespit etmek için kullanılır
 */
object SubscriptionDetectionPatterns {
    
    // Yaygın abonelik servisleri
    val servicePatterns = mapOf(
        "Netflix" to listOf("netflix", "netflix premium", "netflix standart"),
        "Spotify" to listOf("spotify", "spotify premium", "spotify duo", "spotify family"),
        "YouTube Premium" to listOf("youtube premium", "youtube music", "yt premium"),
        "Amazon Prime" to listOf("amazon prime", "prime video", "prime üyelik"),
        "Disney+" to listOf("disney plus", "disney+", "disneyplus"),
        "Apple Music" to listOf("apple music"),
        "Apple TV+" to listOf("apple tv+", "apple tv plus"),
        "iCloud" to listOf("icloud", "icloud+", "icloud storage"),
        "Adobe" to listOf("adobe creative cloud", "adobe photoshop", "adobe"),
        
        // Türk servisleri
        "BluTV" to listOf("blutv", "blu tv"),
        "Exxen" to listOf("exxen"),
        "TOD" to listOf("tod", "turk on demand"),
        "Gain" to listOf("gain"),
        "Mubi" to listOf("mubi"),
        "Fizy" to listOf("fizy"),
        
        // Telekom operatörleri
        "Turkcell" to listOf("turkcell", "platinum", "gnc", "bip"),
        "Vodafone" to listOf("vodafone", "vodafone tv"),
        "Turk Telekom" to listOf("türk telekom", "turk telekom", "tt", "tivibu"),
        
        // Diğer
        "Google One" to listOf("google one", "google storage"),
        "Microsoft 365" to listOf("microsoft 365", "office 365", "onedrive"),
        "Dropbox" to listOf("dropbox"),
    )
    
    // Abonelik ile ilgili Türkçe anahtar kelimeler
    val subscriptionKeywords = listOf(
        "abonelik",
        "abone",
        "üyelik",
        "aylık",
        "yıllık", 
        "otomatik ödeme",
        "düzenli ödeme",
        "periyodik ödeme",
        "yenileme",
        "tahsilat",
        "fatura"
    )
    
    // Tutar pattern'leri - Türk Lirası
    val amountPatterns = listOf(
        Regex("""(\d+[.,]\d{2})\s*(?:TL|₺|TRY)"""),  // 99.99 TL veya 99,99₺
        Regex("""(?:TL|₺|TRY)\s*(\d+[.,]\d{2})"""),  // TL 99.99 veya ₺99,99
        Regex("""(\d+)\s*(?:TL|₺|TRY)"""),           // 99 TL
    )
    
    // Tarih pattern'leri
    val datePatterns = listOf(
        Regex("""(\d{1,2})[./](\d{1,2})[./](\d{2,4})"""),  // 01.02.2026 veya 1/2/26
        Regex("""(\d{1,2})\s+(Ocak|Şubat|Mart|Nisan|Mayıs|Haziran|Temmuz|Ağustos|Eylül|Ekim|Kasım|Aralık)\s+(\d{4})""", RegexOption.IGNORE_CASE),
    )
    
    // Fatura döngüsü pattern'leri
    val billingCyclePatterns = mapOf(
        "MONTHLY" to listOf("aylık", "her ay", "ayda bir", "monthly"),
        "YEARLY" to listOf("yıllık", "her yıl", "yılda bir", "yearly", "annual"),
        "WEEKLY" to listOf("haftalık", "her hafta", "weekly"),
    )
    
    // Banka bildirimi pattern'leri
    val bankPatterns = listOf(
        "hesabınızdan",
        "kartınızdan",
        "çekim yapılmıştır",
        "ödeme alınmıştır",
        "tahsil edilmiştir",
        "otomatik ödeme talimatı",
    )
    
    /**
     * Verilen metinde servis adı tespit eder
     */
    fun detectServiceName(text: String): String? {
        val lowerText = text.lowercase()
        
        for ((serviceName, patterns) in servicePatterns) {
            for (pattern in patterns) {
                if (lowerText.contains(pattern.lowercase())) {
                    return serviceName
                }
            }
        }
        
        return null
    }
    
    /**
     * Verilen metinde tutar tespit eder
     */
    fun detectAmount(text: String): Double? {
        for (pattern in amountPatterns) {
            val match = pattern.find(text)
            if (match != null) {
                val amountStr = match.groupValues.getOrNull(1) ?: continue
                return amountStr.replace(",", ".").toDoubleOrNull()
            }
        }
        return null
    }
    
    /**
     * Verilen metinde fatura döngüsü tespit eder
     */
    fun detectBillingCycle(text: String): String? {
        val lowerText = text.lowercase()
        
        for ((cycle, patterns) in billingCyclePatterns) {
            for (pattern in patterns) {
                if (lowerText.contains(pattern)) {
                    return cycle
                }
            }
        }
        
        return "MONTHLY" // Default
    }
    
    /**
     * Verilen metnin abonelik ile ilgili olup olmadığını kontrol eder
     */
    fun isSubscriptionRelated(text: String): Boolean {
        val lowerText = text.lowercase()
        
        // Servis adı var mı?
        if (detectServiceName(text) != null) return true
        
        // Abonelik anahtar kelimesi var mı?
        for (keyword in subscriptionKeywords) {
            if (lowerText.contains(keyword)) return true
        }
        
        // Banka bildirimi pattern'i var mı?
        for (pattern in bankPatterns) {
            if (lowerText.contains(pattern)) {
                // Banka bildirimi + tutar varsa muhtemelen abonelik
                if (detectAmount(text) != null) return true
            }
        }
        
        return false
    }
}
