package com.halilozcan.halopinlib

import com.halilozcan.halopinlib.HaloPinView.Companion.EMPTY_PLACE_HOLDER


// Code with ❤
//┌─────────────────────────────┐
//│ Created by Halil ÖZCAN      │
//│ ─────────────────────────── │
//│ mail@halilozcan.com         │
//│ ─────────────────────────── │
//│ 12/7/20                     │
//└─────────────────────────────┘

data class PinItem(val pin: String = EMPTY_PLACE_HOLDER, val isEntered: Boolean = false)