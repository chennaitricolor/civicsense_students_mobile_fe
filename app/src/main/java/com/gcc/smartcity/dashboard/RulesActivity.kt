package com.gcc.smartcity.dashboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gcc.smartcity.R
import kotlinx.android.synthetic.main.activity_rules.*

class RulesActivity : AppCompatActivity() {

    private var _id: String? = null
    private var _campaignName: String? = null
    private var rewards: String? = null
    private var rules: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rules)

        if (intent.extras != null) {
            _id = intent.extras!!.getString("_id").toString()
            _campaignName = intent.extras!!.getString("_campaignName").toString()
            rewards = intent.extras!!.getString("rewards").toString()
            rules = intent.extras!!.getString("rules").toString()
        }

        txt_campaignname.text = _campaignName
        txt_rules.text = rules
        btnRulesNext.setOnClickListener {
            val intent = Intent(this, ImageCaptureActivity::class.java)
            intent.putExtra("_id", _id)
            intent.putExtra("_campaignName",_campaignName)
            intent.putExtra("rewards", rewards.toString())
            intent.putExtra("rules", rules.toString())
            startActivity(intent)
            finish()
        }

    }
}
