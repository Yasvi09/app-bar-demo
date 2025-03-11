package com.example.appbardemo.ui.theme

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources.Theme
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.widget.ImageViewCompat

/**
 * Utility class that provides methods to apply theme colors to various UI components
 */
object ThemeUtils {

    /**
     * Applies the current theme color to a text view
     */
    fun applyThemeColorToText(context: Context, textView: TextView) {
        val themeColor = ThemeManager.getInstance(context).getThemeColor()
        textView.setTextColor(themeColor)
    }

    /**
     * Applies the current theme color to an image view (tinting)
     */
    fun applyThemeColorToImageView(context: Context, imageView: ImageView) {
        val themeColor = ThemeManager.getInstance(context).getThemeColor()
        ImageViewCompat.setImageTintList(imageView, ColorStateList.valueOf(themeColor))
    }

    /**
     * Applies the current theme color to a switch
     */
    fun applyThemeColorToSwitch(context: Context, switchView: SwitchCompat) {
        val themeColor = ThemeManager.getInstance(context).getThemeColor()

        // Set thumb and track colors
        switchView.thumbTintList = ColorStateList.valueOf(Color.WHITE)

        // Create a color state list for the track
        val states = arrayOf(
            intArrayOf(android.R.attr.state_checked),
            intArrayOf(-android.R.attr.state_checked)
        )

        val trackColors = intArrayOf(
            themeColor,  // Checked state
            Color.GRAY   // Unchecked state
        )

        val colorStateList = ColorStateList(states, trackColors)
        switchView.trackTintList = colorStateList
    }

    /**
     * Applies the current theme color to a circular view
     */
    fun applyThemeColorToCircle(context: Context, view: View) {
        val themeColor = ThemeManager.getInstance(context).getThemeColor()
        val drawable = view.background
        if (drawable is GradientDrawable) {
            drawable.setColor(themeColor)
        }
    }

    /**
     * Retrieves theme color from current attribute
     */
    fun getThemeColor(context: Context, attr: Int): Int {
        val typedValue = TypedValue()
        val theme: Theme = context.theme
        theme.resolveAttribute(attr, typedValue, true)
        return typedValue.data
    }

    /**
     * Lightens a color by the given factor
     */
    fun lightenColor(color: Int, factor: Float): Int {
        val r = (Color.red(color) * (1 - factor) + 255 * factor).toInt()
        val g = (Color.green(color) * (1 - factor) + 255 * factor).toInt()
        val b = (Color.blue(color) * (1 - factor) + 255 * factor).toInt()
        return Color.rgb(r, g, b)
    }

    /**
     * Darkens a color by the given factor
     */
    fun darkenColor(color: Int, factor: Float): Int {
        val r = (Color.red(color) * (1 - factor)).toInt()
        val g = (Color.green(color) * (1 - factor)).toInt()
        val b = (Color.blue(color) * (1 - factor)).toInt()
        return Color.rgb(r, g, b)
    }
}