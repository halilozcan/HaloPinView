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

    private var pinCount = DEFAULT_PIN_COUNT

    private var pinXLocations = Array(pinCount) { 0f }

    private var animationDuration = DEFAULT_ANIMATION_DURATION

    private var inActivePinRadius = DEFAULT_INACTIVE_RADIUS
    private var activePinRadius = DEFAULT_ACTIVE_PIN_RADIUS

    private var inActivePinColor = Color.DKGRAY
    private var activePinColor = Color.WHITE

    private var onPinFilledListener: ((String) -> Unit)? = null

    private var correctPin: String? = EMPTY_PLACE_HOLDER

    private var isAnimationActive = false

    private var pinYLocation = 0f

    private var pinConfig: PinConfig

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
            pinCount = getInt(R.styleable.HaloPinView_pinCount, 4)
            pinXLocations = Array(pinCount) { 0f }
            inActivePinRadius =
                getDimension(R.styleable.HaloPinView_inActivePinRadius, inActivePinRadius)
            activePinRadius = getDimension(R.styleable.HaloPinView_activePinRadius, activePinRadius)
            inActivePinColor = getColor(R.styleable.HaloPinView_inActivePinColor, inActivePinColor)
            activePinColor = getColor(R.styleable.HaloPinView_activePinColor, activePinColor)
            isAnimationActive =
                getBoolean(R.styleable.HaloPinView_isAnimationActive, isAnimationActive)
            animationDuration = getInt(
                R.styleable.HaloPinView_animationDuration,
                animationDuration.toInt()
            ).toLong()
            correctPin = getString(R.styleable.HaloPinView_correctPin) ?: correctPin
        }

        pinConfig = PinConfig.Builder()
            .inActivePinRadius(inActivePinRadius)
            .activePinRadius(activePinRadius)
            .animationDuration(animationDuration)
            .animationActivationState(isAnimationActive)
            .pinCount(pinCount)
            .inActivePinColor(inActivePinColor)
            .activePinColor(activePinColor)
            .correctPin(correctPin)
            .build()

        activePinPaint.color = pinConfig.activePinColor
        activePinPaint.style = Paint.Style.FILL_AND_STROKE

        inActivePinPaint.color = pinConfig.inActivePinColor
        inActivePinPaint.style = Paint.Style.FILL_AND_STROKE

        initPinItems()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val measuredWidth = if (pinConfig.pinCount <= 0) 0 else measureWidth(widthMeasureSpec)
        val measuredHeight = if (pinConfig.pinCount <= 0) 0 else measureHeight(heightMeasureSpec)

        pinYLocation = measuredHeight.toFloat() / 2f

        calculateRadiusMetrics(measuredWidth, measuredHeight)

        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        pinItems.forEachIndexed { index, pinItem ->
            if (pinItem.isEntered) {
                canvas?.drawCircle(
                    (3 * index + 2) * pinConfig.activePinRadius,
                    pinYLocation,
                    pinConfig.activePinRadius,
                    activePinPaint
                )
            } else {
                canvas?.drawCircle(
                    (3 * index + 2) * pinConfig.activePinRadius,
                    pinYLocation,
                    pinConfig.inActivePinRadius,
                    inActivePinPaint
                )
            }
        }
    }

    fun setOnPinFilledListener(onPinFilledListener: ((String) -> Unit)?) {
        this.onPinFilledListener = onPinFilledListener
    }

    fun enterPin(pin: String) {
        if (lastPinIndex != pinItems.lastIndex) {
            invalidate()
            pinItems[++lastPinIndex] = PinItem(pin, true)
            if (lastPinIndex == pinItems.lastIndex) {
                finishEnterPin()
            }
        }
    }

    fun deletePin() {
        if (lastPinIndex != -1) {
            pinItems[lastPinIndex--] = PinItem("", false)
            invalidate()
        }
    }

    fun getItemCount() = pinConfig.pinCount

    fun setPinConfig(pinConfig: PinConfig) {
        this.pinConfig = pinConfig
        invalidate()
    }

    fun deleteEnteredPins() {
        initPinItems()
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
            else -> pinConfig.activePinRadius.toInt() * 2 + paddingTop + paddingBottom
        }
    }

    private fun calculateRadiusMetrics(measuredWidth: Int, measuredHeight: Int) {
        if (pinConfig.inActivePinRadius > pinConfig.activePinRadius) {
            throw Exception("Inactive pin radius can not be greater than active pin radius")
        }

        if (pinConfig.activePinRadius > measuredHeight / 2f) {
            pinConfig.activePinRadius = measuredHeight / (2f / 3f)
            if (pinConfig.inActivePinRadius > activePinRadius) {
                pinConfig.inActivePinRadius = activePinRadius / (2f / 3f)
            }
        }

        if (measuredWidth < getTotalWidth()) {
            pinConfig.activePinRadius = measuredWidth.toFloat() / getSumOfVertices()
            if (inActivePinRadius > pinConfig.activePinRadius) {
                pinConfig.inActivePinRadius = pinConfig.activePinRadius / (2f / 3f)
            }
        }
    }

    private fun finishEnterPin() {
        if (pinConfig.isCorrectPinEntered) {
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

    private fun getTotalWidthOfSpaces() = (pinConfig.activePinRadius * (pinConfig.pinCount + 1))

    private fun getTotalWidthOfCircles() = (pinConfig.pinCount * 2 * activePinRadius)

    private fun getSumOfVertices() = ((2 * pinConfig.pinCount) + (pinConfig.pinCount + 1))

    private fun getEnteredPin(): String {
        val pinBuilder = StringBuilder()
        pinItems.forEach {
            pinBuilder.append(it.pin)
        }
        return pinBuilder.toString()
    }

    private fun initPinItems() {
        lastPinIndex = -1
        pinItems.clear()
        repeat(pinConfig.pinCount) {
            pinItems.add(PinItem())
        }
        invalidate()
    }

    private fun isEnteredPinOk() = getEnteredPin() == pinConfig.correctPin

    private fun startWrongAnswerAnimation() {
        pinWiggleAnimator.duration = pinConfig.animationDuration
        pinWiggleAnimator.start()
    }

    companion object {
        const val DEFAULT_INACTIVE_RADIUS = 10f
        const val DEFAULT_ACTIVE_PIN_RADIUS = 20f
        const val DEFAULT_ANIMATION_DURATION = 1000L
        const val DEFAULT_PIN_COUNT = 4
        const val EMPTY_PLACE_HOLDER = ""
        const val DEFAULT_ANIMATION_STATE = false
    }
}