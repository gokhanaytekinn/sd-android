# Android Uygulaması (Frontend) Changelog

Tüm değişiklikler ve versiyon notları tarih sırasına göre aşağıda özetlenmiştir.

## [2026-03-03]
- **Feature/Refactor:** Abonelik ekleme ve düzenleme (Add & Edit Subscription) ekranlarında tam tarih (g/a/y) kullanmak yerine sadece **Gün** ve **Ay** (Yıllık abonelikler için) seçmeye olanak sağlayan yapıya geçildi. UI, ViewModels ve API Request DTO'ları tamamen senkronize edildi (`billingDay`, `billingMonth` alanları eklendi).
- **UI/UX:** Abonelik detay ekranındaki gereksiz "Ödeme Geçmişi (Payment History)" sekmesi kaldırılarak arayüz sadeleştirildi.
- **Bug Fix:** Premium paket seçildiğinde ödeme popup ekranı (Billing Flow) gelmesi yerine yanlışlıkla ayarlar ekranına yönlendirmesine sebep olan UI hatası onarıldı (`launchBillingFlow` eklendi).
- **Bug Fix:** Çeşitli Kotlin model (Type Mismatch) ve UI kütüphanesi referans (Unresolved reference) derleme hataları giderildi.

## [2026-03-01]
- **Bug Fix:** Sadece Release (yayın) build’inde ortaya çıkan "Google Login" yetkilendirme hatası; SHA-1 fingerprint ve Firebase/Google Cloud Console yapılandırmalarıyla düzeltildi.
- **Bug Fix:** Network Security Policy sebebiyle (Cleartext communication) dış adrese/port'a (187.124.16.135) HTTP üzerinden çıkılamama hatası erişim izni ayarlarıyla çözüldü.

## [2026-02-27]
- **Bug Fix:** Proje genelindeki dil dosyalarında (`strings.xml`) oluşan biçimlendirme hataları ve yanlış kaçış (escape sequences) karakterlerinden kaynaklı derleme hatalarının tamamı temizlendi.
- **UI/UX:** Alt gezinme (Bottom Navigation) tuşlarında ve diğer clickable ikonlarda tıklama sırasında beliren yarı saydam/gri dikdörtgen dalgalanma (ripple effect) iptal edildi, daha akıcı ve estetik hale getirildi.

## [2026-02-26]
- **UI/UX:** Kayan işlem düğmesi (FAB) ve menüsü için dönme (rotate), animasyonlu açılış/staggered items ve arka planı karartma (dimming) gibi pürüzsüz etkileşim animasyonları projeye kazandırıldı. Kritik ek layout sorunları giderildi.
