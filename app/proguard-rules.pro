# Add project specific ProGuard rules here.
# By default, the flags in this file are applied to all build types.
#
# You can find general ProGuard rules for popular libraries on ProGuard's website.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this if you want to retain annotations and generics in your code
#-keepattributes Signature

# Retain Parcelable constructs
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}