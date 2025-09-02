# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

-dontwarn java.lang.invoke.**
-dontwarn **$$Lambda$*
-dontwarn javax.annotation.**

# Spotify
-dontwarn com.spotify.**
-keep class com.spotify.** { *; }

# RetroFit
-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keep,allowobfuscation,allowshrinking interface retrofit.Callback
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
    <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
    *** rewind();
}

#-dontwarn
#-ignorewarnings

-keepclassmembers enum * { *; }
-keepattributes *Annotation*, Signature, Exception
-keepnames class androidx.navigation.fragment.NavHostFragment
-keep class * extends androidx.fragment.app.Fragment{}
-keepnames class * extends android.os.Parcelable
-keepnames class * extends java.io.Serializable
-keep class com.adgutech.adomusic.remote.api.spotify.** { *; }
-keep class com.adgutech.adomusic.remote.api.spotify.models.** { *; }
-keep class com.adgutech.adomusic.remote.models.** { *; }
-keep class com.adgutech.adomusic.remote.repositories.** { *; }
-keep class com.adgutech.adomusic.remote.ui.fragments.LibraryViewModel
-keep class com.google.android.material.bottomsheet.** { *; }

# TypeToken https://stackoverflow.com/questions/70969756/caused-by-java-lang-runtimeexception-missing-type-parameter
-keep class com.google.gson.reflect.TypeToken
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken
-keep public class * implements java.lang.reflect.Type
-keepattributes AnnotationDefault, RuntimeVisibleAnnotations

-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.OpenSSLProvider