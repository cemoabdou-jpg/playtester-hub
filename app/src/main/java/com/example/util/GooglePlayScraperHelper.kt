package com.example.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.util.regex.Pattern

data class GooglePlayAppInfo(
    val packageName: String,
    val title: String,
    val iconUrl: String
)

object GooglePlayScraperHelper {

    suspend fun fetchAppInfo(queryOrUrl: String): GooglePlayAppInfo? = withContext(Dispatchers.IO) {
        val packageName = extractPackageName(queryOrUrl)
        if (packageName.isBlank()) return@withContext null

        try {
            val playUrl = "https://play.google.com/store/apps/details?id=$packageName&hl=en"
            val conn = (URL(playUrl).openConnection() as HttpURLConnection).apply {
                setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36")
                connectTimeout = 8000
                readTimeout = 8000
                instanceFollowRedirects = true
            }

            if (conn.responseCode in 200..299) {
                val html = conn.inputStream.bufferedReader().use { it.readText() }

                // Extract og:image
                val iconRegex = Pattern.compile("""property="og:image"\s+content="([^"]+)"""")
                val iconMatcher = iconRegex.matcher(html)
                var iconUrl = if (iconMatcher.find()) iconMatcher.group(1) ?: "" else ""

                // Secondary fallback: content="..." property="og:image"
                if (iconUrl.isBlank()) {
                    val revIconRegex = Pattern.compile("""content="([^"]+)"\s+property="og:image"""")
                    val revMatcher = revIconRegex.matcher(html)
                    if (revMatcher.find()) {
                        iconUrl = revMatcher.group(1) ?: ""
                    }
                }

                // Tertiary fallback to play-lh image URL
                if (iconUrl.isBlank()) {
                    val lhRegex = Pattern.compile("""https://play-lh\.googleusercontent\.com/[a-zA-Z0-9_\-=]+""")
                    val lhMatcher = lhRegex.matcher(html)
                    if (lhMatcher.find()) {
                        iconUrl = lhMatcher.group(0)
                    }
                }

                // Extract og:title
                val titleRegex = Pattern.compile("""property="og:title"\s+content="([^"]+)"""")
                val titleMatcher = titleRegex.matcher(html)
                var title = if (titleMatcher.find()) titleMatcher.group(1) ?: "" else ""
                title = title.replace(Regex(" - Apps on Google Play.*"), "").trim()

                return@withContext GooglePlayAppInfo(
                    packageName = packageName,
                    title = title,
                    iconUrl = iconUrl
                )
            }
        } catch (_: Exception) {
            // Network failure or app is in closed test (unlisted publicly)
        }
        null
    }

    fun extractPackageName(input: String): String {
        val trimmed = input.trim()
        if (trimmed.contains("id=")) {
            val afterId = trimmed.substringAfter("id=")
            return afterId.substringBefore("&").substringBefore(" ").substringBefore("#").trim()
        }
        if (trimmed.contains("apps/details")) {
            val query = trimmed.substringAfter("?")
            query.split("&").forEach { param ->
                if (param.startsWith("id=")) return param.substringAfter("id=").trim()
            }
        }
        // Direct package name like com.company.app
        if (trimmed.matches(Regex("^[a-zA-Z0-9_]+(\\.[a-zA-Z0-9_]+)+$"))) {
            return trimmed
        }
        return trimmed
    }
}
