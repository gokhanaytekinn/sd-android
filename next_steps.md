# SD Android Uygulaması Reklam Kurulum Adımları

Bu belge, uygulamanızın AdMob reklam ayarlarını canlıya (Google Play Store) almadan önce tamamlamanız gereken işlemleri adım adım açıklamaktadır.

## Adım 1: AdMob Hesabının Ayarlanması

1.  [Google AdMob](https://apps.admob.com/) web sitesine gidin ve Google hesabınızla giriş yapın.
2.  Sol menüden **Uygulamalar (Apps)** > **Uygulama Ekle (Add App)** seçeneğine tıklayın.
3.  Platform olarak **Android**'i seçin ve uygulamanızın henüz desteklenen bir uygulama mağazasında (Google Play) listelenmediğini belirtin.
4.  Uygulamanıza bir isim verin (Örn: SD Subscriptions) ve ekleyin.

## Adım 2: AdMob Uygulama Kimliğini (App ID) Almak

1.  Uygulamanızı ekledikten sonra sol menüden **Uygulama ayarları (App settings)** bölümüne gidin.
2.  Burada **Uygulama Kimliği (App ID)** değerini göreceksiniz. Bu değer `ca-app-pub-XXXXXXXXXXXXXXXX~YYYYYYYYYY` formatındadır.
3.  Bu değeri kopyalayıp bir kenara not edin.
4.  Uygulamanızın projenize giderek, `sd-android/app/build.gradle.kts` dosyasını açın.
5.  `release` bloğu altındaki `manifestPlaceholders["admobAppId"]` satırını bulun ve mevcut test kimliğini kendi kimliğinizle değiştirin:
    ```kotlin
    release {
        // ...
        manifestPlaceholders["admobAppId"] = "KENDİ_ADMOB_APP_ID'NİZ"
        // ...
    }
    ```

## Adım 3: Reklam Birimlerini (Ad Units) Oluşturmak

Size iki farklı reklam birimi gerekiyor: Biri Banner, diğeri Geçiş (Interstitial) reklamı için.

### Banner Reklam Birimi:

1.  Sol menüden **Reklam birimleri (Ad units)** > **Reklam birimi ekle (Add ad unit)** seçeneğine tıklayın.
2.  **Banner**'ı seçin.
3.  Bir isim verin (Örn: `SD_Banner`) ve reklam birimini oluşturun.
4.  Karşınıza çıkan ekrandaki **Reklam birimi kimliğini (Ad unit ID)** (`ca-app-pub-XXXXXXXXXXXXXXXX/ZZZZZZZZZZ` formatındadır) kopyalayın.
5.  `build.gradle.kts` dosyasında `release` bloğu altındaki `ADMOB_BANNER_ID` alanını bu değerle güncelleyin:
    ```kotlin
    buildConfigField("String", "ADMOB_BANNER_ID", "\"KENDİ_BANNER_AD_UNIT_ID'NİZ\"")
    ```

### Geçiş (Interstitial) Reklam Birimi:

1.  Tekrar **Reklam birimi ekle (Add ad unit)** seçeneğine tıklayıp bu kez **Geçiş (Interstitial)** formatını seçin.
2.  Bir isim verin (Örn: `SD_Interstitial_3Add`) ve reklam birimini oluşturun.
3.  Karşınıza çıkan ekranındaki **Reklam birimi kimliğini (Ad unit ID)** kopyalayın.
4.  `build.gradle.kts` dosyasında `release` bloğu altındaki `ADMOB_INTERSTITIAL_ID` alanını bu değerle güncelleyin:
    ```kotlin
    buildConfigField("String", "ADMOB_INTERSTITIAL_ID", "\"KENDİ_INTERSTITIAL_AD_UNIT_ID'NİZ\"")
    ```

> [!WARNING]
> Test reklam kimlikleri silinmemelidir. `debug` buildType altında her zaman Google'ın sağladığı "Test Kimlikleri" kalmalıdır. Aksi takdirde geliştirme sırasında gerçek reklamlara tıklanırsa hesabınız askıya alınabilir! (Değişiklikleri sadece `release` bloğunda yapın.)

## Adım 4: Geliştirmelerin Test Edilmesi (Developer Test Özelliği)
Test esnasında reklam koşullarını kontrol edebilmeniz için _Ayarlar_ sayfası _Uygulama_ sekmesinin altına geçici bir **Developer: Toggle Premium** seçeneği eklendi.

- **Developer: Toggle Premium AÇIK (True) ise:** Reklam görmemelisiniz (Ne ana sayfada ne abonelik eklemede).
- **Developer: Toggle Premium KAPALI (False) ise:** Ana sayfada banner reklam sürekli görüntülenir, abonelik eklerken ise her yeni kaydın 3.'sünde geçiş reklamı gösterilir.

_Not_: Testi tamamladıktan sonra veya canlıya alırken bu özelliğin arayüzden kaldırılması önerilir. (Kodda `AppSettingsScreen.kt` dosyasından `// Test Premium Toggle` kısmını kolayca kaldırabilirsiniz.)

---
_Her şey tamam! build.gradle.kts'i doğru şekilde doldurduktan ve uygulamanızı Release olarak build ettikten sonra reklamlarınız canlıda aktifleşecektir._
