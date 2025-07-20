# Add project specific ProGuard rules here.
# By default, the flags in this file are applied to all build types.

# --- General Kotlin Rules ---
# Keep Kotlin's metadata annotation. This is CRUCIAL for reflection-based libraries.
-keep,allowobfuscation,allowshrinking class kotlin.Metadata
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# --- Gson Rules (Comprehensive & Definitive) ---
# Keep all data model classes and their members (fields and methods).
-keep public class com.dhobikart.app.models.** { *; }
-keepclassmembers public class com.dhobikart.app.models.** { *; }

# Keep attributes needed by Gson for serialization/deserialization.
-keepattributes Signature, InnerClasses, *Annotation*

# Keep custom TypeAdapter and Factory classes.
-keep class com.dhobikart.app.data.MyTypeAdapterFactory { *; }
-keep class com.dhobikart.app.data.LoginResponseTypeAdapter { *; }
-keep class * extends com.google.gson.TypeAdapter

# --- Retrofit & OkHttp Rules ---
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }
-keepclassmembers interface com.dhobikart.app.data.ApiService { *; }

-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *; }
-keep interface com.squareup.okhttp3.** { *; }

-dontwarn okio.**
-keep class okio.** { *; }

# --- Android Specific Rules ---
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class **.R$* {
    public static <fields>;
}
