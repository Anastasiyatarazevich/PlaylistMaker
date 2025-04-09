package com.example.playlistmaker.domain.impl

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.PreferenceKeys.DARK_THEME
import com.example.playlistmaker.PreferenceKeys.PREFS_NAME
import com.example.playlistmaker.domain.api.SettingsInteractor

class SettingsInteractorImpl(context: Context) : SettingsInteractor {
    private val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun isDarkThemeEnabled(): Boolean {
        return sharedPrefs.getBoolean(DARK_THEME, false)
    }

    override fun switchTheme(enabled: Boolean) {
        sharedPrefs.edit().putBoolean(DARK_THEME, enabled).apply()
        AppCompatDelegate.setDefaultNightMode(
            if (enabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}