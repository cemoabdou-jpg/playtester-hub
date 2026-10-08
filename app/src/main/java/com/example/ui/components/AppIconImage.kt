package com.example.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Base64
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
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
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.util.GooglePlayScraperHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * Universal, high-performance app icon and screenshot image component.
 * Displays with multi-layer fallback:
 * 1. Native app icon from Android device if installed (PackageManager)
 * 2. Cached local image file from app_icons folder
 * 3. Base64 Data URIs (data:image/...;base64,...) via direct native Bitmap decoding
 * 4. Raw Base64 image strings
 * 5. Local file paths (/data/user/... or file://...)
 * 6. Content URIs (content://...)
 * 7. Web URLs (https://... / http://...) via Coil
 * 8. Auto-fetch from Google Play if package name provided
 * 9. Clean, authentic Material 3 initial letter monogram badge (no random icons)
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
    val cleanUrl = iconUrl.trim().let { if (it.startsWith("preset://")) "" else it }
    var scrapedIconUrl by remember(cleanUrl, packageName) { mutableStateOf<String?>(null) }
    var isCoilFailed by remember(cleanUrl, scrapedIconUrl) { mutableStateOf(false) }

    // If icon is blank but package name exists, attempt background scrape from Google Play
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

    val effectiveUrl = when {
        cleanUrl.isNotBlank() -> cleanUrl
        !scrapedIconUrl.isNullOrBlank() -> scrapedIconUrl!!
        else -> ""
    }

    // Bitmap resolution:
    // 1. Direct Base64 / Local File / Content URI
    // 2. Installed App on Device (PackageManager)
    // 3. Local app_icons directory cache matching packageName
    val decodedBitmap: Bitmap? = remember(effectiveUrl, packageName) {
        when {
            // Case A: Base64 string (from gallery picker or database)
            effectiveUrl.startsWith("data:image") || effectiveUrl.contains("base64,") || isLikelyRawBase64(effectiveUrl) -> {
                decodeBase64String(effectiveUrl)
            }
            // Case B: Local file path
            effectiveUrl.startsWith("/") || effectiveUrl.startsWith("file://") -> {
                val filePath = if (effectiveUrl.startsWith("file://")) effectiveUrl.removePrefix("file://") else effectiveUrl
                val file = File(filePath)
                if (file.exists() && file.isFile && file.length() > 0) {
                    try {
                        BitmapFactory.decodeFile(file.absolutePath)
                    } catch (_: Exception) { null }
                } else null
            }
            // Case C: Content URI
            effectiveUrl.startsWith("content://") -> {
                try {
                    context.contentResolver.openInputStream(Uri.parse(effectiveUrl))?.use {
                        BitmapFactory.decodeStream(it)
                    }
                } catch (_: Exception) { null }
            }
            // Case D: If URL is blank, check if app is installed on this device or cached in app_icons
            effectiveUrl.isBlank() && packageName.isNotBlank() -> {
                loadInstalledAppBitmap(context, packageName) ?: loadCachedIconFile(context, packageName)
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
            // Priority 1: Successfully decoded Bitmap (Base64, Local File, Installed app, or Local Cache)
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

            // Priority 2: Web URL loaded via Coil
            (effectiveUrl.startsWith("http://") || effectiveUrl.startsWith("https://")) && !isCoilFailed -> {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(effectiveUrl)
                        .crossfade(true)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36")
                        .listener(onError = { _, _ -> isCoilFailed = true })
                        .build(),
                    contentDescription = "$appName icon",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(shape),
                    contentScale = contentScale,
                    onError = { isCoilFailed = true }
                )
            }

            // Priority 3: Clean, authentic default app monogram badge (NO random icons)
            else -> {
                DefaultAppBadge(
                    appName = appName,
                    size = size,
                    shape = shape
                )
            }
        }
    }
}

/**
 * Professional, clean default app badge.
 * Displays the initial letter of the app or a standard app symbol.
 * NO random icons (no pizza, rocket, headphones, games, etc.) are ever used.
 */
@Composable
fun DefaultAppBadge(
    appName: String,
    size: Dp,
    shape: Shape = RoundedCornerShape(12.dp)
) {
    val initial = remember(appName) {
        val trimmed = appName.trim()
        val firstLetterOrDigit = trimmed.firstOrNull { it.isLetterOrDigit() }
        firstLetterOrDigit?.toString()?.uppercase() ?: ""
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(shape)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .border(
                BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                shape = shape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (initial.isNotEmpty()) {
            Text(
                text = initial,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.ExtraBold,
                fontSize = (size.value * 0.44f).coerceIn(12f, 48f).sp
            )
        } else {
            Icon(
                imageVector = Icons.Default.Apps,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size((size * 0.5f).coerceAtLeast(16.dp))
            )
        }
    }
}

private fun loadInstalledAppBitmap(context: Context, packageName: String): Bitmap? {
    if (packageName.isBlank()) return null
    return try {
        val pm = context.packageManager
        val appInfo = pm.getApplicationInfo(packageName, 0)
        val drawable = appInfo.loadIcon(pm)
        drawableToBitmap(drawable)
    } catch (_: Exception) {
        null
    }
}

private fun loadCachedIconFile(context: Context, packageName: String): Bitmap? {
    if (packageName.isBlank()) return null
    return try {
        val cleanPkg = packageName.replace("[^a-zA-Z0-9._]".toRegex(), "_")
        val iconsDir = File(context.filesDir, "app_icons")
        if (iconsDir.exists()) {
            val files = iconsDir.listFiles { _, name -> name.contains(cleanPkg) && (name.endsWith(".png") || name.endsWith(".jpg")) }
            files?.maxByOrNull { it.lastModified() }?.let { file ->
                BitmapFactory.decodeFile(file.absolutePath)
            }
        } else null
    } catch (_: Exception) {
        null
    }
}

private fun drawableToBitmap(drawable: Drawable): Bitmap {
    if (drawable is BitmapDrawable && drawable.bitmap != null) {
        return drawable.bitmap
    }
    val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 128
    val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 128
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
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
    return trimmed.take(50).all { c ->
        c in 'A'..'Z' || c in 'a'..'z' || c in '0'..'9' || c == '+' || c == '/' || c == '='
    }
}

/**
 * Utility functions for processing app icon images and screenshot proofs picked by users.
 */
object AppIconProcessor {

    fun processPickedImage(context: Context, uri: Uri, packageName: String = ""): String {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            if (inputStream == null) return uri.toString()

            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            if (originalBitmap == null) return uri.toString()

            // Scale to standard high-resolution icon/proof size (max 384x384)
            val maxDimension = 384
            val width = originalBitmap.width
            val height = originalBitmap.height
            val scale = minOf(maxDimension.toFloat() / width, maxDimension.toFloat() / height, 1.0f)
            val targetWidth = (width * scale).toInt().coerceAtLeast(1)
            val targetHeight = (height * scale).toInt().coerceAtLeast(1)

            val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true)

            // 1. Save locally for instant, permanent offline loading without URI expiration
            val iconsDir = File(context.filesDir, "app_icons").apply { mkdirs() }
            val cleanPkg = packageName.ifBlank { "app" }.replace("[^a-zA-Z0-9._]".toRegex(), "_")
            val localFile = File(iconsDir, "icon_${cleanPkg}_${System.currentTimeMillis()}.png")
            FileOutputStream(localFile).use { out ->
                scaledBitmap.compress(Bitmap.CompressFormat.PNG, 95, out)
            }

            // 2. Generate a compact Base64 data string for Cloud sync (< 35KB)
            val byteStream = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, byteStream)
            val base64Bytes = Base64.encodeToString(byteStream.toByteArray(), Base64.NO_WRAP)
            "data:image/jpeg;base64,$base64Bytes"
        } catch (e: Exception) {
            // Fallback: Copy raw file directly so it doesn't fail due to ephemeral permission
            try {
                val iconsDir = File(context.filesDir, "app_icons").apply { mkdirs() }
                val fallbackFile = File(iconsDir, "fallback_${System.currentTimeMillis()}.png")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(fallbackFile).use { output ->
                        input.copyTo(output)
                    }
                }
                fallbackFile.absolutePath
            } catch (_: Exception) {
                uri.toString()
            }
        }
    }
}
