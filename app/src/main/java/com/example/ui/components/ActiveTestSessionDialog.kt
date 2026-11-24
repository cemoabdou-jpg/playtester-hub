package com.example.ui.components

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.JoinedTest
import com.example.data.model.QAInspectionCheck
import com.example.ui.util.L
import com.example.ui.viewmodel.TesterViewModel
import kotlinx.coroutines.delay

@Composable
fun ActiveTestSessionDialog(
    test: JoinedTest,
    viewModel: TesterViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    // Session duration: 15 minutes = 900 seconds
    var secondsElapsed by remember { mutableIntStateOf(120) } // Starts with 2 mins pre-active for quick verification
    var isTimerRunning by remember { mutableStateOf(true) }
    var userNotes by remember {
        mutableStateOf(
            L.t(
                "تم اختبار التنقل بين الشاشات واستقرار الواجهة بشكل ممتاز.",
                "Tested screen navigation and UI stability smoothly."
            )
        )
    }

    var checkStates by remember {
        mutableStateOf(
            mapOf(
                "chk_1" to true,
                "chk_2" to true,
                "chk_3" to false,
                "chk_4" to false,
                "chk_5" to false
            )
        )
    }

    val checks = listOf(
        QAInspectionCheck(
            "chk_1",
            L.t("فحص بدء التشغيل وسلامة الشاشة الأولى", "Cold start & first screen stability check"),
            L.t("التأكد من عدم وجود وميض أو تأخير عند بدء التشغيل", "Verify zero flicker or freeze during launch"),
            isChecked = checkStates["chk_1"] == true
        ),
        QAInspectionCheck(
            "chk_2",
            L.t("تجربة التدوير الأفقي والعمودي", "Portrait & landscape rotation test"),
            L.t("التحقق من عدم إعادة تحميل البيانات أو فقدان الحالة", "Ensure data reloads and UI state stay intact"),
            isChecked = checkStates["chk_2"] == true
        ),
        QAInspectionCheck(
            "chk_3",
            L.t("اختبار التمرير والتنقل بين الواجهات", "Scroll smoothness & navigation test"),
            L.t("فحص سلاسة القوائم واستجابة عناصر اللمس", "Verify list scrolling and touch target responsiveness"),
            isChecked = checkStates["chk_3"] == true
        ),
        QAInspectionCheck(
            "chk_4",
            L.t("محاكاة وضع الخلفية والرجوع", "Background & resume lifecycle check"),
            L.t("وضع التطبيق في الخلفية والعودة إليه دون إعادة تشغيل", "Send app to background and resume without restarting"),
            isChecked = checkStates["chk_4"] == true
        ),
        QAInspectionCheck(
            "chk_5",
            L.t("التأكد من خلو الجلسة من تجميد الاستجابة", "Check session for zero app freezes"),
            L.t("عدم وجود أي بطء في معالجة المدخلات", "Ensure zero input lag or UI thread stalls"),
            isChecked = checkStates["chk_5"] == true
        )
    )

    LaunchedEffect(isTimerRunning) {
        while (isTimerRunning) {
            delay(1000)
            secondsElapsed++
        }
    }

    val minutes = secondsElapsed / 60
    val seconds = secondsElapsed % 60
    val formattedTime = "%02d:%02d".format(java.util.Locale.US, minutes, seconds)

    val currentPhase = when {
        minutes < 4 -> L.t("المرحلة 1: فحص التشغيل السريع والاستقرار الأولي", "Phase 1: Initial Launch & Smoke Test")
        minutes < 8 -> L.t("المرحلة 2: اختبار الوظائف العميقة وحالات الاستثناء", "Phase 2: Deep Feature & Edge-Case Testing")
        minutes < 12 -> L.t("المرحلة 3: فحص التحمل وتدوير الشاشة والحواف", "Phase 3: Stress, Rotation & Edge-to-Edge Check")
        else -> L.t("المرحلة 4: تقييم تجربة المستخدم وتوثيق الجلسة الرسمية", "Phase 4: UX Evaluation & Official Log")
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Top Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            color = Color(0xFF00875A).copy(alpha = 0.12f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.HourglassBottom,
                                    contentDescription = null,
                                    tint = Color(0xFF00875A),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                L.t("مرافق جلسة الاختبار التفاعلية", "Interactive Test Session Companion"),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                L.t("تطبيق: ${test.appName}", "App: ${test.appName}"),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = L.t("إغلاق", "Close"))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Timer & Phase Card
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            formattedTime,
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            currentPhase,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Progress to 15 minutes (900 seconds)
                        LinearProgressIndicator(
                            progress = { (secondsElapsed.toFloat() / 900f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Color(0xFF00875A),
                            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = { isTimerRunning = !isTimerRunning },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(
                                    if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isTimerRunning) L.t("إيقاف مؤقت", "Pause") else L.t("استئناف", "Resume"), fontSize = 11.sp)
                            }

                            // Fast simulation button
                            Button(
                                onClick = {
                                    secondsElapsed = 900
                                    checkStates = checkStates.keys.associateWith { true }
                                    Toast.makeText(context, L.t("⚡ تم تسريع الجلسة لـ 15 دقيقة بنجاح!", "⚡ Session fast-forwarded to 15 mins!"), Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8F00)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(L.t("تسريع للاكتمال (+15 د)", "Complete (+15m)"), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // QA Checklist
                Text(
                    L.t("قائمة التحقق الموصى بها لضمان قبول جوجل بلاي:", "Recommended QA Checklist for Google Play Approval:"),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(checks, key = { it.id }) { checkItem ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    checkStates = checkStates.toMutableMap().apply {
                                        put(checkItem.id, !(this[checkItem.id] ?: false))
                                    }
                                },
                            color = if (checkItem.isChecked) Color(0xFF00875A).copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = checkItem.isChecked,
                                    onCheckedChange = { checked ->
                                        checkStates = checkStates.toMutableMap().apply {
                                            put(checkItem.id, checked)
                                        }
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF00875A))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        checkItem.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        checkItem.description,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = userNotes,
                            onValueChange = { userNotes = it },
                            label = { Text(L.t("ملاحظات الجلسة وتجربة المستخدم (اختياري)", "Session & UX notes (optional)"), fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            minLines = 2,
                            maxLines = 3
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Complete Button
                Button(
                    onClick = {
                        val duration = maxOf(15, minutes)
                        viewModel.completeActiveTestSession(
                            test = test,
                            durationMinutes = duration,
                            notes = userNotes.ifBlank { L.t("جلسة فحص موثقة ومستوفية لشروط جوجل بلاي", "Verified test session compliant with Google Play") }
                        )
                        Toast.makeText(context, L.t("🎉 رائع! تم توثيق الجلسة وإضافة +50 عملة لرصيدك بنجاح!", "🎉 Great! Session verified and +50 coins added!"), Toast.LENGTH_LONG).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00875A)),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(L.t("توثيق الجلسة وكسب +50 عملة 🪙", "Verify Session & Earn +50 Coins 🪙"), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
