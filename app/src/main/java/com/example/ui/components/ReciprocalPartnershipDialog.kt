package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
    val bonusPoints: Int = 200
)

@Composable
fun ReciprocalPartnershipDialog(
    userAppsNames: List<String>,
    onDismiss: () -> Unit,
    onBonusClaimed: (Int) -> Unit = {}
) {
    val language = LocalAppLanguage.current
    val isArabic = language == AppLanguage.AR

    val contracts = remember {
        mutableStateListOf(
            ReciprocalContract(
                id = "1",
                partnerName = if (isArabic) "المهندس أحمد حسام" else "Dev Ahmed H.",
                partnerApp = if (isArabic) "تطبيق حاسبة الزكاة الذكية" else "Smart Zakat Calculator",
                myApp = if (userAppsNames.isNotEmpty()) userAppsNames.first() else if (isArabic) "تطبيقي الرئيسي" else "My Main App",
                daysCompleted = 8,
                targetDays = 14,
                myTestedToday = true,
                partnerTestedToday = true
            ),
            ReciprocalContract(
                id = "2",
                partnerName = if (isArabic) "سارة التميمي (مطور مستقل)" else "Sarah T. (Indie Dev)",
                partnerApp = if (isArabic) "تطبيق منظم الميزانية الشخصية" else "Expense & Budget Tracker",
                myApp = if (userAppsNames.isNotEmpty()) userAppsNames.first() else if (isArabic) "تطبيقي الرئيسي" else "My Main App",
                daysCompleted = 4,
                targetDays = 14,
                myTestedToday = true,
                partnerTestedToday = false
            )
        )
    }

    var showNewContractSheet by remember { mutableStateOf(false) }
    var partnerInputName by remember { mutableStateOf("") }
    var partnerInputApp by remember { mutableStateOf("") }
    var selectedMyApp by remember { mutableStateOf(userAppsNames.firstOrNull() ?: if (isArabic) "تطبيقي" else "My App") }
    var successMsg by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Handshake,
                    contentDescription = null,
                    tint = AmberGold,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (isArabic) "عقود التبادل والالتزام المتبادل (1-on-1)" else "1-on-1 Reciprocal Testing Contracts",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isArabic) "اختبار يومي متبادل مضمون بين المطورين بنسبة 100%" else "Guaranteed 100% mutual daily testing between developers",
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
                // Info Banner
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = AmberGold.copy(alpha = 0.1f),
                    border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Stars, contentDescription = null, tint = AmberGold, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isArabic) {
                                "🤝 التزام ثنائي عادل: أنت تختبر تطبيق شريكك يومياً لمدة 14 يوماً، وهو يلتزم باختبار تطبيقك يومياً بنفس القدر، مع نقاط مضاعفة عند الإتمام!"
                            } else {
                                "🤝 Fair bilateral commitment: You test your partner's app daily for 14 days, and they test yours daily, with double reward points upon completion!"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
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
                                text = if (isArabic) "إنشاء شراكة تبادل جديدة:" else "Create New Testing Partnership:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = CyanAccent
                            )
                            OutlinedTextField(
                                value = partnerInputName,
                                onValueChange = { partnerInputName = it },
                                label = { Text(if (isArabic) "اسم المطور الشريك أو بريده" else "Partner Developer Name / Email") },
                                placeholder = { Text(if (isArabic) "مثال: م. عمر خالد" else "e.g. Alex M.") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = partnerInputApp,
                                onValueChange = { partnerInputApp = it },
                                label = { Text(if (isArabic) "اسم تطبيق الشريك الذي ستختبره" else "Partner's App to Test") },
                                placeholder = { Text(if (isArabic) "مثال: تطبيق مهامي اليومية" else "e.g. Daily Tasks App") },
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
                                        if (partnerInputName.isNotBlank() && partnerInputApp.isNotBlank()) {
                                            contracts.add(
                                                ReciprocalContract(
                                                    id = (contracts.size + 1).toString(),
                                                    partnerName = partnerInputName.trim(),
                                                    partnerApp = partnerInputApp.trim(),
                                                    myApp = selectedMyApp,
                                                    daysCompleted = 1,
                                                    myTestedToday = true,
                                                    partnerTestedToday = false
                                                )
                                            )
                                            partnerInputName = ""
                                            partnerInputApp = ""
                                            showNewContractSheet = false
                                            successMsg = if (isArabic) "تم إنشاء عقد الشراكة بنجاح! تم إشعار المطور لبدء الاختبار المتبادل." else "Partnership contract created! Partner developer notified."
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
                        Text(if (isArabic) "عقد شراكة تبادل جديدة مع مطور" else "Create New 1-on-1 Contract", color = AmberGold, fontWeight = FontWeight.Bold)
                    }
                }

                Text(
                    text = if (isArabic) "عقودك النشطة حالياً (${contracts.size}):" else "Your Active Contracts (${contracts.size}):",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                // Active Contracts List
                contracts.forEach { contract ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
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
                                progress = { contract.daysCompleted.toFloat() / contract.targetDays },
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
