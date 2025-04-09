package com.example.playlistmaker

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.PreferenceKeys.PREFS_NAME
import com.example.playlistmaker.PreferenceKeys.PREF_THEME_KEY

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        val sharedPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        if (!sharedPrefs.contains(PREF_THEME_KEY)) {
            val systemDarkMode = isSystemInDarkMode()
            sharedPrefs.edit().putBoolean(PREF_THEME_KEY, systemDarkMode).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (systemDarkMode) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        } else {
            val darkThemeEnabled = sharedPrefs.getBoolean(PREF_THEME_KEY, false)
            AppCompatDelegate.setDefaultNightMode(
                if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }

    private fun isSystemInDarkMode(): Boolean {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }
}

