// Engineered by uncoalesced
package com.uncoalesced.impart.core.security

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloakManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val PREFS_NAME = "impart_cloak_prefs"
    private val KEY_IS_CLOAKED = "is_cloaked"

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isCloaked(): Boolean {
        return prefs.getBoolean(KEY_IS_CLOAKED, false)
    }

    fun setCloakEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_IS_CLOAKED, enabled).apply()

        val packageManager = context.packageManager
        val mainComponent = ComponentName(context, "com.uncoalesced.impart.MainActivity")
        val aliasComponent = ComponentName(context, "com.uncoalesced.impart.MainActivityAlias")

        if (enabled) {
            // Enable Calculator Alias, Disable Impart Main
            packageManager.setComponentEnabledSetting(
                aliasComponent,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )
            packageManager.setComponentEnabledSetting(
                mainComponent,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )
        } else {
            // Enable Impart Main, Disable Calculator Alias
            packageManager.setComponentEnabledSetting(
                mainComponent,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )
            packageManager.setComponentEnabledSetting(
                aliasComponent,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )
        }
    }
}
