package com.example.appbardemo.ui.theme

import android.os.Bundle
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.example.appbardemo.R

class SettingsActivity : AppCompatActivity() {

    private lateinit var themeManager: ThemeManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        themeManager = ThemeManager.getInstance(this)

        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
        }

        applyThemeToUI()

        setupSettingsListeners()
    }

    private fun applyThemeToUI() {
        val themeColor = themeManager.getThemeColor()

        val switches = listOf(
            findViewById<SwitchCompat>(R.id.colorThemeSwitch),
            findViewById<SwitchCompat>(R.id.externalCodecSwitch),
            findViewById<SwitchCompat>(R.id.brightnessSwitch),
            findViewById<SwitchCompat>(R.id.subtitleSwitch),
            findViewById<SwitchCompat>(R.id.autoplaySwitch),
            findViewById<SwitchCompat>(R.id.resumeSwitch),
            findViewById<SwitchCompat>(R.id.doubleTapSwitch)
        )

        for (switch in switches) {
            themeManager.applyThemeToSwitch(switch)
        }


        val allTextViews = findAllTextViewsInLayout(findViewById(android.R.id.content))
        for (textView in allTextViews) {
            if (textView.currentTextColor == resources.getColor(R.color.green_accent, theme)) {
                themeManager.applyThemeToTextView(textView)
            }
        }
    }

    private fun findAllTextViewsInLayout(view: android.view.View): List<TextView> {
        val textViews = mutableListOf<TextView>()

        if (view is TextView) {
            textViews.add(view)
        }

        if (view is android.view.ViewGroup) {
            for (i in 0 until view.childCount) {
                textViews.addAll(findAllTextViewsInLayout(view.getChildAt(i)))
            }
        }

        return textViews
    }

    private fun setupSettingsListeners() {
        findViewById<TextView>(R.id.getVideoPlayerPremium).setOnClickListener {
            // Handle premium purchase
        }

        findViewById<TextView>(R.id.restorePurchase).setOnClickListener {
            // Handle restore purchase
        }

        findViewById<RelativeLayout>(R.id.colorThemeLayout).setOnClickListener {
            // Open color picker activity
            ColorPickerActivity.start(this)
        }

        findViewById<SwitchCompat>(R.id.colorThemeSwitch).setOnCheckedChangeListener { _, isChecked ->
            // Toggle color theme
        }

        findViewById<RelativeLayout>(R.id.languageLayout).setOnClickListener {
            // Open language selector
        }
    }
}