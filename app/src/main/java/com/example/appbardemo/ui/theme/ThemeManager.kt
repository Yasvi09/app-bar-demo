package com.example.appbardemo.ui.theme

import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import com.example.appbardemo.R
import com.google.android.material.imageview.ShapeableImageView

/**
 * Manages the theme color across the application
 */
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

    /**
     * Gets the current theme color
     */
    fun getThemeColor(): Int {
        val colorString = sharedPreferences.getString(KEY_THEME_COLOR, DEFAULT_THEME_COLOR) ?: DEFAULT_THEME_COLOR
        return Color.parseColor(colorString)
    }

    /**
     * Returns the current theme color as a hex string
     */
    fun getThemeColorString(): String {
        return sharedPreferences.getString(KEY_THEME_COLOR, DEFAULT_THEME_COLOR) ?: DEFAULT_THEME_COLOR
    }

    /**
     * Sets a new theme color
     */
    fun setThemeColor(colorString: String) {
        sharedPreferences.edit().putString(KEY_THEME_COLOR, colorString).apply()
    }

    /**
     * Applies the theme color to a SwitchCompat
     */
    fun applyThemeToSwitch(switch: SwitchCompat) {
        val themeColor = getThemeColor()
        switch.thumbTintList = ColorStateList.valueOf(Color.WHITE)
        switch.trackTintList = ColorStateList.valueOf(themeColor)
    }

    /**
     * Applies the theme to the diamond border
     */
    fun applyThemeToDiamondBorders(view: View) {
        // Implementation would depend on how diamond borders are created
        // This is a placeholder for the actual implementation
    }

    /**
     * Applies theme to an ImageView (for tinting icons)
     */
    fun applyThemeToImageView(imageView: ImageView) {
        imageView.imageTintList = ColorStateList.valueOf(getThemeColor())
    }

    /**
     * Applies theme to a TextView
     */
    fun applyThemeToTextView(textView: TextView) {
        textView.setTextColor(getThemeColor())
    }

    /**
     * Updates all color references in styles and drawables
     * Note: This is called when the app starts or when theme changes
     */
    fun updateAppTheme() {
        // This would normally involve runtime resource overrides
        // or other methods to globally change the theme
        // This is a simple implementation for demonstration
    }

    /**
     * Predefined theme colors
     */
    object ThemeColors {
        val COLORS = listOf(
            "#FF0000", // Red
            "#FF5722", // Deep Orange
            "#FFA500", // Orange
            "#FFEB3B", // Yellow
            "#CDDC39", // Lime

            "#8BC34A", // Light Green
            "#4CAF50", // Green
            "#CBFB0B", // Lime Green (Default)
            "#009688", // Teal
            "#0000FF", // Blue

            "#9C27B0", // Purple
            "#00BCD4", // Cyan
            "#FF9800", // Orange
            "#E91E63", // Pink
            "#2196F3", // Light Blue

            "#FF5722", // Deep Orange
            "#795548", // Brown
            "#FFEB3B", // Yellow
            "#E91E63", // Pink
            "#3F51B5", // Indigo

            "#00BCD4", // Cyan
            "#000080", // Navy
            "#9C27B0", // Purple
            "#009688", // Teal
            "#FF0000", // Red

            "#673AB7", // Deep Purple
            "#FF4081", // Pink Accent
            "#FFC107", // Amber
            "#CDDC39", // Lime
            "#9C27B0", // Purple

            "#795548", // Brown
            "#FF9800", // Orange
            "#FFCCBC", // Light Orange
            "#03A9F4", // Light Blue
            "#E040FB", // Purple Accent

            "#2196F3", // Blue
            "#8BC34A", // Light Green
            "#5D4037", // Brown
            "#7B1FA2", // Purple
            "#BDBDBD"  // Gray
        )
    }
}