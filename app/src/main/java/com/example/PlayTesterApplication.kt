package com.example

import android.app.Application
import android.util.Log
import com.example.util.NotificationHelper
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

class PlayTesterApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        initializeFirebaseSafely()
        NotificationHelper.createNotificationChannel(this)
    }

    private fun initializeFirebaseSafely() {
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                val options = FirebaseOptions.Builder()
                    .setProjectId("playtester-hub-61bd6")
                    .setApplicationId("1:170455936881:android:29bcec6a7e7d0f03c410d0")
                    .setApiKey("AIzaSyCAjx_Z-syoM_CfDnlQqzdgYcAvmqe7Y0o")
                    .setStorageBucket("playtester-hub-61bd6.firebasestorage.app")
                    .build()
                FirebaseApp.initializeApp(this, options)
                Log.d("PlayTesterApp", "FirebaseApp initialized successfully with explicit options.")
            } else {
                Log.d("PlayTesterApp", "FirebaseApp already initialized automatically.")
            }
        } catch (e: Exception) {
            Log.e("PlayTesterApp", "Error initializing Firebase: ${e.message}", e)
        }
    }

    companion object {
        lateinit var instance: PlayTesterApplication
            private set
    }
}
