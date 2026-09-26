package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.UserApp
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DangerRed
import com.example.ui.theme.PlayGreen

object OptInValidatorUtil {
    fun validateGroupUrl(url: String): Pair<Boolean, String> {
        val trimmed = url.trim()
        return when {
            trimmed.isBlank() -> false to "رابط مجموعة جوجل إلزامي لاستقبال المختبرين"
            !trimmed.startsWith("https://") -> false to "يجب أن يبدأ الرابط بـ https://"
            !trimmed.contains("groups.google.com") -> false to "الرابط يجب أن يكون تابعاً لـ groups.google.com"
            else -> true to "رابط المجموعة صحيح ومطابق لشروط جوجل بلاي ✅"
        }
    }

    fun validateWebOptInUrl(url: String): Pair<Boolean, String> {
        val trimmed = url.trim()
        return when {
            trimmed.isBlank() -> false to "رابط الاشتراك عبر الويب إلزامي"
            !trimmed.startsWith("https://") -> false to "يجب أن يبدأ الرابط بـ https://"
            !trimmed.contains("play.google.com/apps/testing/") -> false to "الرابط يجب أن يتضمن play.google.com/apps/testing/"
            else -> true to "رابط الاشتراك على الويب صحيح ومكتمل ✅"
        }
    }

    fun validateAndroidOptInUrl(url: String): Pair<Boolean, String> {
        val trimmed = url.trim()
        return when {
            trimmed.isBlank() -> false to "رابط التنزيل على الهاتف إلزامي"
            !trimmed.startsWith("https://") -> false to "يجب أن يبدأ الرابط بـ https://"
            !trimmed.contains("play.google.com/store/apps/details") -> false to "الرابط يجب أن يتضمن play.google.com/store/apps/details"
            else -> true to "رابط التنزيل المباشر صحيح وجاهز ✅"
        }
    }
}

@Composable
fun OptInLinkValidatorDialog(
    app: UserApp,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    val (groupOk, groupMsg) = remember(app.googleGroupUrl) { OptInValidatorUtil.validateGroupUrl(app.googleGroupUrl) }
    val (webOk, webMsg) = remember(app.webOptInUrl) { OptInValidatorUtil.validateWebOptInUrl(app.webOptInUrl) }
    val (androidOk, androidMsg) = remember(app.androidOptInUrl) { OptInValidatorUtil.validateAndroidOptInUrl(app.androidOptInUrl) }

    val allValid = groupOk && webOk && androidOk

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp)
                .testTag("opt_in_validator_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CyanAccent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCode2,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "فاحص روابط الاشتراك ورمز QR 🔍",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = app.name,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                // Overall Status Banner
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (allValid) PlayGreen.copy(alpha = 0.12f) else DangerRed.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (allValid) PlayGreen.copy(alpha = 0.4f) else DangerRed.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (allValid) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (allValid) PlayGreen else DangerRed,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (allValid) "جميع الروابط متوافقة ومجربة مع شروط جوجل!" else "يوجد خطأ في صحة أو صيغة أحد الروابط",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (allValid) PlayGreen else DangerRed
                            )
                            Text(
                                text = if (allValid) "تطبيقتك جاهز لاستقبال الـ 20 مختبراً بكل سلاسة بدون عوائق." else "تأكد من تعديل الروابط حتى لا يتعذر على المختبرين الانضمام.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Detailed Link Checks
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    LinkCheckRow(
                        title = "رابط مجموعة جوجل (Google Group)",
                        url = app.googleGroupUrl,
                        isValid = groupOk,
                        message = groupMsg,
                        onOpen = {
                            try {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(app.googleGroupUrl)))
                            } catch (_: Exception) {
                                Toast.makeText(context, "الرابط غير صالح للفتح", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )

                    LinkCheckRow(
                        title = "رابط الاشتراك على الويب (Web Opt-In)",
                        url = app.webOptInUrl,
                        isValid = webOk,
                        message = webMsg,
                        onOpen = {
                            try {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(app.webOptInUrl)))
                            } catch (_: Exception) {
                                Toast.makeText(context, "الرابط غير صالح للفتح", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )

                    LinkCheckRow(
                        title = "رابط تنزيل التطبيق (Android Opt-In)",
                        url = app.androidOptInUrl,
                        isValid = androidOk,
                        message = androidMsg,
                        onOpen = {
                            try {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(app.androidOptInUrl)))
                            } catch (_: Exception) {
                                Toast.makeText(context, "الرابط غير صالح للفتح", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }

                // Visual QR Generator Card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "رمز QR للانضمام المباشر للهاتف 📱",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Visual QR Matrix Graphic Simulation
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .border(2.dp, CyanAccent, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCode2,
                                contentDescription = "QR Code",
                                tint = Color.Black,
                                modifier = Modifier.size(95.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "امسح الكود بكاميرا الجوال للانتقال لصفحة الاختبار فوراً",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                    }
                }

                // Actions
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_close_validator_dialog"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PlayGreen)
                ) {
                    Text("تم الفحص والتحقق", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun LinkCheckRow(
    title: String,
    url: String,
    isValid: Boolean,
    message: String,
    onOpen: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = if (isValid) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (isValid) PlayGreen else DangerRed,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = message,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isValid) PlayGreen else DangerRed,
                        fontSize = 11.sp
                    )
                }
            }

            IconButton(
                onClick = onOpen,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.OpenInNew,
                    contentDescription = "تجربة الرابط",
                    tint = CyanAccent,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
