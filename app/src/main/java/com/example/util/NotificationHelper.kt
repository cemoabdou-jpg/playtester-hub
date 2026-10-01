package com.example.util

import android.app.AlarmManager
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
import com.example.data.model.AppNotification
import java.util.Calendar

object NotificationHelper {
    const val CHANNEL_ID = "playtester_notifications"
    const val CHANNEL_NAME = "إشعارات PlayTester Hub"
    const val CHANNEL_DESC = "تنبيهات وتذكيرات الاختبار المغلق وقبول المختبرين"
    const val ACTION_DAILY_REMINDER = "com.example.ACTION_DAILY_REMINDER"
    const val ALARM_REQUEST_CODE = 9901

    private const val PREFS_NAME = "playtester_reminder_prefs"
    private const val KEY_DAILY_REMINDER_ENABLED = "daily_reminder_enabled"
    private const val KEY_REMINDER_HOUR = "reminder_hour"
    private const val KEY_REMINDER_HOURS = "reminder_hours_list"

    fun isDailyReminderEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_DAILY_REMINDER_ENABLED, true)
    }

    fun setDailyReminderEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_DAILY_REMINDER_ENABLED, enabled).apply()
        if (enabled) {
            scheduleAllDailyReminderAlarms(context)
        } else {
            cancelAllDailyReminderAlarms(context)
        }
    }

    fun getReminderHours(context: Context): Set<Int> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val saved = prefs.getString(KEY_REMINDER_HOURS, null)
        if (saved.isNullOrBlank()) {
            val single = prefs.getInt(KEY_REMINDER_HOUR, 20)
            return setOf(9, 14, single)
        }
        return saved.split(",")
            .mapNotNull { it.trim().toIntOrNull() }
            .filter { it in 0..23 }
            .toSet()
            .ifEmpty { setOf(20) }
    }

    fun setReminderHours(context: Context, hours: Set<Int>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val joined = hours.joinToString(",")
        prefs.edit().putString(KEY_REMINDER_HOURS, joined).apply()
        if (isDailyReminderEnabled(context)) {
            scheduleAllDailyReminderAlarms(context)
        }
    }

    fun toggleReminderHour(context: Context, hour: Int): Set<Int> {
        val current = getReminderHours(context).toMutableSet()
        if (current.contains(hour)) {
            if (current.size > 1) {
                current.remove(hour)
            }
        } else {
            current.add(hour)
        }
        setReminderHours(context, current)
        return current
    }

    fun getReminderHour(context: Context): Int {
        val hours = getReminderHours(context)
        return hours.maxOrNull() ?: 20
    }

    fun setReminderHour(context: Context, hour: Int) {
        val current = getReminderHours(context).toMutableSet()
        current.add(hour)
        setReminderHours(context, current)
    }

    fun scheduleAllDailyReminderAlarms(context: Context) {
        if (!isDailyReminderEnabled(context)) return

        val hours = getReminderHours(context)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        for (hour in hours) {
            val intent = Intent(context, DailyReminderReceiver::class.java).apply {
                action = ACTION_DAILY_REMINDER
                putExtra("reminder_hour", hour)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                ALARM_REQUEST_CODE + hour,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)

                // If scheduled time has already passed today, set for tomorrow
                if (timeInMillis <= System.currentTimeMillis()) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }

            try {
                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            } catch (e: Exception) {
                try {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                } catch (_: Exception) {}
            }
        }
    }

    fun scheduleDailyReminderAlarm(context: Context, preferredHour: Int = getReminderHour(context)) {
        scheduleAllDailyReminderAlarms(context)
    }

    fun scheduleNextDailyReminder(context: Context) {
        scheduleAllDailyReminderAlarms(context)
    }

    fun cancelAllDailyReminderAlarms(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        for (hour in 0..23) {
            val intent = Intent(context, DailyReminderReceiver::class.java).apply {
                action = ACTION_DAILY_REMINDER
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                ALARM_REQUEST_CODE + hour,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
    }

    fun cancelDailyReminderAlarm(context: Context) {
        cancelAllDailyReminderAlarms(context)
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

        val notificationManagerCompat = NotificationManagerCompat.from(context)
        if (!notificationManagerCompat.areNotificationsEnabled()) {
            return false
        }

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
            notificationManagerCompat.notify(notificationId, builder.build())
            return true
        } catch (e: SecurityException) {
            return false
        }
    }

    /**
     * Push all active/unread notifications to the Android system drawer.
     * Ensures all testing reminders, status updates, and milestones appear in the status bar.
     */
    fun pushAllNotifications(
        context: Context,
        notifications: List<AppNotification>
    ): Int {
        createNotificationChannel(context)
        var deliveredCount = 0
        val targetList = if (notifications.any { !it.isRead }) {
            notifications.filter { !it.isRead }
        } else {
            notifications.take(8)
        }

        targetList.forEachIndexed { index, notif ->
            val notifId = if (notif.id > 0) notif.id else (System.currentTimeMillis() % 100000).toInt() + index
            val ok = showSystemNotification(
                context = context,
                title = notif.title,
                message = notif.message,
                notificationId = notifId
            )
            if (ok) deliveredCount++
        }

        // Also ensure scheduled background alarms are refreshed
        scheduleAllDailyReminderAlarms(context)

        return deliveredCount
    }
}
