package com.halilozcan.halopinlib

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import com.halilozcan.halopinview.R


// Code with ❤
//┌─────────────────────────────┐
//│ Created by Halil ÖZCAN      │
//│ ─────────────────────────── │
//│ mail@halilozcan.com         │
//│ ─────────────────────────── │
//│ 12/7/20                     │
//└─────────────────────────────┘

class HaloPinView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttrs: Int = 0
) : View(context, attributeSet, defStyleAttrs) {

    private val pinItems = mutableListOf<PinItem>()

    private val activePinPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val inActivePinPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var lastPinIndex = -1

    private var pinCounts = 4

    private var inActivePinRadius = 10f
    private var activePinRadius = 20f

    private var inActivePinColor = Color.DKGRAY
    private var activePinColor = Color.WHITE

    private var viewRect = RectF()

    private var onPinFilledListener: ((String) -> Unit)? = null

    init {

        context.withStyledAttributes(attributeSet, R.styleable.HaloPinView) {
            pinCounts = getInt(R.styleable.HaloPinView_pinCount, 4)
            inActivePinRadius =
                getDimension(R.styleable.HaloPinView_inActiveRadius, inActivePinRadius)
            activePinRadius = getDimension(R.styleable.HaloPinView_activeRadius, activePinRadius)
            inActivePinColor = getColor(R.styleable.HaloPinView_inActivePinColor, inActivePinColor)
            activePinColor = getColor(R.styleable.HaloPinView_activePinColor, activePinColor)
        }
        activePinPaint.color = activePinColor
        activePinPaint.style = Paint.Style.FILL_AND_STROKE

        inActivePinPaint.color = inActivePinColor
        inActivePinPaint.style = Paint.Style.FILL_AND_STROKE
        initPinItems()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        viewRect.set(0f, 0f, w.toFloat(), h.toFloat())
        calculatePoints()
    }

    private fun calculatePoints() {

        if (inActivePinRadius >= activePinRadius) {
            activePinRadius = inActivePinRadius
            inActivePinRadius = activePinRadius / 2f
        }

        if (activePinRadius > viewRect.height() / 2f) {
            activePinRadius = viewRect.height() / 2f
            inActivePinRadius = activePinRadius / 2
        }

        if (viewRect.width() <= (activePinRadius * (pinCounts + 1)) + (pinCounts * 2 * activePinRadius)) {
            activePinRadius = viewRect.width() / ((2 * pinCounts) + (pinCounts + 1))
            inActivePinRadius = activePinRadius / 2f
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        pinItems.forEachIndexed { index, pinItem ->
            if (pinItem.isEntered) {
                canvas?.drawCircle(
                    ((3 * (index)) + 2) * activePinRadius,
                    viewRect.height() / 2f,
                    activePinRadius,
                    activePinPaint
                )
            } else {
                canvas?.drawCircle(
                    ((3 * (index)) + 2) * activePinRadius,
                    viewRect.height() / 2f,
                    inActivePinRadius,
                    inActivePinPaint
                )
            }
        }
    }

    fun setOnPinFilledListener(onPinFilledListener: ((String) -> Unit)?) {
        this.onPinFilledListener = onPinFilledListener
    }

    fun enterPin(number: Int) {
        if (lastPinIndex != pinItems.lastIndex) {
            pinItems[++lastPinIndex] = PinItem(number, true)
            invalidate()

            if (lastPinIndex == pinItems.lastIndex) {
                onPinFilledListener?.invoke(getEnteredPin())
            }
        }
    }

    fun getItemCount() = pinCounts

    fun deletePin() {
        if (lastPinIndex != -1) {
            pinItems[lastPinIndex--] = PinItem(0, false)
            invalidate()
        }
    }

    private fun getEnteredPin(): String {
        val pinBuilder = StringBuilder()
        pinItems.forEach {
            pinBuilder.append(it.number)
        }
        return pinBuilder.toString()
    }

    private fun initPinItems() {
        lastPinIndex = -1
        pinItems.clear()
        repeat(pinCounts) {
            pinItems.add(PinItem())
        }

        invalidate()
    }

    fun deleteEnteredPins() {
        initPinItems()
    }
}