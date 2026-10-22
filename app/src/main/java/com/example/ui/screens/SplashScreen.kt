package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playtesterhub.app.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onTimeout: () -> Unit
) {
    // Pulse animation for the app icon
    val infiniteTransition = rememberInfiniteTransition(label = "IconPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    // Ambient glow halo opacity
    val haloAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "HaloAlpha"
    )

    // Progress percentage animation for the startup bar
    var progress by remember { mutableFloatStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1600, easing = FastOutSlowInEasing),
        label = "StartProgress"
    )

    LaunchedEffect(Unit) {
        progress = 1f
        delay(1200)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("splash_screen")
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF090D16),
                        Color(0xFF0F172A),
                        Color(0xFF131D31)
                    )
                )
            )
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onTimeout()
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            // Main App Icon Container with Ambient Halo
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(200.dp)
            ) {
                // Outer glowing ambient ring
                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF00E676).copy(alpha = haloAlpha),
                                    Color(0xFF00B0FF).copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Secondary soft border ring
                Box(
                    modifier = Modifier
                        .size(164.dp)
                        .clip(RoundedCornerShape(38.dp))
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF00E676).copy(alpha = 0.6f),
                                    Color(0xFF00B0FF).copy(alpha = 0.4f)
                                )
                            ),
                            shape = RoundedCornerShape(38.dp)
                        )
                )

                // The Official App Icon
                Surface(
                    modifier = Modifier
                        .size(148.dp)
                        .scale(pulseScale)
                        .shadow(
                            elevation = 24.dp,
                            shape = RoundedCornerShape(34.dp),
                            spotColor = Color(0xFF00E676)
                        ),
                    shape = RoundedCornerShape(34.dp),
                    color = Color(0xFF0F172A)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF00E676),
                            modifier = Modifier.size(72.dp)
                        )
                        coil.compose.AsyncImage(
                            model = R.drawable.app_icon_logo,
                            contentDescription = "أيقونة التطبيق - PlayTester Hub",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(34.dp))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App Name & Branding
            Text(
                text = "PlayTester Hub",
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                color = Color(0xFF00E676).copy(alpha = 0.12f),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E676).copy(alpha = 0.35f))
            ) {
                Text(
                    text = "منصة اختبارات Google Play Console المعتمدة",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF69F0AE),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "20 مختبراً حقيقياً • 14 يوماً متواصلة • تقارير تدقيق رسمية",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.75f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(52.dp))

            // Loading Progress Bar
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(220.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.White.copy(alpha = 0.15f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress.coerceIn(0.01f, 1f))
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF00E676),
                                        Color(0xFF00B0FF)
                                    )
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "جاري تهيئة بيئة الاختبار ومزامنة المجتمع...",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Bottom Footer
        Text(
            text = "Google Play Closed Testing Compliance v2.4",
            fontSize = 10.sp,
            color = Color.White.copy(alpha = 0.35f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        )
    }
}
