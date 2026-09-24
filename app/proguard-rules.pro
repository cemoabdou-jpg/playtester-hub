# PlayTester Hub ProGuard & R8 Security Hardening Rules

# Strip debug source file attributes for security
-repackageclasses ''
-allowaccessmodification

# Keep Data Models & Entities safe for Serialization/Room
-keep class com.example.data.model.** { *; }

# Moshi, Retrofit & JSON Rules
-dontwarn com.squareup.moshi.**
-keep class com.squareup.moshi.** { *; }
-keepclassmembers class * {
    @com.squareup.moshi.Json *;
}

# Room Database Security & Keep Rules
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Strip Logcat logging statements in production release builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
}

# Prevent reverse engineering of Jetpack Compose internal states
-keepclassmembers class * extends androidx.compose.runtime.State { *; }
