package com.example.data.model

import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PlayGreen
import androidx.compose.ui.graphics.Color

data class UserBadge(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val category: BadgeCategory,
    val isUnlocked: Boolean,
    val currentProgress: Int,
    val maxProgress: Int,
    val pointsReward: Int = 25,
    val rarity: BadgeRarity = BadgeRarity.COMMON
)

enum class BadgeCategory(val title: String) {
    ALL("الكل"),
    TESTING("الاختبار 🔍"),
    DEVELOPER("التطوير 🚀"),
    QUALITY("الجودة 🛡️"),
    STREAK("الاستمرارية 🔥")
}

enum class BadgeRarity(val label: String, val colorHex: Long) {
    COMMON("عادي", 0xFF94A3B8),
    RARE("نادر", 0xFF06B6D4),
    EPIC("مميز", 0xFF8B5CF6),
    LEGENDARY("أسطوري", 0xFFF59E0B)
}

object BadgeCalculator {

    fun calculateBadges(
        wallet: UserWallet,
        joinedTests: List<JoinedTest>,
        userApps: List<UserApp>,
        feedbacks: List<TesterFeedback>,
        dailyLogs: List<DailyTestLog>
    ): List<UserBadge> {
        val completedTestsCount = joinedTests.count { it.isCompleted }
        val activeTestsCount = joinedTests.size
        val feedbackCount = feedbacks.size
        val logsCount = dailyLogs.size
        val streak = wallet.currentStreak
        val appsPublished = userApps.size
        val points = wallet.points

        return listOf(
            UserBadge(
                id = "trusted_tester",
                title = "مختبر موثوق 🏅",
                description = "إكمال اختبار 3 تطبيقات بنجاح لمدة 14 يوماً كاملة",
                iconEmoji = "🏅",
                category = BadgeCategory.TESTING,
                isUnlocked = completedTestsCount >= 3,
                currentProgress = minOf(completedTestsCount, 3),
                maxProgress = 3,
                pointsReward = 50,
                rarity = BadgeRarity.RARE
            ),
            UserBadge(
                id = "bug_hunter",
                title = "صائد الثغرات 🐛",
                description = "كتابة وإرسال 5 تقارير وملاحظات تفصيلية لمساعدة المطورين",
                iconEmoji = "🐛",
                category = BadgeCategory.QUALITY,
                isUnlocked = feedbackCount >= 5,
                currentProgress = minOf(feedbackCount, 5),
                maxProgress = 5,
                pointsReward = 40,
                rarity = BadgeRarity.EPIC
            ),
            UserBadge(
                id = "streak_master",
                title = "خبير الـ 14 يوماً ⭐",
                description = "الحفاظ على ستريك فحص يومي لمدة 7 أيام متواصلة",
                iconEmoji = "⭐",
                category = BadgeCategory.STREAK,
                isUnlocked = streak >= 7,
                currentProgress = minOf(streak, 7),
                maxProgress = 7,
                pointsReward = 60,
                rarity = BadgeRarity.LEGENDARY
            ),
            UserBadge(
                id = "active_publisher",
                title = "مطور نشط 🚀",
                description = "نشر تطبيقك الخاص على المنصة لطلب مختبرين",
                iconEmoji = "🚀",
                category = BadgeCategory.DEVELOPER,
                isUnlocked = appsPublished >= 1,
                currentProgress = minOf(appsPublished, 1),
                maxProgress = 1,
                pointsReward = 30,
                rarity = BadgeRarity.COMMON
            ),
            UserBadge(
                id = "community_pioneer",
                title = "رائد التبادل 👥",
                description = "الانضمام لاختبار 5 تطبيقات في مجتمع المطورين",
                iconEmoji = "👥",
                category = BadgeCategory.TESTING,
                isUnlocked = activeTestsCount >= 5,
                currentProgress = minOf(activeTestsCount, 5),
                maxProgress = 5,
                pointsReward = 45,
                rarity = BadgeRarity.RARE
            ),
            UserBadge(
                id = "quality_guardian",
                title = "حارس الجودة 🛡️",
                description = "تسجيل 10 جلسات فحص يومي مؤكدة بالوقت والتقييم",
                iconEmoji = "🛡️",
                category = BadgeCategory.QUALITY,
                isUnlocked = logsCount >= 10,
                currentProgress = minOf(logsCount, 10),
                maxProgress = 10,
                pointsReward = 50,
                rarity = BadgeRarity.EPIC
            ),
            UserBadge(
                id = "diamond_tester",
                title = "المختبر الماسي 💎",
                description = "تجميع أكثر من 300 نقطة من خلال المساهمة النشطة",
                iconEmoji = "💎",
                category = BadgeCategory.STREAK,
                isUnlocked = points >= 300,
                currentProgress = minOf(points, 300),
                maxProgress = 300,
                pointsReward = 100,
                rarity = BadgeRarity.LEGENDARY
            )
        )
    }

    fun getUserRankTitle(unlockedBadgesCount: Int): Pair<String, String> {
        return when {
            unlockedBadgesCount >= 6 -> "أسطورة الاختبار 👑" to "أعلى رتبة - شريك مجتمعي معتمد"
            unlockedBadgesCount >= 4 -> "مختبر ذهبي 🥇" to "مساهم متميز وفعال في جودة التطبيقات"
            unlockedBadgesCount >= 2 -> "مختبر فضي 🥈" to "ملتزم بالفحص اليومي والملاحظات"
            unlockedBadgesCount >= 1 -> "مختبر برونزي 🥉" to "بدأ رحلة اختبار الـ 14 يوماً بنجاح"
            else -> "مختبر مبتدئ 🌱" to "ابدأ بالاختبار لفتح الشارات والرتب"
        }
    }
}
