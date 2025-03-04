package com.example.appbardemo.ui.theme

import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.core.content.ContextCompat
import com.example.appbardemo.R

class VerticalSeekBar : AppCompatSeekBar {

    private var mIsDragging = false
    private var mOnSeekBarChangeListener: OnSeekBarChangeListener? = null
    private val mLineWidth = 4f // Width of the seekbar line
    private val mLinePaint = Paint()
    private val mProgressPaint = Paint()

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    private fun init() {
        max = 30 // 15 to -15 range (with 0 in the middle)
        progress = 15 // Start at 0 (middle)

        mLinePaint.apply {
            isAntiAlias = true
            color = 0x80FFFFFF.toInt() // Semi-transparent white
            style = Paint.Style.STROKE
            strokeWidth = mLineWidth
        }

        mProgressPaint.apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            strokeWidth = mLineWidth
        }

        // Override the default OnSeekBarChangeListener to handle custom behavior
        super.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                mOnSeekBarChangeListener?.onProgressChanged(seekBar, progress, fromUser)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                mIsDragging = true
                mOnSeekBarChangeListener?.onStartTrackingTouch(seekBar)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                mIsDragging = false
                mOnSeekBarChangeListener?.onStopTrackingTouch(seekBar)
            }
        })
    }

    override fun setOnSeekBarChangeListener(l: OnSeekBarChangeListener?) {
        mOnSeekBarChangeListener = l
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Swap width and height for vertical orientation
        super.onMeasure(heightMeasureSpec, widthMeasureSpec)
        setMeasuredDimension(measuredHeight, measuredWidth)
    }

    override fun onDraw(canvas: Canvas) {
        // Rotate the canvas for vertical drawing
        canvas.rotate(-90f)
        canvas.translate(-height.toFloat(), 0f)

        val width = height.toFloat()
        val height = width / 10 // Thin bar

        // Background track
        canvas.drawLine(width / 2, 0f, width / 2, width.toFloat(), mLinePaint)

        // Calculate progress position
        val progressPosition = (progress / max.toFloat()) * width

        // Draw the progress with gradient
        val greenColor = ContextCompat.getColor(context, R.color.green_accent)
        val gradient = LinearGradient(
            0f, 0f, progressPosition, 0f,
            0x00CBFB0B, greenColor, Shader.TileMode.CLAMP
        )
        mProgressPaint.shader = gradient

        // Draw the progress bar
        val lineWidth = mLineWidth * 3 // Thicker than background
        canvas.drawRect(0f, width / 2 - lineWidth / 2, progressPosition, width / 2 + lineWidth / 2, mProgressPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) {
            return false
        }

        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> {
                // Convert the vertical touch position to progress
                val y = event.y
                val i = max - (max * y / height).toInt()
                if (i != progress) {
                    progress = i.coerceIn(0, max)
                    onSizeChanged(width, height, 0, 0)
                }

                if (event.action == MotionEvent.ACTION_DOWN) {
                    mOnSeekBarChangeListener?.onStartTrackingTouch(this)
                } else if (event.action == MotionEvent.ACTION_UP) {
                    mOnSeekBarChangeListener?.onStopTrackingTouch(this)
                }

                return true
            }
        }
        return super.onTouchEvent(event)
    }
}