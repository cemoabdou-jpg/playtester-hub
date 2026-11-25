package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserApp
import com.example.ui.util.L

private data class GuideFaqItem(
    val id: String,
    val question: String,
    val answer: String
)

@Composable
fun GuideScreen(
    userApps: List<UserApp> = emptyList(),
    onNavigateToMyApps: () -> Unit = {}
) {
    val primaryApp = userApps.firstOrNull()

    val checklistItems = remember(primaryApp, L.currentLanguage) {
        listOf(
            Triple(
                L.t("تسجيل تطبيقك على المنصة", "Register your app on PlayTester Hub"),
                primaryApp != null,
                if (primaryApp != null) primaryApp.name else L.t("أضف تطبيقك من تبويب تطبيقاتي", "Add your app in My Apps tab")
            ),
            Triple(
                L.t("ربط رابط مجموعة جوجل للمختبرين", "Link tester Google Group URL"),
                primaryApp?.googleGroupUrl?.isNotBlank() == true,
                L.t("يسمح للمختبرين بالتسجيل التلقائي", "Allows testers to opt in cleanly")
            ),
            Triple(
                L.t("تعيين رابط الانضمام للاختبار المغلق", "Configure Play Opt-In URL"),
                primaryApp?.webOptInUrl?.isNotBlank() == true,
                L.t("مطلوب لفتح صفحة التثبيت على متجر بلاي", "Required for testers to install from Play Store")
            ),
            Triple(
                L.t("الوصول إلى 20 مختبراً منضمّين", "Reach 20 opted-in testers"),
                (primaryApp?.currentTesters ?: 0) >= 20,
                L.t("التقدم: ${primaryApp?.currentTesters ?: 0}/20", "Progress: ${primaryApp?.currentTesters ?: 0}/20")
            ),
            Triple(
                L.t("استكمال 14 يوماً متواصلة من الفحص", "Complete 14 continuous testing days"),
                (primaryApp?.completedDays ?: 0) >= 14,
                L.t("التقدم: ${primaryApp?.completedDays ?: 0}/14", "Progress: ${primaryApp?.completedDays ?: 0}/14")
            )
        )
    }

    val metCount = remember(checklistItems) { checklistItems.count { it.second } }

    val faqs = remember(L.currentLanguage) {
        listOf(
            GuideFaqItem(
                id = "faq_1",
                question = L.t(
                    "لماذا قد ترفض جوجل بلاي طلب الإنتاج رغم وجود 20 مختبراً؟",
                    "Why might Google Play reject Production access despite having 20 testers?"
                ),
                answer = L.t(
                    "غالباً بسبب نقص التفاعل اليومي الفعلي خلال الـ 14 يوماً أو عدم تثبيت التطبيق من بعض المختبرين. تأكد من فتح التطبيق يومياً ومن وجود مراجعات وبلاغات أعطال محلولة قبل التقديم.",
                    "Usually due to low daily engagement across the 14 days or testers opting in without installing. Ensure daily active opens and document bug fixes before applying."
                )
            ),
            GuideFaqItem(
                id = "faq_2",
                question = L.t(
                    "هل يكفي انضمام المختبر للمجموعة دون فتح رابط الويب؟",
                    "Is joining the Google Group enough without opening the web opt-in link?"
                ),
                answer = L.t(
                    "لا؛ يجب على كل مختبر الانضمام لمجموعة جوجل أولاً ثم فتح رابط الاختبار المغلق (Opt-In) والضغط على 'كن مختبراً' ثم تثبيت التطبيق من متجر بلاي.",
                    "No; each tester must join the Google Group first, then open the web Opt-In link, tap 'Become a tester', and install from Google Play."
                )
            ),
            GuideFaqItem(
                id = "faq_3",
                question = L.t(
                    "ماذا أكتب في استبيان الإنتاج الخاص بجوجل بلاي؟",
                    "What should I write in the Google Play Production questionnaire?"
                ),
                answer = L.t(
                    "استخدم مصفوفة الـ 14 يوماً داخل تطبيقك لنسخ إجابات واضحة ومخصصة توضح كيف استقطبت المختبرين، التغييرات التي نفذتها بناءً على ملاحظاتهم، وكيف تحققت من الاستقرار.",
                    "Open the 14-Day Compliance Matrix for your app to copy tailored answers covering recruitment, feedback-driven UI/bug improvements, and crash stability."
                )
            )
        )
    }

    var expandedFaqId by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Text(
                    L.t(
                        "📘 دليل اجتياز متطلبات متجر جوجل بلاي",
                        "📘 Google Play Closed Testing Guide"
                    ),
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    L.t(
                        "تحقق من جاهزية تطبيقك خطوة بخطوة وتفادَ أسباب الرفض الشائعة",
                        "Check your app readiness step by step and avoid common rejection reasons"
                    ),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Interactive Readiness Checklist Card
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            if (primaryApp != null)
                                L.t("قائمة جاهزية: ${primaryApp.name}", "Readiness Checklist: ${primaryApp.name}")
                            else
                                L.t("قائمة الجاهزية التفاعلية", "Interactive Readiness Checklist"),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Surface(
                            color = if (metCount == 5) Color(0xFF00875A) else MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "$metCount / 5",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    LinearProgressIndicator(
                        progress = { metCount / 5f },
                        modifier = Modifier.fillMaxWidth().height(6.dp),
                        color = if (metCount == 5) Color(0xFF00875A) else MaterialTheme.colorScheme.primary
                    )

                    checklistItems.forEach { (title, done, detail) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (done) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (done) Color(0xFF00875A) else MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Text(detail, fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    if (metCount < 5) {
                        OutlinedButton(
                            onClick = onNavigateToMyApps,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("guide_go_to_my_apps_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Apps, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                if (primaryApp == null) L.t("أضف تطبيقك الأول في تطبيقاتي", "Add Your First App in My Apps")
                                else L.t("تحديث بيانات التطبيق في تطبيقاتي", "Update App Progress in My Apps"),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // 4 Standard Guide Steps
        item {
            CleanGuideCard(
                stepNum = "1",
                title = L.t("إعداد مسار الاختبار المغلق", "Set Up Closed Testing Track"),
                desc = L.t(
                    "أنشئ مسار اختبار مغلق في لوحة مطوري جوجل بلاي وأضف رابط مجموعة جوجل للمختبرين لتمكين التحميل.",
                    "Create a closed testing track in Google Play Console and link your tester Google Group to enable opt-in downloads."
                )
            )
        }
        item {
            CleanGuideCard(
                stepNum = "2",
                title = L.t("جمع 20 مختبراً وتأكيد الانضمام", "Recruit 20 Opted-In Testers"),
                desc = L.t(
                    "يجب على 20 مختبراً على الأقل النقر على رابط الانضمام وتثبيت التطبيق على أجهزتهم الحقيقية.",
                    "At least 20 real testers must open your opt-in invitation URL and install the app on physical Android devices."
                )
            )
        }
        item {
            CleanGuideCard(
                stepNum = "3",
                title = L.t("التفاعل المستمر لـ 14 يوماً متواصلة", "Daily Active Engagement for 14 Days"),
                desc = L.t(
                    "لا يكفي مجرد التثبيت؛ يقوم نظام المنصة بتذكير المختبرين بفتح التطبيق يومياً لضمان نشاط الحسابات.",
                    "Installing alone isn't enough; PlayTester Hub reminds testers daily to open and interact with your app consistently."
                )
            )
        }
        item {
            CleanGuideCard(
                stepNum = "4",
                title = L.t(
                    "استخراج تقرير التدقيق الرسمي والتقديم للإنتاج",
                    "Export Audit Dossier & Apply for Production"
                ),
                desc = L.t(
                    "استخدم زر تقرير التدقيق الرسمي في المنصة لإرفاق مستند الإثبات والإجابة على استبيان المراجعة بنجاح.",
                    "Use the Official Audit Report tool in PlayTester Hub to export verification proofs and answer the Play Console questionnaire."
                )
            )
        }

        // Expandable Google Play Rejection FAQ
        item {
            Text(
                L.t("❓ أسئلة شائعة وتفادي رفض جوجل بلاي", "❓ Common Questions & Avoiding Rejection"),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.padding(top = 6.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        items(faqs, key = { it.id }) { faq ->
            val isExpanded = expandedFaqId == faq.id
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedFaqId = if (isExpanded) null else faq.id }
                    .testTag("guide_${faq.id}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            faq.question,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = L.t("عرض الإجابة", "Toggle answer"),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    AnimatedVisibility(visible = isExpanded) {
                        Column {
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                faq.answer,
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CleanGuideCard(stepNum: String, title: String, desc: String) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        stepNum,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    desc,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
