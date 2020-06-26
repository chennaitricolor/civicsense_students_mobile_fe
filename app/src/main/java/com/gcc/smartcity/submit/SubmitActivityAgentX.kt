package com.gcc.smartcity.submit

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.gcc.smartcity.R
import com.gcc.smartcity.fontui.FontTextView
import kotlinx.android.synthetic.main.activity_submit.*

class SubmitActivityAgentX : AppCompatActivity() {

    private var rewards: String? = null
    private var gemCounter: FontTextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit)

        gemCounter = findViewById(R.id.numberOfGems)

        if (intent.extras != null) {
            rewards = intent.extras!!.getString("rewards").toString()
        }

        buttonEffect(continueBtn)

        continueBtn.setOnClickListener {
            finish()
        }

        setGemCount(rewards)
    }

    private fun setGemCount(rewards: String?) {
        gemCounter?.text = rewards
    }

    private fun buttonEffect(button: View) {
        button.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.background.setColorFilter(
                        Color.parseColor("#3DAFD4"),
                        PorterDuff.Mode.SRC_ATOP
                    )
                    v.invalidate()
                }
                MotionEvent.ACTION_UP -> {
                    v.background.clearColorFilter()
                    v.invalidate()
                }
            }
            false
        }
    }

}
