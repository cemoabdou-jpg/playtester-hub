package com.example.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

enum class AppLanguage(val code: String, val displayName: String, val flag: String) {
    AR("ar", "العربية", "🇸🇦"),
    EN("en", "English", "🇬🇧")
}

val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.AR }

object L {
    @Composable
    @ReadOnlyComposable
    fun strings(): AppStrings = when (LocalAppLanguage.current) {
        AppLanguage.AR -> ArabicStrings
        AppLanguage.EN -> EnglishStrings
    }

    fun get(lang: AppLanguage): AppStrings = when (lang) {
        AppLanguage.AR -> ArabicStrings
        AppLanguage.EN -> EnglishStrings
    }
}

interface AppStrings {
    // Navigation Tabs
    val tabCommunity: String
    val tabMyTesting: String
    val tabMyApps: String
    val tabGuide: String
    val tabSettings: String
    val tabAdmin: String

    // Header
    val appTitle: String
    val ownerBadge: String
    val pointsLabel: String
    val streakLabel: String
    val toggleThemeDesc: String
    val notificationsDesc: String
    val languageToggleDesc: String
    val switchLanguage: String

    // Community / Market Screen
    val marketTitle: String
    val searchPlaceholder: String
    val sortLabel: String
    val sortPriority: String
    val sortUrgent: String
    val sortReward: String
    val categoryAll: String
    val categoryTools: String
    val categoryGames: String
    val categoryProductivity: String
    val categorySocial: String
    val categoryEducation: String
    val categoryFinance: String
    val reciprocalTitle: String
    val reciprocalDesc: String
    val reciprocalButton: String
    val cloudSyncTitle: String
    val cloudSyncCheck: String
    val importAppBtn: String
    val publishNewAppBtn: String
    val joinTestBtn: String
    val joinedTestBtn: String
    val openAppBtn: String
    val testersNeededFmt: (current: Int, target: Int) -> String
    val rewardPointsFmt: (points: Int) -> String
    val noAppsFound: String
    val clearFilters: String

    // My Testing Screen
    val testingActiveCount: String
    val testingCompletedCount: String
    val daysCompletedFmt: (days: Int) -> String
    val dailyCheckIn: String
    val checkInDoneToday: String
    val sendFeedbackBtn: String
    val openGooglePlayBtn: String
    val deleteJoinedTest: String
    val noTestsJoinedYet: String
    val browseMarketBtn: String
    val dailyReminderSwitch: String
    val streakBonusNotice: String

    // My Apps Screen
    val myAppsTitle: String
    val postAppHeroCta: String
    val playApprovalSecretsTitle: String
    val playApprovalSecretsSubtitle: String
    val viewSecretsBtn: String
    val noAppsPublishedYet: String
    val shareInviteBtn: String
    val groupLinkBtn: String
    val editGroupLink: String
    val storeLinkBtn: String
    val timeline14DaysBtn: String
    val consoleHelperBtn: String
    val readinessCertificateBtn: String
    val manageTestersBtn: String
    val manageVersionsBtn: String
    val policyLinterBtn: String
    val checklistBtn: String
    val changelogBtn: String
    val deviceDiversityBtn: String
    val privacyPolicyBtn: String
    val linkValidatorBtn: String
    val deleteAppBtn: String
    val devToolsSectionTitle: String
    val collapseTools: String
    val expandTools: String
    val uploadToCloudBtn: String
    val uploadSuccessMsg: String

    // Guide Screen
    val guideTitle: String
    val guideSubtitle: String
    val guideTabSteps: String
    val guideTabConsole: String
    val guideTabFaq: String
    val guideTabFiles: String
    val bundleAndMappingTitle: String
    val bundleExplanation: String
    val mappingExplanation: String
    val copyGroupEmail: String
    val groupEmailCopied: String
    val optInTipTitle: String
    val optInTipBody: String

