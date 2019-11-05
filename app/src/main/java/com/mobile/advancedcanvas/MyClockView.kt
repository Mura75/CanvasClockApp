package com.mobile.advancedcanvas

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import java.util.*

class MyClockView : View {

    enum class TimeType(val value: Int) {
        HOUR(1),
        MINUTE(2),
        SECOND(3)
    }

    private lateinit var timeType: TimeType

    private lateinit var paint: Paint
    private var radius = 0f
    private var fontSize = 0f

    private var numbers = intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)

    private var rect = Rect()

    private var padding = 40

    private var arrowTruncation = 0f
    private var hourArrowTruncation = 0f

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        init()

        canvas.drawColor(Color.WHITE)
        drawCircle(canvas)
        drawNumber(canvas)
        drawArrows(canvas)

        postInvalidateDelayed(500)
        invalidate()
    }

    private fun init() {
        fontSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            19f,
            resources.displayMetrics
        )
        val min = Math.min(height, width).toFloat()
        radius = min / 2 - 50
        paint = Paint()
        arrowTruncation = min / 20
        hourArrowTruncation = min / 7
    }

    private fun drawCircle(canvas: Canvas) {
        paint.reset()
        paint.setColor(Color.BLACK)
        paint.strokeWidth = 5f
        paint.style = Paint.Style.STROKE
        canvas.drawCircle(
            (width / 2).toFloat(),
            (height / 2).toFloat(),
            radius + padding - 10,
            paint
        )
    }

    private fun drawNumber(canvas: Canvas) {
        paint.textSize = fontSize
        numbers.forEach { number ->
            paint.getTextBounds(number.toString(), 0, number.toString().length, rect)
            val angle = Math.PI / 6 * (number - 3)
            val x = (width / 2 + Math.cos(angle) * radius - rect.width() / 2).toFloat()
            val y = (height / 2 + Math.sin(angle) * radius + rect.height() / 2).toFloat()
            canvas.drawText(number.toString(), x, y, paint)
        }
    }

    private fun drawArrows(canvas: Canvas) {
        val calendar = Calendar.getInstance()
        var hour = calendar.get(Calendar.HOUR_OF_DAY)
        hour = if (hour > 12) {
            hour - 12
        } else {
            hour
        }
        drawArrow(canvas = canvas,
            location = (hour + calendar.get(Calendar.MINUTE) / 60) * 5f,
            timeType = TimeType.HOUR
        )
        drawArrow(canvas = canvas,
            location = calendar.get(Calendar.MINUTE).toFloat(),
            timeType = TimeType.MINUTE
        )
        drawArrow(canvas = canvas,
            location = calendar.get(Calendar.SECOND).toFloat(),
            timeType = TimeType.SECOND
        )
    }

    private fun drawArrow(canvas: Canvas, location: Float, timeType: TimeType) {
        val angle = Math.PI * location / 30 - Math.PI / 2
        val arrowRadius = when(timeType.value) {
            TimeType.HOUR.value -> { radius - hourArrowTruncation - arrowTruncation }
            TimeType.MINUTE.value -> { radius - arrowTruncation * 2.5f }
            else -> { radius - arrowTruncation }
        }
        canvas.drawLine(
            (width / 2).toFloat(),
            (height / 2).toFloat(),
            (width / 2 + Math.cos(angle) * arrowRadius).toFloat(),
            (height / 2 + Math.sin(angle) * arrowRadius).toFloat(),
            paint
        )
    }
}