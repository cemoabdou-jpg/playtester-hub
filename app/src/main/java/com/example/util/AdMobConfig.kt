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
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.delay

/**
 * AdMob Configuration & Monetization Engine
 */
object AdMobConfig {
    private const val TAG = "AdMobConfig"

    // Production AdMob IDs (Publisher: pub-4457316141776318)
    const val LIVE_APP_ID = "ca-app-pub-4457316141776318~5453365517"
    const val LIVE_BANNER_ID = "ca-app-pub-4457316141776318/9790768606"
    const val LIVE_REWARDED_ID = "ca-app-pub-4457316141776318/3740540584"
    const val LIVE_INTERSTITIAL_ID = "ca-app-pub-4457316141776318/8738880112"

    // Custom live IDs
    var customBannerId: String? = LIVE_BANNER_ID
    var customRewardedId: String? = LIVE_REWARDED_ID
    var customInterstitialId: String? = LIVE_INTERSTITIAL_ID

    private var interstitialAd: InterstitialAd? = null
    private var isInterstitialLoading = false
    private var isInitialized = false
    private var lastInterstitialShownAtMs: Long = 0L
    private const val INTERSTITIAL_COOLDOWN_MS = 180_000L // 3 minutes minimum between interstitials

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

    fun getBannerAdUnitId(): String = customBannerId?.takeIf { it.isNotBlank() } ?: LIVE_BANNER_ID
    fun getRewardedAdUnitId(): String = customRewardedId?.takeIf { it.isNotBlank() } ?: LIVE_REWARDED_ID
    fun getInterstitialAdUnitId(): String = customInterstitialId?.takeIf { it.isNotBlank() } ?: LIVE_INTERSTITIAL_ID

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
     * Preload interstitial ad in background.
     */
    fun loadInterstitialAd(context: Context) {
        if (isRunningInEmulator() || interstitialAd != null || isInterstitialLoading) return

        initIfNeeded(context)
        isInterstitialLoading = true

        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            getInterstitialAdUnitId(),
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isInterstitialLoading = false
                    Log.d(TAG, "Interstitial ad loaded successfully")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    interstitialAd = null
                    isInterstitialLoading = false
                    Log.e(TAG, "Interstitial ad failed to load: ${loadAdError.message}")
                }
            }
        )
    }

    /**
     * Show interstitial ad only after explicit user actions, respecting a 3-minute cooldown.
     */
    fun showInterstitialAfterAction(
        activity: Activity,
        onAdClosed: () -> Unit = {}
    ) {
        val now = System.currentTimeMillis()
        if (now - lastInterstitialShownAtMs < INTERSTITIAL_COOLDOWN_MS) {
            onAdClosed()
            return
        }
        showInterstitialAd(activity, onAdClosed)
    }

    /**
     * Show interstitial ad with emulator guard and callback on close.
     */
    fun showInterstitialAd(
        activity: Activity,
        onAdClosed: () -> Unit = {}
    ) {
        if (isRunningInEmulator()) {
            lastInterstitialShownAtMs = System.currentTimeMillis()
            onAdClosed()
            return
        }

        if (interstitialAd != null) {
            lastInterstitialShownAtMs = System.currentTimeMillis()
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    loadInterstitialAd(activity)
                    onAdClosed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    interstitialAd = null
                    loadInterstitialAd(activity)
                    onAdClosed()
                }
            }
            interstitialAd?.show(activity)
        } else {
            loadInterstitialAd(activity)
            onAdClosed()
        }
    }

    /**
     * Loads and shows a Rewarded Video Ad.
     * Policy-compliant: Rewards are strictly credited only when earned or in emulator preview.
     */
    fun showRewardedAd(
        activity: Activity,
        onRewardEarned: (Int) -> Unit,
        onDismissedOrFailed: () -> Unit = {}
    ) {
        if (isRunningInEmulator()) {
            Toast.makeText(
                activity,
                com.example.ui.util.L.t(
                    "معاينة المحاكي: تمت مشاهدة إعلان المكافأة واحتساب +50 عملة!",
                    "Emulator preview: Rewarded ad completed (+50 coins credited)!"
                ),
                Toast.LENGTH_SHORT
            ).show()
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
                        rewardedAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                onDismissedOrFailed()
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                Toast.makeText(
                                    activity,
                                    com.example.ui.util.L.t(
                                        "تعذر عرض الإعلان حالياً، يرجى المحاولة لاحقاً",
                                        "Unable to display the ad right now, please try again later"
                                    ),
                                    Toast.LENGTH_SHORT
                                ).show()
                                onDismissedOrFailed()
                            }
                        }
                        rewardedAd.show(activity) { rewardItem: RewardItem ->
                            val rewardAmount = if (rewardItem.amount > 0) rewardItem.amount else 50
                            Log.d(TAG, "User earned reward: $rewardAmount")
                            onRewardEarned(rewardAmount)
                        }
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        Log.e(TAG, "Rewarded ad failed to load: ${loadAdError.message}")
                        Toast.makeText(
                            activity,
                            com.example.ui.util.L.t(
                                "لا يتوفر إعلان مكافأة حالياً، يرجى المحاولة بعد قليل",
                                "No rewarded ad available right now, please try again shortly"
                            ),
                            Toast.LENGTH_SHORT
                        ).show()
                        onDismissedOrFailed()
                    }
                }
            )
        } catch (e: Throwable) {
            Log.e(TAG, "Exception showing rewarded ad: ${e.message}")
            Toast.makeText(
                activity,
                com.example.ui.util.L.t(
                    "تعذر تحميل الإعلان حالياً، يرجى المحاولة لاحقاً",
                    "Could not load the ad right now, please try again later"
                ),
                Toast.LENGTH_SHORT
            ).show()
            onDismissedOrFailed()
        }
    }
}

/**
 * Standard AdMob Banner View for Jetpack Compose.
 */
@Composable
fun AdBannerView(
    modifier: Modifier = Modifier
) {
    val isEmulator = remember { AdMobConfig.isRunningInEmulator() }

    if (isEmulator) {
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
                        com.example.ui.util.L.t(
                            "مساحة إعلانية نشطة",
                            "Active Ad Space"
                        ),
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
                        com.example.ui.util.L.t("إعلان", "Ad"),
                        color = Color(0xFF00875A),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    } else {
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