    // Profile & Settings Screen
    val profileTitle: String
    val appLanguageTitle: String
    val appLanguageDesc: String
    val accountSection: String
    val displayNameLabel: String
    val editName: String
    val passwordLabel: String
    val googleSignedIn: String
    val changeBtn: String
    val verifiedEmail: String
    val sendTestEmail: String
    val themeSection: String
    val themeModeTitle: String
    val themeModeDesc: String
    val themeModeDark: String
    val themeModeLight: String
    val themeModeSystem: String
    val themeAuto: String
    val themeAutoSub: String
    val themeLight: String
    val themeLightSub: String
    val themeDark: String
    val themeDarkSub: String
    val darkModeActive: String
    val lightModeActive: String
    val simplifiedModeTitle: String
    val simplifiedModeSubtitle: String
    val simplifiedModeHubTitle: String
    val simplifiedModeHubDesc: String
    val backupRestoreTitle: String
    val exportBackupBtn: String
    val restoreBackupBtn: String
    val backupJsonTitle: String
    val backupJsonDesc: String
    val manageBackupBtn: String
    val systemAndPreferences: String
    val testDeviceEnvTitle: String
    val testDeviceEnvDesc: String
    val deviceModelLabel: String
    val androidVersionLabel: String
    val hapticFeedbackTitle: String
    val hapticFeedbackDesc: String
    val savePreferences: String
    val saveDeviceSpecsBtn: String
    val badgesAndRankTitle: String
    val achievementsTitle: String
    val viewBadgesBtn: String
    val pointsBalanceLabel: String
    val activeStreakLabel: String
    val testsCompletedLabel: String
    val memberSinceLabel: String
    val roleAdmin: String
    val roleTester: String
    val notificationsSettingsTitle: String
    val notifPermGranted: String
    val notifPermNeeded: String
    val notifPermDescActive: String
    val notifPermDescInactive: String
    val enableBtn: String
    val sendTestNotifBtn: String
    val dailyStreakReminderTitle: String
    val dailyStreakReminderDesc: String
    val preferredReminderTime: String
    val newTestersAlertsTitle: String
    val newTestersAlertsDesc: String
    val newAppsAlertsTitle: String
    val newAppsAlertsDesc: String
    val readinessReportAlertsTitle: String
    val readinessReportAlertsDesc: String
    val interfaceAndSystem: String
    val replayOnboardingTitle: String
    val replayOnboardingDesc: String
    val startTourBtn: String
    val aboutAndComplianceTitle: String
    val appVersionLabel: String
    val googlePlay14DaysStandard: String
    val compliant100: String
    val complianceNotice: String
    val accountActionsTitle: String
    val signOutBtn: String
    val deleteAccountPermBtn: String
    val checkForUpdatesBtn: String
    val logoutBtn: String
    val deleteAccountBtn: String
    val declutterNotice: String

    // App Update
    val updateBannerTitle: String
    val updateBannerSubtitleFmt: (version: String) -> String
    val updateNowBtn: String
    val updateDialogTitle: String
    val updateWhatsNewTitle: String
    val updateDefaultReleaseNotes: String
    val remindMeLaterBtn: String
}

object ArabicStrings : AppStrings {
    override val tabCommunity = "التبادل"
    override val tabMyTesting = "اختباراتي"
    override val tabMyApps = "تطبيقاتي"
    override val tabGuide = "الدليل"
    override val tabSettings = "حسابي"
    override val tabAdmin = "المالك"

    override val appTitle = "PlayTester Hub"
    override val ownerBadge = "👑 مالك"
    override val pointsLabel = "نقطة"
    override val streakLabel = "يوم متتالي"
    override val toggleThemeDesc = "تغيير المظهر الداكن/الفاتح"
    override val notificationsDesc = "الإشعارات"
    override val languageToggleDesc = "تغيير اللغة"
    override val switchLanguage = "English"

