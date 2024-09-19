package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val back = findViewById<ImageView>(R.id.back_button)
        val shareAppButton = findViewById<FrameLayout>(R.id.share_app)
        val writeSupportButton = findViewById<FrameLayout>(R.id.support)
        val agreementButton = findViewById<FrameLayout>(R.id.user_agreement)

        back.setOnClickListener {
            finish()
        }

        shareAppButton.setOnClickListener {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(
                    Intent.EXTRA_TEXT,
                    getString(R.string.share_text)
                )
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_title)))
        }

        writeSupportButton.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:${getString(R.string.support_email)}")
                putExtra(
                    Intent.EXTRA_EMAIL,
                    getString(R.string.support_email))
                putExtra(
                    Intent.EXTRA_SUBJECT,
                    getString(R.string.support_subject)
                )
                putExtra(
                    Intent.EXTRA_TEXT,
                    getString(R.string.support_message)
                )
            }
            if (emailIntent.resolveActivity(packageManager) != null) {
                startActivity(emailIntent)
            }else
                Toast.makeText(this, getString(R.string.share_text_fail), Toast.LENGTH_SHORT).show()
        }

        agreementButton.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.agreement_url)))
            startActivity(browserIntent)
        }
    }
}