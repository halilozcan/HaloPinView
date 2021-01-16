package com.halilozcan.halopinlib

import android.graphics.Color
import com.halilozcan.halopinlib.HaloPinView.Companion.EMPTY_PLACE_HOLDER


class PinConfig(private val pinConfigBuilder: Builder) {

    var inActivePinRadius: Float = pinConfigBuilder.getInActivePinRadius()

    var activePinRadius: Float = pinConfigBuilder.getActivePinRadius()

    val animationDuration: Long = pinConfigBuilder.getAnimationDuration()

    val isAnimationActive: Boolean = pinConfigBuilder.isAnimationActive()

    val pinCount: Int = pinConfigBuilder.getPinCount()

    val inActivePinColor: Int = pinConfigBuilder.getInActivePinColor()

    val activePinColor: Int = pinConfigBuilder.getActivePinColor()

    var correctPin: String = pinConfigBuilder.getCorrectPin()

    var isCorrectPinEntered: Boolean = pinConfigBuilder.isCorrectPinEntered()

    class Builder {
        private var inActivePinRadius: Float = HaloPinView.DEFAULT_INACTIVE_RADIUS

        private var activePinRadius: Float = HaloPinView.DEFAULT_ACTIVE_PIN_RADIUS

        private var animationDuration: Long = HaloPinView.DEFAULT_ANIMATION_DURATION

        private var isAnimationActive: Boolean = HaloPinView.DEFAULT_ANIMATION_STATE

        private var pinCount: Int = 4

        private var inActivePinColor: Int = Color.DKGRAY

        private var activePinColor: Int = Color.WHITE

        private var correctPin: String = EMPTY_PLACE_HOLDER

        private var isCorrectPinEntered = false

        fun inActivePinRadius(inActivePinRadius: Float): Builder {
            this.inActivePinRadius = inActivePinRadius
            return this
        }

        fun activePinRadius(activePinRadius: Float): Builder {
            this.activePinRadius = activePinRadius
            return this
        }

        fun animationDuration(animationDuration: Long): Builder {
            this.animationDuration = animationDuration
            return this
        }

        fun animationActivationState(isAnimationActive: Boolean): Builder {
            this.isAnimationActive = isAnimationActive
            return this
        }

        fun pinCount(pinCount: Int): Builder {
            this.pinCount = pinCount
            return this
        }

        fun inActivePinColor(inActivePinColor: Int): Builder {
            this.inActivePinColor = inActivePinColor
            return this
        }

        fun activePinColor(activePinColor: Int): Builder {
            this.activePinColor = activePinColor
            return this
        }

        fun correctPin(correctPin: String?): Builder {
            correctPin?.let {
                this.correctPin = it
                isCorrectPinEntered = true
            }
            return this
        }

        fun build(): PinConfig {
            return PinConfig(this)
        }

        internal fun getInActivePinRadius() = inActivePinRadius

        internal fun getActivePinRadius() = activePinRadius

        internal fun getAnimationDuration() = animationDuration

        internal fun isAnimationActive() = isAnimationActive

        internal fun getPinCount() = pinCount

        internal fun getInActivePinColor() = inActivePinColor

        internal fun getActivePinColor() = activePinColor

        internal fun getCorrectPin() = correctPin

        internal fun isCorrectPinEntered() = isCorrectPinEntered
    }
}