# ProGuard rules for SD Android

# Preserve Generic Signatures (Fixes ParameterizedType issues in Retrofit/Gson)
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses

# Retrofit 2 rules
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes RuntimeVisibleAlphaAnnotations, RuntimeVisibleParameterAnnotations

# OkHttp 3 rules
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Gson rules
-keep class com.google.gson.** { *; }
-keep class com.google.gson.reflect.TypeToken
-keep class * extends com.google.gson.TypeAdapter

# Keep Data Models (Prevents field renaming for JSON mapping)
-keep class com.gokhanaytekinn.sdandroid.data.model.** { *; }

# Keep App ID and metadata
-keepclassmembers class com.gokhanaytekinn.sdandroid.BuildConfig { *; }

# Credential Manager
-keep class androidx.credentials.** { *; }
-keep class com.google.android.libraries.identity.googleid.** { *; }
