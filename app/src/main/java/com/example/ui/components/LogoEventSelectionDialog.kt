package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.LauncherIconManager
import com.example.ui.theme.LogoEvent
import com.example.ui.theme.LogoEventManager
import com.example.ui.util.L

@Composable
fun LogoEventSelectionDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedEvent by remember { mutableStateOf(LogoEventManager.selectedEvent) }
    val liveAutoEvent = remember(selectedEvent) {
        if (selectedEvent == LogoEvent.AUTO) LogoEventManager.getEffectiveEvent() else selectedEvent
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
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
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Celebration,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                L.t("تأثيرات وأجواء الشعار", "Logo Events & Effects"),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                L.t("تفاعل الشعار والأيقونة الخارجية طبقاً للأحداث", "Dynamic logo & external app icon by season & event"),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = L.t("إغلاق", "Close"), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Auto External Launcher Icon Status Banner
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = liveAutoEvent.primaryColor.copy(alpha = 0.10f),
                    border = BorderStroke(1.dp, liveAutoEvent.primaryColor.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = liveAutoEvent.primaryColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                L.t(
                                    "الأيقونة الخارجية تتكيف تلقائياً",
                                    "External Launcher Icon Adapts Automatically"
                                ),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.5.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                L.t(
                                    "المظهر النشط حالياً: ${liveAutoEvent.getTitle()} ${liveAutoEvent.badgeEmoji}",
                                    "Active theme now: ${liveAutoEvent.getTitle()} ${liveAutoEvent.badgeEmoji}"
                                ),
                                fontSize = 10.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // List of Events
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(LogoEvent.values()) { event ->
                        val isSelected = selectedEvent == event
                        val previewEvent = if (event == LogoEvent.AUTO) {
                            LogoEventManager.getEffectiveEvent()
                        } else {
                            event
                        }
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedEvent = event },
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) event.primaryColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            border = BorderStroke(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) event.primaryColor else Color.Transparent
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Dynamic mini preview for each specific event
                                DynamicEventLogo(
                                    size = 32.dp,
                                    showBadge = true,
                                    overrideEvent = previewEvent
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            event.getTitle(),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(event.badgeEmoji, fontSize = 13.sp)
                                    }
                                    Text(
                                        event.getSubtitle(),
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 15.sp
                                    )
                                }

                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedEvent = event },
                                    colors = RadioButtonDefaults.colors(selectedColor = event.primaryColor)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Save button
                Button(
                    onClick = {
                        LogoEventManager.setEvent(selectedEvent, context)
                        LauncherIconManager.syncWithEvent(
                            context = context,
                            event = if (selectedEvent == LogoEvent.AUTO) {
                                LogoEventManager.getEffectiveEvent()
                            } else {
                                selectedEvent
                            }
                        )
                        Toast.makeText(
                            context,
                            L.t(
                                "تم تطبيق المظهر وتحديث الأيقونة الخارجية بنجاح ✨",
                                "Theme applied & external app icon updated ✨"
                            ),
                            Toast.LENGTH_SHORT
                        ).show()
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(L.t("حفظ وتطبيق المظهر المختار", "Save & Apply Effect"), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}
