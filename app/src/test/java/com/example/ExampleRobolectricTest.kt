package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.model.AppNotification
import com.example.util.NotificationHelper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    private lateinit var database: AppDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("PlayTester Hub", appName)
    }

    @Test
    fun `notification channel creation and DAO operations`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        NotificationHelper.createNotificationChannel(context)

        val dao = database.appDao()
        val notif = AppNotification(
            title = "انضمام مختبر جديد",
            message = "انضم مختبر لتطبيقك بنجاح",
            type = "TESTER_JOINED",
            actionRoute = "my_apps"
        )
        val id = dao.insertNotification(notif)
        assertTrue(id > 0)

        val list = dao.getAllNotifications().first()
        assertEquals(1, list.size)
        assertEquals("انضمام مختبر جديد", list[0].title)
        assertEquals(false, list[0].isRead)

        dao.markNotificationAsRead(id.toInt())
        val unreadCount = dao.getUnreadNotificationsCount().first()
        assertEquals(0, unreadCount)
    }

    @Test
    fun `daily test confirmation log insertion and query`() = runBlocking {
        val dao = database.appDao()
        val log = com.example.data.model.DailyTestLog(
            joinedTestId = 101,
            appName = "تطبيق تجريبي",
            packageName = "com.test.sample",
            dayNumber = 1,
            testDate = "2026-09-20",
            durationMinutes = 15,
            rating = 5,
            notes = "تم تجربة التطبيق بسلاسة تامة",
            verifiedAt = System.currentTimeMillis()
        )
        val logId = dao.insertDailyTestLog(log)
        assertTrue(logId > 0)

        val logsForApp = dao.getLogsForJoinedTest(101).first()
        assertEquals(1, logsForApp.size)
        assertEquals(15, logsForApp[0].durationMinutes)
        assertEquals(5, logsForApp[0].rating)

        val todayLog = dao.getTodayLog(101, "2026-09-20")
        assertTrue(todayLog != null)
        assertEquals("2026-09-20", todayLog?.testDate)
    }

    @Test
    fun `password reset and email registration check`() = runBlocking {
        val repo = com.example.data.repository.TesterRepository(database.appDao())

        // Register user
        val regRes = repo.registerUser("مطور الاختبار", "testdev@example.com", "oldpassword123")
        assertTrue(regRes.isSuccess)

        // Check email registered
        val isRegistered = repo.isEmailRegistered("testdev@example.com")
        assertTrue(isRegistered)

        // Reset password
        val resetRes = repo.resetPassword("testdev@example.com", "newSecurePassword456")
        assertTrue(resetRes.isSuccess)

        // Verify login with new password works
        val loginRes = repo.loginWithEmail("testdev@example.com", "newSecurePassword456")
        assertTrue(loginRes.isSuccess)

        // Verify login with old password fails
        val failLogin = repo.loginWithEmail("testdev@example.com", "oldpassword123")
        assertTrue(failLogin.isFailure)
    }

    @Test
    fun `joining app does not auto mark day 1 as tested`() = runBlocking {
        val dao = database.appDao()
        val repo = com.example.data.repository.TesterRepository(dao)

        val communityApp = com.example.data.model.CommunityApp(
            id = 55,
            name = "تطبيق حقيقي",
            developerName = "المطور",
            packageName = "com.real.testapp",
            description = "وصف التطبيق",
            category = "أدوات",
            googleGroupUrl = "https://groups.google.com/g/player-test-hup",
            webOptInUrl = "https://play.google.com/apps/testing/com.real.testapp",
            androidOptInUrl = "https://play.google.com/store/apps/details?id=com.real.testapp"
        )
        dao.insertCommunityApp(communityApp)

        val joinRes = repo.joinCommunityApp(communityApp)
        assertTrue(joinRes.isSuccess)

        val joined = dao.getJoinedTestByCommunityId(55)
        assertTrue(joined != null)
        // Verify it starts with 0 days completed, NOT 1!
        assertEquals(0, joined?.daysCompleted)
        assertEquals("", joined?.lastCheckInDate)
        assertEquals("", joined?.checkedDaysCsv)
        assertEquals(false, joined?.isCompleted)

        // Now perform actual daily confirmation
        val confirmRes = repo.confirmDailyTest(joined!!, durationMinutes = 15, rating = 5, notes = "تجربة ممتازة")
        assertTrue(confirmRes.isSuccess)

        val updated = dao.getJoinedTestByCommunityId(55)
        assertEquals(1, updated?.daysCompleted)
        assertEquals("1", updated?.checkedDaysCsv)
    }

    @Test
    fun `notification preferences and alarm scheduling test`() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // Test daily reminder toggle & hour setting
        com.example.util.NotificationHelper.setDailyReminderEnabled(context, true)
        assertTrue(com.example.util.NotificationHelper.isDailyReminderEnabled(context))

        com.example.util.NotificationHelper.setReminderHour(context, 15)
        assertEquals(15, com.example.util.NotificationHelper.getReminderHour(context))

        // Schedule daily reminder alarm
        com.example.util.NotificationHelper.scheduleDailyReminderAlarm(context)

        // Disable daily reminder
        com.example.util.NotificationHelper.setDailyReminderEnabled(context, false)
        assertFalse(com.example.util.NotificationHelper.isDailyReminderEnabled(context))
    }

    @Test
    fun `smart feedback classifier and optin validator test`() {
        // Feedback Classifier Test
        val bugCat = com.example.ui.components.FeedbackClassifierUtil.classify("توقف التطبيق فجأة عند الضغط", 1, "BUG")
        assertEquals(com.example.ui.components.FeedbackCategory.BUG, bugCat)

        val posCat = com.example.ui.components.FeedbackClassifierUtil.classify("التطبيق ممتاز وسلس للغاية", 5, "FEEDBACK")
        assertEquals(com.example.ui.components.FeedbackCategory.POSITIVE, posCat)

        // OptIn Validator Test
        val (groupValid, _) = com.example.ui.components.OptInValidatorUtil.validateGroupUrl("https://groups.google.com/g/my-test-group")
        assertTrue(groupValid)

        val (invalidGroup, _) = com.example.ui.components.OptInValidatorUtil.validateGroupUrl("http://example.com")
        assertFalse(invalidGroup)
    }
}
