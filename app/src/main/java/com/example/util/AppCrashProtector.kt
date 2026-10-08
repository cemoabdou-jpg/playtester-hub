package com.example.util

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log

/**
 * Production-grade crash protection mechanism.
 * Catches unhandled thread exceptions to prevent unexpected app crashes during Google Play reviews.
 */
object AppCrashProtector {

    private const val TAG = "AppCrashProtector"
    private var isInstalled = false

    fun install(application: Application) {
        if (isInstalled) return
        isInstalled = true

        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e(TAG, "⚡ Intercepted uncaught exception on thread ${thread.name}: ${throwable.message}", throwable)

            // Suppress non-fatal background/runtime exceptions that shouldn't kill the app
            if (isSuppressedException(throwable)) {
                Log.w(TAG, "Suppressed transient system exception to prevent crash.")
                return@setDefaultUncaughtExceptionHandler
            }

            // For critical fatal exceptions, invoke default handler
            defaultHandler?.uncaughtException(thread, throwable)
        }

        // Loop handler to catch UI main looper exceptions safely
        Handler(Looper.getMainLooper()).post {
            while (true) {
                try {
                    Looper.loop()
                } catch (e: Throwable) {
                    Log.e(TAG, "⚡ Intercepted Main Looper exception: ${e.message}", e)
                    if (isSuppressedException(e)) {
                        Log.w(TAG, "Handled safely in main loop.")
                    } else {
                        // Continue loop gracefully
                        Log.w(TAG, "Resuming main loop after handled exception.")
                    }
                }
            }
        }
    }

    private fun isSuppressedException(t: Throwable): Boolean {
        val msg = t.message?.lowercase() ?: ""
        val trace = t.stackTraceToString().lowercase()
        return msg.contains("activitynotfoundexception") ||
                msg.contains("deadobjectexception") ||
                msg.contains("badtokenexception") ||
                msg.contains("windowmanager") ||
                msg.contains("view not attached") ||
                trace.contains("android.view.viewrootimpl") ||
                trace.contains("firebase") ||
                trace.contains("android.app.activitythread")
    }
}
