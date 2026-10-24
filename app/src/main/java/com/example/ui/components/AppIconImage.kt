package com.example.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.LruCache
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import kotlin.math.absoluteValue

// In-memory cache for decoded Base64 and local bitmaps
private val bitmapMemoryCache = LruCache<String, Bitmap>(60)

data class PresetIconOption(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val gradient: List<Color>
)

val PRESET_APP_ICONS = listOf(
    PresetIconOption("preset:games", "ألعاب", Icons.Default.SportsEsports, listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9))),
    PresetIconOption("preset:tools", "أدوات", Icons.Default.Build, listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8))),
    PresetIconOption("preset:productivity", "إنتاجية", Icons.Default.TaskAlt, listOf(Color(0xFF10B981), Color(0xFF047857))),
    PresetIconOption("preset:social", "تواصل", Icons.Default.Forum, listOf(Color(0xFFEC4899), Color(0xFFBE185D))),
    PresetIconOption("preset:education", "تعليم", Icons.Default.School, listOf(Color(0xFFF59E0B), Color(0xFFD97706))),
    PresetIconOption("preset:finance", "مال وأعمال", Icons.Default.AccountBalance, listOf(Color(0xFF059669), Color(0xFF065F46))),
    PresetIconOption("preset:health", "صحة ورياضة", Icons.Default.Favorite, listOf(Color(0xFFEF4444), Color(0xFFB91C1C))),
    PresetIconOption("preset:shopping", "تسوق ومتاجر", Icons.Default.ShoppingBag, listOf(Color(0xFFF97316), Color(0xFFC2410C))),
    PresetIconOption("preset:media", "ميديا وفيديو", Icons.Default.PlayCircle, listOf(Color(0xFF06B6D4), Color(0xFF0E7490))),
    PresetIconOption("preset:code", "تطوير وبرمجة", Icons.Default.Code, listOf(Color(0xFF475569), Color(0xFF1E293B)))
)

