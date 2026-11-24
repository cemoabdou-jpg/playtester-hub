package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.util.L
import kotlinx.coroutines.delay

@Composable
fun EmailVerificationDialog(
    initialEmail: String,
    initialName: String,
    currentCode: String,
    onSendCode: (String, String) -> Unit,
    onConfirmCode: (String) -> Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var isCodeSentStep by remember { mutableStateOf(currentCode.isNotBlank()) }

    var name by remember { mutableStateOf(initialName) }
    var email by remember { mutableStateOf(initialEmail) }
    var password by remember { mutableStateOf("") }
    var enteredOtp by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var resendCountdown by remember { mutableIntStateOf(60) }
    var canResend by remember { mutableStateOf(false) }

    LaunchedEffect(isCodeSentStep, currentCode) {
        if (isCodeSentStep) {
            resendCountdown = 60
            canResend = false
            while (resendCountdown > 0) {
                delay(1000)
                resendCountdown--
            }
            canResend = true
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = L.t("إغلاق", "Close"))
                    }
                    Text(
                        if (!isCodeSentStep) L.t("إنشاء حساب وتأكيد الاشتراك", "Create Account & Verify") else L.t("تأكيد رمز البريد الإلكتروني", "Verify Email Code"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.size(32.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (!isCodeSentStep) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.MarkEmailRead,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        L.t(
                            "أدخل بياناتك وسيتم إرسال رمز تحقق سري إلى بريدك الإلكتروني لتأكيد التسجيل.",
                            "Enter your details to receive a secret OTP verification code via email."
                        ),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(L.t("الاسم الكامل أو اسم المطور", "Full Name or Developer Name")) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(L.t("البريد الإلكتروني", "Email Address")) },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(L.t("كلمة المرور", "Password")) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        onClick = {
                            if (email.isBlank() || !email.contains("@")) {
                                errorMessage = L.t("يرجى إدخال بريد إلكتروني صحيح", "Please enter a valid email address")
                                return@Button
                            }
                            if (name.isBlank()) {
                                errorMessage = L.t("يرجى كتابة اسمك", "Please enter your name")
                                return@Button
                            }
                            errorMessage = null
                            onSendCode(email.trim(), name.trim())
                            isCodeSentStep = true
                            Toast.makeText(context, L.t("تم إرسال رمز التحقق إلى $email", "Verification code sent to $email"), Toast.LENGTH_LONG).show()
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(L.t("إرسال رمز التأكيد للبريد", "Send Verification Code"), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                } else {
                    Surface(
                        color = Color(0xFF00875A).copy(alpha = 0.12f),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Pin,
                                contentDescription = null,
                                tint = Color(0xFF00875A),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        L.t("تم إرسال رمز التحقق المكون من 6 أرقام إلى:", "We sent a 6-digit verification code to:"),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        email,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        border = BorderStroke(1.dp, Color(0xFF81C784))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    L.t("رمز التحقق المرسل: $currentCode", "Sent Code: $currentCode"),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1B5E20)
                                )
                            }
                            TextButton(
                                onClick = {
                                    enteredOtp = currentCode
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText(L.t("رمز التحقق", "OTP"), currentCode))
                                    Toast.makeText(context, L.t("تم تعبئة الرمز تلقائياً", "Code auto-filled"), Toast.LENGTH_SHORT).show()
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(L.t("تعبئة تلقائية", "Auto Fill"), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = enteredOtp,
                        onValueChange = {
                            if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                                enteredOtp = it
                                errorMessage = null
                            }
                        },
                        label = { Text(L.t("أدخل الرمز المكون من 6 أرقام", "Enter 6-digit code")) },
                        placeholder = { Text("• • • • • •") },
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(
                            textAlign = TextAlign.Center,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 6.sp
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        onClick = {
                            if (enteredOtp.length != 6) {
                                errorMessage = L.t("يرجى كتابة الرمز كاملاً (6 أرقام)", "Please enter the full 6-digit code")
                                return@Button
                            }
                            val success = onConfirmCode(enteredOtp)
                            if (success) {
                                Toast.makeText(context, L.t("🎉 تم تأكيد الاشتراك بنجاح وإضافة +100 نقطة لحسابك!", "🎉 Account verified! +100 coins added!"), Toast.LENGTH_LONG).show()
                            } else {
                                errorMessage = L.t("الرمز المدخل غير صحيح، يرجى المحاولة مجدداً", "Invalid code, please try again")
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00875A))
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(L.t("تأكيد الاشتراك وتفعيل الحساب", "Confirm & Activate Account"), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = { isCodeSentStep = false },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(L.t("تعديل البريد الإلكتروني", "Change Email"), fontSize = 11.sp)
                        }

                        TextButton(
                            onClick = {
                                if (canResend) {
                                    onSendCode(email, name)
                                    Toast.makeText(context, L.t("تمت إعادة إرسال رمز جديد بنجاح", "New code sent successfully"), Toast.LENGTH_SHORT).show()
                                }
                            },
                            enabled = canResend,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                if (canResend) L.t("إعادة إرسال الرمز", "Resend Code") else L.t("إعادة الإرسال بعد (${resendCountdown} ث)", "Resend in (${resendCountdown}s)"),
                                fontSize = 11.sp,
                                color = if (canResend) MaterialTheme.colorScheme.primary else Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}
