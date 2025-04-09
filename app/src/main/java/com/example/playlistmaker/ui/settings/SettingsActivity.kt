package com.example.playlistmaker.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.presentation.Creator
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView


class SettingsActivity : AppCompatActivity() {

    private lateinit var settingsInteractor: SettingsInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        settingsInteractor = Creator.provideSettingsInteractor(this)

        val back = findViewById<MaterialToolbar>(R.id.back_button)
        val shareAppButton = findViewById<MaterialTextView>(R.id.share_app)
        val writeSupportButton = findViewById<MaterialTextView>(R.id.support)
        val agreementButton = findViewById<MaterialTextView>(R.id.user_agreement)
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.theme_switcher)

        back.setNavigationOnClickListener {
            finish()
        }

        themeSwitcher.isChecked = settingsInteractor.isDarkThemeEnabled()

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            settingsInteractor.switchTheme(checked)
        }

        shareAppButton.setOnClickListener {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text))
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_title)))
        }

        writeSupportButton.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:${getString(R.string.support_email)}")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.support_message))
            }
            if (emailIntent.resolveActivity(packageManager) != null) {
                startActivity(emailIntent)
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.share_text_fail),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        agreementButton.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.agreement_url)))
            startActivity(browserIntent)
        }
    }
}