    override val marketTitle = "تطبيقات بحاجة لمختبرين"
    override val searchPlaceholder = "ابحث عن تطبيق أو مطور..."
    override val sortLabel = "ترتيب:"
    override val sortPriority = "⭐ الأولوية"
    override val sortUrgent = "🔥 الأكثر احتياجاً"
    override val sortReward = "💎 الأعلى مكافأة"
    override val categoryAll = "الكل"
    override val categoryTools = "أدوات"
    override val categoryGames = "ألعاب"
    override val categoryProductivity = "إنتاجية"
    override val categorySocial = "تواصل"
    override val categoryEducation = "تعليم"
    override val categoryFinance = "مالية"
    override val reciprocalTitle = "شراكات التبادل (1-on-1)"
    override val reciprocalDesc = "التزام متبادل لاختبار تطبيقك يومياً لـ 14 يوماً بنقاط مضاعفة"
    override val reciprocalButton = "عرض الشراكات ←"
    override val cloudSyncTitle = "مزامنة السحابة"
    override val cloudSyncCheck = "فحص السحابة"
    override val importAppBtn = "استيراد برابط"
    override val publishNewAppBtn = "نشر تطبيقك"
    override val joinTestBtn = "انضمام للاختبار"
    override val joinedTestBtn = "منضم مسبقاً ✓"
    override val openAppBtn = "فتح التطبيق"
    override val testersNeededFmt = { current: Int, target: Int -> "$current / $target مختبر" }
    override val rewardPointsFmt = { points: Int -> "+$points نقطة" }
    override val noAppsFound = "لا توجد تطبيقات مطابقة لبحثك"
    override val clearFilters = "إعادة ضبط التصفية"

    override val testingActiveCount = "تطبيقات قيد الاختبار"
    override val testingCompletedCount = "أتممت 14 يوماً"
    override val daysCompletedFmt = { days: Int -> "اليوم $days من 14" }
    override val dailyCheckIn = "تأكيد اختبار اليوم (+25 نقطة)"
    override val checkInDoneToday = "تم تأكيد اختبار اليوم ✓"
    override val sendFeedbackBtn = "إرسال تقرير/ملاحظات"
    override val openGooglePlayBtn = "فتح في Google Play"
    override val deleteJoinedTest = "إلغاء الانضمام"
    override val noTestsJoinedYet = "لم تنضم لأي اختبار بعد!"
    override val browseMarketBtn = "تصفح سوق التطبيقات"
    override val dailyReminderSwitch = "تذكير يومي بالاختبار"
    override val streakBonusNotice = "الاستمرار اليومي يمنحك نقاطاً إضافية وشارات تميز"

    override val myAppsTitle = "تطبيقاتك المنشورة"
    override val postAppHeroCta = "نشر تطبيق جديد لطلب 20 مختبراً 🚀"
    override val playApprovalSecretsTitle = "أسرار وقواعد القبول في Google Play 🌟"
    override val playApprovalSecretsSubtitle = "كيف تتجاوز استبيان الإنتاج والمراجعة بدون رفض"
    override val viewSecretsBtn = "عرض الأسرار ←"
    override val noAppsPublishedYet = "لم تنشر أي تطبيق بعد. أضف تطبيقك الآن واطلب 20 مختبراً مجاناً!"
    override val shareInviteBtn = "مشاركة الدعوة"
    override val groupLinkBtn = "المجموعة"
    override val editGroupLink = "تعديل رابط المجموعة"
    override val storeLinkBtn = "رابط المتجر"
    override val timeline14DaysBtn = "مخطط الـ 14 يوماً ⏱️"
    override val consoleHelperBtn = "مساعد الكونسول 🚀"
    override val readinessCertificateBtn = "عرض شهادة الجاهزية 🏅"
    override val manageTestersBtn = "إدارة المختبرين 👥"
    override val manageVersionsBtn = "إدارة الإصدارات 📦"
    override val policyLinterBtn = "فحص السياسات 🛡️"
    override val checklistBtn = "قائمة التحقق 📋"
    override val changelogBtn = "سجل التغييرات 📜"
    override val deviceDiversityBtn = "تنوع الأجهزة 📱"
    override val privacyPolicyBtn = "سياسة الخصوصية 🔒"
    override val linkValidatorBtn = "فحص الروابط 🔍"
    override val deleteAppBtn = "حذف التطبيق"
    override val devToolsSectionTitle = "🛠️ أدوات الكونسول والمراجعة المتقدمة"
    override val collapseTools = "طي الأدوات ▲"
    override val expandTools = "عرض كل أدوات الكونسول (10 أدوات) ▼"
    override val uploadToCloudBtn = "رفع للسحابة"
    override val uploadSuccessMsg = "تم رفع التطبيقات بنجاح"

