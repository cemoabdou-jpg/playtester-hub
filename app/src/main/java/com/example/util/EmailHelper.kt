package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object EmailHelper {

    /**
     * Opens the device's default email client (Gmail, Outlook, etc.) with pre-filled recipient,
     * subject, and body.
     */
    fun launchEmailApp(
        context: Context,
        recipientEmail: String,
        subject: String,
        body: String
    ): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(recipientEmail.trim()))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
                true
            } else {
                val chooser = Intent.createChooser(intent, "اختر تطبيق البريد الإلكتروني").apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(chooser)
                true
            }
        } catch (e: Exception) {
            Toast.makeText(context, "تعذر فتح تطبيق البريد: ${e.message}", Toast.LENGTH_SHORT).show()
            false
        }
    }

    /**
     * Launches email client with password recovery code for the given user email.
     */
    fun sendRecoveryEmail(
        context: Context,
        recipientEmail: String,
        recoveryCode: String
    ): Boolean {
        val subject = "[PlayTester Hub] رمز استعادة كلمة المرور الخاص بك"
        val body = """
            مرحباً،
            
            لقد طلبت استعادة كلمة المرور لحسابك في تطبيق PlayTester Hub.
            
            البريد المسجل: $recipientEmail
            رمز الاستعادة والتحقق: $recoveryCode
            
            يرجى إدخال هذا الرمز وكلمة المرور الجديدة في شاشة التطبيق لاستعادة حسابك فوراً.
            
            ملاحظة أمان: لا تشارك هذا الرمز مع أي شخص آخر.
            فريق PlayTester Hub
        """.trimIndent()
        return launchEmailApp(context, recipientEmail, subject, body)
    }

    /**
     * Launches email client with registration verification / confirmation details.
     */
    fun sendVerificationTestEmail(
        context: Context,
        recipientEmail: String
    ): Boolean {
        val subject = "[PlayTester Hub] تأكيد تسجيل وتفعيل حساب المطور"
        val body = """
            مرحباً بك في PlayTester Hub!
            
            تم تفعيل وتسجيل حسابك بنجاح للبريد الإلكتروني: $recipientEmail
            
            حسابك جاهز الآن للمشاركة في:
            1. اختبار تطبيقات المطورين لمدة 14 يوماً وكسب النقاط.
            2. نشر تطبيقاتك للحصول على 20 مختبراً مغلقاً لمتجر Google Play.
            3. الانضمام إلى مجموعة الاختبار الرسمية في Google Groups:
               https://groups.google.com/g/player-test-hup
               
            شكراً لاهتمامك ومشاركتك في مجتمع المطورين العربي!
        """.trimIndent()
        return launchEmailApp(context, recipientEmail, subject, body)
    }
}
