package com.mobile.advancedcanvas

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import java.util.*
import kotlin.collections.HashMap

class MyClockView : View {

    enum class TimeType(val value: Int) {
        HOUR(1),
        MINUTE(2),
        SECOND(3)
    }

    private val paint: Paint = Paint()
    private var radius = 0f
    private var fontSize = 0f

    private var numbers = intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)

    private var numbersPositionsX = FloatArray(numbers.size)
    private var numbersPositionsY = FloatArray(numbers.size)

    private var rect = Rect()

    private var padding = 40

    private var arrowTruncation = 0f
    private var hourArrowTruncation = 0f

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    init {
        fontSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            19f,
            resources.displayMetrics
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        paint.setColor(Color.BLACK)
        paint.strokeWidth = 5f
        paint.style = Paint.Style.STROKE

        val min = Math.min(height, width).toFloat()
        radius = min / 2 - 50
        arrowTruncation = min / 20
        hourArrowTruncation = min / 7

        paint.textSize = fontSize
        numbers.forEachIndexed { index, number ->
            val angle = Math.PI / 6 * (number - 3)
            val x = (width / 2 + Math.cos(angle) * radius - rect.width() / 2).toFloat() - 15
            val y = (height / 2 + Math.sin(angle) * radius + rect.height() / 2).toFloat() + 15
            numbersPositionsX[index] = x
            numbersPositionsY[index] = y
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawCircle(canvas)
        drawNumber(canvas)
        drawArrows(canvas)
        postInvalidateDelayed(500)
    }

    private fun drawCircle(canvas: Canvas) {
        canvas.drawCircle(
            (width / 2).toFloat(),
            (height / 2).toFloat(),
            radius + padding - 0,
            paint
        )
    }

    private fun drawNumber(canvas: Canvas) {
        numbers.forEachIndexed { index, number ->
            paint.getTextBounds(number.toString(), 0, number.toString().length, rect)
            val x = numbersPositionsX[index]
            val y = numbersPositionsY[index]
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