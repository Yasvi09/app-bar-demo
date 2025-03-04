package com.example.appbardemo.ui.theme

import android.content.Context
import android.media.audiofx.Equalizer
import android.os.Bundle
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.appbardemo.R

class EqualizerActivity : AppCompatActivity() {

    private lateinit var seekBars: List<VerticalSeekBar>
    private lateinit var presetViews: List<TextView>

    // Audio session ID from MediaPlayer would be passed in a real app
    private var audioSessionId: Int = 0
    private var equalizer: Equalizer? = null

    private val presets = listOf("Rock", "Electronic", "Jazz", "Lounge", "Classic")

    // Preset EQ band levels (in dB, will be converted to equalizer band levels)
    private val presetLevels = mapOf(
        "Rock" to floatArrayOf(3f, 0f, 0f, 2f, 3f),
        "Electronic" to floatArrayOf(-2f, 0f, 2f, -1f, 3f),
        "Jazz" to floatArrayOf(3f, 2f, -2f, 0f, 2f),
        "Lounge" to floatArrayOf(2f, 0f, 0f, -1f, 0f),
        "Classic" to floatArrayOf(3f, 3f, 0f, 2f, -2f)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_equalizer)

        // Get audio session ID from intent
        audioSessionId = intent.getIntExtra("audio_session_id", 0)

        initializeUI()
        setupClickListeners()

        // Try to initialize the equalizer if we have a valid audio session
        if (audioSessionId != 0) {
            try {
                initializeEqualizer()
            } catch (e: Exception) {
                // Handle exception - could not initialize equalizer
                e.printStackTrace()
            }
        }

        // Set initial preset
        applyPreset("Electronic")
    }

    private fun initializeUI() {
        // Get all seekbars
        seekBars = listOf(
            findViewById(R.id.seekBar1),
            findViewById(R.id.seekBar2),
            findViewById(R.id.seekBar3),
            findViewById(R.id.seekBar4),
            findViewById(R.id.seekBar5)
        )

        // Get all preset views
        presetViews = listOf(
            findViewById(R.id.presetRock),
            findViewById(R.id.presetElectronic),
            findViewById(R.id.presetJazz),
            findViewById(R.id.presetLounge),
            findViewById(R.id.presetClassic)
        )

        // Set initial progress for all seekbars (mid-point)
        seekBars.forEach { seekBar ->
            seekBar.progress = 15

            // Set seek bar listener
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser && equalizer != null) {
                        val index = seekBars.indexOf(seekBar)
                        val bandValue = convertProgressToBandLevel(progress)
                        setEqualizerBand(index, bandValue)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }
    }

    private fun setupClickListeners() {
        // Back button
        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
        }

        // Menu button
        findViewById<ImageView>(R.id.menuButton).setOnClickListener {
            // Show options menu
        }

        // Preset buttons
        for (i in presetViews.indices) {
            presetViews[i].setOnClickListener {
                val presetName = presets[i]
                applyPreset(presetName)
            }
        }
    }

    private fun initializeEqualizer() {
        try {
            equalizer = Equalizer(0, audioSessionId)
            equalizer?.enabled = true

            // Check the number of bands available
            val numBands = equalizer?.numberOfBands?.toInt() ?: 0
            if (numBands > 0) {
                // We have 5 sliders, but the actual device might have a different number of bands
                // In a real app, you would dynamically create the UI based on the number of bands
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun applyPreset(presetName: String) {
        // Update UI to show selected preset
        for (i in presetViews.indices) {
            val view = presetViews[i]
            val selected = presets[i] == presetName

            view.setBackgroundResource(if (selected) R.drawable.selected_preset_background else 0)
            view.alpha = if (selected) 1f else 0.5f
        }

        // Apply the preset levels to the sliders and equalizer
        val levels = presetLevels[presetName] ?: return
        for (i in 0 until minOf(levels.size, seekBars.size)) {
            val progress = convertBandLevelToProgress(levels[i])
            seekBars[i].progress = progress

            // Also apply to actual equalizer if available
            if (equalizer != null) {
                setEqualizerBand(i, levels[i])
            }
        }
    }

    /**
     * Convert seekbar progress (0-30) to equalizer band level (-15 to 15 dB)
     */
    private fun convertProgressToBandLevel(progress: Int): Float {
        return (progress - 15).toFloat()
    }

    /**
     * Convert equalizer band level (-15 to 15 dB) to seekbar progress (0-30)
     */
    private fun convertBandLevelToProgress(bandLevel: Float): Int {
        return (bandLevel + 15).toInt().coerceIn(0, 30)
    }

    /**
     * Set the equalizer band to the specified level in dB
     */
    private fun setEqualizerBand(bandIndex: Int, levelInDb: Float) {
        if (equalizer == null || bandIndex < 0) return

        try {
            val numberOfBands = equalizer?.numberOfBands ?: 0
            if (bandIndex < numberOfBands) {
                // Convert dB level to millibel (required by the Android Equalizer)
                val millibel = (levelInDb * 100).toInt().toShort()

                // Get the band level range
                val bandRange = equalizer?.getBandLevelRange()
                val minLevel = bandRange?.get(0) ?: -1500
                val maxLevel = bandRange?.get(1) ?: 1500

                // Calculate the actual level within the device's supported range
                val range = maxLevel - minLevel
                val normalizedLevel = minLevel + ((levelInDb + 15) / 30f * range)

                equalizer?.setBandLevel(bandIndex.toShort(), normalizedLevel.toInt().toShort())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        equalizer?.release()
        equalizer = null
    }

    companion object {
        const val EXTRA_AUDIO_SESSION_ID = "audio_session_id"

        /**
         * Helper method to save the equalizer settings to SharedPreferences
         */
        fun saveEqualizerSettings(context: Context, presetName: String, bandLevels: FloatArray) {
            val sharedPrefs = context.getSharedPreferences("equalizer_settings", Context.MODE_PRIVATE)
            val editor = sharedPrefs.edit()

            // Save the current preset name
            editor.putString("current_preset", presetName)

            // Save each band level
            for (i in bandLevels.indices) {
                editor.putFloat("band_$i", bandLevels[i])
            }

            editor.apply()
        }

        /**
         * Helper method to load equalizer settings from SharedPreferences
         */
        fun loadEqualizerSettings(context: Context): Pair<String, FloatArray> {
            val sharedPrefs = context.getSharedPreferences("equalizer_settings", Context.MODE_PRIVATE)
            val presetName = sharedPrefs.getString("current_preset", "Electronic") ?: "Electronic"

            // Default to 5 bands
            val bandLevels = FloatArray(5) { 0f }

            // Load each band level
            for (i in bandLevels.indices) {
                bandLevels[i] = sharedPrefs.getFloat("band_$i", 0f)
            }

            return Pair(presetName, bandLevels)
        }
    }
}