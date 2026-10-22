package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserApp::class,
        CommunityApp::class,
        JoinedTest::class,
        CrashReport::class,
        TesterFeedback::class,
        DailyTestLog::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "playtester_db"
                ).fallbackToDestructiveMigration()
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            INSTANCE?.let { database ->
                                seedInitialData(database.appDao())
                            }
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun seedInitialData(dao: AppDao) {
            val sampleLogs = mutableListOf<DailyTestLog>()
            val devices = listOf("Samsung Galaxy S23", "Google Pixel 8", "Xiaomi 13", "OnePlus 11", "Sony Xperia 5")
            val osList = listOf("Android 14 (API 34)", "Android 13 (API 33)", "Android 14 (API 34)", "Android 12 (API 31)")
            val apps = listOf("com.cairofiles.detective1948", "com.sprinthon.notesflow", "io.github.cemohamedabdou_commits.staradventure")

            for (appId in apps) {
                for (day in 1..14) {
                    for (i in 1..20) {
                        sampleLogs.add(
                            DailyTestLog(
                                id = "log_${appId}_${day}_$i",
                                appId = appId,
                                testerName = "مختبر معتمد #$i",
                                testerEmail = "tester$i@playtesterhub.com",
                                deviceModel = devices[i % devices.size],
                                osVersion = osList[i % osList.size],
                                dayNumber = day,
                                checkInTimestamp = System.currentTimeMillis() - ((14 - day) * 86400000L) + (i * 360000L),
                                sessionDurationMinutes = 12 + (i % 15),
                                notes = "جلسة اختبار تفاعلية، تنقل سلس واستقرار تام"
                            )
                        )
                    }
                }
            }
            dao.insertDailyTestLogs(sampleLogs)

            val sampleCrashes = listOf(
                CrashReport(
                    id = "crash_1",
                    appId = "com.sprinthon.notesflow",
                    title = "NullPointerException in NotesSyncService",
                    stackTrace = "java.lang.NullPointerException: Attempt to invoke virtual method on a null object reference at com.sprinthon.notesflow.SyncService.onResume(SyncService.kt:42)",
                    deviceModel = "Samsung Galaxy S22",
                    osVersion = "Android 13 (API 33)",
                    appVersion = "v1.0.2",
                    occurrences = 3,
                    status = "RESOLVED",
                    reportedAt = System.currentTimeMillis() - (10L * 86400000L),
                    resolvedAt = System.currentTimeMillis() - (8L * 86400000L),
                    resolutionNote = "تم إصلاح التهيئة المتأخرة وإضافة فحص الأمان لـ NotesSyncService"
                ),
                CrashReport(
                    id = "crash_2",
                    appId = "com.cairofiles.detective1948",
                    title = "ANR (Application Not Responding) on FileLoad",
                    stackTrace = "Input dispatching timed out (Waiting to send key event because the focused window has not finished processing).",
                    deviceModel = "Xiaomi Redmi Note 12",
                    osVersion = "Android 12 (API 31)",
                    appVersion = "v1.0.1",
                    occurrences = 2,
                    status = "RESOLVED",
                    reportedAt = System.currentTimeMillis() - (12L * 86400000L),
                    resolvedAt = System.currentTimeMillis() - (11L * 86400000L),
                    resolutionNote = "تم نقل عمليات فك ضغط الملفات إلى Coroutine Dispatchers.IO خلف الكواليس"
                )
            )
            dao.insertCrashReports(sampleCrashes)
        }
    }
}
