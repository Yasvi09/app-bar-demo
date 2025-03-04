package com.example.appbardemo.ui.theme

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.appbardemo.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
        }

        setupSettingsListeners()
    }

    private fun setupSettingsListeners() {

        findViewById<android.widget.TextView>(R.id.getVideoPlayerPremium).setOnClickListener {

        }

        findViewById<android.widget.TextView>(R.id.restorePurchase).setOnClickListener {

        }


        findViewById<androidx.appcompat.widget.SwitchCompat>(R.id.colorThemeSwitch).setOnCheckedChangeListener { _, isChecked ->

        }

        findViewById<android.widget.RelativeLayout>(R.id.languageLayout).setOnClickListener {

        }

    }
}