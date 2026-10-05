package com.example.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PlayGreen
import com.example.util.GooglePlayScraperHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * High-performance, universal app icon component.
 * Guaranteed to display:
 * 1. Base64 Data URIs (data:image/...;base64,...) via direct native Bitmap decoding
 * 2. Raw Base64 image strings
 * 3. Local file paths (/data/user/... or file://...)
 * 4. Content URIs (content://...)
 * 5. Web URLs (https://... / http://...) via Coil
 * 6. Auto-fetch from Google Play if package name provided and icon is blank
 * 7. Polished gradient fallback with app initials if no icon is available
 */
@Composable
fun AppIconImage(
    iconUrl: String,
    appName: String,
    modifier: Modifier = Modifier,
    packageName: String = "",
    shape: Shape = RoundedCornerShape(12.dp),
    size: Dp = 48.dp,
    contentScale: ContentScale = ContentScale.Crop
) {
    val context = LocalContext.current
    val cleanUrl = iconUrl.trim()
    var scrapedIconUrl by remember(packageName) { mutableStateOf<String?>(null) }
    var isCoilFailed by remember(cleanUrl, scrapedIconUrl) { mutableStateOf(false) }

    // If icon is blank but package name exists, attempt background scrape
    LaunchedEffect(cleanUrl, packageName) {
        if (cleanUrl.isBlank() && packageName.isNotBlank()) {
            withContext(Dispatchers.IO) {
                try {
                    val info = GooglePlayScraperHelper.fetchAppInfo(packageName)
                    if (info != null && info.iconUrl.isNotBlank()) {
                        scrapedIconUrl = info.iconUrl
                    }
                } catch (_: Exception) {}
            }
        }
    }

    val effectiveUrl = if (cleanUrl.isNotBlank()) cleanUrl else (scrapedIconUrl ?: "")

    // Try native decoding for Base64 and local files first (instant & 100% reliable)
    val decodedBitmap: Bitmap? = remember(effectiveUrl) {
        when {
            effectiveUrl.isBlank() -> null
            effectiveUrl.startsWith("data:image") || effectiveUrl.contains("base64,") -> {
                decodeBase64String(effectiveUrl)
            }
            effectiveUrl.startsWith("/") || effectiveUrl.startsWith("file://") -> {
                val filePath = if (effectiveUrl.startsWith("file://")) effectiveUrl.removePrefix("file://") else effectiveUrl
                val file = File(filePath)
                if (file.exists() && file.isFile && file.length() > 0) {
                    try {
                        BitmapFactory.decodeFile(file.absolutePath)
                    } catch (_: Exception) {
                        null
                    }
                } else null
            }
            effectiveUrl.startsWith("content://") -> {
                try {
                    context.contentResolver.openInputStream(Uri.parse(effectiveUrl))?.use {
                        BitmapFactory.decodeStream(it)
                    }
                } catch (_: Exception) {
                    null
                }
            }
            // If it looks like raw Base64 without header
            isLikelyRawBase64(effectiveUrl) -> {
                decodeBase64String(effectiveUrl)
            }
            else -> null
        }
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        when {
            // Case 1: Successfully decoded Bitmap (Base64, local file, or content Uri)
            decodedBitmap != null -> {
                Image(
                    bitmap = decodedBitmap.asImageBitmap(),
                    contentDescription = "$appName icon",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(shape),
                    contentScale = contentScale
                )
            }

            // Case 2: Web URL (http/https) loaded via Coil
            effectiveUrl.startsWith("http://") || effectiveUrl.startsWith("https://") -> {
                if (!isCoilFailed) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(effectiveUrl)
                            .crossfade(true)
                            .listener(
                                onError = { _, _ -> isCoilFailed = true }
                            )
                            .build(),
                        contentDescription = "$appName icon",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(shape),
                        contentScale = contentScale,
                        onError = { isCoilFailed = true }
                    )
                } else {
                    InitialsBadge(appName = appName, size = size)
                }
            }

            // Case 3: Empty or unsupported URL -> elegant initials badge
            else -> {
                InitialsBadge(appName = appName, size = size)
            }
        }
    }
}

@Composable
private fun InitialsBadge(appName: String, size: Dp) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(
                        PlayGreen.copy(alpha = 0.25f),
                        CyanAccent.copy(alpha = 0.20f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        val initials = if (appName.isNotBlank()) {
            appName.trim().take(2).uppercase()
        } else "AP"

        if (size <= 28.dp) {
            Icon(
                imageVector = Icons.Default.Android,
                contentDescription = null,
                tint = PlayGreen,
                modifier = Modifier.size(size * 0.65f)
            )
        } else {
            Text(
                text = initials,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = PlayGreen,
                fontSize = if (size <= 40.dp) 11.sp else 15.sp
            )
        }
    }
}

private fun decodeBase64String(input: String): Bitmap? {
    return try {
        val cleanData = if (input.contains(",")) {
            input.substringAfter(",")
        } else {
            input
        }.trim()
        val bytes = Base64.decode(cleanData, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    } catch (_: Exception) {
        null
    }
}

private fun isLikelyRawBase64(input: String): Boolean {
    val trimmed = input.trim()
    if (trimmed.length < 80) return false
    if (trimmed.startsWith("http") || trimmed.startsWith("/") || trimmed.startsWith("content:")) return false
    // Check if composed of base64 valid characters
    return trimmed.take(50).all { c ->
        c in 'A'..'Z' || c in 'a'..'z' || c in '0'..'9' || c == '+' || c == '/' || c == '='
    }
}

/**
 * Utility functions for processing app icon images picked by developers.
 */
object AppIconProcessor {

    /**
     * Reads a picked image URI, scales it to 256x256 max, saves a local file,
     * and returns a compact Base64 data URI so the icon travels via Firestore
     * and is rendered across all devices without external image hosting.
     */
    fun processPickedImage(context: Context, uri: Uri, packageName: String = ""): String {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            if (inputStream == null) return uri.toString()

            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            if (originalBitmap == null) return uri.toString()

            // Scale to icon standard size (max 256x256)
            val maxDimension = 256
            val width = originalBitmap.width
            val height = originalBitmap.height
            val scale = minOf(maxDimension.toFloat() / width, maxDimension.toFloat() / height, 1.0f)
            val targetWidth = (width * scale).toInt().coerceAtLeast(1)
            val targetHeight = (height * scale).toInt().coerceAtLeast(1)

            val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true)

            // 1. Save locally for instant offline loading
            val iconsDir = File(context.filesDir, "app_icons").apply { mkdirs() }
            val cleanPkg = packageName.ifBlank { "app" }.replace("[^a-zA-Z0-9._]".toRegex(), "_")
            val localFile = File(iconsDir, "icon_${cleanPkg}_${System.currentTimeMillis()}.png")
            FileOutputStream(localFile).use { out ->
                scaledBitmap.compress(Bitmap.CompressFormat.PNG, 95, out)
            }

            // 2. Generate a compact Base64 data string for Cloud sync (< 25KB)
            val byteStream = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, byteStream)
            val base64Bytes = Base64.encodeToString(byteStream.toByteArray(), Base64.NO_WRAP)
            val base64DataUri = "data:image/jpeg;base64,$base64Bytes"

            // Return the Base64 Data URI so it is completely portable locally and remotely
            base64DataUri
        } catch (e: Exception) {
            uri.toString()
        }
    }
}