    override val guideTitle = "دليل المطور الشامل لاختبار Google Play المغلق"
    override val guideSubtitle = "خارطة طريق خطوة بخطوة للحصول على 20 مختبراً وتخطي فترة الـ 14 يوماً بنجاح"
    override val guideTabSteps = "خطوات الإطلاق"
    override val guideTabConsole = "شروط Google Play"
    override val guideTabFaq = "الأسئلة الشائعة"
    override val guideTabFiles = "ملفي الباندل والمابينق"
    override val bundleAndMappingTitle = "شرح ملفي الباندل (.aab) والمابينق (mapping.txt)"
    override val bundleExplanation = "ملف الباندل (.aab) هو الملف الأساسي الذي يحتوي على الكود الكامل لتطبيقك. يُرفع في Google Play Console في مسار الاختبار المغلق (Closed Testing)."
    override val mappingExplanation = "ملف المابينق (mapping.txt) هو ملف فك التعتيم (De-obfuscation). يُرفع اختياريًا في الكونسول لفك تشفير وتوضيح تقارير الأعطال (Crash reports) البرمجية بدقة."
    override val copyGroupEmail = "نسخ بريد مجموعة المنصة"
    override val groupEmailCopied = "تم نسخ بريد المجموعة للحافظة"
    override val optInTipTitle = "ملاحظة هامة حول روابط التفعيل (Opt-in Links):"
    override val optInTipBody = "الروابط لن تعمل إلا مع الحسابات التي أضفتها مسبقاً في قائمة المختبرين بـ Google Play Console سواء بقائمة إيميلات أو بمجموعة Google Groups."

