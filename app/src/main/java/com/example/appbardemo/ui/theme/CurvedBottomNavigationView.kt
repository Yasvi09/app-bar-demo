package com.example.appbardemo.ui.theme

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import kotlin.math.sqrt

class CurvedBottomNavigationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val path: Path = Path()
    private val paint: Paint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.WHITE
        isAntiAlias = true
    }

    // Customizable properties as private variables
    private var navColor: Int = Color.WHITE
    private var cornerRadiusValue: Float = 20f
    private var fabSizeValue: Float = 70f  // Size of your FAB (width/height in dp)
    private var notchCornerRadiusValue: Float = 10f // Radius for the notch corners

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()

        // Calculate the size of the diamond cutout (45° rotated square)
        val notchWidth = fabSizeValue * sqrt(2f)
        val notchDepth = notchWidth / 2

        // Calculate center position
        val centerX = width / 2f

        // Reset path
        path.reset()

        // Start from top-left after the corner radius
        path.moveTo(0f, cornerRadiusValue)

        // Draw top-left corner with proper radius
        path.quadTo(0f, 0f, cornerRadiusValue, 0f)

        // Draw top edge until notch starts
        path.lineTo(centerX - notchWidth/2, 0f)

        // Draw the notch with rounded corners
        // Calculate control points for the rounded corners of the notch
        val leftNotchX = centerX - notchWidth/2
        val rightNotchX = centerX + notchWidth/2
        val notchBottomY = notchDepth

        // First corner of notch (left side)
        // Instead of a sharp angle, create a curve
        path.lineTo(leftNotchX + notchCornerRadiusValue, 0f)
        path.quadTo(leftNotchX, 0f, leftNotchX + notchCornerRadiusValue, notchCornerRadiusValue)

        // Line to near bottom point
        path.lineTo(centerX - notchCornerRadiusValue, notchBottomY - notchCornerRadiusValue)

        // Bottom point of notch with rounded corner
        path.quadTo(centerX, notchBottomY, centerX + notchCornerRadiusValue, notchBottomY - notchCornerRadiusValue)

        // Line to near right corner
        path.lineTo(rightNotchX - notchCornerRadiusValue, notchCornerRadiusValue)

        // Right corner of notch with rounded corner
        path.quadTo(rightNotchX, 0f, rightNotchX - notchCornerRadiusValue, 0f)

        // Draw remaining top edge
        path.lineTo(width - cornerRadiusValue, 0f)

        // Draw top-right corner with proper radius
        path.quadTo(width, 0f, width, cornerRadiusValue)

        // Draw right edge
        path.lineTo(width, height)

        // Draw bottom edge
        path.lineTo(0f, height)

        // Draw left edge back to starting point
        path.lineTo(0f, cornerRadiusValue)

        // Close the path
        path.close()

        // Draw the final shape
        paint.color = navColor
        canvas.drawPath(path, paint)
    }

    // Public setter methods
    fun setNavColor(color: Int) {
        navColor = color
        invalidate()
    }

    fun setCornerRadius(radius: Float) {
        cornerRadiusValue = radius
        invalidate()
    }

    fun setFabSize(size: Float) {
        fabSizeValue = size
        invalidate()
    }

    fun setNotchCornerRadius(radius: Float) {
        notchCornerRadiusValue = radius
        invalidate()
    }

    // Override View's setBackgroundColor to work with our custom implementation
    override fun setBackgroundColor(color: Int) {
        setNavColor(color)
    }
}