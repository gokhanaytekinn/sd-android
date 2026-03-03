# Reklam Entegrasyonu Sonrası Yapılacaklar Rehberi

Reklamların uygulamada gerçek kullanıcılar tarafından görülebilmesi ve gelir elde edebilmeniz için aşağıdaki adımları tamamlamanız gerekmektedir.

## 1. AdMob Hesabı Oluşturma
- [Google AdMob](https://admob.google.com/) adresine gidin ve bir hesap oluşturun.

## 2. Uygulama Kaydı
- AdMob panelinden "Uygulamalar" -> "Uygulama Ekle" seçeneğine tıklayın.
- Android platformunu seçin ve uygulamanızı kaydedin.
- Kayıt sonrası size bir **Uygulama Kimliği (App ID)** verilecek (Örn: `ca-app-pub-xxxxxxxxxxxxxxxx~yyyyyyyyyy`).

## 3. Reklam Birimleri Oluşturma
Sizin için iki adet reklam alanı hazırladım, bunlar için panelden birim oluşturmalısınız:

### A. Banner Reklam (Ekranın Altındaki Alan)
- "Reklam Birimi Ekle" -> "Banner" seçeneğini seçin.
- Bir isim verin (Örn: `Dashboard_Bottom_Banner`) ve birimi oluşturun.
- Size bir **Reklam Birimi Kimliği (Ad Unit ID)** verilecek (Örn: `ca-app-pub-xxxxxxxxxxxxxxxx/zzzzzzzzzz`).

### B. Native Reklam (Liste İçindeki Alan)
- "Reklam Birimi Ekle" -> "Yerel Gelişmiş (Native Advanced)" seçeneğini seçin.
- Bir isim verin (Örn: `Dashboard_List_Native`) ve tasarımı kaydetmeden ilerleyin (kod tarafında biz yönetiyoruz).
- Size bir **Reklam Birimi Kimliği (Ad Unit ID)** verilecek.

## 4. Kod İçindeki Kimlikleri Güncelleme

### Uygulama Kimliği (App ID)
- `AndroidManifest.xml` dosyasındaki aşağıdaki satırı kendi Uygulama Kimliğinizle değiştirin:
```xml
<meta-data
    android:name="com.google.android.gms.ads.APPLICATION_ID"
    android:value="BURAYA_AD_MOB_APP_ID_GELECEK"/>
```

### Reklam Birim Kimlikleri (Ad Unit IDs)
- `AdMobComponents.kt` dosyasındaki test ID'lerini kendi birim ID'lerinizle değiştirin:
    - `BannerAdView` içindeki `adUnitId` parametresini güncelleyin.
    - `NativeStyleAdView` içindeki `adUnitId` parametresini güncelleyin.

## 5. Ödeme ve Onay
- AdMob panelinden ödeme bilgilerinizi girin.
- Uygulamanızın Google Play Store'da yayında olması, reklamların tam kapasiteyle gösterilmesi için önemlidir (yayınlanmasa da test edilebilir).

> [!IMPORTANT]
> Test aşamasında mutlaka kodda bıraktığım test ID'lerini kullanmaya devam edin. Kendi reklamlarınıza tıklamak AdMob hesabınızın askıya alınmasına neden olabilir.
