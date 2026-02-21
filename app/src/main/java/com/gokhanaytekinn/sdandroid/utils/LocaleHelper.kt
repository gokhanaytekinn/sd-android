package com.gokhanaytekinn.sdandroid.utils

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleHelper {
    
    fun setLocale(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        
        return context.createConfigurationContext(config)
    }
    
    fun getLanguageName(languageCode: String): String {
        return when (languageCode) {
            "en" -> "English"
            "tr" -> "Türkçe"
            "es" -> "Español"
            "ru" -> "Русский"
            "zh" -> "简体中文"
            "fr" -> "Français"
            else -> "English"


        }
    }
}
