package com.example

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log

class PlayTesterApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Global uncaught crash logging
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("PLAYTESTER_CRASH", "FATAL CRASH on thread ${thread.name}: ${throwable.message}", throwable)
            defaultHandler?.uncaughtException(thread, throwable)
        }

        // Initialize Firebase
        try {
            com.google.firebase.FirebaseApp.initializeApp(this)
        } catch (e: Throwable) {
            Log.e("PLAYTESTER_APP", "FirebaseApp init: ${e.message}")
        }


        // Initialize Theme Manager
        try {
            com.example.ui.theme.ThemeManager.init(this)
        } catch (e: Throwable) {
            Log.e("PLAYTESTER_APP", "ThemeManager init error: ${e.message}")
        }

        // Notifications channel
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    "playtester_channel",
                    "PlayTester Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Daily reminders and testing updates"
                }
                val manager = getSystemService(NotificationManager::class.java)
                manager?.createNotificationChannel(channel)
            }
        } catch (e: Throwable) {
            Log.e("PLAYTESTER_APP", "Notification channel error: ${e.message}")
        }
    }
}
