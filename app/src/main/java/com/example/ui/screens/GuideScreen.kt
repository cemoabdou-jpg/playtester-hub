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
                        "📘 دليل اجتياز متطلبات Google Play",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "خطوات معتمدة لتفادي الرفض واستيفاء الـ 14 يوماً",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            CleanGuideCard(
                stepNum = "1",
                title = "إعداد مسار الاختبار المغلق (Closed Track)",
                desc = "أنشئ مسار اختبار مغلق في Google Play Console وأضف رابط مجموعة Google Group للمختبرين لتمكين التحميل."
            )
        }
        item {
            CleanGuideCard(
                stepNum = "2",
                title = "جمع الـ 20 مختبراً وتأكيد الـ Opt-in",
                desc = "يجب على 20 مختبراً على الأقل النقر على رابط الانضمام وتثبيت التطبيق على أجهزتهم الحقيقية."
            )
        }
        item {
            CleanGuideCard(
                stepNum = "3",
                title = "التفاعل المستمر لـ 14 يوماً متواصلة",
                desc = "لا يكفي مجرد التثبيت؛ يقوم نظام PlayTester Hub بتذكير المختبرين بفتح التطبيق يومياً لضمان نشاط الحسابات."
            )
        }
        item {
            CleanGuideCard(
                stepNum = "4",
                title = "استخراج تقرير التدقيق الرسمي والتقديم للإنتاج",
                desc = "استخدم زر 'تقرير التدقيق الرسمي' في PlayTester Hub لإرفاق مستند الإثبات والإجابة على استبيان Google بنجاح."
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
