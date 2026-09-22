package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R

object NotificationHelper {
    const val CHANNEL_ID = "playtester_notifications"
    const val CHANNEL_NAME = "إشعارات PlayTester Hub"
    const val CHANNEL_DESC = "تنبيهات وتذكيرات الاختبار المغلق وقبول المختبرين"
    private const val PREFS_NAME = "playtester_reminder_prefs"
    private const val KEY_DAILY_REMINDER_ENABLED = "daily_reminder_enabled"
    private const val KEY_REMINDER_HOUR = "reminder_hour"

    fun isDailyReminderEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_DAILY_REMINDER_ENABLED, true)
    }

    fun setDailyReminderEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_DAILY_REMINDER_ENABLED, enabled).apply()
    }

    fun triggerDailyTestingReminder(
        context: Context,
        pendingAppsCount: Int,
        currentStreak: Int
    ): Boolean {
        if (!isDailyReminderEnabled(context)) return false

        val title = if (pendingAppsCount > 0) {
            "⏰ تذكير الفحص اليومي ($pendingAppsCount تطبيق بانتظارك!)"
        } else {
            "🔥 ستريك الفحص اليومي مكتمل! ($currentStreak يوم)"
        }

        val message = if (pendingAppsCount > 0) {
            "لديك $pendingAppsCount تطبيق يحتاج إلى فتح وتجربة اليوم لضمان عدم انقطاع شرط الـ 14 يوماً المتواصلة والحفاظ على الستريك $currentStreak يوم."
        } else {
            "رائع! لقد أكملت فحص جميع تطبيقاتك اليومية بنجاح. استمر في التميز ورفع نقاطك ورتبتك في المجتمع!"
        }

        return showSystemNotification(
            context = context,
            title = title,
            message = message,
            notificationId = 14001
        )
    }

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableVibration(true)
                enableLights(true)
                lightColor = android.graphics.Color.parseColor("#00C853")
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showSystemNotification(
        context: Context,
        title: String,
        message: String,
        notificationId: Int = (System.currentTimeMillis() % 100000).toInt()
    ): Boolean {
        createNotificationChannel(context)

        // Check POST_NOTIFICATIONS permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Load app logo bitmap for LargeIcon in notification shade
        val appLogoBitmap: Bitmap? = try {
            BitmapFactory.decodeResource(context.resources, R.drawable.playtester_app_icon_1789977058621)
                ?: BitmapFactory.decodeResource(context.resources, R.drawable.ic_tester_logo)
        } catch (e: Exception) {
            null
        }

        val brandColor = ContextCompat.getColor(context, R.color.play_green)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_playtester)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setColor(brandColor)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)

        if (appLogoBitmap != null) {
            builder.setLargeIcon(appLogoBitmap)
        }

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(notificationId, builder.build())
            return true
        } catch (e: SecurityException) {
            return false
        }
    }
}
