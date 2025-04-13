package com.example.playlistmaker.settings.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import com.example.playlistmaker.util.Creator


class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val settingsRepository = Creator.provideSettingsRepository(this)
        val sharingInteractor = Creator.provideSharingInteractor(this)

        viewModel = ViewModelProvider(
            this,
            SettingsViewModel.Factory(settingsRepository, sharingInteractor)
        )
            .get(SettingsViewModel::class.java)

        viewModel.getThemeSettings().observe(this) { themeSettings ->
            if (binding.themeSwitcher.isChecked != themeSettings.isDarkTheme) {
                binding.themeSwitcher.isChecked = themeSettings.isDarkTheme
            }
        }

        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.switchTheme(isChecked)
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
            getDelegate().applyDayNight()
        }

        binding.shareApp.setOnClickListener {
            viewModel.shareApp()
        }

        binding.support.setOnClickListener {
            viewModel.openSupport()
        }

        binding.userAgreement.setOnClickListener {
            viewModel.openAgreement()
        }

        binding.backButton.setNavigationOnClickListener { finish() }
    }
}