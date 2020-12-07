package com.halilozcan.pinview

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        simulate_pin_button.setOnClickListener {
            simulateEnteringPin(pin_view.getItemCount())
        }

        pin_view.setOnPinFilledListener {
            Toast.makeText(this, "Entered Pin: $it", Toast.LENGTH_LONG).show()
        }
    }

    private fun simulateEnteringPin(pinsCount: Int) {

        object : CountDownTimer(pinsCount * 1000.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                pin_view.enterPin((0..9).random())
            }

            override fun onFinish() {
                pin_view.deleteEnteredPins()
            }
        }.start()
    }

}