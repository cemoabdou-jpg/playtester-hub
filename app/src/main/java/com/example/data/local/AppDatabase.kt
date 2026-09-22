package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AppBuildArtifact
import com.example.data.model.AppNotification
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
        TesterFeedback::class
    ],
    version = 7,
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
            val initialWallet = UserWallet(
                id = 1,
                points = 160,
                testsCompleted = 3,
                currentStreak = 5,
                lastActiveDate = ""
            )
            dao.insertOrUpdateWallet(initialWallet)

            val seedCommunityApps = listOf(
                CommunityApp(
                    name = "مفكرتي السريعة (QuickNotes)",
                    developerName = "أحمد البرمجي",
                    packageName = "com.devahmed.quicknotes",
                    description = "تطبيق تدوين ملاحظات سريع مع ميزة التشفير والنسخ الاحتياطي السحابي، بحاجة لمختبرين لاختبار الاستقرار.",
                    category = "إنتاجية",
                    googleGroupUrl = "https://groups.google.com/g/quicknotes-closed-testers",
                    webOptInUrl = "https://play.google.com/apps/testing/com.devahmed.quicknotes",
                    androidOptInUrl = "https://play.google.com/store/apps/details?id=com.devahmed.quicknotes",
                    testersCurrent = 16,
                    testersTarget = 20,
                    rewardPoints = 40,
                    isFeatured = true
                ),
                CommunityApp(
                    name = "حاسبة الراتب والمصاريف",
                    developerName = "سارة تك",
                    packageName = "com.saratech.salarycalc",
                    description = "تطبيق تنظيم الميزانية الشخصية، مطلوب تجربة إضافة الدخل والتقارير الشهرية وإبداء الملاحظات.",
                    category = "مالية",
                    googleGroupUrl = "https://groups.google.com/g/salarycalc-testers",
                    webOptInUrl = "https://play.google.com/apps/testing/com.saratech.salarycalc",
                    androidOptInUrl = "https://play.google.com/store/apps/details?id=com.saratech.salarycalc",
                    testersCurrent = 14,
                    testersTarget = 20,
                    rewardPoints = 45,
                    isFeatured = true
                ),
                CommunityApp(
                    name = "منبه الفجر والأذكار الذكي",
                    developerName = "نور الدين للبرمجيات",
                    packageName = "com.noorapps.fajralarm",
                    description = "تطبيق منبه يعتمد على الموقع الجغرافي لحساب مواقيت الصلاة بدقة وتذكير الأذكار.",
                    category = "نمط حياة",
                    googleGroupUrl = "https://groups.google.com/g/fajralarm-testing-group",
                    webOptInUrl = "https://play.google.com/apps/testing/com.noorapps.fajralarm",
                    androidOptInUrl = "https://play.google.com/store/apps/details?id=com.noorapps.fajralarm",
                    testersCurrent = 19,
                    testersTarget = 20,
                    rewardPoints = 35,
                    isFeatured = false
                ),
                CommunityApp(
                    name = "قارئ PDF وماسح المستندات",
                    developerName = "عمر التطوير",
                    packageName = "com.omardev.smartpdf",
                    description = "ماسح ضوئي ذكي للمستندات مع تحويل النصوص إلى PDF وحفظها دون إنترنت.",
                    category = "أدوات",
                    googleGroupUrl = "https://groups.google.com/g/smartpdf-testing",
                    webOptInUrl = "https://play.google.com/apps/testing/com.omardev.smartpdf",
                    androidOptInUrl = "https://play.google.com/store/apps/details?id=com.omardev.smartpdf",
                    testersCurrent = 11,
                    testersTarget = 20,
                    rewardPoints = 50,
                    isFeatured = false
                ),
                CommunityApp(
                    name = "تحدي الكلمات المتقاطعة العربي",
                    developerName = "استوديو الألعاب العربي",
                    packageName = "com.arabgames.crosswords",
                    description = "لعبة ألغاز وكلمات متقاطعة مسلية باللغة العربية، تم بناء 50 مستوى، مطلوب فحص التوافق.",
                    category = "ألعاب",
                    googleGroupUrl = "https://groups.google.com/g/arab-crosswords-testers",
                    webOptInUrl = "https://play.google.com/apps/testing/com.arabgames.crosswords",
                    androidOptInUrl = "https://play.google.com/store/apps/details?id=com.arabgames.crosswords",
                    testersCurrent = 17,
                    testersTarget = 20,
                    rewardPoints = 50,
                    isFeatured = false
                )
            )
            dao.insertCommunityApps(seedCommunityApps)

            // Seed a sample joined test so the user immediately understands the 14-day streak tracker
            val sampleJoined = JoinedTest(
                communityAppId = 1,
                appName = "مفكرتي السريعة (QuickNotes)",
                packageName = "com.devahmed.quicknotes",
                category = "إنتاجية",
                googleGroupUrl = "https://groups.google.com/g/quicknotes-closed-testers",
                webOptInUrl = "https://play.google.com/apps/testing/com.devahmed.quicknotes",
                androidOptInUrl = "https://play.google.com/store/apps/details?id=com.devahmed.quicknotes",
                daysCompleted = 6,
                checkedDaysCsv = "1,2,3,4,5,6",
                lastCheckInDate = "",
                isCompleted = false,
                feedbackSent = true
            )
            dao.insertJoinedTest(sampleJoined)

            // Seed a sample user app
            val sampleUserApp = UserApp(
                name = "تطبيقي الرائع (My Great App)",
                packageName = "com.mycompany.greatproject",
                category = "أدوات",
                googleGroupUrl = "https://groups.google.com/g/mygreatapp-testers",
                webOptInUrl = "https://play.google.com/apps/testing/com.mycompany.greatproject",
                androidOptInUrl = "https://play.google.com/store/apps/details?id=com.mycompany.greatproject",
                instructions = "يرجى فتح التطبيق مرة يومياً وتجربة حفظ الإعدادات، وإرسال تقرير خطأ إن وُجد.",
                testersCount = 15,
                targetTesters = 20,
                daysElapsed = 8,
                targetDays = 14,
                status = "TESTING"
            )
            val userAppId = dao.insertUserApp(sampleUserApp).toInt()

            // Seed owner/admin account and default developer account
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

            val defaultUser = AuthUser(
                email = "developer@playtesterhub.com",
                passwordHash = "password123",
                displayName = "المطور المعتمد",
                authProvider = "EMAIL",
                role = "DEVELOPER",
                isLoggedIn = false
            )
            dao.insertAuthUser(defaultUser)

            // Seed sample build artifacts
            val artifact1 = AppBuildArtifact(
                userAppId = userAppId,
                versionName = "1.0.1",
                versionCode = 2,
                artifactType = "AAB",
                fileName = "app-release-1.0.1.aab",
                fileSizeMb = 18.4,
                releaseNotes = "إصلاح انهيار مفاجئ في أجهزة أندرويد 14 وتحسين استهلاك الذاكرة",
                isActiveForTesting = true,
                track = "CLOSED_TESTING"
            )
            val artifact2 = AppBuildArtifact(
                userAppId = userAppId,
                versionName = "1.0.0",
                versionCode = 1,
                artifactType = "APK",
                fileName = "app-debug-1.0.0.apk",
                fileSizeMb = 24.1,
                releaseNotes = "الإصدار الأولي لبدء مرحلة الاختبار المغلق",
                isActiveForTesting = false,
                track = "INTERNAL"
            )
            dao.insertArtifact(artifact1)
            dao.insertArtifact(artifact2)

            // Seed invited testers
            val tester1 = InvitedTester(
                userAppId = userAppId,
                email = "tester.khalid@gmail.com",
                status = "TESTING",
                notes = "مختبر نشط - سامسونج S23"
            )
            val tester2 = InvitedTester(
                userAppId = userAppId,
                email = "maryam.dev@gmail.com",
                status = "ACCEPTED",
                notes = "قبلت الدعوة عبر البريد"
            )
            val tester3 = InvitedTester(
                userAppId = userAppId,
                email = "hassan.test@outlook.com",
                status = "INVITED",
                notes = "تم إرسال رابط المشاركة"
            )
            dao.insertInvitedTester(tester1)
            dao.insertInvitedTester(tester2)
            dao.insertInvitedTester(tester3)

            // Seed notifications
            val notif1 = AppNotification(
                title = "مختبر جديد انضم لتطبيقك",
                message = "انضم المختبر tester.khalid@gmail.com وبدأ فحص تطبيقك بنجاح.",
                type = "TESTER_JOINED",
                timestamp = System.currentTimeMillis() - (1000 * 60 * 35),
                isRead = false,
                actionRoute = "my_apps"
            )
            val notif2 = AppNotification(
                title = "تذكير يومي: واصل سلسلة الاختبارات",
                message = "سجل تفاعلك اليومي في التطبيقات المنضم إليها لتحصل على +20 نقطة وتحافظ على سلسلتك المستمرة (4 أيام)!",
                type = "DAILY_REMINDER",
                timestamp = System.currentTimeMillis() - (1000 * 60 * 180),
                isRead = false,
                actionRoute = "joined"
            )
            val notif3 = AppNotification(
                title = "تطبيق جديد يحتاج مختبرين في المجتمع",
                message = "تمت إضافة 'FitPulse - رفيق اللياقة الذكي'. اختبره الآن واكسب 50 نقطة فورية.",
                type = "COMMUNITY_APP",
                timestamp = System.currentTimeMillis() - (1000 * 60 * 60 * 24),
                isRead = true,
                actionRoute = "community"
            )
            val notif4 = AppNotification(
                title = "إنجاز 14 يوماً يقترب!",
                message = "تطبيقك 'سجل الفواتير والمصاريف' وصل إلى 9 أيام اختبار مغلق مع 14 مختبراً نشطاً.",
                type = "MILESTONE",
                timestamp = System.currentTimeMillis() - (1000 * 60 * 60 * 48),
                isRead = true,
                actionRoute = "my_apps"
            )
            dao.insertNotification(notif1)
            dao.insertNotification(notif2)
            dao.insertNotification(notif3)
            dao.insertNotification(notif4)
        }
    }
}
