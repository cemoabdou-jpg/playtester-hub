package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.ui.util.L

@Composable
fun GuideScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
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
                            "خطوات معتمدة لتفادي الرفض واستيفاء 14 يوماً",
                            "Proven steps to avoid rejection & pass 14 continuous days"
                        ),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

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
