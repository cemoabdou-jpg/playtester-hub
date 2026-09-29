package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.UserApp
import com.example.ui.theme.PlayGreen
import com.example.ui.util.AppLanguage
import com.example.ui.util.LocalAppLanguage

@Composable
fun ShareInviteDialog(
    app: UserApp,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val language = LocalAppLanguage.current
    val isArabic = language == AppLanguage.AR

    val inviteMessage = remember(app, isArabic) {
        if (isArabic) {
            """
مرحباً زملائي المطورين! 🚀
أحتاج 20 مختبراً لتطبيقي الجديد على Google Play لاجتياز شرط الـ 14 يوماً:

📱 اسم التطبيق: ${app.name}
📦 الحزمة: ${app.packageName}

خطوات الانضمام للاختبار المغلق:
1️⃣ انضم لمجموعة الاختبار أولاً (ضروري):
${app.googleGroupUrl}

2️⃣ اضغط على رابط الانضمام عبر الويب أو الأندرويد:
الويب: ${if (app.webOptInUrl.isNotBlank()) app.webOptInUrl else "متوفر في المجموعة"}
أندرويد: ${if (app.androidOptInUrl.isNotBlank()) app.androidOptInUrl else "متوفر في المتجر"}

🤝 سأقوم باختبار تطبيقك بالمقابل يومياً طوال الـ 14 يوماً! شكراً لدعمكم.
            """.trimIndent()
        } else {
            """
Hey fellow Android Developers! 🚀
I need 20 testers for my Google Play closed test to satisfy the 14-day requirement:

📱 App Name: ${app.name}
📦 Package: ${app.packageName}

How to join the closed test:
1️⃣ Join the testing Google Group (Required):
${app.googleGroupUrl}

2️⃣ Accept Opt-in invitation via Web or Android:
Web: ${if (app.webOptInUrl.isNotBlank()) app.webOptInUrl else "Available in Group"}
Android: ${if (app.androidOptInUrl.isNotBlank()) app.androidOptInUrl else "Available on Play Store"}

🤝 I will reciprocate and test your app daily for the full 14 days! Thank you for the support.
            """.trimIndent()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    tint = PlayGreen,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isArabic) "مشاركة دعوة الاختبار المغلق" else "Share Closed Test Invitation",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (isArabic)
                        "شارك هذه الرسالة في مجموعات واتساب، تيليجرام، أو مجتمعات المطورين للحصول على 20 مختبراً بسرعة:"
                    else
                        "Share this invite to developer groups (WhatsApp, Telegram, Discord, Reddit) to quickly recruit 20 verified testers:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = inviteMessage,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    shareText(context, inviteMessage, if (isArabic) "مشاركة دعوة المختبرين" else "Share Tester Invitation")
                },
                colors = ButtonDefaults.buttonColors(containerColor = PlayGreen),
                modifier = Modifier.testTag("share_action_btn")
            ) {
                Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (isArabic) "مشاركة" else "Share")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = {
                    copyToClipboard(context, inviteMessage)
                    Toast.makeText(context, if (isArabic) "تم نسخ نص الدعوة إلى الحافظة" else "Invitation text copied to clipboard", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.testTag("copy_invite_btn")
            ) {
                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (isArabic) "نسخ النص" else "Copy Text")
            }
        }
    )
}

fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("PlayTester Invite", text)
    clipboard.setPrimaryClip(clip)
}

fun shareText(context: Context, text: String, chooserTitle: String = "Share Tester Invitation") {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, chooserTitle)
    context.startActivity(shareIntent)
}
