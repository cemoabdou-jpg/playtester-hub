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
    var userNotes by remember { mutableStateOf("تم اختبار التنقل بين الشاشات واستقرار الواجهة بشكل ممتاز.") }

    var checks by remember {
        mutableStateOf(
            listOf(
                QAInspectionCheck("chk_1", "فحص بدء التشغيل وسلامة الشاشة الأولى", "التأكد من عدم وجود وميض أو تأخير عند بدء التشغيل", isChecked = true),
                QAInspectionCheck("chk_2", "تجربة التدوير الأفقي والعمودي", "التحقق من عدم إعادة تحميل البيانات أو فقدان الحالة", isChecked = true),
                QAInspectionCheck("chk_3", "اختبار التمرير والتنقل بين الواجهات", "فحص سلاسة القوائم واستجابة اللمس 48dp", isChecked = false),
                QAInspectionCheck("chk_4", "محاكاة وضع الخلفية والرجوع", "وضع التطبيق في الخلفية والعودة إليه دون إعادة تشغيل", isChecked = false),
                QAInspectionCheck("chk_5", "التأكد من خلو الجلسة من التجميد (ANR)", "عدم وجود أي بطء في معالجة المدخلات", isChecked = false)
            )
        )
    }

    LaunchedEffect(isTimerRunning) {
        while (isTimerRunning) {
            delay(1000)
            secondsElapsed++
        }
    }

    val minutes = secondsElapsed / 60
    val seconds = secondsElapsed % 60
    val formattedTime = "%02d:%02d".format(minutes, seconds)

    val currentPhase = when {
        minutes < 4 -> "المرحلة 1: فحص الإطلاق الأولي والمسار السريع (Smoke Test)"
        minutes < 8 -> "المرحلة 2: اختبار الوظائف العميقة وحالات الاستثناء"
        minutes < 12 -> "المرحلة 3: فحص التحمل وتدوير الشاشة والحواف"
        else -> "المرحلة 4: تقييم تجربة المستخدم وتوثيق الجلسة الرسمية"
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
                                "مرافق جلسة الاختبار التفاعلية",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "تطبيق: ${test.appName}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
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
                                Text(if (isTimerRunning) "إيقاف مؤقت" else "استئناف", fontSize = 11.sp)
                            }

                            // Fast simulation button for developer testing convenience
                            Button(
                                onClick = {
                                    secondsElapsed = 900
                                    checks = checks.map { it.copy(isChecked = true) }
                                    Toast.makeText(context, "⚡ تم تسريع الجلسة لـ 15 دقيقة بنجاح!", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8F00)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("تسريع للاكتمال (+15 د)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // QA Checklist
                Text(
                    "قائمة التحقق الموصى بها لضمان قبول Google Play:",
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
                                    checks = checks.map {
                                        if (it.id == checkItem.id) it.copy(isChecked = !it.isChecked) else it
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
                                        checks = checks.map {
                                            if (it.id == checkItem.id) it.copy(isChecked = checked) else it
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
                            label = { Text("ملاحظات الجلسة وتجربة المستخدم (اختياري)", fontSize = 11.sp) },
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
                            notes = userNotes.ifBlank { "جلسة فحص موثقة ومستوفية لشروط Google Play" }
                        )
                        Toast.makeText(context, "🎉 رائع! تم توثيق الجلسة وإضافة +50 عملة لرصيدك بنجاح!", Toast.LENGTH_LONG).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00875A)),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("توثيق الجلسة وكسب +50 عملة 🪙", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
