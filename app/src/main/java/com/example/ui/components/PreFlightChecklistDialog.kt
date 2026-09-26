package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserApp
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DangerRed
import com.example.ui.theme.PlayGreen
import com.example.ui.theme.SuccessGreen

data class PreFlightItem(
    val id: String,
    val title: String,
    val description: String,
    val penaltyIfFailed: String,
    val isCrucial: Boolean = true
)

@Composable
fun PreFlightChecklistDialog(
    app: UserApp?,
    onDismiss: () -> Unit
) {
    val checklistItems = remember {
        listOf(
            PreFlightItem(
                id = "testers_14_days",
                title = "20 مختبراً نشطاً لمدة 14 يوماً متواصلة",
                description = "تأكد من أن 20 مختبراً على الأقل ظلوا مشتركين في المسار المغلق ولم ينسحبوا قبل انتهاء اليوم الـ 14.",
                penaltyIfFailed = "إعادة دورة الـ 14 يوماً من الصفر إذا انخفض العدد عن 20."
            ),
            PreFlightItem(
                id = "target_sdk_34",
                title = "استهداف Target SDK 34 (Android 14) أو أحدث",
                description = "تشترط Google لجميع التطبيقات والترقيات في 2026 استهداف مستوى API 34 كحد أدنى.",
                penaltyIfFailed = "رفض حزمة AAB فوراً عند الرفع في كونسول المطورين."
            ),
            PreFlightItem(
                id = "privacy_policy_active",
                title = "رابط سياسة خصوصية نشط ويعمل بـ HTTPS",
                description = "يجب أن يكون الرابط متاحاً للعامة ويذكر اسم التطبيق الصريح ونوع البيانات وبيانات المطور.",
                penaltyIfFailed = "السبب الأكبر لرفض طلب الإنتاج أو تعليق الحساب."
            ),
            PreFlightItem(
                id = "data_safety_form",
                title = "إكمال استبيان أمان البيانات (Data Safety Section)",
                description = "التصريح الدقيق عن كل إذن يطلبه التطبيق (الكاميرا، الموقع، التخزين، إلخ) والغرض منه.",
                penaltyIfFailed = "إيقاف مراجعة التطبيق حتى تصحيح التضارب."
            ),
            PreFlightItem(
                id = "release_update_feedback",
                title = "إطلاق تحديث واحد على الأقل وتلقي ملاحظات فعلية",
                description = "تتأكد لجان مراجعة Google من أن المطور تواصل مع المختبرين وحل مشاكلهم عبر تحديث جديد.",
                penaltyIfFailed = "رفض طلب الإنتاج لعدم وجود أدلة كافية على الاختبار الحقيقي."
            ),
            PreFlightItem(
                id = "store_graphics_assets",
                title = "أصول المتجر (أيقونة 512x512، لافتة مميزة، لقطات شاشة)",
                description = "لقطات شاشة حقيقية توضح وظائف التطبيق خالية من صور الهواتف المضللة.",
                penaltyIfFailed = "رفض قائمة المتجر (Store Listing rejection)."
            ),
            PreFlightItem(
                id = "app_access_credentials",
                title = "بيانات دخول تجريبية (App Access) إذا كان يتطلب تسجيلاً",
                description = "إذا كان التطبيق يتطلب تسجيل دخول أو OTP، يجب تقديم حساب تجريبي وكلمة مرور لفريق مراجعة جوجل.",
                penaltyIfFailed = "رفض فوري لتعذر وصول مراجعي جوجل للميزات الداخلية."
            ),
            PreFlightItem(
                id = "content_rating_cert",
                title = "استبيان تصنيف المحتوى واستخراج شهادة IARC",
                description = "الإجابة بدقة عن أسئلة العنف واللغة والإعلانات في كونسول المطورين.",
                penaltyIfFailed = "تعليق النشر لغياب التصنيف العمري المعتمد."
            )
        )
    }

    val checkedStates = remember {
        mutableStateMapOf<String, Boolean>().apply {
            checklistItems.forEach { put(it.id, false) }
        }
    }

    val completedCount by remember {
        derivedStateOf { checkedStates.values.count { it } }
    }

    val progressPercent by remember {
        derivedStateOf {
            if (checklistItems.isEmpty()) 0f
            else completedCount.toFloat() / checklistItems.size.toFloat()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.FactCheck,
                    contentDescription = null,
                    tint = if (completedCount == checklistItems.size) SuccessGreen else PlayGreen,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "قائمة فحص الامتثال قبل طلب الإنتاج",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "تأكد من استيفاء كافة الشروط لمنع رفض جوجل المفاجئ",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Readiness Score Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(
                        1.dp,
                        if (completedCount == checklistItems.size) SuccessGreen else PlayGreen
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "مستوى الجاهزية للتقديم:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$completedCount من ${checklistItems.size} (${(progressPercent * 100).toInt()}%)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (completedCount == checklistItems.size) SuccessGreen else PlayGreen
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { progressPercent },
                            modifier = Modifier.fillMaxWidth().height(8.dp),
                            color = if (completedCount == checklistItems.size) SuccessGreen else PlayGreen
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (completedCount == checklistItems.size)
                                "🎉 ممتاز جداً! تطبيقك جاهز بنسبة 100% للتقديم على الإنتاج (Apply for Production) دون أي مخاطرة."
                            else
                                "⚠️ تبقى لديك ${checklistItems.size - completedCount} متطلبات لم تكتمل بعد. لا تضغط 'Apply for Production' قبل إكمالها!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = "المعايير الإلزامية لمراجعي Google Play:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                // List of items
                checklistItems.forEachIndexed { index, item ->
                    val isChecked = checkedStates[item.id] == true
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isChecked) SuccessGreen.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(
                            1.dp,
                            if (isChecked) SuccessGreen.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { checkedStates[item.id] = !isChecked }
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { checkedStates[item.id] = it },
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${index + 1}. ${item.title}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isChecked) SuccessGreen else MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = item.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                                if (!isChecked) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = AmberGold,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "عقوبة الإهمال: ${item.penaltyIfFailed}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = AmberGold,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = PlayGreen),
                modifier = Modifier.testTag("btn_close_preflight_dialog")
            ) {
                Text("إغلاق")
            }
        }
    )
}
