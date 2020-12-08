package com.halilozcan.halopinlib

import android.animation.Animator


// Code with ❤
//┌─────────────────────────────┐
//│ Created by Halil ÖZCAN      │
//│ ─────────────────────────── │
//│ mail@halilozcan.com         │
//│ ─────────────────────────── │
//│ 12/8/20                     │
//└─────────────────────────────┘

abstract class AbstractAnimatorListener : Animator.AnimatorListener{
    override fun onAnimationCancel(animation: Animator?) {}

    override fun onAnimationEnd(animation: Animator?) {}

    override fun onAnimationRepeat(animation: Animator?) {}

    override fun onAnimationStart(animation: Animator?) {}
}