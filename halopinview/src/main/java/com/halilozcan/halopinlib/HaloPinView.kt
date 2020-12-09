package com.halilozcan.halopinlib

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
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

    private var pinXLocations = Array(pinCounts) { 0f }

    private var animationDuration = 1000L

    private var inActivePinRadius = 10f
    private var activePinRadius = 20f

    private var inActivePinColor = Color.DKGRAY
    private var activePinColor = Color.WHITE

    private var onPinFilledListener: ((Int) -> Unit)? = null

    private var correctPin = Integer.MIN_VALUE

    private var isCorrectPinEntered = false
    private var isAnimationActive = false

    private var pinYLocation = 0f

    private val pinWiggleAnimator = ObjectAnimator.ofFloat(
        this,
        "translationX",
        0f,
        25f,
        -25f,
        25f,
        -25f,
        15f,
        -15f,
        6f,
        -6f,
        0f
    ).apply {
        addListener(object : AbstractAnimatorListener() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                initPinItems()
            }
        })
    }

    init {
        context.withStyledAttributes(attributeSet, R.styleable.HaloPinView) {
            pinCounts = getInt(R.styleable.HaloPinView_pinCount, 4)
            pinXLocations = Array(pinCounts) { 0f }
            inActivePinRadius =
                getDimension(R.styleable.HaloPinView_inActiveRadius, inActivePinRadius)
            activePinRadius = getDimension(R.styleable.HaloPinView_activeRadius, activePinRadius)
            inActivePinColor = getColor(R.styleable.HaloPinView_inActivePinColor, inActivePinColor)
            activePinColor = getColor(R.styleable.HaloPinView_activePinColor, activePinColor)
            isAnimationActive =
                getBoolean(R.styleable.HaloPinView_isAnimationActive, isAnimationActive)
            animationDuration = getInt(
                R.styleable.HaloPinView_animationDuration,
                animationDuration.toInt()
            ).toLong()
        }
        activePinPaint.color = activePinColor
        activePinPaint.style = Paint.Style.FILL_AND_STROKE
        activePinPaint.isAntiAlias = true

        inActivePinPaint.color = inActivePinColor
        inActivePinPaint.style = Paint.Style.FILL_AND_STROKE
        inActivePinPaint.isAntiAlias = true
        initPinItems()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val measuredWidth = if (pinCounts <= 0) 0 else measureWidth(widthMeasureSpec)
        val measuredHeight = if (pinCounts <= 0) 0 else measureHeight(heightMeasureSpec)

        pinYLocation = measuredHeight.toFloat() / 2f

        calculateRadiusMetrics(measuredWidth, measuredHeight)

        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        pinItems.forEachIndexed { index, pinItem ->
            if (pinItem.isEntered) {
                canvas?.drawCircle(
                    (3 * index + 2) * activePinRadius,
                    pinYLocation,
                    activePinRadius,
                    activePinPaint
                )
            } else {
                canvas?.drawCircle(
                    (3 * index + 2) * activePinRadius,
                    pinYLocation,
                    inActivePinRadius,
                    inActivePinPaint
                )
            }
        }
    }

    fun setOnPinFilledListener(onPinFilledListener: ((Int) -> Unit)?) {
        this.onPinFilledListener = onPinFilledListener
    }

    fun enterPin(number: Int) {
        if (lastPinIndex != pinItems.lastIndex) {
            invalidate()
            pinItems[++lastPinIndex] = PinItem(number, true)
            if (lastPinIndex == pinItems.lastIndex) {
                finishEnterPin()
            }
        }
    }

    fun setCorrectPin(correctPin: Int) {
        this.correctPin = correctPin
        isCorrectPinEntered = true
    }

    fun getItemCount() = pinCounts

    fun deletePin() {
        if (lastPinIndex != -1) {
            pinItems[lastPinIndex--] = PinItem(0, false)
            invalidate()
        }
    }

    fun setNumberOfPins(numberOfPins: Int) {
        pinCounts = numberOfPins
        invalidate()
    }

    fun deleteEnteredPins() {
        initPinItems()
    }

    fun setAnimationDuration(animationDurationInMillisecond: Long) {
        animationDuration = animationDurationInMillisecond
    }

    fun setAnimationState(isAnimationActive: Boolean) {
        this.isAnimationActive = isAnimationActive
    }

    fun setActivePinColor(color: Int) {
        activePinColor = color
    }

    fun setInActivePinColor(color: Int) {
        inActivePinColor = color
    }

    fun setActivePinRadius(radius: Float) {
        activePinRadius = radius
    }

    fun setInActivePinRadius(radius: Float) {
        inActivePinRadius = radius
    }

    private fun measureWidth(widthMeasureSpec: Int): Int {
        val specMode = MeasureSpec.getMode(widthMeasureSpec)
        val specSize = MeasureSpec.getSize(widthMeasureSpec)

        return when (specMode) {
            MeasureSpec.EXACTLY -> specSize
            else -> getTotalWidth().toInt()
        }
    }

    private fun measureHeight(heightMeasureSpec: Int): Int {
        val specMode = MeasureSpec.getMode(heightMeasureSpec)
        val specSize = MeasureSpec.getSize(heightMeasureSpec)

        return when (specMode) {
            MeasureSpec.EXACTLY -> specSize
            else -> activePinRadius.toInt() * 2 + paddingTop + paddingBottom
        }
    }

    private fun calculateRadiusMetrics(measuredWidth: Int, measuredHeight: Int) {
        if (inActivePinRadius > activePinRadius) {
            activePinRadius = inActivePinRadius
            inActivePinRadius = activePinRadius / 2f
        }

        if (activePinRadius > measuredHeight / 2f) {
            activePinRadius = measuredHeight / (2f / 3f)
            if (inActivePinRadius > activePinRadius) {
                inActivePinRadius = activePinRadius / (2f / 3f)
            }
        }

        if (measuredWidth < getTotalWidth()) {
            activePinRadius = measuredWidth.toFloat() / getSumOfVertices()
            if (inActivePinRadius > activePinRadius) {
                inActivePinRadius = activePinRadius / (2f / 3f)
            }
        }
    }

    private fun finishEnterPin() {
        if (isCorrectPinEntered) {
            if (isEnteredPinOk()) {
                onPinFilledListener?.invoke(getEnteredPin())
                initPinItems()
            } else {
                if (isAnimationActive) {
                    startWrongAnswerAnimation()
                } else {
                    onPinFilledListener?.invoke(getEnteredPin())
                    initPinItems()
                }
            }
        } else {
            onPinFilledListener?.invoke(getEnteredPin())
            initPinItems()
        }
    }

    private fun getTotalWidth() = getTotalWidthOfSpaces() + getTotalWidthOfCircles()

    private fun getTotalWidthOfSpaces() = (activePinRadius * (pinCounts + 1))

    private fun getTotalWidthOfCircles() = (pinCounts * 2 * activePinRadius)

    private fun getSumOfVertices() = ((2 * pinCounts) + (pinCounts + 1))

    private fun getEnteredPin(): Int {
        val pinBuilder = StringBuilder()
        pinItems.forEach {
            pinBuilder.append(it.number)
        }
        return pinBuilder.toString().toInt()
    }

    private fun initPinItems() {
        lastPinIndex = -1
        pinItems.clear()
        repeat(pinCounts) {
            pinItems.add(PinItem())
        }
        invalidate()
    }

    private fun isEnteredPinOk() = getEnteredPin() == correctPin

    private fun startWrongAnswerAnimation() {
        pinWiggleAnimator.duration = animationDuration
        pinWiggleAnimator.start()
    }
}