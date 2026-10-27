package com.example.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast

object AppInstallVerifier {

    /**
     * Checks if the application package is installed on the user's Android device.
     */
    fun isAppInstalled(context: Context, packageName: String): Boolean {
        if (packageName.isBlank()) return false
        val cleanPkg = packageName.trim()
        val pm = context.packageManager

        // Method 1: Check Launch Intent
        try {
            if (pm.getLaunchIntentForPackage(cleanPkg) != null) {
                return true
            }
        } catch (_: Exception) {}

        // Method 2: Check Package Info directly
        return try {
            pm.getPackageInfo(cleanPkg, 0)
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Launches the installed app directly from PlayTester Hub.
     */
    fun launchApp(context: Context, packageName: String): Boolean {
        val cleanPkg = packageName.trim()
        val launchIntent = context.packageManager.getLaunchIntentForPackage(cleanPkg)
        return if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                context.startActivity(launchIntent)
                true
            } catch (e: Exception) {
                Toast.makeText(context, "تعذر تشغيل التطبيق: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                false
            }
        } else {
            Toast.makeText(context, "التطبيق ليس مثبتاً على جهازك حالياً", Toast.LENGTH_SHORT).show()
            false
        }
    }

    /**
     * Opens the app in Google Play Store or opens the Web Opt-in testing invitation link.
     */
    fun openStoreOrOptIn(context: Context, packageName: String, optInUrl: String = "", googleGroupUrl: String = "") {
        val cleanPkg = packageName.trim()
        val targetUrl = when {
            optInUrl.isNotBlank() -> optInUrl.trim()
            googleGroupUrl.isNotBlank() -> googleGroupUrl.trim()
            cleanPkg.isNotBlank() -> "https://play.google.com/apps/testing/$cleanPkg"
            else -> "https://play.google.com"
        }

        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "تعذر فتح الرابط: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Parses and extracts the Android Package Name (e.g. com.example.app)
     * from full URLs (Play Store link, Opt-In testing link) or raw text.
     */
    fun extractPackageName(input: String): String {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return ""

        // Case 1: https://play.google.com/store/apps/details?id=com.company.app
        if (trimmed.contains("id=")) {
            try {
                val uri = Uri.parse(trimmed)
                val idParam = uri.getQueryParameter("id")
                if (!idParam.isNullOrBlank()) {
                    return idParam.trim()
                }
            } catch (_: Exception) {}
        }

        // Case 2: https://play.google.com/apps/testing/com.company.app
        if (trimmed.contains("play.google.com/apps/testing/")) {
            val afterTesting = trimmed.substringAfter("play.google.com/apps/testing/").substringBefore("?").substringBefore("/")
            if (afterTesting.isNotBlank()) {
                return afterTesting.trim()
            }
        }

        // Case 3: Raw package name or other URLs containing standard reverse-DNS
        val candidate = trimmed.substringBefore("?").substringBefore("&")
        val cleanCandidate = candidate.substringAfterLast("/")
        return if (isValidPackageName(cleanCandidate)) cleanCandidate else trimmed
    }

    /**
     * Validates Android package name pattern: e.g. com.example.app
     */
    fun isValidPackageName(packageName: String): Boolean {
        val regex = Regex("^[a-zA-Z][a-zA-Z0-9_]*(\\.[a-zA-Z][a-zA-Z0-9_]*)+$")
        return regex.matches(packageName.trim())
    }

    /**
     * Generates standard Google Play Opt-in URL and Direct Store URL for a package.
     */
    fun generateGooglePlayUrls(packageName: String): Pair<String, String> {
        val clean = packageName.trim()
        val webOptIn = "https://play.google.com/apps/testing/$clean"
        val storeUrl = "https://play.google.com/store/apps/details?id=$clean"
        return Pair(webOptIn, storeUrl)
    }
}
