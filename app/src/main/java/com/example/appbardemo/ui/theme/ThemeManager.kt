package com.example.appbardemo.ui.theme

import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import com.example.appbardemo.R
import com.google.android.material.imageview.ShapeableImageView

class ThemeManager private constructor(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val appContext = context.applicationContext

    companion object {
        private const val PREFS_NAME = "theme_prefs"
        private const val KEY_THEME_COLOR = "theme_color"
        private const val DEFAULT_THEME_COLOR = "#CBFB0B" // Default lime green color

        @Volatile
        private var instance: ThemeManager? = null

        fun getInstance(context: Context): ThemeManager {
            return instance ?: synchronized(this) {
                instance ?: ThemeManager(context).also { instance = it }
            }
        }
    }
    fun applyThemeToFabGradient(view: View) {
        val themeColor = getThemeColor()

        // Get the background drawable
        val background = view.background

        if (background is GradientDrawable) {
            // Calculate a complementary or darker color for the end of the gradient
            val endColor = calculateGradientEndColor(themeColor)

            // Set new gradient colors without changing other properties of the drawable
            background.colors = intArrayOf(themeColor, endColor)

            // Invalidate the view to redraw with new colors
            view.invalidate()
        }
    }

    fun applyThemeToDiamondBorder(view: View) {
        val themeColor = getThemeColor()

        // Get the background drawable from the view
        val background = view.background

        if (background is LayerDrawable) {
            // The diamond_border.xml is a LayerDrawable with 2 layers
            // The first layer (index 0) contains the gradient
            val gradientLayer = background.getDrawable(0)

            if (gradientLayer is GradientDrawable) {
                // Calculate end color for the gradient
                val endColor = calculateGradientEndColor(themeColor)

                // Update the gradient colors
                gradientLayer.colors = intArrayOf(themeColor, endColor)

                // Invalidate the view to redraw
                view.invalidate()
            }
        }
    }

    // Helper method to calculate an appropriate end color for the gradient
    private fun calculateGradientEndColor(startColor: Int): Int {
        // If the start color is #CAF90B (lime green), return #03BE7B (teal green)
        if (startColor == Color.parseColor("#CAF90B")) {
            return Color.parseColor("#03BE7B")
        }

        // For other colors, calculate a complementary color
        val hsv = FloatArray(3)
        Color.colorToHSV(startColor, hsv)

        // Shift the hue by 60 degrees and reduce saturation slightly
        hsv[0] = (hsv[0] + 60) % 360
        hsv[1] = Math.max(hsv[1] - 0.1f, 0f) // Reduce saturation slightly
        hsv[2] = Math.min(hsv[2] * 0.9f, 1f) // Darken slightly

        return Color.HSVToColor(hsv)
    }

    fun getThemeColor(): Int {
        val colorString = sharedPreferences.getString(KEY_THEME_COLOR, DEFAULT_THEME_COLOR) ?: DEFAULT_THEME_COLOR
        return Color.parseColor(colorString)
    }

    fun getThemeColorString(): String {
        return sharedPreferences.getString(KEY_THEME_COLOR, DEFAULT_THEME_COLOR) ?: DEFAULT_THEME_COLOR
    }


    fun setThemeColor(colorString: String) {
        sharedPreferences.edit().putString(KEY_THEME_COLOR, colorString).apply()
    }

    fun applyThemeToSwitch(switch: SwitchCompat) {
        val themeColor = getThemeColor()
        switch.thumbTintList = ColorStateList.valueOf(Color.WHITE)
        switch.trackTintList = ColorStateList.valueOf(themeColor)
    }

    fun applyThemeToDiamondBorders(view: View) {
        // Implementation would depend on how diamond borders are created
        // This is a placeholder for the actual implementation
    }

    fun applyThemeToImageView(imageView: ImageView) {
        imageView.imageTintList = ColorStateList.valueOf(getThemeColor())
    }

    fun applyThemeToTextView(textView: TextView) {
        textView.setTextColor(getThemeColor())
    }

    fun updateAppTheme() {
        // This would normally involve runtime resource overrides
        // or other methods to globally change the theme
        // This is a simple implementation for demonstration
    }

    object ThemeColors {
        val COLORS = listOf(
            "#FF0000", // Red
            "#FB450B", // Deep Orange
            "#FBC60B", // Dark yellow
            "#FFF730", // lemon Yellow
            "#56F71E", // neon

            "#8BC34A", // Light Green
            "#00C814", // Green
            "#CBFB0B", // Lime Green (Default)
            "#00909E", // Teal
            "#00072D", // Neavy blue

            "#FA26A0", // Pink
            "#05DFD7", // Cyan
            "#05DFD7", // Orange
            "#FF4301", // Pink
            "#41EAD4", // Light cyan

            "#FA7D09", // light Orange
            "#34495e", // Dark gray
            "#BC3908", // Dark orange
            "#E91E63", // Pink
            "#d2b4de", // Light pink

            "#900C3F", // Beat
            "#FF9595", // Peach
            "#3490DE", // light blue
            "#B0DFFF", // sky blue
            "#2D00F7", // Rediam blue

            "#76EAD7", // Deep cyan
            "#111D5E", // blue Accent
            "#6A197D", // dark purple
            "#32E0C4", // cyan
            "#EF233C", // light red

            "#8338EC", // pruple
            "#FF0054", // pink accent
            "#FFD23F", // Amber
            "#BC00DD", // purple accent
            "#A5BE00", // Lime Accent

            "#CA5252", // cool pink
            "#C87700", // Light brown
            "#FFC773", // skin
            "#00D1FF", // sky blue
            "#eb984e", // light orange

            "#00A3FF", // cloud blue
            "#76FF73", // Light Green
            "#5F3900", // Brown
            "#761C1C", // blood red
            "#BFBFBF"  // Gray
        )
    }
}