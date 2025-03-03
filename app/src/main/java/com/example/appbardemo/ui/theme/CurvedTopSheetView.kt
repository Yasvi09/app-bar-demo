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

    private var sheetColor: Int = Color.WHITE
    private var cornerRadiusValue: Float = 20f
    private var buttonSizeValue: Float = 70f
    private var notchCornerRadiusValue: Float = 10f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()

        val notchWidth = buttonSizeValue * sqrt(2f)
        val notchDepth = notchWidth / 2

        val centerX = width / 2f

        path.reset()

        path.moveTo(0f, cornerRadiusValue)

        path.lineTo(0f, height)

        path.lineTo(width, height)

        path.lineTo(width, cornerRadiusValue)

        path.quadTo(width, 0f, width - cornerRadiusValue, 0f)

        path.lineTo(centerX + notchWidth/2, 0f)

        path.quadTo(centerX + notchWidth/2, 0f, centerX + notchWidth/2 - notchCornerRadiusValue, notchCornerRadiusValue)

        path.lineTo(centerX + notchCornerRadiusValue, notchDepth - notchCornerRadiusValue)

        path.quadTo(centerX, notchDepth, centerX - notchCornerRadiusValue, notchDepth - notchCornerRadiusValue)

        path.lineTo(centerX - notchWidth/2 + notchCornerRadiusValue, notchCornerRadiusValue)

        path.quadTo(centerX - notchWidth/2, 0f, centerX - notchWidth/2, 0f)

        path.lineTo(cornerRadiusValue, 0f)

        path.quadTo(0f, 0f, 0f, cornerRadiusValue)

        path.close()

        paint.color = sheetColor
        canvas.drawPath(path, paint)
    }

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

    override fun setBackgroundColor(color: Int) {
        setSheetColor(color)
    }
}