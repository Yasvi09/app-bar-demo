package com.example.appbardemo.ui.theme

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appbardemo.R

class ColorPickerActivity : AppCompatActivity() {

    private lateinit var colorGrid: GridLayout
    private lateinit var themeManager: ThemeManager
    private var selectedColorPosition: Int = -1
    private var selectedColorString: String = ""
    private val colorViews = mutableListOf<View>()
    private val checkIcons = mutableListOf<ImageView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_picker)

        themeManager = ThemeManager.getInstance(this)
        colorGrid = findViewById(R.id.colorGrid)

        // Set click listener for the back button
        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
        }

        // Set click listener for the apply button
        findViewById<View>(R.id.applyButton).setOnClickListener {
            if (selectedColorString.isNotEmpty()) {
                applySelectedTheme()
            } else {
                Toast.makeText(this, "Please select a color", Toast.LENGTH_SHORT).show()
            }
        }

        setupColorGrid()

        // Inside ColorPickerActivity.kt, in onCreate method after initializing views
        val colorSelectText = findViewById<TextView>(R.id.colorSelectText)
        val applyButton = findViewById<Button>(R.id.applyButton)

// Apply current theme color to text elements
        val currentThemeColor = themeManager.getThemeColor()
        colorSelectText.setTextColor(currentThemeColor)
        applyButton.setTextColor(currentThemeColor)
    }

    private fun setupColorGrid() {
        val colors = ThemeManager.ThemeColors.COLORS
        val currentThemeColor = themeManager.getThemeColorString()

        for (i in colors.indices) {
            val colorView = LayoutInflater.from(this).inflate(R.layout.color_circle_item, colorGrid, false)
            val circle = colorView.findViewById<View>(R.id.colorCircle)
            val checkIcon = colorView.findViewById<ImageView>(R.id.checkIcon)

            // Set the color of the circle
            val colorInt = Color.parseColor(colors[i])
            val background = circle.background as GradientDrawable
            background.setColor(colorInt)

            // Check if this is the current theme color
            if (colors[i].equals(currentThemeColor, ignoreCase = true)) {
                checkIcon.visibility = View.VISIBLE
                selectedColorPosition = i
                selectedColorString = colors[i]
            }

            // Set click listener for the color
            colorView.setOnClickListener {
                // Update selection
                updateSelection(i, colors[i])
            }

            // Add to GridLayout
            val params = GridLayout.LayoutParams()
            params.width = GridLayout.LayoutParams.WRAP_CONTENT
            params.height = GridLayout.LayoutParams.WRAP_CONTENT
            colorGrid.addView(colorView, params)

            // Save references for later
            colorViews.add(circle)
            checkIcons.add(checkIcon)
        }
    }

    private fun updateSelection(position: Int, colorString: String) {
        // Clear previous selection
        if (selectedColorPosition != -1 && selectedColorPosition < checkIcons.size) {
            checkIcons[selectedColorPosition].visibility = View.GONE
        }

        // Set new selection
        selectedColorPosition = position
        selectedColorString = colorString
        checkIcons[position].visibility = View.VISIBLE
    }

    private fun applySelectedTheme() {
        // Save the selected color to preferences
        themeManager.setThemeColor(selectedColorString)

        // Show success message
        Toast.makeText(this, "Theme color updated", Toast.LENGTH_SHORT).show()

        // Restart the app to apply the new theme
        // In a real app, you might want to use a more elegant approach
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ColorPickerActivity::class.java)
            context.startActivity(intent)
        }
    }
}