    override val profileTitle = "الملف الشخصي والإعدادات"
    override val appLanguageTitle = "لغة التطبيق / App Language"
    override val appLanguageDesc = "اختر اللغة المفضلة للمنصة كاملة"
    override val accountSection = "الحساب والأمان"
    override val displayNameLabel = "الاسم الظاهر"
    override val editName = "تعديل الاسم"
    override val passwordLabel = "كلمة المرور"
    override val googleSignedIn = "تم تسجيل الدخول عبر حساب Google"
    override val changeBtn = "تغيير"
    override val verifiedEmail = "البريد الإلكتروني المعتمد"
    override val sendTestEmail = "إرسال تجريبي"
    override val themeSection = "المظهر والألوان"
    override val themeModeTitle = "المظهر ووضع الإضاءة (Theme Mode)"
    override val themeModeDesc = "اختر وضع الإضاءة المناسب لراحة عينيك وتقليل استهلاك البطارية في الإضاءة المنخفضة:"
    override val themeModeDark = "الوضع الداكن"
    override val themeModeLight = "الوضع الفاتح"
    override val themeModeSystem = "تلقائي (حسب النظام)"
    override val themeAuto = "تلقائي"
    override val themeAutoSub = "حسب النظام"
    override val themeLight = "فاتح"
    override val themeLightSub = "نهاري ناصع"
    override val themeDark = "داكن"
    override val themeDarkSub = "ليلي مريح"
    override val darkModeActive = "الوضع الداكن مفعّل حالياً"
    override val lightModeActive = "الوضع الفاتح مفعّل حالياً"
    override val simplifiedModeTitle = "الوضع المبسط (تخفيف الزحمة)"
    override val simplifiedModeSubtitle = "إخفاء التفاصيل الزائدة والتركيز على المهام اليومية فقط"
    override val simplifiedModeHubTitle = "الوضع المبسط (Quick Hub)"
    override val simplifiedModeHubDesc = "عرض بطاقة الإجراءات السريعة في أعلى الشاشة الرئيسية"
    override val backupRestoreTitle = "النسخ الاحتياطي السحابي"
    override val exportBackupBtn = "تصدير نسخة JSON"
    override val restoreBackupBtn = "استعادة نسخة سابقة"
    override val backupJsonTitle = "النسخ الاحتياطي واستعادة البيانات (JSON)"
    override val backupJsonDesc = "تصدير نسخة احتياطية من نقاطك وتطبيقاتك وسجلاتك أو استعادتها بأمان"
    override val manageBackupBtn = "إدارة النسخ"
    override val systemAndPreferences = "تفضيلات الجهاز والتنبيهات"
    override val testDeviceEnvTitle = "بيئة ومواصفات جهاز الاختبار"
    override val testDeviceEnvDesc = "حدد مواصفات هاتفك لتسهيل توجيه التطبيقات المتوافقة وتأكيد تقارير الـ 14 يوماً:"
    override val deviceModelLabel = "طراز الجهاز (Device Model)"
    override val androidVersionLabel = "إصدار الأندرويد (Android OS)"
    override val hapticFeedbackTitle = "الاستجابة اللمسية (Haptics)"
    override val hapticFeedbackDesc = "اهتزاز تفاعلي خفيف عند تأكيد الاختبار وإرسال الملاحظات"
    override val savePreferences = "حفظ التفضيلات"
    override val saveDeviceSpecsBtn = "حفظ بيانات بيئة الاختبار"
    override val badgesAndRankTitle = "الأوسمة والرتب"
    override val achievementsTitle = "سجل الإنجازات والرتب"
    override val viewBadgesBtn = "عرض الشارات 🏆"
    override val pointsBalanceLabel = "رصيد النقاط"
    override val activeStreakLabel = "الستريك النشط"
    override val testsCompletedLabel = "اختبارات مكتملة"
    override val memberSinceLabel = "عضو منذ:"
    override val roleAdmin = "👑 صاحب المنصة (Admin)"
    override val roleTester = "⭐ مطور معتمد (Tester)"
    override val notificationsSettingsTitle = "إعدادات الإشعارات والتنبيهات المتقدمة"
    override val notifPermGranted = "إذن إشعارات النظام: مفعّل وجاهز"
    override val notifPermNeeded = "إذن إشعارات النظام: يحتاج تفعيل"
    override val notifPermDescActive = "ستصلك تنبيهات الاختبار اليومي وتحديثات المختبرين."
    override val notifPermDescInactive = "اضغط لمنح إذن الإشعارات لضمان عدم تفويت الستريك."
    override val enableBtn = "تفعيل"
    override val sendTestNotifBtn = "إرسال إشعار تجريبي فوري للتأكد"
    override val dailyStreakReminderTitle = "تذكير الفحص اليومي (14-Day Streak)"
    override val dailyStreakReminderDesc = "تلقي إشعار لتسجيل فتح وتجربة التطبيقات والحفاظ على الستريك"
    override val preferredReminderTime = "توقيت التذكير اليومي المفضل:"
    override val newTestersAlertsTitle = "تنبيهات انضمام المختبرين لتطبيقي"
    override val newTestersAlertsDesc = "إشعار فوري عند انضمام مطور جديد لاختبار أحد تطبيقاتك"
    override val newAppsAlertsTitle = "تنبيهات التطبيقات الجديدة بالمجتمع"
    override val newAppsAlertsDesc = "إشعار عند نشر تطبيقات جديدة تحتاج لمختبرين لكسب نقاط سريعة"
    override val readinessReportAlertsTitle = "تنبيهات تقرير الجاهزية واكتمال الاختبار"
    override val readinessReportAlertsDesc = "إشعار عند وصول أي تطبيق لـ 20 مختبراً واكتمال 14 يوماً بنجاح"
    override val interfaceAndSystem = "الواجهة والنظام"
    override val replayOnboardingTitle = "إعادة جولة الشرح التفاعلية"
    override val replayOnboardingDesc = "تعرف على شروط الـ 20 مختبراً وطريقة نشر وقبول التطبيقات"
    override val startTourBtn = "بدء الجولة"
    override val aboutAndComplianceTitle = "حول المنصة والشروط"
    override val appVersionLabel = "رقم الإصدار (App Version)"
    override val googlePlay14DaysStandard = "معيار الـ 14 يوماً لـ Google Play"
    override val compliant100 = "متوافق 100%"
    override val complianceNotice = "المنصة تضمن تتبع واستمرارية 20 مختبراً حقيقياً عبر مجموعات Google Groups وروابط الاختبار المغلق الرسمية."
    override val accountActionsTitle = "إجراءات الحساب"
    override val signOutBtn = "تسجيل الخروج من الحساب"
    override val deleteAccountPermBtn = "حذف الحساب نهائياً"
    override val checkForUpdatesBtn = "التحقق من وجود تحديثات"
    override val logoutBtn = "تسجيل الخروج"
    override val deleteAccountBtn = "حذف الحساب نهائياً"
    override val declutterNotice = "تم تنظيم الشاشات لتقليل الزحمة وتسهيل الوصول للأدوات"

