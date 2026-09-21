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
}
