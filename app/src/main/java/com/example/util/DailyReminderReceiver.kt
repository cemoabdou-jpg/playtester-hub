package com.example.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DailyReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_BOOT_COMPLETED) {
            // Reschedule alarm on device reboot if daily reminder is enabled
            if (NotificationHelper.isDailyReminderEnabled(context)) {
                NotificationHelper.scheduleDailyReminderAlarm(context)
            }
        } else if (action == NotificationHelper.ACTION_DAILY_REMINDER || action == null) {
            // Trigger the daily notification
            if (NotificationHelper.isDailyReminderEnabled(context)) {
                val db = AppDatabase.getInstance(context)
                val appDao = db.appDao()

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val joinedTests = appDao.getJoinedTestsOnce()
                        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                        val pendingAppsCount = joinedTests.count { it.lastCheckInDate != todayStr }
                        val currentStreak = appDao.getWalletOnce()?.currentStreak ?: 1

                        NotificationHelper.triggerDailyTestingReminder(
                            context = context,
                            pendingAppsCount = pendingAppsCount,
                            currentStreak = currentStreak
                        )
                    } catch (e: Exception) {
                        // Fallback notification if DB query fails
                        NotificationHelper.showSystemNotification(
                            context = context,
                            title = "⏰ تذكير الفحص اليومي لـ PlayTester Hub",
                            message = "لديك اختبارات يومية بانتظارك! افتح التطبيق لتجربة التطبيقات والحفاظ على الستريك.",
                            notificationId = 14001
                        )
                    } finally {
                        // Reschedule next day's alarm
                        NotificationHelper.scheduleNextDailyReminder(context)
                    }
                }
            }
        }
    }
}
