package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.SettingsRepository
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import com.example.playlistmaker.sharing.domain.SharingInteractor
import com.example.playlistmaker.util.PreferenceKeys
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val settingsModule = module {
    factory<SettingsRepository> { (context: Context) ->
        val sharedPrefs = context.getSharedPreferences(PreferenceKeys.PREFS_NAME, Context.MODE_PRIVATE)
        SettingsRepositoryImpl(sharedPrefs)
    }

    viewModel { (context: Context) ->
        val repo = get<SettingsRepository> { parametersOf(context) }
        val share = get<SharingInteractor> { parametersOf(context) }
        SettingsViewModel(repo, share)
    }
}