package com.gokhanaytekinn.sdandroid.ui.components

import android.content.Context
import android.util.Log
import com.gokhanaytekinn.sdandroid.BuildConfig
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class InterstitialAdManager(private val context: Context) {

    private var interstitialAd: InterstitialAd? = null
    var isAdLoaded = false
        private set

    fun loadAd() {
        val adRequest = AdRequest.Builder().build()
        
        InterstitialAd.load(
            context,
            BuildConfig.ADMOB_INTERSTITIAL_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isAdLoaded = true
                    Log.d("AdMob", "Interstitial Ad yüklendi.")

                    interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            // Kullanıcı reklamı kapattığında
                            interstitialAd = null
                            isAdLoaded = false
                            // Sonraki işlem için yenisini hazırla
                            loadAd()
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            // Gösterim hatası
                            interstitialAd = null
                            isAdLoaded = false
                        }

                        override fun onAdShowedFullScreenContent() {
                            // Reklam tam ekran gösterildiğinde
                            interstitialAd = null
                            isAdLoaded = false
                        }
                    }
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e("AdMob", "Interstitial Ad yüklenemedi: \${adError.message}")
                    interstitialAd = null
                    isAdLoaded = false
                }
            }
        )
    }

    fun showAd(activity: android.app.Activity, onAdDismissed: () -> Unit) {
        if (interstitialAd != null) {
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    isAdLoaded = false
                    onAdDismissed()
                    loadAd() // Yenisini hazırla
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    interstitialAd = null
                    isAdLoaded = false
                    onAdDismissed()
                }

                override fun onAdShowedFullScreenContent() {
                    interstitialAd = null
                    isAdLoaded = false
                }
            }
            interstitialAd?.show(activity)
        } else {
            // Reklam henüz yüklenmediyse doğrudan işlemi bitmiş say (kullanıcıyı bekletmemek için)
            onAdDismissed()
            loadAd()
        }
    }
}