    override val updateBannerTitle = "🚀 يتوفر تحديث جديد للتطبيق!"
    override val updateBannerSubtitleFmt = { v: String -> "يتوفر إصدار أحدث v$v على Google Play يتضمن تحسينات ومزايا جديدة." }
    override val updateNowBtn = "تحديث الآن ⚡"
    override val updateDialogTitle = "يتوفر تحديث جديد على Google Play"
    override val updateWhatsNewTitle = "ما الجديد في هذا التحديث؟"
    override val updateDefaultReleaseNotes = "تحسين أداء التطبيق، إضافة مزايا جديدة في سوق التبادل، واستقرار أسرع في المزامنة مع متجر جوجل بلاي."
    override val remindMeLaterBtn = "تذكيري لاحقاً"
}

object EnglishStrings : AppStrings {
    override val tabCommunity = "Market"
    override val tabMyTesting = "My Tests"
    override val tabMyApps = "My Apps"
    override val tabGuide = "Guide"
    override val tabSettings = "Profile"
    override val tabAdmin = "Owner"

    override val appTitle = "PlayTester Hub"
    override val ownerBadge = "👑 Owner"
    override val pointsLabel = "Points"
    override val streakLabel = "Days Streak"
    override val toggleThemeDesc = "Toggle Dark/Light Mode"
    override val notificationsDesc = "Notifications"
    override val languageToggleDesc = "Change Language"
    override val switchLanguage = "العربية"

    override val marketTitle = "Apps Needing Testers"
    override val searchPlaceholder = "Search app or developer..."
    override val sortLabel = "Sort by:"
    override val sortPriority = "⭐ Priority"
    override val sortUrgent = "🔥 Most Needed"
    override val sortReward = "💎 Highest Reward"
    override val categoryAll = "All"
    override val categoryTools = "Tools"
    override val categoryGames = "Games"
    override val categoryProductivity = "Productivity"
    override val categorySocial = "Social"
    override val categoryEducation = "Education"
    override val categoryFinance = "Finance"
    override val reciprocalTitle = "1-on-1 Reciprocal Matching"
    override val reciprocalDesc = "Mutual agreement to test each other's app daily for 14 days with bonus points"
    override val reciprocalButton = "View Partnerships →"
    override val cloudSyncTitle = "Cloud Sync"
    override val cloudSyncCheck = "Cloud Health"
    override val importAppBtn = "Import Link"
    override val publishNewAppBtn = "Publish App"
    override val joinTestBtn = "Join Testing"
    override val joinedTestBtn = "Already Joined ✓"
    override val openAppBtn = "Open App"
    override val testersNeededFmt = { current: Int, target: Int -> "$current / $target Testers" }
    override val rewardPointsFmt = { points: Int -> "+$points Pts" }
    override val noAppsFound = "No apps found matching your search"
    override val clearFilters = "Reset Filters"

    override val testingActiveCount = "Apps Being Tested"
    override val testingCompletedCount = "14 Days Completed"
    override val daysCompletedFmt = { days: Int -> "Day $days of 14" }
    override val dailyCheckIn = "Confirm Daily Test (+25 Pts)"
    override val checkInDoneToday = "Tested Today ✓"
    override val sendFeedbackBtn = "Send Feedback / Bug Report"
    override val openGooglePlayBtn = "Open on Google Play"
    override val deleteJoinedTest = "Leave Test"
    override val noTestsJoinedYet = "You haven't joined any tests yet!"
    override val browseMarketBtn = "Browse App Market"
    override val dailyReminderSwitch = "Daily Testing Reminder"
    override val streakBonusNotice = "Daily streaks earn you bonus points and achievement badges"

