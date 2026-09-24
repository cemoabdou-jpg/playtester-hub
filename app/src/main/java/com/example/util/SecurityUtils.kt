package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import java.security.MessageDigest
import java.util.Locale

object SecurityUtils {

    /**
     * Sanitizes user input text by removing potentially harmful HTML/script tags or control characters.
     */
    fun sanitizeInput(input: String, maxLength: Int = 500): String {
        if (input.isBlank()) return ""
        val clean = input.trim()
            .replace(Regex("<[^>]*>"), "") // Remove HTML/script tags
            .replace(Regex("[\\r\\n]+"), " ") // Flatten line breaks
        return if (clean.length > maxLength) clean.substring(0, maxLength) else clean
    }

    /**
     * Validates if a URL is a secure HTTPS link.
     */
    fun isSecureHttpsUrl(url: String): Boolean {
        if (url.isBlank()) return false
        val trimmed = url.trim().lowercase(Locale.US)
        return trimmed.startsWith("https://") && (
                trimmed.contains("play.google.com") ||
                        trimmed.contains("groups.google.com") ||
                        trimmed.contains("github.com") ||
                        trimmed.contains("firebaseapp.com") ||
                        trimmed.contains("web.app")
                )
    }

    /**
     * Validates an Android Package Name (e.g., com.example.myapp).
     */
    fun isValidPackageName(packageName: String): Boolean {
        val regex = Regex("^[a-zA-Z][a-zA-Z0-9_]*(\\.[a-zA-Z][a-zA-Z0-9_]*)+$")
        return packageName.isNotBlank() && regex.matches(packageName.trim())
    }

    /**
     * Hashes sensitive string using SHA-256 for secure comparison.
     */
    fun hashSha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Safely opens an external URL using explicit Android Intent with scheme validation.
     */
    fun safeLaunchUrl(context: Context, url: String) {
        val trimmed = url.trim()
        if (!trimmed.startsWith("https://", ignoreCase = true)) {
            Toast.makeText(context, "⚠️ خطأ أمان: يُسمح فقط بفتح الروابط الآمنة (HTTPS)", Toast.LENGTH_LONG).show()
            return
        }

        try {
            val uri = Uri.parse(trimmed)
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "تعذر فتح الرابط بشكل آمن: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }
}
