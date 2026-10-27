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
import com.playtesterhub.app.R

class DailyReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_BOOT_COMPLETED) {
            DailyReminderScheduler.scheduleDailyReminder(context)
            return
        }

        showReminderNotification(context)
    }

    private fun showReminderNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "playtester_daily_reminders"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "تذكيرات الاختبار اليومية",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "تنبيهات يومية لاختبار التطبيقات والحفاظ على سلسلة الـ 14 يوماً"
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
            .setContentTitle("PlayTester Hub ⏰ تذكير الاختبار اليومي")
            .setContentText("حافظ على سلسلة الـ 14 يوماً! لديك تطبيقات تحتاج اختبار اليوم لضمان امتثال Google Play Console 🚀")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("حافظ على سلسلة الـ 14 يوماً!\nلديك تطبيقات تنتظر اختبارك اليومي في PlayTester Hub لدعم المطورين والحصول على عملات الترقية المجانية 🪙")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(2001, notification)
    }
}
