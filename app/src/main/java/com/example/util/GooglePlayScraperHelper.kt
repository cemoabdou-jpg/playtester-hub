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
            val playUrl = "https://play.google.com/store/apps/details?id=$packageName&hl=en&gl=US"
            val conn = (URL(playUrl).openConnection() as HttpURLConnection).apply {
                setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 14; Pixel 8) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Mobile Safari/537.36")
                setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                setRequestProperty("Accept-Language", "en-US,en;q=0.9")
                connectTimeout = 7000
                readTimeout = 7000
                instanceFollowRedirects = true
            }

            if (conn.responseCode in 200..299) {
                val html = conn.inputStream.bufferedReader().use { it.readText() }

                var iconUrl = ""

                // 1. og:image
                val iconRegex1 = Pattern.compile("""property="og:image"\s+content="([^"]+)"""")
                val m1 = iconRegex1.matcher(html)
                if (m1.find()) {
                    iconUrl = m1.group(1) ?: ""
                }

                // 2. content="..." property="og:image"
                if (iconUrl.isBlank()) {
                    val iconRegex2 = Pattern.compile("""content="([^"]+)"\s+property="og:image"""")
                    val m2 = iconRegex2.matcher(html)
                    if (m2.find()) {
                        iconUrl = m2.group(1) ?: ""
                    }
                }

                // 3. twitter:image
                if (iconUrl.isBlank()) {
                    val iconRegex3 = Pattern.compile("""name="twitter:image"\s+content="([^"]+)"""")
                    val m3 = iconRegex3.matcher(html)
                    if (m3.find()) {
                        iconUrl = m3.group(1) ?: ""
                    }
                }

                // 4. play-lh image URL pattern
                if (iconUrl.isBlank()) {
                    val lhRegex = Pattern.compile("""https://play-lh\.googleusercontent\.com/[a-zA-Z0-9_\-]+""")
                    val lhMatcher = lhRegex.matcher(html)
                    if (lhMatcher.find()) {
                        iconUrl = lhMatcher.group(0) ?: ""
                    }
                }

                // Extract title
                val titleRegex = Pattern.compile("""property="og:title"\s+content="([^"]+)"""")
                val titleMatcher = titleRegex.matcher(html)
                var title = if (titleMatcher.find()) titleMatcher.group(1) ?: "" else ""
                title = title.replace(Regex(" - Apps on Google Play.*"), "").trim()

                if (iconUrl.isNotBlank()) {
                    return@withContext GooglePlayAppInfo(
                        packageName = packageName,
                        title = title.ifBlank { packageName },
                        iconUrl = iconUrl
                    )
                }
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
        if (trimmed.contains("apps/testing/")) {
            val after = trimmed.substringAfter("apps/testing/")
            return after.substringBefore("/").substringBefore("?").substringBefore("&").trim()
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
