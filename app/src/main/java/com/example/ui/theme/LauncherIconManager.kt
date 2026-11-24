package com.example.ui.theme

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager

/**
 * Dynamically adapts the external home-screen launcher icon by toggling
 * Manifest <activity-alias> components according to the active LogoEvent.
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

    /**
     * Synchronizes the external launcher icon with the active event (or AUTO evaluation).
     * Idempotent: returns true if a change was applied, false if already up to date or skipped.
     */
    fun syncWithEvent(
        context: Context,
        event: LogoEvent = LogoEventManager.getEffectiveEvent()
    ): Boolean {
        return try {
            val appContext = context.applicationContext
            val pm = appContext.packageManager
            val packageName = appContext.packageName

            val resolvedEvent = if (event == LogoEvent.AUTO) {
                LogoEventManager.getEffectiveEvent()
            } else {
                event
            }
            val targetAlias = Alias.fromEvent(resolvedEvent)
            val targetComponent = ComponentName(packageName, targetAlias.className)

            val currentTargetState = pm.getComponentEnabledSetting(targetComponent)
            val isTargetAlreadyActive =
                currentTargetState == PackageManager.COMPONENT_ENABLED_STATE_ENABLED ||
                    (targetAlias == Alias.DEFAULT &&
                        currentTargetState == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT)

            val hasStaleEnabledAlias = Alias.entries.any { alias ->
                if (alias == targetAlias) return@any false
                val comp = ComponentName(packageName, alias.className)
                val state = pm.getComponentEnabledSetting(comp)
                state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED ||
                    (alias == Alias.DEFAULT &&
                        state == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT)
            }

            if (isTargetAlreadyActive && !hasStaleEnabledAlias) {
                return false
            }

            // Enable target alias FIRST so the launcher never loses all entry points
            pm.setComponentEnabledSetting(
                targetComponent,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )

            // Disable all other aliases
            Alias.entries.forEach { alias ->
                if (alias != targetAlias) {
                    val otherComponent = ComponentName(packageName, alias.className)
                    val state = pm.getComponentEnabledSetting(otherComponent)
                    if (state != PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
                        pm.setComponentEnabledSetting(
                            otherComponent,
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                            PackageManager.DONT_KILL_APP
                        )
                    }
                }
            }
            true
        } catch (_: Throwable) {
            false
        }
    }
}
