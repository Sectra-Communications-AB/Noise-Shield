package com.sectra.noiseshield

import android.app.Service.RESTRICTIONS_SERVICE
import android.content.Context
import android.content.RestrictionsManager
import android.content.SharedPreferences
import android.os.Build

class NoiseSettings(context: Context) {

    companion object {
        const val PREFERENCES_KEY = "preferences"
        const val APP_RESTRICTION_NO_FILE_INFO_SHOWN = "app_restriction_no_file_info_shown"
        const val MINIMUM_VOLUME_PERCENT_USER_KEY = "minimum_volume_percent_user"
        const val MINIMUM_VOLUME_PERCENT_DEFAULT = 60
        const val MINIMUM_VOLUME_PERCENT_KEY = "minimum_volume_percent"
        private var MINIMUM_VOLUME_PERCENT_KEY_MODEL_SPECIFIC =
            "${MINIMUM_VOLUME_PERCENT_KEY}_${Build.MODEL}"
    }

    private var prefs: SharedPreferences =
        context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE)
    private var restrictionsManager: RestrictionsManager =
        context.getSystemService(RESTRICTIONS_SERVICE) as RestrictionsManager

    private fun getAppRestrictionNoFileInfoShown(): Boolean {
        return prefs.getBoolean(APP_RESTRICTION_NO_FILE_INFO_SHOWN, false)
    }

    fun setAppRestrictionNoFileInfoShown(value: Boolean) {
        prefs.edit().putBoolean(APP_RESTRICTION_NO_FILE_INFO_SHOWN, value).apply()
    }

    private fun hasMinimumVolumePercentUser(): Boolean {
        return prefs.contains(MINIMUM_VOLUME_PERCENT_USER_KEY)
    }

    fun setMinimumVolumePercentUser(value: Float) {
        prefs.edit().putFloat(MINIMUM_VOLUME_PERCENT_USER_KEY, value).apply()
    }

    private fun getMinimumVolumePercentUser(): Float {
        return prefs.getFloat(
            MINIMUM_VOLUME_PERCENT_USER_KEY,
            MINIMUM_VOLUME_PERCENT_DEFAULT.toFloat(),
        )
    }

    fun getMinimumVolumePercent(): Int {
        val restrictions = restrictionsManager.applicationRestrictions

        return if (restrictions.containsKey(MINIMUM_VOLUME_PERCENT_KEY_MODEL_SPECIFIC)) {
            restrictions.getInt(MINIMUM_VOLUME_PERCENT_KEY_MODEL_SPECIFIC)
        } else if (restrictions.containsKey(MINIMUM_VOLUME_PERCENT_KEY)) {
            restrictions.getInt(MINIMUM_VOLUME_PERCENT_KEY)
        } else {
            getMinimumVolumePercentUser().toInt()
        }
    }

    fun mdmVolumeRestrictionExist(): Boolean {
        val restrictions = restrictionsManager.applicationRestrictions
        val modelSpecificKey = MINIMUM_VOLUME_PERCENT_KEY_MODEL_SPECIFIC
        val modelSpecificKeyPresent = restrictions.containsKey(modelSpecificKey)
        val generalKeyPresent = restrictions.containsKey(MINIMUM_VOLUME_PERCENT_KEY)
        return modelSpecificKeyPresent || generalKeyPresent
    }

    fun appVolumeRestrictionExist(): Boolean {
        val userKeyPresent = hasMinimumVolumePercentUser()
        return mdmVolumeRestrictionExist() || userKeyPresent
    }

    fun showNoConfigurationMessage(): Boolean {
        return !getAppRestrictionNoFileInfoShown() && !appVolumeRestrictionExist()
    }
}