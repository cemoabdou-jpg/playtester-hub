package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.DynamicEventLogo
import com.example.ui.util.AppLanguage
import com.example.ui.util.L
import com.example.ui.viewmodel.TesterViewModel

@Composable
fun MandatoryAuthScreen(
    viewModel: TesterViewModel
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val pendingEmail by viewModel.pendingEmail.collectAsState()
    val pendingName by viewModel.pendingName.collectAsState()

    var nameInput by remember { mutableStateOf(pendingName) }
    var emailInput by remember { mutableStateOf(pendingEmail) }
    var referralInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 560.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Language Switcher Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilterChip(
                                selected = L.isArabic(),
                                onClick = { L.setLanguage(AppLanguage.ARABIC) },
                                label = { Text("العربية", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.testTag("auth_lang_ar")
                            )
                            FilterChip(
                                selected = !L.isArabic(),
                                onClick = { L.setLanguage(AppLanguage.ENGLISH) },
                                label = { Text("English", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.testTag("auth_lang_en")
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                DynamicEventLogo(
                    size = 72.dp,
                    showBadge = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = L.t("منصة مختبري التطبيقات", "PlayTester Hub"),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = L.t(
                        "منصة تبادل المختبرين الرسمية لمتجر جوجل بلاي",
                        "Official Google Play Closed Testing Community"
                    ),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Cloud Sync & Account Badge
                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFF81C784))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CloudDone,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = L.t(
                                "مزامنة سحابية فورية لرصيدك وتطبيقاتك عبر الأجهزة",
                                "Instant Cloud Sync for Your Coins & Testing Projects"
                            ),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                Icons.Default.AccountCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    L.t("تسجيل الدخول السريع واستعادة الحساب", "Quick Sign-In & Account Restore"),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    L.t(
                                        "أدخل اسمك وبريدك المرتبط باختبارات جوجل بلاي لفتح لوحتك",
                                        "Enter your name and Google Play testing email to open your workspace"
                                    ),
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = {
                                nameInput = it
                                errorMessage = null
                            },
                            label = { Text(L.t("الاسم الكامل أو اسم المطور", "Full Name or Developer Name")) },
                            placeholder = { Text(L.t("مثال: م. أحمد أو استوديو التطوير", "e.g. Alex Morgan or App Studio")) },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_name_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = {
                                emailInput = it
                                errorMessage = null
                            },
                            label = { Text(L.t("البريد الإلكتروني للمطور", "Developer Email Address")) },
                            placeholder = { Text("developer@gmail.com") },
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_email_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = referralInput,
                            onValueChange = {
                                referralInput = it.uppercase()
                                errorMessage = null
                            },
                            label = { Text(L.t("رمز الدعوة (اختياري • +50 عملة)", "Referral Code (Optional • +50 coins)")) },
                            placeholder = { Text("PTH-XXXXXX") },
                            leadingIcon = {
                                Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = Color(0xFFFF8F00))
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Characters,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_referral_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        if (errorMessage != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                errorMessage!!,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("auth_submit_button"),
                            enabled = !isSubmitting,
                            onClick = {
                                isSubmitting = true
                                focusManager.clearFocus()
                                viewModel.completeQuickSignIn(
                                    email = emailInput,
                                    name = nameInput,
                                    referralCode = referralInput
                                ) { success, error ->
                                    isSubmitting = false
                                    if (success) {
                                        Toast.makeText(
                                            context,
                                            L.t(
                                                "تم تسجيل الدخول واستعادة بياناتك بنجاح",
                                                "Signed in and synced your workspace!"
                                            ),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        errorMessage = error
                                    }
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00875A))
                        ) {
                            Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                L.t("الدخول ومزامنة الحساب السحابي", "Sign In & Sync Cloud Workspace"),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.GppGood, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                L.t("مميزات ربط حسابك البريدي:", "Benefits of linking your developer email:"),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            L.t(
                                "• حفظ نقاطك، سلسلة الأيام، ومشاريعك في السحابة دون فقدانها عند تسجيل الخروج.\n" +
                                "• استقبال إشعارات فورية عندما ينضم مختبر أو يرسل ملاحظة لتطبيقك.\n" +
                                "• تنسيق تبادل المختبرين الحقيقيين لاجتياز شرط 20 مختبراً و14 يوماً.",
                                "• Preserves your coins, streaks, and testing projects in the cloud across sessions.\n" +
                                "• Receive real-time notifications when testers join or report bugs on your app.\n" +
                                "• Coordinates real developer test swaps to pass Google Play's 20-tester rule."
                            ),
                            fontSize = 11.sp,
                            lineHeight = 17.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
