package com.gcc.smartcity.submit

import android.os.Bundle
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

        continueBtn.setOnClickListener {
            finish()
        }

        setGemCount(rewards)
    }

    private fun setGemCount(rewards: String?) {
        gemCounter?.text = rewards
    }

}
