package com.example.ui.theme

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager

/**
 * Dynamically adapts the external home-screen launcher icon by toggling
 * Manifest <activity-alias> components cleanly without leaving duplicate icons.
 */
object LauncherIconManager {

    enum class Alias(val className: String) {
        DEFAULT("com.example.LauncherDefault"),
        RAMADAN("com.example.LauncherRamadan"),
        EID("com.example.LauncherEid"),
        GOLD_RUSH("com.example.LauncherGoldRush"),
        TECH_RADAR("com.example.LauncherTechRadar"),
        ROYAL_OWNER("com.example.LauncherRoyalOwner"),
        NEW_YEAR("com.example.LauncherNewYear");

        companion object {
            fun fromEvent(event: LogoEvent): Alias = when (event) {
                LogoEvent.AUTO -> DEFAULT
                LogoEvent.RAMADAN -> RAMADAN
                LogoEvent.EID -> EID
                LogoEvent.GOLD_RUSH -> GOLD_RUSH
                LogoEvent.TECH_RADAR -> TECH_RADAR
                LogoEvent.ROYAL_OWNER -> ROYAL_OWNER
                LogoEvent.NEW_YEAR -> NEW_YEAR
            }
        }
    }

    private fun isAliasActive(
        pm: PackageManager,
        packageName: String,
        alias: Alias
    ): Boolean {
        val state = pm.getComponentEnabledSetting(ComponentName(packageName, alias.className))
        return when (state) {
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED -> true
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED -> false
            else -> alias == Alias.DEFAULT // In AndroidManifest, only LauncherDefault is enabled="true"
        }
    }

    /**
     * Synchronizes the external launcher icon with the given event.
     *
     * Key anti-duplication rules:
     * 1. Never call setComponentEnabledSetting on aliases that are already disabled in the manifest.
     * 2. Enable the new alias first with DONT_KILL_APP so the process stays alive to finish disabling the old one.
     * 3. Disable only the previously active alias(es), using flags = 0 on the final disable call
     *    so Android's launcher receives a full refresh and purges the old icon cleanly instead of leaving duplicates.
     */
    fun syncWithEvent(
        context: Context,
        event: LogoEvent = LogoEventManager.getEffectiveEvent(),
        forceSystemRefresh: Boolean = true
    ): Boolean {
        val resolvedEvent = if (event == LogoEvent.AUTO) {
            LogoEventManager.getEffectiveEvent()
        } else {
            event
        }
        return syncWithAlias(
            context = context,
            targetAlias = Alias.fromEvent(resolvedEvent),
            forceSystemRefresh = forceSystemRefresh
        )
    }

    fun resetToDefault(context: Context, forceSystemRefresh: Boolean = true): Boolean {
        return syncWithAlias(
            context = context,
            targetAlias = Alias.DEFAULT,
            forceSystemRefresh = forceSystemRefresh
        )
    }

    private fun syncWithAlias(
        context: Context,
        targetAlias: Alias,
        forceSystemRefresh: Boolean
    ): Boolean {
        return try {
            val appContext = context.applicationContext
            val pm = appContext.packageManager
            val packageName = appContext.packageName
            val targetComponent = ComponentName(packageName, targetAlias.className)

            val isTargetActive = isAliasActive(pm, packageName, targetAlias)
            val activeOldAliases = Alias.entries.filter { alias ->
                alias != targetAlias && isAliasActive(pm, packageName, alias)
            }

            if (isTargetActive && activeOldAliases.isEmpty()) {
                return false
            }

            // 1. Ensure target alias is enabled first (keep process alive so step 2 always executes)
            if (!isTargetActive) {
                pm.setComponentEnabledSetting(
                    targetComponent,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP
                )
            }

            // 2. Disable ONLY previously active alias(es); pass 0 on the final call to flush launcher cache
            activeOldAliases.forEachIndexed { index, oldAlias ->
                val isLast = index == activeOldAliases.lastIndex
                val flags = if (isLast && forceSystemRefresh) 0 else PackageManager.DONT_KILL_APP
                pm.setComponentEnabledSetting(
                    ComponentName(packageName, oldAlias.className),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    flags
                )
            }
            true
        } catch (_: Throwable) {
            false
        }
    }
}
