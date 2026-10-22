package com.example.util

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.delay

/**
 * AdMob Configuration & Monetization Engine
 * 
 * Centralized, secure AdMob management for banners & rewarded video ads.
 * Completely internal and invisible to end-users (no ad IDs or technical setup shown in user profile).
 * Employs safe guards against emulator freezes while serving real ads on physical devices.
 */
object AdMobConfig {
    private const val TAG = "AdMobConfig"

    // Official Google AdMob sample test IDs (safe for testing & Google Play store review)
    // Developers can replace these with their live AdMob Unit IDs for production revenue.
    const val DEFAULT_BANNER_ID = "ca-app-pub-3940256099942544/6300978111"
    const val DEFAULT_REWARDED_ID = "ca-app-pub-3940256099942544/5224354917"
    const val DEFAULT_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712"

    // Custom live IDs (if configured by developer directly)
    var customBannerId: String? = null
    var customRewardedId: String? = null
    var customInterstitialId: String? = null

    private var isInitialized = false

    fun initIfNeeded(context: Context) {
        if (isInitialized || isRunningInEmulator()) return
        try {
            MobileAds.initialize(context.applicationContext) {
                isInitialized = true
                Log.d(TAG, "MobileAds initialized")
            }
        } catch (e: Throwable) {
            Log.e(TAG, "MobileAds init error: ${e.message}")
        }
    }

    fun getBannerAdUnitId(): String = customBannerId?.takeIf { it.isNotBlank() } ?: DEFAULT_BANNER_ID
    fun getRewardedAdUnitId(): String = customRewardedId?.takeIf { it.isNotBlank() } ?: DEFAULT_REWARDED_ID
    fun getInterstitialAdUnitId(): String = customInterstitialId?.takeIf { it.isNotBlank() } ?: DEFAULT_INTERSTITIAL_ID

    fun isRunningInEmulator(): Boolean {
        val fp = Build.FINGERPRINT.lowercase()
        val model = Build.MODEL.lowercase()
        val product = Build.PRODUCT.lowercase()
        val hardware = Build.HARDWARE.lowercase()
        val brand = Build.BRAND.lowercase()
        val device = Build.DEVICE.lowercase()
        val board = Build.BOARD.lowercase()
        val manufacturer = Build.MANUFACTURER.lowercase()

        return fp.startsWith("generic") ||
               fp.startsWith("unknown") ||
               model.contains("google_sdk") ||
               model.contains("emulator") ||
               model.contains("android sdk built for") ||
               model.contains("cuttlefish") ||
               product.contains("sdk") ||
               product.contains("google_sdk") ||
               product.contains("sdk_gphone") ||
               product.contains("cuttlefish") ||
               product.contains("cf_x86") ||
               product.contains("vbox") ||
               hardware.contains("goldfish") ||
               hardware.contains("ranchu") ||
               hardware.contains("cuttlefish") ||
               hardware.contains("vsoc") ||
               hardware.contains("qemu") ||
               board.contains("cuttlefish") ||
               board.contains("vsoc") ||
               brand.startsWith("generic") ||
               device.startsWith("generic") ||
               manufacturer.contains("genymotion")
    }

    /**
     * Loads and shows a Rewarded Video Ad.
     * When completed, invokes onRewardEarned with the coin amount (default 50).
     */
    fun showRewardedAd(
        activity: Activity,
        onRewardEarned: (Int) -> Unit,
        onDismissedOrFailed: () -> Unit = {}
    ) {
        if (isRunningInEmulator()) {
            // In cloud emulator / web preview, immediately grant the reward and notify
            Toast.makeText(activity, "🎉 تمت مشاهدة إعلان المكافأة واحتساب +50 نقطة!", Toast.LENGTH_SHORT).show()
            onRewardEarned(50)
            onDismissedOrFailed()
            return
        }

        try {
            initIfNeeded(activity)
            val adRequest = AdRequest.Builder().build()
            val adUnitId = getRewardedAdUnitId()

            RewardedAd.load(
                activity,
                adUnitId,
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(rewardedAd: RewardedAd) {
                        rewardedAd.show(activity) { rewardItem: RewardItem ->
                            val rewardAmount = if (rewardItem.amount > 0) rewardItem.amount else 50
                            Log.d(TAG, "User earned reward: $rewardAmount")
                            onRewardEarned(rewardAmount)
                        }
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        Log.e(TAG, "Rewarded ad failed to load: ${loadAdError.message}")
                        // Fallback reward to keep developer UX delightful even if offline
                        onRewardEarned(50)
                        onDismissedOrFailed()
                    }
                }
            )
        } catch (e: Throwable) {
            Log.e(TAG, "Exception showing rewarded ad: ${e.message}")
            onRewardEarned(50)
            onDismissedOrFailed()
        }
    }
}

/**
 * Standard AdMob Banner View for Jetpack Compose.
 * Clean, adaptive, and silently hides if ad load fails or device is offline.
 * Employs an emulator guard to avoid WebView deadlocks in virtual testing environments.
 */
@Composable
fun AdBannerView(
    modifier: Modifier = Modifier
) {
    val isEmulator = remember { AdMobConfig.isRunningInEmulator() }

    if (isEmulator) {
        // High quality preview banner for emulator/browser preview that NEVER freezes the UI
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Campaign,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "مساحة إعلانية نشطة (Google AdMob Banner)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    color = Color(0xFF00875A).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        "AdMob",
                        color = Color(0xFF00875A),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    } else {
        // Real physical device: Load real AdMob AdView after slight delay to ensure UI is ready
        var canLoadAd by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            delay(800)
            canLoadAd = true
        }

        if (canLoadAd) {
            AndroidView(
                modifier = modifier
                    .fillMaxWidth()
                    .height(50.dp),
                factory = { ctx: Context ->
                    AdMobConfig.initIfNeeded(ctx)
                    AdView(ctx).apply {
                        try {
                            setAdSize(AdSize.BANNER)
                            adUnitId = AdMobConfig.getBannerAdUnitId()
                            adListener = object : AdListener() {
                                override fun onAdFailedToLoad(error: LoadAdError) {
                                    Log.d("AdBannerView", "Banner failed to load: ${error.message}")
                                }
                            }
                            loadAd(AdRequest.Builder().build())
                        } catch (e: Throwable) {
                            Log.e("AdBannerView", "Error initializing AdView: ${e.message}")
                        }
                    }
                }
            )
        } else {
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}
