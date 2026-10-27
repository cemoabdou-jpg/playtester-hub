# Google Play R8 / DEX Optimization & Obfuscation Rules

# Optimization & Obfuscation Settings
-repackageclasses
-allowaccessmodification
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Data Models & Room Persistence
-keep class com.example.data.model.** { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
-keep class * extends androidx.room.RoomDatabase { *; }
-keepclassmembers class * {
    @androidx.room.* *;
}
-dontwarn androidx.room.paging.**

# Firebase Firestore & Auth
-keepattributes *Annotation*
-keepclassmembers class * {
    @com.google.firebase.firestore.* *;
}
-dontwarn com.google.firebase.**

# Google Mobile Ads (AdMob)
-keep public class com.google.android.gms.ads.** { public *; }
-keep public class com.google.ads.** { public *; }
-keep class com.google.ads.mediation.** { *; }
-keep class com.google.android.gms.internal.ads.** { *; }
-dontwarn com.google.android.gms.ads.**

# Coil Image Loader
-dontwarn coil.**
-keep class coil.** { *; }

# Coroutines
-dontwarn kotlinx.coroutines.**
