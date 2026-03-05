# 1. Proje Başlığı

**Sub Tracker Android App**

# 2. Proje Açıklaması

> [!NOTE]
> Bu uygulamanın kodlanması, MVVM mimari kurgusu ve Jetpack Compose tasarımlarının tamamı %100 oranında Yapay Zeka (AI) kullanılarak hayata geçirilmiştir.

**Sub Tracker**, kullanıcıların tüm tekrarlayan aboneliklerini, aidatlarını ve düzenli ödemelerini kolayca takip edip yönetebileceği modern bir Android uygulamasıdır. Güncel Kotlin ve Jetpack Compose teknolojileri ile inşa edilmiş, üst düzey kullanıcı deneyimi sunan arayüzü sayesinde harcamalarınız artık tam kontrolünüz altında.

# 3. Özellikler

- **Gelişmiş Abonelik Takibi:** Aktif ve pasif tüm aboneliklerinizi tek bir çatı altında gruplayıp listeleme imkanı.
- **Hatırlatıcılar ve Bildirimler:** Ödeme tarihlerinden önce gönderilen otomatik bildirimler sayesinde gecikme faizi ve istenmeyen kesinti riskini ortadan kaldırma.
- **Çoklu Dil Desteği:** Kullanıcıların konforu için İngilizce, Türkçe, Almanca, Fransızca, İspanyolca gibi birçok dilde %100 yerelleştirilmiş dil desteği.
- **Ücretsiz ve Premium Seçenekler:** Uygulama içi ücretsiz özellikler ve sınırların ötesine geçmek isteyenler için Premium plan alternatifleri.

# 4. Teknolojiler

Mobil İstemci, Android ekosisteminin en güncel ve en kararlı teknolojilerine dayanmaktadır:

- **Dil:** Kotlin
- **Arayüz ve Bileşenler:** Android SDK, Jetpack Compose, Material Design 3
- **Ağ ve Asenkron İşlemler:** Retrofit, OkHttp, Kotlin Coroutines, Flow
- **Bağımlılık Enjeksiyonu:** Hilt (Dagger-Hilt)
- **Gelir Modeli ve Analitik:** Google Play Faturalandırma Kütüphanesi, Google AdMob, Firebase

# 5. Mimari

**İstemci (Mobil) Mimarisi:**
Uygulama, **MVVM (Model-View-ViewModel)** tasarım deseni üzerine kurulmuştur ve **Temiz Mimari (Clean Architecture)** kurallarını referans alır:
- **Arayüz Katmanı (Jetpack Compose):** Sadece arayüzün çizildiği, veriden bağımsız olan en dış katman.
- **ViewModel Katmanı:** Ekranların durumlarını tutan ve kullanıcı etkileşimlerini servis katmanına bağlayan katman.
- **Veri (Repository) Katmanı:** Uzak bir sunucudan (API) gelen verilerin toplanması, modellenmesi ve uygulama geneline asenkron aktarılmasından sorumlu olan katman.

# 6. Proje Yapısı

Projenin `app/src/main/` içerisindeki paket mimarisi:

```
com.gokhanaytekinn.sdandroid/
├── ui/              # Jetpack Compose ekranları, temalar, bileşenler
├── viewmodel/       # MVVM Durum (State) ve ViewModel sınıfları
├── data/            # Retrofit servisleri, ağ verisi modelleri, Repository sınıfları
├── di/              # Hilt Enjeksiyon modülleri (Ağ Modülü vs.)
├── model/           # Uygulama içi veri yapıları
└── util/            # Sabitler, para birimi biçimlendirici vb. yardımcı araçlar
```

# 7. Kurulum

Projeyi kendi ortamınızda test edebilmeniz için ihtiyacınız olan adımlar:

1. Android Studio'nun güncel sürümünü bilgisayarınıza kurun.
2. Bu depoyu indirin ve `sd-android` klasörünü Android Studio üzerinden içe aktarın (Aç seçeneğiyle).
3. Gradle senkronizasyonunun tamamlanmasını bekleyin.
4. "Çalıştır (Run)" tuşuna basarak uygulamanızı fiziksel cihazda ya da simülatörde test edebilirsiniz. (Minimum SDK: 26+)

# 8. Yapılandırma

Sorunsuz bir kurulum için yapılandırma gereksinimleri:

