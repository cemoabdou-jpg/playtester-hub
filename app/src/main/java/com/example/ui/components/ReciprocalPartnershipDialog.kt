package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DangerRed
import com.example.ui.theme.PlayGreen
import com.example.ui.theme.SuccessGreen
import com.example.ui.util.AppLanguage
import com.example.ui.util.LocalAppLanguage

data class ReciprocalContract(
    val id: String,
    val partnerName: String,
    val partnerApp: String,
    val myApp: String,
    val daysCompleted: Int,
    val targetDays: Int = 14,
    val myTestedToday: Boolean,
    val partnerTestedToday: Boolean,
    val bonusPoints: Int = 200,
    val isRewardClaimed: Boolean = false
)

@Composable
fun ReciprocalPartnershipDialog(
    userAppsNames: List<String>,
    onDismiss: () -> Unit,
    onBonusClaimed: (Int, String) -> Unit = { _, _ -> }
) {
    val language = LocalAppLanguage.current
    val isArabic = language == AppLanguage.AR

    // Initial state without any default/dummy mock applications
    val contracts = remember { mutableStateListOf<ReciprocalContract>() }

    var showNewContractSheet by remember { mutableStateOf(false) }
    var partnerInputName by remember { mutableStateOf("") }
    var partnerInputApp by remember { mutableStateOf("") }
    var customMyAppInput by remember { mutableStateOf("") }
    var selectedMyApp by remember { mutableStateOf(userAppsNames.firstOrNull() ?: "") }
    var successMsg by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = AmberGold.copy(alpha = 0.18f),
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Handshake,
                            contentDescription = null,
                            tint = AmberGold,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (isArabic) "عقود التبادل والالتزام المتبادل (1-on-1)" else "1-on-1 Reciprocal Testing Contracts",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isArabic) "نظام شراكات التبادل والنقاط المكتسبة المتبادلة" else "Reciprocal exchange & earned points system",
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Info Banner about Points Economy
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AmberGold.copy(alpha = 0.1f),
                    border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Stars, contentDescription = null, tint = AmberGold, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isArabic) "🤝 نظام النقاط المكتسبة في شراكات التبادل:" else "🤝 Earned Points System in Partnerships:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = AmberGold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isArabic) {
                                    "• +25 نقطة لكل فحص يومي لتطبيق الشريك.\n• +200 نقطة مكافأة كبرى عند إتمام الـ 14 يوماً.\n• التزام متبادل لضمان جاهزية تطبيقك لـ Google Play."
                                } else {
                                    "• +25 points for every daily test of partner app.\n• +200 bonus points upon completing 14 full days.\n• Mutual commitment for Google Play readiness."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                successMsg?.let { msg ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SuccessGreen.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, SuccessGreen),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = msg,
                            color = SuccessGreen,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                // New Contract Form
                if (showNewContractSheet) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, CyanAccent),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = if (isArabic) "إنشاء عقد شراكة تبادل جديدة:" else "Create New Testing Partnership:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = CyanAccent
                            )

                            // Select or enter My App
                            if (userAppsNames.isNotEmpty()) {
                                Text(
                                    text = if (isArabic) "اختر تطبيقك للشراكة:" else "Select Your App for Partnership:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    userAppsNames.forEach { appName ->
                                        val isSelected = selectedMyApp == appName
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isSelected) CyanAccent.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                                            border = BorderStroke(1.dp, if (isSelected) CyanAccent else MaterialTheme.colorScheme.outlineVariant),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = appName,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) CyanAccent else MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.padding(8.dp)
                                            )
                                        }
                                    }
                                }
                            } else {
                                OutlinedTextField(
                                    value = customMyAppInput,
                                    onValueChange = { customMyAppInput = it },
                                    label = { Text(if (isArabic) "اسم تطبيقك" else "Your App Name") },
                                    placeholder = { Text(if (isArabic) "مثال: تطبيقي الأول" else "e.g. My Awesome App") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                            }

                            OutlinedTextField(
                                value = partnerInputName,
                                onValueChange = { partnerInputName = it },
                                label = { Text(if (isArabic) "اسم المطور الشريك أو بريده" else "Partner Developer Name / Email") },
                                placeholder = { Text(if (isArabic) "مثال: م. عمر أحمد" else "e.g. Alex Dev") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = partnerInputApp,
                                onValueChange = { partnerInputApp = it },
                                label = { Text(if (isArabic) "اسم تطبيق الشريك الذي ستختبره" else "Partner's App to Test") },
                                placeholder = { Text(if (isArabic) "مثال: تطبيق الملاحظات السريعة" else "e.g. Quick Notes App") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                OutlinedButton(onClick = { showNewContractSheet = false }) {
                                    Text(if (isArabic) "إلغاء" else "Cancel")
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        val effectiveMyApp = if (userAppsNames.isNotEmpty()) {
                                            selectedMyApp.ifBlank { userAppsNames.first() }
                                        } else {
                                            customMyAppInput.trim().ifBlank { if (isArabic) "تطبيقي" else "My App" }
                                        }

                                        if (partnerInputName.isNotBlank() && partnerInputApp.isNotBlank()) {
                                            contracts.add(
                                                ReciprocalContract(
                                                    id = System.currentTimeMillis().toString(),
                                                    partnerName = partnerInputName.trim(),
                                                    partnerApp = partnerInputApp.trim(),
                                                    myApp = effectiveMyApp,
                                                    daysCompleted = 0,
                                                    myTestedToday = false,
                                                    partnerTestedToday = false
                                                )
                                            )
                                            partnerInputName = ""
                                            partnerInputApp = ""
                                            customMyAppInput = ""
                                            showNewContractSheet = false
                                            successMsg = if (isArabic) "تم إنشاء عقد الشراكة بنجاح! يمكنك الآن تسجيل فحص اليوم وكسب النقاط." else "Partnership created! You can now record today's test and earn points."
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent)
                                ) {
                                    Text(if (isArabic) "تأكيد الشراكة" else "Confirm Partnership", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    OutlinedButton(
                        onClick = { showNewContractSheet = true },
                        modifier = Modifier.fillMaxWidth().testTag("btn_new_reciprocal_contract"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = AmberGold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isArabic) "إنشاء عقد شراكة تبادل جديدة مع مطور" else "Create New 1-on-1 Contract", color = AmberGold, fontWeight = FontWeight.Bold)
                    }
                }

                Text(
                    text = if (isArabic) "عقود التبادل النشطة (${contracts.size}):" else "Your Active Contracts (${contracts.size}):",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                // Empty State when no contracts exist
                if (contracts.isEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Handshake,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = if (isArabic) "لا توجد شراكات تبادل مسجلة حالياً" else "No active exchange partnerships yet",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isArabic) {
                                    "اضغط على 'إنشاء عقد شراكة تبادل جديدة' للاتفاق مع مطور شريك واختبار تطبيقات بعضكم البعض يومياً لكسب +25 نقطة يومياً ومكافأة +200 نقطة عند الإتمام!"
                                } else {
                                    "Tap 'Create New 1-on-1 Contract' to pair with a fellow developer, test each other's apps daily to earn +25 daily points and +200 bonus points!"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                }

                // Active Contracts List
                contracts.forEachIndexed { index, contract ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, if (contract.daysCompleted >= contract.targetDays) SuccessGreen else MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = contract.partnerName,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = PlayGreen.copy(alpha = 0.12f)
                                    ) {
                                        Text(
                                            text = if (isArabic) "+${contract.bonusPoints} نقطة مكافأة" else "+${contract.bonusPoints} Bonus Pts",
                                            color = PlayGreen,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = { contracts.removeAt(index) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = if (isArabic) "حذف" else "Delete", tint = DangerRed.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                                    }
                                }
                            }

                            Text(
                                text = if (isArabic) "تطبيقك: ${contract.myApp} ⇄ تطبيقه: ${contract.partnerApp}" else "Your App: ${contract.myApp} ⇄ Theirs: ${contract.partnerApp}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            // Progress
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isArabic) "التقدم المتبادل: اليوم ${contract.daysCompleted} من ${contract.targetDays}" else "Mutual Progress: Day ${contract.daysCompleted} of ${contract.targetDays}",
                                    style = MaterialTheme.typography.labelSmall
                                )
                                Text(
                                    text = "${((contract.daysCompleted.toFloat() / contract.targetDays) * 100).toInt()}%",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PlayGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            LinearProgressIndicator(
                                progress = { (contract.daysCompleted.toFloat() / contract.targetDays).coerceIn(0f, 1f) },
                                modifier = Modifier.fillMaxWidth().height(6.dp),
                                color = PlayGreen
                            )

                            // Daily status comparison
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (contract.myTestedToday) SuccessGreen.copy(alpha = 0.1f) else AmberGold.copy(alpha = 0.1f),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (contract.myTestedToday) Icons.Default.CheckCircle else Icons.Default.HourglassTop,
                                            contentDescription = null,
                                            tint = if (contract.myTestedToday) SuccessGreen else AmberGold,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isArabic) {
                                                if (contract.myTestedToday) "أنت اختبرت اليوم ✅" else "لم تختبر اليوم بعد"
                                            } else {
                                                if (contract.myTestedToday) "You tested today ✅" else "Pending your test"
                                            },
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (contract.partnerTestedToday) SuccessGreen.copy(alpha = 0.1f) else AmberGold.copy(alpha = 0.1f),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (contract.partnerTestedToday) Icons.Default.CheckCircle else Icons.Default.HourglassTop,
                                            contentDescription = null,
                                            tint = if (contract.partnerTestedToday) SuccessGreen else AmberGold,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isArabic) {
                                                if (contract.partnerTestedToday) "الشريك اختبر اليوم ✅" else "بانتظار الشريك"
                                            } else {
                                                if (contract.partnerTestedToday) "Partner tested ✅" else "Waiting for partner"
                                            },
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }

                            // Earned Points Actions for this Contract
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (!contract.myTestedToday) {
                                    Button(
                                        onClick = {
                                            val newDays = (contract.daysCompleted + 1).coerceAtMost(contract.targetDays)
                                            contracts[index] = contract.copy(
                                                daysCompleted = newDays,
                                                myTestedToday = true
                                            )
                                            onBonusClaimed(25, "فحص يومي لشراكة تبادل (${contract.partnerApp})")
                                            successMsg = if (isArabic) "تم توثيق فحص اليوم لتطبيق الشريك وكسبت +25 نقطة! 🌟" else "Daily test logged! You earned +25 points! 🌟"
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = PlayGreen),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.MonetizationOn, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (isArabic) "فحص اليوم (+25 نقطة)" else "Test Today (+25 pts)", fontSize = 12.sp)
                                    }
                                }

                                if (contract.daysCompleted >= contract.targetDays && !contract.isRewardClaimed) {
                                    Button(
                                        onClick = {
                                            contracts[index] = contract.copy(isRewardClaimed = true)
                                            onBonusClaimed(contract.bonusPoints, "مكافأة إتمام شراكة التبادل لـ 14 يوماً (${contract.partnerApp})")
                                            successMsg = if (isArabic) "تهانينا! استلمت مكافأة إتمام الشراكة (+${contract.bonusPoints} نقطة) 🏆" else "Congrats! Claimed completion bonus (+${contract.bonusPoints} pts) 🏆"
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.Stars, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (isArabic) "استلام المكافأة (+200)" else "Claim Reward (+200)", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
                modifier = Modifier.testTag("btn_close_reciprocal_dialog")
            ) {
                Text(if (isArabic) "إغلاق" else "Close")
            }
        }
    )
}