@Composable
fun AppIconImage(
    iconUrl: String?,
    appName: String,
    category: String = "",
    modifier: Modifier = Modifier.size(50.dp),
    shape: Shape = RoundedCornerShape(14.dp)
) {
    val raw = iconUrl?.trim().orEmpty()
    var isImageError by remember(raw) { mutableStateOf(false) }

    // Check if user selected one of our preset icons
    val preset = remember(raw) {
        if (raw.startsWith("preset:")) {
            PRESET_APP_ICONS.firstOrNull { it.id == raw }
        } else null
    }

    // Normalize and transform any URL format
    val cleanUrl = remember(raw) {
        if (preset != null) "" else normalizeImageUrl(raw)
    }

    // Try decoding Base64 if image is embedded
    val base64Bitmap: Bitmap? = remember(cleanUrl) {
        if (cleanUrl.isBlank() || isLikelyHttpUrl(cleanUrl)) {
            null
        } else {
            decodeBase64Safely(cleanUrl)
        }
    }

    val paletteIndex = (appName.hashCode().absoluteValue) % PRESET_APP_ICONS.size
    val fallbackPreset = PRESET_APP_ICONS[paletteIndex]
    val activePreset = preset ?: fallbackPreset
    val activeIcon = getCategoryIcon(category, appName)

    Box(
        modifier = modifier
            .clip(shape)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.2f),
                shape = shape
            ),
        contentAlignment = Alignment.Center
    ) {
        when {
            // 1. Preset Icon selected by developer
            preset != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    preset.gradient[0],
                                    preset.gradient[1]
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Subtle glossy highlight
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.45f)
                            .align(Alignment.TopCenter)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.25f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                    Icon(
                        imageVector = preset.icon,
                        contentDescription = appName,
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            // 2. Decoded Base64 / Local Bitmap
            base64Bitmap != null && !isImageError -> {
                Image(
                    bitmap = base64Bitmap.asImageBitmap(),
                    contentDescription = appName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // 3. Web URL, Local File URI, or Android Content URI via Coil
            cleanUrl.isNotBlank() && isLoadableViaCoil(cleanUrl) && !isImageError -> {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(cleanUrl)
                        .crossfade(true)
                        .allowHardware(true)
                        .build(),
                    contentDescription = appName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    onState = { state ->
                        if (state is AsyncImagePainter.State.Error) {
                            isImageError = true
                        }
                    }
                )
            }

            // 4. Fallback: High-Quality 3D Play Store Stylized Emblem
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    activePreset.gradient[0],
                                    activePreset.gradient[1]
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Glossy shine overlay
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.45f)
                            .align(Alignment.TopCenter)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.3f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = activeIcon,
                            contentDescription = appName,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        val initial = appName.trim().take(1).uppercase()
                        if (initial.isNotBlank()) {
                            Text(
                                text = initial,
                                color = Color.White.copy(alpha = 0.95f),
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Normalizes image URLs from Google Drive, Dropbox, Play Store, or missing protocols.
 */
private fun normalizeImageUrl(raw: String): String {
    if (raw.isBlank()) return ""
    var url = raw.trim()

    if (url.contains("drive.google.com")) {
        val fileId = extractGoogleDriveFileId(url)
        if (fileId != null) {
            return "https://drive.google.com/uc?export=view&id=$fileId"
        }
    }

    if (url.contains("dropbox.com") && url.contains("dl=0")) {
        url = url.replace("dl=0", "raw=1")
    }

    return when {
        url.startsWith("data:image", ignoreCase = true) -> url
        url.startsWith("file://", ignoreCase = true) -> url
        url.startsWith("content://", ignoreCase = true) -> url
        url.startsWith("http://", ignoreCase = true) -> url
        url.startsWith("https://", ignoreCase = true) -> url
        url.startsWith("//") -> "https:$url"
        url.startsWith("www.") -> "https://$url"
        url.contains(".") && !url.contains(" ") && url.indexOf(".") < url.indexOf("/").let { if (it == -1) url.length else it } -> {
            "https://$url"
        }
        else -> url
    }
}

private fun isLikelyHttpUrl(url: String): Boolean {
    return url.startsWith("http://", ignoreCase = true) ||
           url.startsWith("https://", ignoreCase = true) ||
           url.startsWith("file://", ignoreCase = true) ||
           url.startsWith("content://", ignoreCase = true)
}

private fun isLoadableViaCoil(url: String): Boolean {
    return isLikelyHttpUrl(url)
}

private fun decodeBase64Safely(raw: String): Bitmap? {
    try {
        val cacheKey = if (raw.length > 100) raw.take(50) + raw.takeLast(50) else raw
        bitmapMemoryCache.get(cacheKey)?.let { return it }

        val isDataUri = raw.startsWith("data:image", ignoreCase = true)
        val base64Data = if (isDataUri && raw.contains(",")) {
            raw.substringAfter(",")
        } else {
            raw
        }

        val sanitized = base64Data.replace("\n", "").replace("\r", "").replace(" ", "").trim()
        if (sanitized.length < 50) return null

        val decodedBytes = Base64.decode(sanitized, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        if (bitmap != null) {
            bitmapMemoryCache.put(cacheKey, bitmap)
        }
        return bitmap
    } catch (_: Throwable) {
        return null
    }
}

private fun extractGoogleDriveFileId(url: String): String? {
    return try {
        if (url.contains("/file/d/")) {
            url.substringAfter("/file/d/").substringBefore("/")
        } else if (url.contains("id=")) {
            url.substringAfter("id=").substringBefore("&")
        } else {
            null
        }
    } catch (_: Exception) {
        null
    }
}

private fun getCategoryIcon(category: String, appName: String): ImageVector {
    val combined = "$category $appName".lowercase()
    return when {
        combined.contains("لعب") || combined.contains("game") -> Icons.Default.SportsEsports
        combined.contains("أدوات") || combined.contains("tool") -> Icons.Default.Build
        combined.contains("إنتاج") || combined.contains("product") || combined.contains("عمل") -> Icons.Default.TaskAlt
        combined.contains("تعليم") || combined.contains("edu") || combined.contains("كتاب") -> Icons.Default.School
        combined.contains("تواصل") || combined.contains("chat") || combined.contains("social") -> Icons.Default.Forum
        combined.contains("مال") || combined.contains("finance") || combined.contains("pay") -> Icons.Default.AccountBalance
        combined.contains("صحة") || combined.contains("health") || combined.contains("fit") -> Icons.Default.Favorite
        combined.contains("تسوق") || combined.contains("shop") || combined.contains("store") -> Icons.Default.ShoppingBag
        combined.contains("ميديا") || combined.contains("media") || combined.contains("فيديو") || combined.contains("صوت") -> Icons.Default.PlayCircle
        else -> Icons.Default.Smartphone
    }
}
