package com.example.appbardemo.ui.theme

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import kotlin.math.sqrt

class CurvedTopSheetView @JvmOverloads constructor(
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

    // Customizable properties
    private var sheetColor: Int = Color.WHITE
    private var cornerRadiusValue: Float = 20f
    private var buttonSizeValue: Float = 70f  // Size of your down button
    private var notchCornerRadiusValue: Float = 10f // Radius for the notch corners

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()

        // Calculate the size of the notch (for the down button)
        val notchWidth = buttonSizeValue * sqrt(2f)
        val notchDepth = notchWidth / 2

        // Calculate center position
        val centerX = width / 2f

        // Reset path
        path.reset()

        // Start path at top-left of the sheet (after the corner radius)
        path.moveTo(0f, cornerRadiusValue)

        // Draw left edge up to the corner radius point
        path.lineTo(0f, height)

        // Draw bottom edge
        path.lineTo(width, height)

        // Draw right edge up to the top-right corner
        path.lineTo(width, cornerRadiusValue)

        // Draw top-right corner with proper radius
        path.quadTo(width, 0f, width - cornerRadiusValue, 0f)

        // Draw top edge until notch starts
        path.lineTo(centerX + notchWidth/2, 0f)

        // Right corner of notch with rounded corner
        path.quadTo(centerX + notchWidth/2, 0f, centerX + notchWidth/2 - notchCornerRadiusValue, notchCornerRadiusValue)

        // Line to near bottom point of notch
        path.lineTo(centerX + notchCornerRadiusValue, notchDepth - notchCornerRadiusValue)

        // Bottom point of notch with rounded corner
        path.quadTo(centerX, notchDepth, centerX - notchCornerRadiusValue, notchDepth - notchCornerRadiusValue)

        // Line to near left corner of notch
        path.lineTo(centerX - notchWidth/2 + notchCornerRadiusValue, notchCornerRadiusValue)

        // Left corner of notch with rounded corner
        path.quadTo(centerX - notchWidth/2, 0f, centerX - notchWidth/2, 0f)

        // Draw remaining top edge
        path.lineTo(cornerRadiusValue, 0f)

        // Draw top-left corner with proper radius
        path.quadTo(0f, 0f, 0f, cornerRadiusValue)

        // Close the path
        path.close()

        // Draw the final shape
        paint.color = sheetColor
        canvas.drawPath(path, paint)
    }

    // Public setter methods
    fun setSheetColor(color: Int) {
        sheetColor = color
        invalidate()
    }

    fun setCornerRadius(radius: Float) {
        cornerRadiusValue = radius
        invalidate()
    }

    fun setButtonSize(size: Float) {
        buttonSizeValue = size
        invalidate()
    }

    fun setNotchCornerRadius(radius: Float) {
        notchCornerRadiusValue = radius
        invalidate()
    }

    // Override View's setBackgroundColor to work with our custom implementation
    override fun setBackgroundColor(color: Int) {
        setSheetColor(color)
    }
}