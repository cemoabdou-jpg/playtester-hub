package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LogoEvent
import com.example.ui.theme.LogoEventManager
import com.playtesterhub.app.R
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DynamicEventLogo(
    modifier: Modifier = Modifier,
    size: Dp = 38.dp,
    isOwner: Boolean = false,
    showBadge: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val activeEvent = if (isOwner) LogoEvent.ROYAL_OWNER else LogoEventManager.getEffectiveEvent()

    // 1. Continuous Animations (Rotation, Pulse, Shimmer, Glow)
    val infiniteTransition = rememberInfiniteTransition(label = "DynamicLogoEffects")

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "LogoRotation"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "LogoPulse"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "LogoGlow"
    )

    // Interactive Tap Bounce
    var isTapped by remember { mutableStateOf(false) }
    val tapScale by animateFloatAsState(
        targetValue = if (isTapped) 0.88f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        finishedListener = { isTapped = false },
        label = "TapBounce"
    )

    val cornerRadius = (size * 0.28f).coerceAtLeast(8.dp)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size + 14.dp)
            .scale(tapScale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isTapped = true
                onClick?.invoke()
            }
    ) {
        // 2. Dynamic Canvas Particle & Aura System
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasW = this.size.width
            val canvasH = this.size.height
            val center = Offset(canvasW / 2f, canvasH / 2f)
            val baseRadius = (canvasW / 2f) * 0.85f

            // A. Glowing Ambient Outer Halo
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        activeEvent.glowColor.copy(alpha = glowAlpha * 0.5f),
                        activeEvent.primaryColor.copy(alpha = glowAlpha * 0.25f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = baseRadius * 1.35f
                ),
                radius = baseRadius * 1.35f,
                center = center
            )

            // B. Specific Occasion Canvas Effects
            when (activeEvent) {
                LogoEvent.RAMADAN -> {
                    // Ramadan: Golden Crescent & Twinkling Orbiting Stars
                    rotate(rotationAngle, pivot = center) {
                        for (i in 0 until 4) {
                            val deg = (i * 90f) + (rotationAngle * 0.5f)
                            val rad = (deg * Math.PI / 180.0).toFloat()
                            val starDist = baseRadius * 1.15f
                            val sx = center.x + starDist * cos(rad)
                            val sy = center.y + starDist * sin(rad)
                            drawCircle(
                                color = activeEvent.secondaryColor.copy(alpha = 0.8f),
                                radius = 2.dp.toPx(),
                                center = Offset(sx, sy)
                            )
                        }
                    }
                }

                LogoEvent.TECH_RADAR -> {
                    // Tech Radar: Sweep arc + radar pulse wave
                    drawCircle(
                        color = activeEvent.primaryColor.copy(alpha = 0.25f),
                        radius = baseRadius * 1.12f,
                        center = center,
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                    rotate(rotationAngle, pivot = center) {
                        drawArc(
                            brush = Brush.sweepGradient(
                                listOf(
                                    Color.Transparent,
                                    activeEvent.primaryColor.copy(alpha = 0.6f)
                                )
                            ),
                            startAngle = 0f,
                            sweepAngle = 75f,
                            useCenter = false,
                            topLeft = Offset(center.x - baseRadius * 1.12f, center.y - baseRadius * 1.12f),
                            size = Size(baseRadius * 2.24f, baseRadius * 2.24f),
                            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }

                LogoEvent.GOLD_RUSH -> {
                    // Gold Rush: Radiant Fiery Sparks
                    rotate(-rotationAngle * 1.2f, pivot = center) {
                        for (i in 0 until 6) {
                            val deg = (i * 60f)
                            val rad = (deg * Math.PI / 180.0).toFloat()
                            val sparkDist = baseRadius * (1.08f + (0.08f * sin(rotationAngle + i)))
                            val sx = center.x + sparkDist * cos(rad)
                            val sy = center.y + sparkDist * sin(rad)
                            drawCircle(
                                color = activeEvent.glowColor,
                                radius = 2.5.dp.toPx(),
                                center = Offset(sx, sy)
                            )
                        }
                    }
                }

                LogoEvent.EID -> {
                    // Eid Celebrations: Colorful Confetti Orbit
                    rotate(rotationAngle * 0.8f, pivot = center) {
                        val confettiColors = listOf(
                            Color(0xFFFF1744),
                            Color(0xFFFFD600),
                            Color(0xFF00E676),
                            Color(0xFF00E5FF),
                            Color(0xFFD500F9)
                        )
                        confettiColors.forEachIndexed { idx, col ->
                            val deg = (idx * (360f / confettiColors.size))
                            val rad = (deg * Math.PI / 180.0).toFloat()
                            val dist = baseRadius * 1.18f
                            val cx = center.x + dist * cos(rad)
                            val cy = center.y + dist * sin(rad)
                            drawCircle(color = col, radius = 2.dp.toPx(), center = Offset(cx, cy))
                        }
                    }
                }

                LogoEvent.ROYAL_OWNER -> {
                    // Royal Owner: Majestic Violet-Gold Pulsing Ring
                    drawCircle(
                        brush = Brush.sweepGradient(
                            listOf(
                                Color(0xFFFFD700),
                                Color(0xFF7C3AED),
                                Color(0xFFFFD700)
                            )
                        ),
                        radius = baseRadius * 1.15f,
                        center = center,
                        style = Stroke(width = 1.8.dp.toPx())
                    )
                }

                LogoEvent.NEW_YEAR -> {
                    // New Year: Rotating Diamond Snowflakes
                    rotate(rotationAngle * 0.5f, pivot = center) {
                        for (i in 0 until 4) {
                            val deg = (i * 90f)
                            val rad = (deg * Math.PI / 180.0).toFloat()
                            val dist = baseRadius * 1.15f
                            val cx = center.x + dist * cos(rad)
                            val cy = center.y + dist * sin(rad)
                            drawCircle(
                                color = Color.White.copy(alpha = 0.85f),
                                radius = 2.dp.toPx(),
                                center = Offset(cx, cy)
                            )
                        }
                    }
                }

                else -> {
                    // Standard Soft Glow
                    drawCircle(
                        color = activeEvent.primaryColor.copy(alpha = 0.2f),
                        radius = baseRadius * 1.1f,
                        center = center,
                        style = Stroke(width = 1.dp.toPx())
                    )
                }
            }
        }

        // 3. Central Brand Logo Container
        Surface(
            modifier = Modifier
                .size(size)
                .scale(pulseScale)
                .clip(RoundedCornerShape(cornerRadius))
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            activeEvent.glowColor.copy(alpha = 0.85f),
                            activeEvent.primaryColor.copy(alpha = 0.65f)
                        )
                    ),
                    shape = RoundedCornerShape(cornerRadius)
                ),
            shape = RoundedCornerShape(cornerRadius),
            color = Color(0xFF0F172A)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                // Background subtle tone
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    activeEvent.primaryColor.copy(alpha = 0.15f),
                                    Color(0xFF0A0E1A)
                                )
                            )
                        )
                )

                // High-Res App Icon Image (R.drawable.app_icon_logo)
                Image(
                    painter = painterResource(id = R.drawable.app_icon_logo),
                    contentDescription = "شعار PlayTester Hub",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(cornerRadius))
                )

                // Fallback / Overlay Tint for special role (Owner)
                if (isOwner) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF7C3AED).copy(alpha = 0.15f))
                    )
                }
            }
        }

        // 4. Occasion Badge / Emoji Indicator (Bottom-Right / Top-Right)
        if (showBadge && activeEvent.badgeEmoji.isNotBlank()) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 4.dp, y = 4.dp)
                    .size((size * 0.44f).coerceAtLeast(16.dp))
                    .clip(CircleShape)
                    .background(Color(0xFF0F172A))
                    .border(1.dp, activeEvent.glowColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = activeEvent.badgeEmoji,
                    fontSize = (size.value * 0.24f).coerceAtLeast(9f).sp,
                    lineHeight = 12.sp
                )
            }
        }
    }
}
