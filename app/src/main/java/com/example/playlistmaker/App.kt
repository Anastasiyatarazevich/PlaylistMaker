package com.example.playlistmaker

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.di.appModule
import com.example.playlistmaker.di.mediaModule
import com.example.playlistmaker.di.playerModule
import com.example.playlistmaker.di.searchModule
import com.example.playlistmaker.di.settingsModule
import com.example.playlistmaker.di.sharingModule
import com.example.playlistmaker.util.PreferenceKeys.PREFS_NAME
import com.example.playlistmaker.util.PreferenceKeys.PREF_THEME_KEY
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(appModule, playerModule, searchModule, settingsModule, sharingModule, mediaModule)
        }

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

