package com.sample.spin_wheel

import android.support.v7.app.AppCompatActivity
import android.R.interpolator
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.activity_spin_wheel.*

import java.util.Random

class MainActivity : AppCompatActivity() {
    private var mfDegreePerSegment: Float = 0.toFloat()
    private var mbSpinStarted = false

    private var wheelRotation: RotateAnimation? = null
    private var mAngleToRotate: Float = 0.toFloat()

    // Coins won
    private var mCoinsWon: Int = 0


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spin_wheel)
        iv_wheel_image.post {
            val params = iv_wheel_image.layoutParams as RelativeLayout.LayoutParams
            params.width = iv_wheel_image.height
            iv_wheel_image.layoutParams = params
        }

        mfDegreePerSegment = 360f / 12

        wheel_btn_spin.setOnClickListener {
            tv_winning_amount!!.visibility = View.GONE
            initSpin()
        }
    }


    public override fun onResume() {
        super.onResume()
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_top)
    }


    private fun initSpin() {
        if (mbSpinStarted) return
        spin()
    }


    private fun spin() {
        mAngleToRotate = 360f * NUM_OF_ROTATIONS + mfDegreePerSegment * getRandomNumberBetween(1, 12)

        wheelRotation = RotateAnimation(0.0f, mAngleToRotate, iv_wheel_image.width / 2.0f, iv_wheel_image.height / 2.0f)

        wheelRotation!!.duration = (SPIN_DURATION * 1000).toLong()

        wheelRotation!!.setInterpolator(this, interpolator.accelerate_decelerate)
        wheelRotation!!.fillAfter = true

        iv_wheel_image.startAnimation(wheelRotation)

        wheelRotation!!.setAnimationListener(object : MyAnimationListener() {
            override fun onAnimationEnd(animation: Animation) {
                if (ll_wheel_header_congrat.visibility == View.GONE) {
                    vf_header_flipper.setInAnimation(applicationContext, R.anim.in_from_right)
                    vf_header_flipper.setOutAnimation(applicationContext, R.anim.out_to_left)
                    vf_header_flipper.showNext()
                }

                // Print coins won!
                tv_winning_amount_lg.text = "$" + Integer.toString(mCoinsWon)
                tv_winning_amount.text = "$" + Integer.toString(mCoinsWon)
                tv_winning_amount.visibility = View.VISIBLE
                mbSpinStarted = false
            }

            override fun onAnimationRepeat(animation: Animation) {
                animation.setInterpolator(applicationContext, interpolator.accelerate_decelerate)
                animation.repeatCount = 0
                animation.fillAfter = true
            }

            override fun onAnimationStart(animation: Animation) {
                mbSpinStarted = true
            }
        })


        val wonPosition = mAngleToRotate.toInt() % 360 / mfDegreePerSegment.toInt()

        mCoinsWon = COINS_TO_WIN[wonPosition]

        Log.i(TAG, "Position: " + wonPosition)
        Log.i(TAG, "Coins to win: " + mCoinsWon)

    }

    companion object {
        private val TAG = "MainActivity"
        private val SPIN_DURATION = 5
        private val NUM_OF_ROTATIONS = 5f

        // Coins to win
        private val COINS_TO_WIN = intArrayOf(2500, 2000, 3000, 10000, 1000, 5000, 9000, 5000, 1200, 3000, 6000, 4000)

        private fun getRandomNumberBetween(aStart: Int, aEnd: Int): Int {
            val random = Random()

            if (aStart > aEnd) {
                throw IllegalArgumentException("Start cannot exceed End.")
            }

            //get the range, casting to long to avoid overflow problems
            val range = aEnd.toLong() - aStart.toLong() + 1

            // compute a fraction of the range, 0 <= frac < range
            val fraction = (range * random.nextDouble()).toLong()

            val randomNumber = (fraction + aStart).toInt()

            Log.i(TAG, "Generated : " + randomNumber)

            return randomNumber
        }
    }
}
