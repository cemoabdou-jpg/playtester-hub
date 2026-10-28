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

    fun initIfNeeded(context: Context) {
        if (isInitialized || isRunningInEmulator()) return
        try {
            MobileAds.initialize(context.applicationContext) {
                isInitialized = true
                Log.d(TAG, "MobileAds initialized")
            }
        } catch (e: Throwable) {
            Log.e(TAG, "MobileAds init
