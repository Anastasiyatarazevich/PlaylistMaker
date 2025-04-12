package com.example.playlistmaker.settings.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.SettingsRepository
import com.example.playlistmaker.settings.domain.model.ThemeSettings
import com.example.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private val themeSettings = MutableLiveData<ThemeSettings>()
    fun getThemeSettings(): LiveData<ThemeSettings> = themeSettings

    init {
        themeSettings.value = settingsRepository.getThemeSettings()
    }

    fun switchTheme(isDark: Boolean) {
        val newSettings = ThemeSettings(isDarkTheme = isDark)
        settingsRepository.updateThemeSetting(newSettings)
        themeSettings.value = newSettings
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun openSupport() {
        sharingInteractor.openSupport()
    }

    fun openAgreement() {
        sharingInteractor.openTerms()
    }

    class Factory(
        private val settingsRepository: SettingsRepository,
        private val sharingInteractor: SharingInteractor
    ) : androidx.lifecycle.ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(settingsRepository, sharingInteractor) as T
        }
    }
}