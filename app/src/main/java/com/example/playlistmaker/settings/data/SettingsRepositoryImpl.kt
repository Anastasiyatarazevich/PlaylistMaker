package com.example.playlistmaker.settings.data

import android.content.SharedPreferences
import com.example.playlistmaker.util.PreferenceKeys
import com.example.playlistmaker.settings.domain.SettingsRepository
import com.example.playlistmaker.settings.domain.model.ThemeSettings

class SettingsRepositoryImpl(private val sharedPrefs: SharedPreferences) : SettingsRepository {
    override fun getThemeSettings(): ThemeSettings {
        val darkThemeEnabled = sharedPrefs.getBoolean(PreferenceKeys.DARK_THEME, false)
        return ThemeSettings(isDarkTheme = darkThemeEnabled)
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        sharedPrefs.edit().putBoolean(PreferenceKeys.DARK_THEME, settings.isDarkTheme).apply()
    }
}