package com.example.ui.components

import android.app.Activity
import android.content.Context
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.util.AdMobConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class SpinSlice(
    val coins: Int,
    val label: String,
    val color: Color
)

val SPIN_SLICES = listOf(
    SpinSlice(15, "+15 🪙", Color(0xFF2563EB)),
    SpinSlice(50, "+50 ⭐", Color(0xFF7C3AED)),
    SpinSlice(25, "+25 🪙", Color(0xFF059669)),
    SpinSlice(100, "+100 👑", Color(0xFFD97706)),
    SpinSlice(35, "+35 🪙", Color(0xFF0891B2)),
    SpinSlice(150, "💎 150", Color(0xFFDC2626))
)

// مدة الانتظار بين اللفات المجانية (24 ساعة)
private const val COOLDOWN_HOURS = 24L
private const val COOLDOWN_MILLIS = COOLDOWN_HOURS * 60 * 60 * 1000L

@Composable
fun DailyLuckySpinDialog(
    onDismiss: () -> Unit,
    onRewardWon: (Int) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val prefs = remember { context.getSharedPreferences("lucky_wheel_prefs", Context.MODE_PRIVATE) }

    fun checkCanSpinFree(): Boolean {
        val lastSpin = prefs.getLong("last_free_spin_time", 0L)
        return (System.currentTimeMillis() - lastSpin) >= COOLDOWN_MILLIS
    }

    fun getRemainingCooldown(): Long {
        val lastSpin = prefs.getLong("last_free_spin_time", 0L)
        val passed = System.currentTimeMillis() - lastSpin
        return (COOLDOWN_MILLIS - passed).coerceAtLeast(0L)
    }

    var canSpinFree by remember { mutableStateOf(checkCanSpinFree()) }
    var remainingTimeMs by remember { mutableLongStateOf(getRemainingCooldown()) }
    var isSpinning by remember { mutableStateOf(false) }
    var lastWonCoins by remember { mutableStateOf<Int?>(null) }
    var showWonSheet by remember { mutableStateOf(false) }
    var isDoubleClaimed by remember { mutableStateOf(false) } // لمنع تكرار المضاعفة
    val rotation = remember { Animatable(0f) }

    // تحديث العداد التنازلي كل ثانية إذا كانت اللفة المجانية غير متاحة
    LaunchedEffect(canSpinFree) {
        while (!canSpinFree) {
            val remaining = getRemainingCooldown()
            remainingTimeMs = remaining
            if (remaining <= 0L) {
                canSpinFree = true
                break
            }
            delay(1000L)
        }
    }

    fun formatRemaining(ms: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(ms)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(ms) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(ms) % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    fun executeSpin(targetSliceIndex: Int, earnedCoins: Int) {
        if (isSpinning) return
        isSpinning = true
        lastWonCoins = null
        showWonSheet = false
        isDoubleClaimed = false // تصفير حالة المضاعفة للدورة الجديدة

        coroutineScope.launch {
            val sliceAngle = 360f / SPIN_SLICES.size
            val currentRotation = rotation.value
            val extraRotations = (5 + Random.nextInt(3)) * 360f
            val targetOffset = (SPIN_SLICES.size - targetSliceIndex) * sliceAngle - (sliceAngle / 2f)
            val destinationAngle = currentRotation + extraRotations + targetOffset - (currentRotation % 360f)

            rotation.animateTo(
                targetValue = destinationAngle,
                animationSpec = tween(
                    durationMillis = 3600,
                    easing = FastOutSlowInEasing
                )
            )

            isSpinning = false
            lastWonCoins = earnedCoins
            showWonSheet = true
            onRewardWon(earnedCoins)
        }
    }

    Dialog(onDismissRequest = { if (!isSpinning) onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { if (!isSpinning) onDismiss() },
                        enabled = !isSpinning,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }

                    Text(
                        "عجلة الحظ اليومية 🎡",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Surface(
                        color = if (canSpinFree) Color(0xFFFFB300).copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (canSpinFree) "1 لفة مجانية" else formatRemaining(remainingTimeMs),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (canSpinFree) Color(0xFFE65100) else Color.Gray,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (canSpinFree) {
                        "لف العجلة يومياً مجاناً، أو شاهد إعلاناً للحصول على لفات إضافية غير محدودة!"
                    } else {
                        "استهلكت اللفة اليومية المجانية! شاهد إعلاناً للّف الآن أو انتظر انتهاء الوقت."
                    },
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // The Spinning Wheel Container
                Box(
                    modifier = Modifier.size(230.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer Glow Ring
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(
                                Brush.sweepGradient(
                                    listOf(
                                        Color(0xFFFFB300),
                                        Color(0xFFE65100),
                                        Color(0xFF7C3AED),
                                        Color(0xFFFFB300)
                                    )
                                )
                            )
                            .padding(6.dp)
                    ) {
                        // Inner Wheel Canvas
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .rotate(rotation.value)
                        ) {
                            val canvasSize = size.minDimension
                            val radius = canvasSize / 2f
                            val center = Offset(radius, radius)
                            val sliceAngle = 360f / SPIN_SLICES.size

                            SPIN_SLICES.forEachIndexed { index, slice ->
                                val startAngle = index * sliceAngle - 90f
                                drawArc(
                                    color = slice.color,
                                    startAngle = startAngle,
                                    sweepAngle = sliceAngle,
                                    useCenter = true,
                                    size = Size(canvasSize, canvasSize)
                                )

                                // Draw separator lines
                                val angleRad = (startAngle * PI / 180f).toFloat()
                                val lineEnd = Offset(
                                    center.x + radius * cos(angleRad),
                                    center.y + radius * sin(angleRad)
                                )
                                drawLine(
                                    color = Color.White.copy(alpha = 0.6f),
                                    start = center,
                                    end = lineEnd,
                                    strokeWidth = 3f
                                )

                                // Draw label text using native canvas
                                val midAngleRad = ((startAngle + sliceAngle / 2f) * PI / 180f).toFloat()
                                val textDist = radius * 0.62f
                                val textPos = Offset(
                                    center.x + textDist * cos(midAngleRad),
                                    center.y + textDist * sin(midAngleRad)
                                )

                                drawContext.canvas.nativeCanvas.apply {
                                    val paint = android.graphics.Paint().apply {
                                        color = android.graphics.Color.WHITE
                                        textSize = 34f
                                        typeface = android.graphics.Typeface.DEFAULT_BOLD
                                        textAlign = android.graphics.Paint.Align.CENTER
                                        isAntiAlias = true
                                    }
                                    drawText(slice.label, textPos.x, textPos.y + 12f, paint)
                                }
                            }
                        }
                    }

                    // Center Hub Button
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF1E293B),
                        border = androidx.compose.foundation.BorderStroke(3.dp, Color(0xFFFFB300)),
                        modifier = Modifier.size(54.dp),
                        shadowElevation = 6.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFB300),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    // Top Pointer Triangle
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-6).dp)
                    ) {
                        Canvas(modifier = Modifier.size(24.dp, 20.dp)) {
                            val path = Path().apply {
                                moveTo(size.width / 2f, size.height)
                                lineTo(0f, 0f)
                                lineTo(size.width, 0f)
                                close()
                            }
                            drawPath(path, Color(0xFFFFB300))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Celebratory Outcome Banner
                if (showWonSheet && lastWonCoins != null) {
                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4CAF50)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "🎉 مبروك! كسبت $lastWonCoins عملة!",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF2E7D32)
                                )
                                Text(
                                    "أضيفت لرصيدك في الحساب",
                                    fontSize = 10.sp,
                                    color = Color(0xFF388E3C)
                                )
                            }

                            Button(
                                onClick = {
                                    if (isDoubleClaimed || isSpinning) return@Button
                                    val activity = context as? Activity
                                    if (activity != null) {
                                        AdMobConfig.showRewardedAd(
                                            activity = activity,
                                            onRewardEarned = {
                                                if (!isDoubleClaimed) {
                                                    isDoubleClaimed = true
                                                    val rewardToAdd = lastWonCoins ?: 0
                                                    if (rewardToAdd > 0) {
                                                        onRewardWon(rewardToAdd)
                                                        lastWonCoins = rewardToAdd * 2
                                                    }
                                                }
                                            }
                                        )
                                    }
                                },
                                enabled = !isDoubleClaimed && !isSpinning,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isDoubleClaimed) Color.Gray else Color(0xFF2E7D32)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text(
                                    text = if (isDoubleClaimed) "تمت المضاعفة ✓" else "ضاعفها (2x) 🎬",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Spin Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (canSpinFree) {
                        Button(
                            onClick = {
                                prefs.edit().putLong("last_free_spin_time", System.currentTimeMillis()).apply()
                                canSpinFree = false
                                remainingTimeMs = COOLDOWN_MILLIS

                                val picked = Random.nextInt(SPIN_SLICES.size)
                                executeSpin(picked, SPIN_SLICES[picked].coins)
                            },
                            enabled = !isSpinning,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00875A)),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 10.dp)
                        ) {
                            Text("تدوير مجاني 🎡", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    // Watch Ad for Extra Spin
                    Button(
                        onClick = {
                            val activity = context as? Activity
                            if (activity != null) {
                                AdMobConfig.showRewardedAd(
                                    activity = activity,
                                    onRewardEarned = {
                                        val picked = Random.nextInt(SPIN_SLICES.size)
                                        executeSpin(picked, SPIN_SLICES[picked].coins)
                                    }
                                )
                            } else {
                                val picked = Random.nextInt(SPIN_SLICES.size)
                                executeSpin(picked, SPIN_SLICES[picked].coins)
                            }
                        },
                        enabled = !isSpinning,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8F00)),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        Icon(Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("لفة إضافية (إعلان 🎬)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
