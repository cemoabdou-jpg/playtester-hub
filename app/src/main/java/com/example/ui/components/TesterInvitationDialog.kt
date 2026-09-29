package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.InvitedTester
import com.example.data.model.UserApp
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DangerRed
import com.example.ui.theme.PlayGreen
import com.example.ui.theme.SuccessGreen
import com.example.ui.util.AppLanguage
import com.example.ui.util.LocalAppLanguage

@Composable
fun TesterInvitationDialog(
    app: UserApp,
    invitedTesters: List<InvitedTester>,
    onInviteTester: (email: String, notes: String) -> Unit,
    onUpdateStatus: (testerId: Int, newStatus: String) -> Unit,
    onRemoveTester: (testerId: Int) -> Unit,
    onTipTester: ((InvitedTester) -> Unit)? = null,
    onDismiss: () -> Unit
) {
    val language = LocalAppLanguage.current
    val isArabic = language == AppLanguage.AR

    var emailInput by remember { mutableStateOf("") }
    var notesInput by remember { mutableStateOf("") }
    var inputError by remember { mutableStateOf<String?>(null) }
    var showAddTesterSection by remember { mutableStateOf(false) }

    val acceptedCount = invitedTesters.count { it.status == "ACCEPTED" || it.status == "TESTING" }
    val pendingCount = invitedTesters.count { it.status == "INVITED" }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 24.dp)
                .testTag("tester_invitation_dialog"),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
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
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(CyanAccent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.GroupAdd,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isArabic) "إدارة ودعوة مختبري التطبيق" else "Manage & Invite App Testers",
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

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_tester_dialog")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = if (isArabic) "إغلاق" else "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stats overview chip row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${invitedTesters.size}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isArabic) "إجمالي المدعوين" else "Total Invited",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        color = SuccessGreen.copy(alpha = 0.12f)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "$acceptedCount / 20",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
                            Text(
                                text = if (isArabic) "مختبرين نشطين" else "Active Testers",
                                style = MaterialTheme.typography.labelSmall,
                                color = SuccessGreen
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        color = AmberGold.copy(alpha = 0.12f)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "$pendingCount",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AmberGold
                            )
                            Text(
                                text = if (isArabic) "في انتظار القبول" else "Pending Opt-in",
                                style = MaterialTheme.typography.labelSmall,
                                color = AmberGold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Toggle invite form / quick add button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isArabic) "قائمة المختبرين المدعوين (${invitedTesters.size})" else "Invited Testers (${invitedTesters.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Button(
                        onClick = {
                            showAddTesterSection = !showAddTesterSection
                            inputError = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (showAddTesterSection) MaterialTheme.colorScheme.surfaceVariant else PlayGreen,
                            contentColor = if (showAddTesterSection) MaterialTheme.colorScheme.onSurfaceVariant else Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("btn_toggle_invite_form")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (showAddTesterSection) {
                                if (isArabic) "إخفاء الحقول" else "Hide Form"
                            } else {
                                if (isArabic) "دعوة مختبر جديد" else "Invite Tester"
                            }
                        )
                    }
                }

                // Invite Input Form
                AnimatedVisibility(visible = showAddTesterSection) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                            .testTag("invite_tester_form")
                    ) {
                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = {
                                emailInput = it
                                inputError = null
                            },
                            label = { Text(if (isArabic) "البريد الإلكتروني لحساب Google للمختبر" else "Tester's Google Account Email") },
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = null, tint = PlayGreen)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_invite_email"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = notesInput,
                            onValueChange = { notesInput = it },
                            label = { Text(if (isArabic) "ملاحظة أو نوع الجهاز (اختياري: مثال: Samsung S22)" else "Notes / Device model (Optional: e.g. Pixel 8)") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_invite_notes"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        inputError?.let { err ->
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = err,
                                color = DangerRed,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                val email = emailInput.trim().lowercase()
                                if (email.isBlank() || !email.contains("@")) {
                                    inputError = if (isArabic) "يرجى إدخال بريد إلكتروني صالح للمختبر" else "Please enter a valid email address"
                                    return@Button
                                }
                                onInviteTester(email, notesInput.trim())
                                emailInput = ""
                                notesInput = ""
                                showAddTesterSection = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("btn_submit_tester_invite"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PlayGreen)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isArabic) "إرسال دعوة الاختبار المغلق الآن" else "Send Closed Testing Invite Now", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Testers List
                if (invitedTesters.isEmpty()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.GroupAdd,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isArabic) "لم تقم بدعوة مختبرين بعد لهذا التطبيق" else "No testers invited yet for this app",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isArabic) {
                                    "اضغط على 'دعوة مختبر جديد' لإضافة إيميلات المختبرين ومتابعة قبولهم للاختبار."
                                } else {
                                    "Click 'Invite Tester' to add tester emails and track their opt-in progress."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .testTag("invited_testers_list"),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(invitedTesters, key = { it.id }) { tester ->
                            TesterItemCard(
                                tester = tester,
                                isArabic = isArabic,
                                onUpdateStatus = { newStatus -> onUpdateStatus(tester.id, newStatus) },
                                onRemove = { onRemoveTester(tester.id) },
                                onTip = if (onTipTester != null) { { onTipTester(tester) } } else null
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TesterItemCard(
    tester: InvitedTester,
    isArabic: Boolean = true,
    onUpdateStatus: (String) -> Unit,
    onRemove: () -> Unit,
    onTip: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("tester_item_${tester.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tester.email,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                if (tester.notes.isNotBlank()) {
                    Text(
                        text = tester.notes,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Status Badge
                when (tester.status) {
                    "TESTING" -> {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SuccessGreen.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isArabic) "يختبر التطبيق بنشاط" else "Actively Testing",
                                    color = SuccessGreen,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    "ACCEPTED" -> {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = PlayGreen.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PlayGreen, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isArabic) "تم قبول الدعوة" else "Invite Accepted",
                                    color = PlayGreen,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    else -> {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AmberGold.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.HourglassTop, contentDescription = null, tint = AmberGold, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isArabic) "تم الإرسال - بانتظار القبول" else "Invited - Pending",
                                    color = AmberGold,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Actions
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (tester.status == "INVITED") {
                    OutlinedButton(
                        onClick = { onUpdateStatus("ACCEPTED") },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_accept_tester_${tester.id}")
                    ) {
                        Text(if (isArabic) "تأكيد القبول" else "Confirm Accept", fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                } else if (tester.status == "ACCEPTED") {
                    Button(
                        onClick = { onUpdateStatus("TESTING") },
                        colors = ButtonDefaults.buttonColors(containerColor = PlayGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_start_testing_${tester.id}")
                    ) {
                        Text(if (isArabic) "بدء الاختبار" else "Start Test", fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }

                if (onTip != null) {
                    OutlinedButton(
                        onClick = onTip,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_tip_tester_${tester.id}")
                    ) {
                        Text(if (isArabic) "بقشيش 💎" else "Tip 💎", fontSize = 11.sp, color = AmberGold, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }

                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(32.dp).testTag("btn_remove_tester_${tester.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = if (isArabic) "حذف المختبر" else "Delete Tester",
                        tint = DangerRed.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
