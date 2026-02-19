# Firebase Kurulum ve Entegrasyon Rehberi

Bu rehber, Push Notification (Bildirim) gönderebilmek için gerekli olan `serviceAccountKey.json` dosyasını nasıl alacağınızı ve Frontend (Android) tarafında yapmanız gerekenleri anlatır.

## 1. serviceAccountKey.json Dosyasını Alma (Backend İçin)

Backend servisinin Google Firebase sunucularıyla konuşabilmesi için bu yetki dosyasına ihtiyacı vardır.

1.  [Firebase Console](https://console.firebase.google.com/) adresine gidin ve projenizi seçin (Yoksa yeni bir proje oluşturun).
2.  Sol üstteki **Dişli Çark (Ayarlar)** simgesine tıklayın ve **Project settings** (Proje ayarları) seçeneğine gidin.
3.  Üstteki sekmelerden **Service accounts** (Hizmet hesapları) sekmesine tıklayın.
4.  Alt kısımda **Firebase Admin SDK** seçili olduğundan emin olun.
5.  **Generate new private key** (Yeni özel anahtar oluştur) butonuna tıklayın.
6.  Çıkan uyarıda tekrar **Generate key** diyerek onaylayın.
7.  Bilgisayarınıza `.json` uzantılı bir dosya inecek.
8.  Bu dosyanın adını `serviceAccountKey.json` olarak değiştirin ve bana iletin veya projenin `src/main/resources` klasörüne (veya belirlediğimiz bir güvenli konuma) koyacağız.

---

## 2. google-services.json Dosyasını Alma (Android İçin)

Android uygulamasının Firebase servislerine (FCM, Analytics vb.) bağlanabilmesi için bu dosyaya ihtiyacı vardır.

1.  [Firebase Console](https://console.firebase.google.com/) adresine gidin.
2.  Projenizi seçin.
3.  Sol menüdeki **Dişli Çark (Ayarlar)** simgesine tıklayın ve **Project settings**'e gidin.
4.  **General** (Genel) sekmesinde aşağı kaydırarak **Your apps** (Uygulamalarınız) bölümünü bulun.
5.  Listeden Android uygulamanızı (`com.gokhanaytekinn.sdandroid`) seçin.
6.  **google-services.json** butonuna tıklayarak dosyayı indirin.
7.  İndirdiğiniz dosyayı projenizdeki `app/` klasörünün içine kopyalayın (mevcut taslak dosyanın üzerine yazın).

---

## 3. Frontend (Android) Entegrasyonu

Kullanıcının telefonuna bildirim gönderebilmek için telefonun **FCM Token** (Firebase Cloud Messaging Token) bilgisini alıp Backend'e göndermemiz gerekiyor.

### Adım 1: Firebase SDK Kurulumu
Projenizin `build.gradle` dosyalarına Firebase Messaging kütüphanesini ekleyin:

```kotlin
// app/build.gradle.kts
dependencies {
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-messaging")
}
```

### Adım 2: Token Alma ve Backend'e Gönderme
Uygulamanın ana aktivitesinde (örneğin `MainActivity.kt` veya `Login` sonrası) şu kodu çalıştırarak token'ı alıp backend'e göndermeliyiz:

```kotlin
import com.google.firebase.messaging.FirebaseMessaging
import android.util.Log

fun getAndSendFCMToken() {
    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
        if (!task.isSuccessful) {
            Log.w("FCM", "Fetching FCM registration token failed", task.exception)
            return@addOnCompleteListener
        }

        // 1. Token'ı al
        val token = task.result
        Log.d("FCM", "Token: $token")

        // 2. Token'ı Backend'e gönder
        sendTokenToBackend(token)
    }
}

fun sendTokenToBackend(token: String) {
    // Burası Retrofit veya kendi API servisinizi kullanarak Backend'e istek atacağınız yerdir.
    // Örnek Endpoint: POST /api/users/fcm-token
    // Body: { "token": "cihaz_token_degeri_buraya" }
    
    /* 
    apiService.updateFcmToken(UpdateTokenRequest(token)).enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            Log.d("API", "Token sent successfully")
        }
        override fun onFailure(call: Call<Void>, t: Throwable) {
            Log.e("API", "Failed to send token", t)
        }
    })
    */
}
```

### Adım 3: Manifest İzni
`AndroidManifest.xml` dosyanızda internet izni olduğundan emin olun (zaten vardır ama kontrol etmekte fayda var):

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

---

---

## 4. Test Bildirimi Gönderme

Entegrasyonu doğrulamak için Firebase Console üzerinden test bildirimi gönderebilirsiniz:

1.  [Firebase Console](https://console.firebase.google.com/)'a gidin ve projenizi seçin.
2.  Sol menüde **Engage** (Etkileşim) başlığı altındaki **Messaging** (Mesajlaşma) sekmesine tıklayın.
3.  **Create your first campaign** (İlk kampanyanızı oluşturun) butonuna basın ve **Firebase Device Highlights** (eskiden Firebase Cloud Messaging) seçeneğini işaretleyip **Create** deyin.
4.  **Notification title** ve **Notification text** kısımlarına test mesajınızı yazın.
5.  Sağ taraftaki **Send test message** (Test mesajı gönder) butonuna tıklayın.
6.  Sizden bir **FCM registration token** isteyecektir.
    - Android Studio'daki **Logcat** sekmesini açın.
    - Arama kısmına `FCM` veya `Token:` yazın. 
    - Uygulama açıldığında loglanan token değerini kopyalayıp Firebase Console'daki alana yapıştırın.
7.  **Test** butonuna basın. Telefonunuza (veya emülatöre) bildirim gelecektir.

---

## Özet
1.  **Backend** için `serviceAccountKey.json` dosyasını indirin.
2.  **google-services.json** dosyasını `app/` dizinine koyun.
3.  **Frontend** uygulamasını çalıştırın, Logcat'ten token'ı alıp Firebase Console üzerinden test edin.
