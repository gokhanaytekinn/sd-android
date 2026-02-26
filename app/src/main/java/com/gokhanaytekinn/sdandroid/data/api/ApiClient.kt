package com.gokhanaytekinn.sdandroid.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val languageInterceptor = okhttp3.Interceptor { chain ->
        val original = chain.request()
        val language = java.util.Locale.getDefault().language
        val request = original.newBuilder()
            .header("Accept-Language", language)
            .build()
        chain.proceed(request)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(languageInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(NetworkConfig.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val subscriptionApi: SubscriptionApiService by lazy {
        retrofit.create(SubscriptionApiService::class.java)
    }
    
    val authApi: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }
}
