package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.ui.util.L
import com.playtesterhub.app.R

class DailyReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        L.init(context)
        try {
            com.example.ui.theme.LogoEventManager.init(context)
            if (com.example.ui.theme.LogoEventManager.externalIconSyncEnabled) {
                com.example.ui.theme.LauncherIconManager.syncWithEvent(context)
            }
        } catch (_: Throwable) {}

        val action = intent.action
        if (action == Intent.ACTION_BOOT_COMPLETED) {
            DailyReminderScheduler.scheduleDailyReminder(context)
            return
        }

        showReminderNotification(context)
    }

    private fun showReminderNotification(context: Context) {
        L.init(context)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "playtester_daily_reminders"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                L.t("تذكيرات الاختبار اليومية", "Daily Testing Reminders"),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = L.t(
                    "تنبيهات يومية لاختبار التطبيقات والحفاظ على سلسلة الـ 14 يوماً",
                    "Daily alerts to test apps and maintain your 14-day testing streak"
                )
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            1001,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(
                L.t(
                    "منصة المختبرين ⏰ تذكير الاختبار اليومي",
                    "PlayTester Hub ⏰ Daily Testing Reminder"
                )
            )
            .setContentText(
                L.t(
                    "حافظ على سلسلة الـ 14 يوماً! لديك تطبيقات تحتاج اختبار اليوم لضمان امتثال لوحة مطوري جوجل بلاي 🚀",
                    "Keep your 14-day streak alive! Apps are waiting for today's test for Google Play Console compliance 🚀"
                )
            )
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(
                        L.t(
                            "حافظ على سلسلة الـ 14 يوماً!\nلديك تطبيقات تنتظر اختبارك اليومي في منصة مختبري التطبيقات لدعم المطورين والحصول على عملات الترقية المجانية 🪙",
                            "Keep your 14-day streak alive!\nApps are waiting for your daily test in PlayTester Hub to support developers and earn free promotion coins 🪙"
                        )
                    )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(2001, notification)
    }
}