- **Firebase Yapılandırması:** Analitik ve yapılandırmalar için `app/` dizininin içine geçerli bir `google-services.json` dosyasının konulması zorunludur. (Proje içerisinde mevcuttur).
- **Arka Uç Adresi (URL):** Uygulamanın bağlı kalacağı servisin IP adresi API servis konfigürasyonu içerisindeki sabitten (örn. `Constants.BASE_URL`) ayarlanmalıdır.

# 9. Uygulama Ekranları

Uygulamanın arayüz tasarımı, modern ve akıcı bir kullanıcı deneyimi hedeflenerek geliştirilmiştir. Uygulamanın çeşitli özelliklerini (Giriş Yap, Özel Listelemeler, Profil, Kontrol Paneli vs.) yansıtan ekran görüntüleri, proje ana dizinindeki `Ekranlar/` klasöründe yer almaktadır.

# 10. Gelir Modeli

Proje, hem ücretsiz temel sürüme hem de ücretli ekstra özelliklere sahip bir iş modeli üzerine kurulmuştur:
- **Reklamlar (AdMob):** Ücretsiz kullanıcılar için uygulama esnasında geçiş reklamları sunulmaktadır.
- **Uygulama İçi Satın Alma:** Kullanıcıların reklamlardan kurtulmasını sağlayan, **Google Play Faturalandırma** desteği entegre edilmiş Abonelik planları bulunmaktadır.

# 11. Arka Uç Entegrasyonu

Uygulama, güvenli veri alışverişini Spring Boot tabanlı merkezi bir arka uçla ile sağlar:
- HTTP REST protokolü üzerinden Retrofit kullanılarak `application/json` formatıyla haberleşilir.
- Kullanıcıya tahsis edilmiş JSON Web Token (JWT) anahtarı, `NetworkInterceptor` aracılığı ile tüm kimlik doğrulaması gereken isteklere dahil edilir.

# 12. Test Durumu

Kararlı sürümlerin müşterilere ulaştırılması için:
- **Kapalı Test:** Uygulama şu anda Google Play Console üzerinden, farklı cihaz konfigürasyonlarına sahip kullanıcılardan oluşan bir gruba kapalı test şeklinde sunulmaktadır. Sorunların giderilmesini takiben açık üretime geçecektir.

# 13. Yol Haritası

Uygulamanın kullanıcı tarafında gelişmesi hedeflenen modülleri:
- Gelişmiş ana ekran istatistiksel görselleştirme eklentileri (Kategori grafik sınırları vs).
- Sık kullanılan abonelik firmalarının logolarını kapsayan daha geniş simge seti (Spotify, Netflix vb.).
- Kullanıcıların kendi abonelik listelerini aile üyeleriyle ortak tek havuzda birleştirebileceği "Paylaşımlı Abonelik" ekranları.

# 14. Katkı Sağlama

Projeye katkıda bulunarak ekibi genişletmek isterseniz:
1. Depoyu kendi Github hesabınıza atın ("Fork").
2. Dal oluşturun (`git checkout -b ozellik/MukemmelHizmet`).
3. Değişiklikleri kayıt altına alın (`git commit -m 'Mukemmel Hizmet özelliği getirildi'`).
4. Bu depoya "Değişiklik Birleştirme İsteği (Pull Request)" oluşturarak birleşme teklifi yollayın.

# 15. Lisans

Telif Hakkı (c) 2026 Gökhan Aytekin

Tüm hakları saklıdır.

Bu depo yalnızca görüntüleme ve eğitim amaçlı olarak herkese açık bir şekilde paylaşılmıştır.

Yazarın açık yazılı izni olmadan şunları yapamazsınız:
- Bu kodu üretim ortamında kullanmak
- Kodun önemli kısımlarını kopyalamak
- Kodu yeniden dağıtmak
- Kodu değiştirmek ve dağıtmak

Bu kodu kullanmak isterseniz, lütfen yazarla iletişime geçin.

# 16. İletişim

Geri dönüşleriniz ve ticari işbirlikleri için:
- Bu Github sayfasındaki "Sorunlar (Issues)" bölümünü kullanarak takip kartı oluşturabilirsiniz.
- Veya geliştirici yetkilisine doğrudan e-mail bağlantıları aracılığıyla ulaşabilirsiniz.