    override val myAppsTitle = "Your Published Apps"
    override val postAppHeroCta = "Publish New App for 20 Testers 🚀"
    override val playApprovalSecretsTitle = "Google Play Approval Secrets 🌟"
    override val playApprovalSecretsSubtitle = "How to pass production review without rejection on your first try"
    override val viewSecretsBtn = "View Secrets →"
    override val noAppsPublishedYet = "You haven't published any apps yet. Publish now to get 20 testers for free!"
    override val shareInviteBtn = "Share Invite"
    override val groupLinkBtn = "Group Link"
    override val editGroupLink = "Edit Group Link"
    override val storeLinkBtn = "Store Link"
    override val timeline14DaysBtn = "14-Day Timeline ⏱️"
    override val consoleHelperBtn = "Console Helper 🚀"
    override val readinessCertificateBtn = "View Readiness Certificate 🏅"
    override val manageTestersBtn = "Manage Testers 👥"
    override val manageVersionsBtn = "Manage Versions 📦"
    override val policyLinterBtn = "Policy Linter 🛡️"
    override val checklistBtn = "Pre-Flight Checklist 📋"
    override val changelogBtn = "Changelog Generator 📜"
    override val deviceDiversityBtn = "Device Diversity 📱"
    override val privacyPolicyBtn = "Privacy Policy 🔒"
    override val linkValidatorBtn = "Validate Links 🔍"
    override val deleteAppBtn = "Delete App"
    override val devToolsSectionTitle = "🛠️ Advanced Console & Review Tools"
    override val collapseTools = "Collapse Tools ▲"
    override val expandTools = "Show All Console Tools (10 Tools) ▼"
    override val uploadToCloudBtn = "Upload to Cloud"
    override val uploadSuccessMsg = "Apps uploaded successfully"

    override val guideTitle = "Google Play Closed Testing Developer Guide"
    override val guideSubtitle = "Step-by-step roadmap to recruit 20 testers and pass the 14-day trial smoothly"
    override val guideTabSteps = "Launch Steps"
    override val guideTabConsole = "Play Policies"
    override val guideTabFaq = "FAQs"
    override val guideTabFiles = "Bundle & Mapping Files"
    override val bundleAndMappingTitle = "Bundle (.aab) and Mapping (mapping.txt) Files Explained"
    override val bundleExplanation = "The Android App Bundle (.aab) is the main file containing your app's code. Upload it to Google Play Console under the Closed Testing track."
    override val mappingExplanation = "The mapping file (mapping.txt) is for crash de-obfuscation. Upload it optionally in the Console next to your bundle to view readable crash stack traces."
    override val copyGroupEmail = "Copy Shared Group Email"
    override val groupEmailCopied = "Group email copied to clipboard"
    override val optInTipTitle = "Important Note on Opt-in Links:"
    override val optInTipBody = "Opt-in links only work for accounts you have explicitly added in Google Play Console (via an email list or Google Group)."

