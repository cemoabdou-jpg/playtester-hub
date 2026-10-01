package com.example.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability

data class AppUpdateStatus(
    val isUpdateAvailable: Boolean = false,
    val latestVersionCode: Int = 13,
    val latestVersionName: String = "2.2",
    val releaseNotes: String = "",
    val isMandatory: Boolean = false,
    val playUpdateInfo: AppUpdateInfo? = null,
    val checkedAtMillis: Long = System.currentTimeMillis()
)

object AppUpdateHelper {
    private const val TAG = "AppUpdateHelper"

    /**
     * Checks Google Play Store In-App Update API for available updates.
     */
    fun checkPlayInAppUpdate(
        context: Context,
        onUpdateResult: (AppUpdateStatus) -> Unit
    ) {
        try {
            val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(context)
            val appUpdateInfoTask = appUpdateManager.appUpdateInfo

            appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
                val availability = appUpdateInfo.updateAvailability()
                val updateAvailable = availability == UpdateAvailability.UPDATE_AVAILABLE ||
                        availability == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS

                val currentVersionCode = getInstalledVersionCode(context)
                val availableVersionCode = if (updateAvailable) appUpdateInfo.availableVersionCode() else currentVersionCode
                val availableVersionName = if (updateAvailable) "1.${availableVersionCode - 7}" else getInstalledVersionName(context)

                val status = AppUpdateStatus(
                    isUpdateAvailable = updateAvailable,
                    latestVersionCode = availableVersionCode,
                    latestVersionName = availableVersionName,
                    releaseNotes = "تحديث جديد يتضمن تحسينات في الأداء وسرعة المزامنة وإصلاحات لملاحظات الاختبار.",
                    isMandatory = false,
                    playUpdateInfo = appUpdateInfo
                )
                onUpdateResult(status)
            }.addOnFailureListener { exception ->
                Log.d(TAG, "Play Store update check unavailable in current environment: ${exception.message}")
                // Return current installed version status gracefully
                onUpdateResult(
                    AppUpdateStatus(
                        isUpdateAvailable = false,
                        latestVersionCode = getInstalledVersionCode(context),
                        latestVersionName = getInstalledVersionName(context)
                    )
                )
            }
        } catch (e: Exception) {
            Log.d(TAG, "Play Store service binding exception: ${e.message}")
            onUpdateResult(
                AppUpdateStatus(
                    isUpdateAvailable = false,
                    latestVersionCode = getInstalledVersionCode(context),
                    latestVersionName = getInstalledVersionName(context)
                )
            )
        }
    }

    /**
     * Starts Google Play In-App Update flow (Flexible or Immediate)
     */
    fun startInAppUpdateFlow(
        activity: Activity,
        appUpdateInfo: AppUpdateInfo,
        requestCode: Int = 1001,
        updateType: Int = AppUpdateType.FLEXIBLE
    ): Boolean {
        return try {
            val appUpdateManager = AppUpdateManagerFactory.create(activity)
            if (appUpdateInfo.isUpdateTypeAllowed(updateType)) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    activity,
                    AppUpdateOptions.defaultOptions(updateType),
                    requestCode
                )
                true
            } else {
                openGooglePlayPage(activity)
                false
            }
        } catch (e: Exception) {
            Log.d(TAG, "In-app update flow fallback: ${e.message}")
            openGooglePlayPage(activity)
            false
        }
    }

    /**
     * Opens the Google Play Store page directly for this app so user can click Update.
     */
    fun openGooglePlayPage(context: Context) {
        val packageName = context.packageName
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(webIntent)
        }
    }

    /**
     * Helper to get installed version code
     */
    fun getInstalledVersionCode(context: Context): Int {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                packageInfo.longVersionCode.toInt()
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode
            }
        } catch (e: Exception) {
            16
        }
    }

    /**
     * Helper to get installed version name
     */
    fun getInstalledVersionName(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "2.5"
        } catch (e: Exception) {
            "2.5"
        }
    }
}
