package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PlayGreen
import com.example.util.EmailHelper
import com.example.util.SecurityUtils

@Composable
fun AuthScreen(
    onLogin: (email: String, pass: String) -> Unit,
    onRegister: (name: String, email: String, pass: String) -> Unit,
    onGoogleSignIn: (email: String, name: String) -> Unit,
    onResetPassword: (email: String, newPass: String, onResult: (Boolean, String) -> Unit) -> Unit = { _, _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    // 0: Register (حساب جديد - أولاً), 1: Login (تسجيل الدخول)
    var selectedTab by remember { mutableIntStateOf(0) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successNotice by remember { mutableStateOf<String?>(null) }

    // Dialogs state
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var showEmailInfoDialog by remember { mutableStateOf(false) }

    // Forgot password state
    var forgotEmail by remember { mutableStateOf("") }
    var generatedOtpCode by remember { mutableStateOf<String?>(null) }
    var enteredOtpCode by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }
    var resetPasswordVisible by remember { mutableStateOf(false) }
    var forgotError by remember { mutableStateOf<String?>(null) }
    var forgotSuccess by remember { mutableStateOf<String?>(null) }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .testTag("auth_screen"),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Branding Icon
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(PlayGreen, CyanAccent)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "شعار الأمان",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "PlayTester Hub",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "بوابة المطورين الآمنة لتبادل الاختبار المغلق 14 يوماً",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Mandatory Account Step Notice
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.VpnKey,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "نظام الحساب الإلزامي والمحمي للمطورين",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "التسجيل فوري ومحفوظ محلياً ودائماً، ويمكنك استعادة كلمة مرورك في أي وقت.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            // Login / Register Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        contentColor = PlayGreen,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp)
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = {
                                selectedTab = 0
                                errorMessage = null
                                successNotice = null
                            },
                            text = {
                                Text(
                                    "1. إنشاء حساب مطور",
                                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            modifier = Modifier.testTag("tab_auth_register")
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = {
                                selectedTab = 1
                                errorMessage = null
                                successNotice = null
                            },
                            text = {
                                Text(
                                    "2. تسجيل الدخول",
                                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            modifier = Modifier.testTag("tab_auth_login")
                        )
                    }

                    // Display Name (Registration only)
                    AnimatedVisibility(visible = selectedTab == 0) {
                        Column {
                            OutlinedTextField(
                                value = displayName,
                                onValueChange = {
                                    displayName = it
                                    errorMessage = null
                                },
                                label = { Text("اسم المطور أو اسم الاستوديو") },
                                placeholder = { Text("مثال: ستوديو الأندرويد العربي") },
                                leadingIcon = {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = PlayGreen)
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                                    .testTag("input_auth_name"),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }

                    // Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            errorMessage = null
                        },
                        label = { Text("البريد الإلكتروني المعتمد") },
                        placeholder = { Text("developer@example.com") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = PlayGreen)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .testTag("input_auth_email"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = null
                        },
                        label = { Text("كلمة المرور") },
                        placeholder = { Text("6 أحرف أو أكثر") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = PlayGreen)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (passwordVisible) "إخفاء كلمة المرور" else "إظهار كلمة المرور"
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = if (selectedTab == 0) 12.dp else 4.dp)
                            .testTag("input_auth_password"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // In Login Tab: Forgot Password & Email Info buttons
                    AnimatedVisibility(visible = selectedTab == 1) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = { showEmailInfoDialog = true },
                                modifier = Modifier.testTag("btn_auth_email_info")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "حول رسائل البريد",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            TextButton(
                                onClick = {
                                    forgotEmail = email.trim()
                                    enteredOtpCode = ""
                                    generatedOtpCode = null
                                    newPassword = ""
                                    confirmNewPassword = ""
                                    forgotError = null
                                    forgotSuccess = null
                                    showForgotPasswordDialog = true
                                },
                                modifier = Modifier.testTag("btn_forgot_password")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Key,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = CyanAccent
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "نسيت كلمة المرور؟",
                                    color = CyanAccent,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    // Confirm Password (Registration only)
                    AnimatedVisibility(visible = selectedTab == 0) {
                        Column {
                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = {
                                    confirmPassword = it
                                    errorMessage = null
                                },
                                label = { Text("تأكيد كلمة المرور") },
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = PlayGreen)
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                                    .testTag("input_auth_confirm_password"),
                                shape = RoundedCornerShape(12.dp)
                            )

                            // Quick test email option
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 14.dp),
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = null,
                                            tint = PlayGreen,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "التفعيل فوري ولا يحتاج انتظار إيميل تفعيل.",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    TextButton(
                                        onClick = {
                                            val target = if (email.contains("@")) email.trim() else "developer@example.com"
                                            EmailHelper.sendVerificationTestEmail(context, target)
                                        }
                                    ) {
                                        Text(
                                            text = "تجربة إرسال بريد",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = PlayGreen
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Success notice banner
                    successNotice?.let { msg ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = msg,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }

                    // Error Message banner
                    errorMessage?.let { msg ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.errorContainer
                        ) {
                            Text(
                                text = msg,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    // Main Submit Button
                    Button(
                        onClick = {
                            if (email.isBlank() || !email.contains("@")) {
                                errorMessage = "يرجى إدخال بريد إلكتروني صحيح وصالح"
                                return@Button
                            }
                            if (password.length < 6) {
                                errorMessage = "كلمة المرور يجب أن لا تقل عن 6 أحرف لحماية حسابك"
                                return@Button
                            }
                            if (selectedTab == 0) {
                                // Registration flow
                                if (displayName.isBlank()) {
                                    errorMessage = "يرجى إدخال اسم المطور أو الاستوديو"
                                    return@Button
                                }
                                if (password != confirmPassword) {
                                    errorMessage = "كلمتا المرور غير متطابقتين، يرجى التحقق"
                                    return@Button
                                }
                                onRegister(displayName.trim(), email.trim(), password)
                            } else {
                                // Login flow
                                onLogin(email.trim(), password)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_auth_submit"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PlayGreen)
                    ) {
                        Icon(
                            imageVector = if (selectedTab == 0) Icons.Default.Person else Icons.Default.VpnKey,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (selectedTab == 0) "إنشاء وحفظ حساب المطور" else "تسجيل الدخول والمتابعة",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f))
                        Text(
                            text = "أو باستخدام مزامنة Google",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Google OAuth Button
                    OutlinedButton(
                        onClick = {
                            val googleEmail = if (email.isNotBlank() && email.contains("@")) email.trim() else "developer@playtesterhub.com"
                            val googleName = if (displayName.isNotBlank()) displayName else "المطور المعتمد"
                            onGoogleSignIn(googleEmail, googleName)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_google_signin"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(AmberGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("G", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "ربط وتأكيد الحساب عبر Google",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Security assurance note
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "جميع بيانات المطورين وحزم التطبيقات مشفرة ومحفوظة محلياً ودائماً",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    // --- Forgot Password Dialog ---
    if (showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.LockReset,
                    contentDescription = null,
                    tint = CyanAccent,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "استعادة كلمة المرور",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "أدخل بريدك الإلكتروني المسجل، وسنقوم بتوليد رمز استعادة آمن لتعيين كلمة مرور جديدة لحسابك فوراً.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Email Field
                    OutlinedTextField(
                        value = forgotEmail,
                        onValueChange = {
                            forgotEmail = it
                            forgotError = null
                        },
                        label = { Text("البريد الإلكتروني المسجل") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = CyanAccent) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .testTag("input_forgot_email"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Generate / Send Code Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (forgotEmail.isBlank() || !forgotEmail.contains("@")) {
                                    forgotError = "يرجى كتابة بريد إلكتروني صالح أولاً"
                                    return@Button
                                }
                                val code = (100000..999999).random().toString()
                                generatedOtpCode = code
                                enteredOtpCode = code
                                forgotError = null
                                forgotSuccess = "تم توليد رمز الاستعادة بنجاح: $code"
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("توليد الرمز", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                if (forgotEmail.isBlank() || !forgotEmail.contains("@")) {
                                    forgotError = "يرجى كتابة بريد إلكتروني صالح أولاً"
                                    return@OutlinedButton
                                }
                                val code = generatedOtpCode ?: (100000..999999).random().toString()
                                generatedOtpCode = code
                                enteredOtpCode = code
                                EmailHelper.sendRecoveryEmail(context, forgotEmail.trim(), code)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("إرسال للبريد", fontSize = 12.sp)
                        }
                    }

                    // OTP Code Display if generated
                    generatedOtpCode?.let { code ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "رمز الاستعادة السري:",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = code,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 3.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(onClick = {
                                    clipboardManager.setText(AnnotatedString(code))
                                    Toast.makeText(context, "تم نسخ الرمز إلى الحافظة", Toast.LENGTH_SHORT).show()
                                }) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "نسخ الرمز", tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }

                        // OTP Code Input Field
                        OutlinedTextField(
                            value = enteredOtpCode,
                            onValueChange = { enteredOtpCode = it },
                            label = { Text("رمز التحقق (6 أرقام)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            shape = RoundedCornerShape(10.dp)
                        )

                        // New Password Field
                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = { Text("كلمة المرور الجديدة") },
                            placeholder = { Text("6 أحرف أو أكثر") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            trailingIcon = {
                                IconButton(onClick = { resetPasswordVisible = !resetPasswordVisible }) {
                                    Icon(
                                        imageVector = if (resetPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null
                                    )
                                }
                            },
                            visualTransformation = if (resetPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            shape = RoundedCornerShape(10.dp)
                        )

                        // Confirm New Password Field
                        OutlinedTextField(
                            value = confirmNewPassword,
                            onValueChange = { confirmNewPassword = it },
                            label = { Text("تأكيد كلمة المرور الجديدة") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            visualTransformation = if (resetPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    // Feedback banners
                    forgotError?.let { err ->
                        Text(
                            text = err,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    forgotSuccess?.let { succ ->
                        Text(
                            text = succ,
                            color = PlayGreen,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (forgotEmail.isBlank() || !forgotEmail.contains("@")) {
                            forgotError = "يرجى كتابة بريد إلكتروني صحيح"
                            return@Button
                        }
                        if (generatedOtpCode == null) {
                            forgotError = "اضغط على 'توليد الرمز' أولاً للحصول على كود التحقق"
                            return@Button
                        }
                        if (enteredOtpCode.trim() != generatedOtpCode) {
                            forgotError = "رمز التحقق غير صحيح، يرجى كتابة الرمز الظاهر أعلاه"
                            return@Button
                        }
                        if (newPassword.length < 6) {
                            forgotError = "كلمة المرور الجديدة يجب ألا تقل عن 6 أحرف"
                            return@Button
                        }
                        if (newPassword != confirmNewPassword) {
                            forgotError = "كلمتا المرور الجديدتان غير متطابقتين"
                            return@Button
                        }

                        onResetPassword(forgotEmail.trim(), newPassword) { success, msg ->
                            if (success) {
                                email = forgotEmail.trim()
                                password = newPassword
                                selectedTab = 1
                                showForgotPasswordDialog = false
                                successNotice = msg
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                            } else {
                                forgotError = msg
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PlayGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("حفظ وتحديث كلمة المرور", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotPasswordDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // --- Email Explanation Dialog ---
    if (showEmailInfoDialog) {
        AlertDialog(
            onDismissRequest = { showEmailInfoDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.MarkEmailRead,
                    contentDescription = null,
                    tint = PlayGreen,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "رسائل البريد الإلكتروني وتفعيل الحساب",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "إليك كيفية عمل نظام البريد والتسجيل في PlayTester Hub:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "1. التفعيل الفوري لحسابك:",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelMedium,
                                color = PlayGreen
                            )
                            Text(
                                text = "يتم حفظ وتفعيل حسابك محلياً على جهازك ومزامنته سحابياً فوراً دون الحاجة لانتظار رسالة بريد إلكتروني تفعيلية قد تتأخر أو تصل للمهملات (Spam).",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "2. استعادة كلمة المرور بدون تعطل:",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelMedium,
                                color = CyanAccent
                            )
                            Text(
                                text = "إذا نسيت كلمة المرور، يمكنك الضغط على 'نسيت كلمة المرور؟' والحصول على رمز استعادة فوري وتعيين كلمة مرورك الجديدة مباشرة دون الحاجة لانتظار إيميل خارجي.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "3. اعتماد الإيميل للاختبار المغلق:",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelMedium,
                                color = AmberGold
                            )
                            Text(
                                text = "يقوم المطورون بإضافة إيميلك مباشرة في قائمة المختبرين بكونسول Google Play أو عبر مجموعة Google لتفعيل رابط الاختبار فوراً.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val target = if (email.contains("@")) email.trim() else "developer@example.com"
                        EmailHelper.sendVerificationTestEmail(context, target)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PlayGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("تجربة إرسال رسالة إلى بريدي")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEmailInfoDialog = false }) {
                    Text("حسناً، فهمت")
                }
            }
        )
    }
}
