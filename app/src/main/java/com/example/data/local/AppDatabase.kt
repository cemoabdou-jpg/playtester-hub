package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AppBuildArtifact
import com.example.data.model.AppNotification
import com.example.data.model.AppSetting
import com.example.data.model.AppTesterActivity
import com.example.data.model.AuthUser
import com.example.data.model.CommunityApp
import com.example.data.model.DailyTestLog
import com.example.data.model.InvitedTester
import com.example.data.model.JoinedTest
import com.example.data.model.TesterFeedback
import com.example.data.model.UserApp
import com.example.data.model.UserWallet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        CommunityApp::class,
        JoinedTest::class,
        UserApp::class,
        UserWallet::class,
        AuthUser::class,
        AppBuildArtifact::class,
        InvitedTester::class,
        AppNotification::class,
        DailyTestLog::class,
        TesterFeedback::class,
        AppSetting::class,
        AppTesterActivity::class
    ],
    version = 12,
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
                    "play_tester_hub.db"
                ).fallbackToDestructiveMigration(dropAllTables = true)
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
            // Clean initial wallet with 0 tests and 0 streak
            val initialWallet = UserWallet(
                id = 1,
                points = 50, // رصيد ترحيبي بسيط للمستخدم الجديد
                testsCompleted = 0,
                currentStreak = 0,
                lastActiveDate = ""
            )
            dao.insertOrUpdateWallet(initialWallet)

            // Seed admin account for platform management
            val adminOwner = AuthUser(
                email = "cemoabdou@gmail.com",
                passwordHash = "password123",
                displayName = "المدير العام",
                authProvider = "EMAIL",
                role = "ADMIN",
                isLoggedIn = false
            )
            dao.insertAuthUser(adminOwner)

            val legacyAdmin = AuthUser(
                email = "admin@playtesterhub.com",
                passwordHash = "admin123",
                displayName = "مدير المنصة",
                authProvider = "EMAIL",
                role = "ADMIN",
                isLoggedIn = false
            )
            dao.insertAuthUser(legacyAdmin)

            // Initial welcome notification
            val welcomeNotif = AppNotification(
                title = "مرحباً بك في مجتمع اختبار التطبيقات",
                message = "ابدأ بإضافة تطبيقك الخاص لجمع 20 مختبراً حقيقياً، أو تصفح تطبيقات المطورين لاكتساب النقاط والمساعدة المتبادلة.",
                type = "WELCOME",
                timestamp = System.currentTimeMillis(),
                isRead = false,
                actionRoute = "my_apps"
            )
            dao.insertNotification(welcomeNotif)

            // Seed official Google Group settings
            dao.saveSetting(AppSetting("official_google_group_url", "https://groups.google.com/g/player-test-hup"))
            dao.saveSetting(AppSetting("official_google_group_email", "player-test-hup@googlegroups.com"))
        }
    }
}
