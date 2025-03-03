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

    private var navColor: Int = Color.WHITE
    private var cornerRadiusValue: Float = 20f
    private var fabSizeValue: Float = 70f
    private var notchCornerRadiusValue: Float = 10f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()

        val notchWidth = fabSizeValue * sqrt(2f)
        val notchDepth = notchWidth / 2 + 10

        val centerX = width / 2f

        path.reset()

        path.moveTo(0f, cornerRadiusValue)

        path.quadTo(0f, 0f, cornerRadiusValue, 0f)

        path.lineTo(centerX - notchWidth/2, 0f)

        val leftNotchX = centerX - notchWidth/2
        val rightNotchX = centerX + notchWidth/2
        val notchBottomY = notchDepth

        path.lineTo(leftNotchX , 0f)
        path.quadTo(leftNotchX, notchCornerRadiusValue/2, leftNotchX + notchCornerRadiusValue/2, notchCornerRadiusValue)

        path.lineTo(centerX - notchCornerRadiusValue, notchBottomY - notchCornerRadiusValue)
        path.quadTo(centerX, notchBottomY, centerX + notchCornerRadiusValue, notchBottomY - notchCornerRadiusValue)

        path.lineTo(rightNotchX - notchCornerRadiusValue/2, notchCornerRadiusValue)
        path.quadTo(rightNotchX, notchCornerRadiusValue/2, rightNotchX, 0f)

        path.lineTo(width - cornerRadiusValue, 0f)
        path.quadTo(width, 0f, width, cornerRadiusValue)

        path.lineTo(width, height)
        path.lineTo(0f, height)

        path.lineTo(0f, cornerRadiusValue)
        path.close()

        paint.color = navColor
        canvas.drawPath(path, paint)
    }

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

    override fun setBackgroundColor(color: Int) {
        setNavColor(color)
    }
}