    override val profileTitle = "Profile & Settings"
    override val appLanguageTitle = "App Language / لغة التطبيق"
    override val appLanguageDesc = "Choose your preferred platform language"
    override val accountSection = "Account & Security"
    override val displayNameLabel = "Display Name"
    override val editName = "Edit Name"
    override val passwordLabel = "Password"
    override val googleSignedIn = "Signed in with Google Account"
    override val changeBtn = "Change"
    override val verifiedEmail = "Verified Email"
    override val sendTestEmail = "Send Test"
    override val themeSection = "Theme & Appearance"
    override val themeModeTitle = "Theme & Appearance Mode"
    override val themeModeDesc = "Choose your preferred theme mode for eye comfort and battery savings:"
    override val themeModeDark = "Dark Mode"
    override val themeModeLight = "Light Mode"
    override val themeModeSystem = "System Default"
    override val themeAuto = "Auto"
    override val themeAutoSub = "System Default"
    override val themeLight = "Light"
    override val themeLightSub = "Clean Day"
    override val themeDark = "Dark"
    override val themeDarkSub = "Comfort Night"
    override val darkModeActive = "Dark mode is currently active"
    override val lightModeActive = "Light mode is currently active"
    override val simplifiedModeTitle = "Simplified Mode (Declutter)"
    override val simplifiedModeSubtitle = "Hide extra cards and focus only on primary daily tasks"
    override val simplifiedModeHubTitle = "Simplified Mode (Quick Hub)"
    override val simplifiedModeHubDesc = "Show quick action hub on the main screen"
    override val backupRestoreTitle = "Cloud Backup & Restore"
    override val exportBackupBtn = "Export JSON Backup"
    override val restoreBackupBtn = "Restore Previous Backup"
    override val backupJsonTitle = "Backup & Restore Data (JSON)"
    override val backupJsonDesc = "Safely export or restore your points, apps, and testing logs"
    override val manageBackupBtn = "Manage Backup"
    override val systemAndPreferences = "Device Preferences & Alerts"
    override val testDeviceEnvTitle = "Testing Device Specifications"
    override val testDeviceEnvDesc = "Specify your phone model & OS version to aid in 14-day compatibility reports:"
    override val deviceModelLabel = "Device Model"
    override val androidVersionLabel = "Android OS Version"
    override val hapticFeedbackTitle = "Haptic Feedback"
    override val hapticFeedbackDesc = "Gentle haptic pulse when confirming tests and submitting feedback"
    override val savePreferences = "Save Preferences"
    override val saveDeviceSpecsBtn = "Save Device Specifications"
    override val badgesAndRankTitle = "Badges & Achievements"
    override val achievementsTitle = "Achievements & Ranks"
    override val viewBadgesBtn = "View Badges 🏆"
    override val pointsBalanceLabel = "Points Balance"
    override val activeStreakLabel = "Active Streak"
    override val testsCompletedLabel = "Tests Completed"
    override val memberSinceLabel = "Member since:"
    override val roleAdmin = "👑 Platform Owner (Admin)"
    override val roleTester = "⭐ Verified Developer (Tester)"
    override val notificationsSettingsTitle = "Advanced Notifications & Alerts"
    override val notifPermGranted = "System Notifications: Enabled & Ready"
    override val notifPermNeeded = "System Notifications: Permission Needed"
    override val notifPermDescActive = "You will receive daily testing reminders and tester updates."
    override val notifPermDescInactive = "Tap to grant notification permission to safeguard your streak."
    override val enableBtn = "Enable"
    override val sendTestNotifBtn = "Send Instant Test Notification"
    override val dailyStreakReminderTitle = "Daily Testing Reminder (14-Day Streak)"
    override val dailyStreakReminderDesc = "Receive daily alert to open & test apps and maintain streak"
    override val preferredReminderTime = "Preferred Daily Reminder Time:"
    override val newTestersAlertsTitle = "New Testers Joined Alerts"
    override val newTestersAlertsDesc = "Instant notification when a new tester joins your app"
    override val newAppsAlertsTitle = "New Community Apps Alerts"
    override val newAppsAlertsDesc = "Notification when new apps need testers to earn fast points"
    override val readinessReportAlertsTitle = "Readiness & Completion Alerts"
    override val readinessReportAlertsDesc = "Notification when an app hits 20 testers & completes 14 days"
    override val interfaceAndSystem = "Interface & System"
    override val replayOnboardingTitle = "Replay Interactive Onboarding"
    override val replayOnboardingDesc = "Learn about 20-tester closed testing rules and app workflow"
    override val startTourBtn = "Start Tour"
    override val aboutAndComplianceTitle = "About Platform & Compliance"
    override val appVersionLabel = "App Version"
    override val googlePlay14DaysStandard = "Google Play 14-Day Standard"
    override val compliant100 = "100% Compliant"
    override val complianceNotice = "The platform ensures tracking 20 real testers via Google Groups and official Play Store closed testing links."
    override val accountActionsTitle = "Account Actions"
    override val signOutBtn = "Sign Out from Account"
    override val deleteAccountPermBtn = "Delete Account Permanently"
    override val checkForUpdatesBtn = "Check for Updates"
    override val logoutBtn = "Log Out"
    override val deleteAccountBtn = "Delete Account Permanently"
    override val declutterNotice = "Screens have been optimized and decluttered for faster access to tools"

    override val updateBannerTitle = "🚀 New App Update Available!"
    override val updateBannerSubtitleFmt = { v: String -> "A newer version v$v is available on Google Play with new features and performance enhancements." }
    override val updateNowBtn = "Update Now ⚡"
    override val updateDialogTitle = "New Update Available on Google Play"
    override val updateWhatsNewTitle = "What's new in this update?"
    override val updateDefaultReleaseNotes = "Improved app performance, new exchange community features, and faster sync with Google Play."
    override val remindMeLaterBtn = "Remind Me Later"
}
