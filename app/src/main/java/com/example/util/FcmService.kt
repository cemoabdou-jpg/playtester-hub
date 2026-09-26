package com.example.util

import android.content.Context
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FcmService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM Registration Token generated: $token")
        saveFcmToken(applicationContext, token)
        subscribeToDefaultTopicsSafely()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "FCM Message Received from: ${remoteMessage.from}")

        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: "📢 إشعار جديد من PlayTester Hub"

        val body = remoteMessage.notification?.body
            ?: remoteMessage.data["body"]
            ?: remoteMessage.data["message"]
            ?: "يتوفر تحديث جديد أو طلب اختبار في المنصة"

        val type = remoteMessage.data["type"] ?: "GENERAL"

        val formattedTitle = when (type) {
            "APP_UPDATE" -> "🚀 تحديث تطبيق جديد متوفر!"
            "TEST_REQUEST" -> "🧪 طلب اختبار تطبيق جديد"
            "READINESS_REPORT" -> "🎉 اكتمال فترة الـ 14 يوماً"
            else -> title
        }

        NotificationHelper.showSystemNotification(
            context = applicationContext,
            title = formattedTitle,
            message = body
        )
    }

    companion object {
        private const val TAG = "FcmService"
        private const val PREFS_NAME = "playtester_fcm_prefs"
        private const val KEY_FCM_TOKEN = "fcm_token"

        fun saveFcmToken(context: Context, token: String) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString(KEY_FCM_TOKEN, token).apply()
        }

        fun getFcmToken(context: Context): String? {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getString(KEY_FCM_TOKEN, null)
        }

        fun initializeFcm(context: Context) {
            try {
                // Ensure a fallback token is stored locally for push notification routing
                if (getFcmToken(context).isNull_or_empty()) {
                    saveFcmToken(context, "fcm_dev_token_${System.currentTimeMillis()}")
                }

                val firebaseApp = try {
                    com.google.firebase.FirebaseApp.getInstance()
                } catch (e: Exception) {
                    null
                }

                val gcmSenderId = firebaseApp?.options?.gcmSenderId

                // Only invoke FCM cloud registration if a valid, non-dummy GCM sender ID is configured
                val isSenderIdValid = !gcmSenderId.isNullOrEmpty() &&
                        gcmSenderId != "1234567890" &&
                        !gcmSenderId.contains("dummy", ignoreCase = true)

                val messaging = try {
                    FirebaseMessaging.getInstance()
                } catch (e: Exception) {
                    null
                }

                if (messaging != null) {
                    // Keep auto-init disabled by default unless valid registration token is confirmed
                    messaging.isAutoInitEnabled = false

                    if (isSenderIdValid) {
                        messaging.token.addOnCompleteListener { task ->
                            if (task.isSuccessful && !task.result.isNullOrEmpty()) {
                                val token = task.result
                                Log.d(TAG, "FCM Token retrieved successfully: $token")
                                saveFcmToken(context, token)
                                messaging.isAutoInitEnabled = true
                                subscribeToDefaultTopicsSafely()
                            } else {
                                Log.d(TAG, "FCM token registration unavailable: ${task.exception?.message}")
                                messaging.isAutoInitEnabled = false
                                clearFcmTopicQueue(context)
                            }
                        }
                    } else {
                        Log.d(TAG, "FCM cloud registration skipped (Local notification mode active)")
                        clearFcmTopicQueue(context)
                    }
                } else {
                    clearFcmTopicQueue(context)
                }
            } catch (e: Exception) {
                Log.d(TAG, "FirebaseMessaging initialization handled safely: ${e.message}")
            }
        }

        fun clearFcmTopicQueue(context: Context) {
            try {
                listOf(
                    "com.google.android.gms.appid",
                    "com.google.firebase.messaging",
                    "fcm_topic_preferences"
                ).forEach { name ->
                    try {
                        context.getSharedPreferences(name, Context.MODE_PRIVATE).edit().clear().apply()
                    } catch (_: Exception) {}
                }
            } catch (e: Exception) {
                Log.d(TAG, "FCM topic queue clear handled: ${e.message}")
            }
        }

        private fun subscribeToDefaultTopicsSafely() {
            try {
                val messaging = FirebaseMessaging.getInstance()
                messaging.subscribeToTopic("all_users")
                messaging.subscribeToTopic("testing_requests")
                messaging.subscribeToTopic("app_updates")
            } catch (e: Exception) {
                Log.d(TAG, "Topic subscription skipped: ${e.message}")
            }
        }
    }
}

private fun String?.isNull_or_empty(): Boolean = this == null || this.trim().isEmpty()
