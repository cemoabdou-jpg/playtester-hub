package com.example.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.LruCache
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

// In-memory cache for decoded Base64 and local bitmaps so they don't re-decode on every recomposition
private val bitmapMemoryCache = LruCache<String, Bitmap>(50)

// Vibrant dynamic palettes for icons when image is not present
private val GRADIENT_PALETTES = listOf(
    listOf(Color(0xFF4F46E5), Color(0xFF7C3AED)), // Indigo -> Purple
    listOf(Color(0xFF059669), Color(0xFF10B981)), // Emerald -> Teal
    listOf(Color(0xFFD97706), Color(0xFFF59E0B)), // Amber -> Orange
    listOf(Color(0xFFDC2626), Color(0xFFF43F5E)), // Crimson -> Rose
    listOf(Color(0xFF2563EB), Color(0xFF06B6D4)), // Blue -> Cyan
    listOf(Color(0xFF7C3AED), Color(0xFFEC4899)), // Violet -> Pink
    listOf(Color(0xFF0891B2), Color(0xFF14B8A6)), // Cyan -> Teal
    listOf(Color(0xFFEA580C), Color(0xFFF97316)), // Orange -> Amber
    listOf(Color(0xFF475569), Color(0xFF64748B))  // Slate
)

@Composable
fun AppIconImage(
    iconUrl: String?,
    appName: String,
    category: String = "",
    modifier: Modifier = Modifier.size(46.dp),
    shape: Shape = RoundedCornerShape(12.dp)
) {
    val raw = iconUrl?.trim().orEmpty()
    var isImageError by remember(raw) { mutableStateOf(false) }

    // Normalize and transform any URL format
    val cleanUrl = remember(raw) {
        normalizeImageUrl(raw)
    }

    // Try decoding Base64 if image is embedded (data:image/... or raw Base64)
    val base64Bitmap: Bitmap? = remember(cleanUrl) {
        if (cleanUrl.isBlank() || isLikelyHttpUrl(cleanUrl)) {
            null
        } else {
            decodeBase64Safely(cleanUrl)
        }
    }

    val paletteIndex = (appName.hashCode().absoluteValue) % GRADIENT_PALETTES.size
    val gradientColors = GRADIENT_PALETTES[paletteIndex]
    val categoryIcon = getCategoryIcon(category, appName)

    Box(
        modifier = modifier
            .clip(shape)
            .border(
                width = 0.5.dp,
                color = Color.White.copy(alpha = 0.25f),
                shape = shape
            ),
        contentAlignment = Alignment.Center
    ) {
        when {
            // 1. Decoded Base64 / Local Bitmap
            base64Bitmap != null && !isImageError -> {
                Image(
                    bitmap = base64Bitmap.asImageBitmap(),
                    contentDescription = appName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // 2. Web URL, Local File URI, or Android Content URI via Coil
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

            // 3. Fallback: Dynamic Stylish App Store Tile with App Initial / Category Icon
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.linearGradient(gradientColors)),
                    contentAlignment = Alignment.Center
                ) {
                    val firstChar = appName.trim().firstOrNull()
                    if (firstChar != null && (firstChar.isLetter() || firstChar.isDigit())) {
                        Text(
                            text = firstChar.uppercaseChar().toString(),
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp
                        )
                    } else {
                        Icon(
                            imageVector = categoryIcon,
                            contentDescription = appName,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
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

    // 1. Google Drive view link -> direct download link
    if (url.contains("drive.google.com")) {
        val fileId = extractGoogleDriveFileId(url)
        if (fileId != null) {
            return "https://drive.google.com/uc?export=view&id=$fileId"
        }
    }

    // 2. Dropbox share link -> raw link
    if (url.contains("dropbox.com") && url.contains("dl=0")) {
        url = url.replace("dl=0", "raw=1")
    }

    // 3. Protocol normalizations
    return when {
        url.startsWith("data:image", ignoreCase = true) -> url
        url.startsWith("file://", ignoreCase = true) -> url
        url.startsWith("content://", ignoreCase = true) -> url
        url.startsWith("http://", ignoreCase = true) -> url
        url.startsWith("https://", ignoreCase = true) -> url
        url.startsWith("//") -> "https:$url"
        url.startsWith("www.") -> "https://$url"
        // Domain name like play-lh.googleusercontent.com/xyz or imgur.com/xyz
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

/**
 * Safely decodes Base64 data with caching and sanitization.
 */
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

        // Sanitize newlines and spaces that often appear in Base64
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
        else -> Icons.Default.Smartphone
    }
}
