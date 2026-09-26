package com.example.everydayquest

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class QuestProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val backgroundPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 25f
        }

    private val progressPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 25f
        }

    private val textPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 48f
            textAlign = Paint.Align.CENTER
            color = android.graphics.Color.rgb(0, 180, 255)
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }

    private var progress = 0f

    fun setProgress(value: Float) {
        progress = value.coerceIn(0f, 1f)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val size = min(width, height).toFloat()
        val padding = 30f

        val rect = RectF(
            padding,
            padding,
            size - padding,
            size - padding
        )

        canvas.drawArc(
            rect,
            0f,
            360f,
            false,
            backgroundPaint
        )

        canvas.drawArc(
            rect,
            -90f,
            360f * progress,
            false,
            progressPaint
        )

        val percentage = (progress * 100).toInt()

        val centerX = width / 2f

        val centerY =
            height / 2f -
                    (textPaint.ascent() +
                            textPaint.descent()) / 2f

        canvas.drawText(
            "$percentage%",
            centerX,
            centerY,
            textPaint
        )
    }
}