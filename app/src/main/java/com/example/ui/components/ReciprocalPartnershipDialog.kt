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
import androidx.compose.material.icons.filled.AutoAwesome
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
    val contracts = remember {
        mutableStateListOf(
            ReciprocalContract(
                id = "1",
                partnerName = "المهندس أحمد حسام",
                partnerApp = "تطبيق حاسبة الزكاة الذكية",
                myApp = if (userAppsNames.isNotEmpty()) userAppsNames.first() else "تطبيقي الرئيسي",
                daysCompleted = 8,
                targetDays = 14,
                myTestedToday = true,
                partnerTestedToday = true
            ),
            ReciprocalContract(
                id = "2",
                partnerName = "سارة التميمي (مطور مستقل)",
                partnerApp = "تطبيق منظم الميزانية الشخصية",
                myApp = if (userAppsNames.isNotEmpty()) userAppsNames.first() else "تطبيقي الرئيسي",
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
    var selectedMyApp by remember { mutableStateOf(userAppsNames.firstOrNull() ?: "تطبيقي") }
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
                        text = "عقود التبادل والالتزام المتبادل (1-on-1)",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "اختبار يومي متبادل مضمون بين المطورين بنسبة 100%",
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
                            text = "🤝 التزام ثنائي عادل: أنت تختبر تطبيق شريكك يومياً لمدة 14 يوماً، وهو يلتزم باختبار تطبيقك يومياً بنفس القدر، مع نقاط مضاعفة عند الإتمام!",
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
                                text = "إنشاء شراكة تبادل جديدة:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = CyanAccent
                            )
                            OutlinedTextField(
                                value = partnerInputName,
                                onValueChange = { partnerInputName = it },
                                label = { Text("اسم المطور الشريك أو بريده") },
                                placeholder = { Text("مثال: م. عمر خالد") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = partnerInputApp,
                                onValueChange = { partnerInputApp = it },
                                label = { Text("اسم تطبيق الشريك الذي ستختبره") },
                                placeholder = { Text("مثال: تطبيق مهامي اليومية") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                OutlinedButton(onClick = { showNewContractSheet = false }) {
                                    Text("إلغاء")
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
                                            successMsg = "تم إنشاء عقد الشراكة بنجاح! تم إشعار المطور لبدء الاختبار المتبادل."
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent)
                                ) {
                                    Text("تأكيد الشراكة", color = Color.Black, fontWeight = FontWeight.Bold)
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
                        Text("عقد شراكة تبادل جديدة مع مطور", color = AmberGold, fontWeight = FontWeight.Bold)
                    }
                }

                Text(
                    text = "عقودك النشطة حالياً (${contracts.size}):",
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
                                        text = "+${contract.bonusPoints} نقطة مكافأة",
                                        color = PlayGreen,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Text(
                                text = "تطبيقك: ${contract.myApp} ⇄ تطبيقه: ${contract.partnerApp}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            // Progress
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "التقدم المتبادل: اليوم ${contract.daysCompleted} من ${contract.targetDays}",
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
                                            text = if (contract.myTestedToday) "أنت اختبرت اليوم ✅" else "لم تختبر اليوم بعد",
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
                                            text = if (contract.partnerTestedToday) "الشريك اختبر اليوم ✅" else "بانتظار الشريك",
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
                Text("إغلاق")
            }
        }
    )
}
