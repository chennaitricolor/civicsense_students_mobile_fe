package com.gcc.smartcity.dashboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.gcc.smartcity.R
import com.gcc.smartcity.dashboard.form.DynamicFormActivity
import com.gcc.smartcity.dashboard.model.NewMissionListModel
import com.gcc.smartcity.preference.SessionStorage
import kotlinx.android.synthetic.main.activity_rules.*

class RulesActivity : AppCompatActivity() {

    private var _id: String? = null
    private var _campaignName: String? = null
    private var rewards: String? = null
    private var rules: String? = null
    private var isBasicFormNeeded: Boolean? = false
    private var newMissionListModel: NewMissionListModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rules)
        newMissionListModel = SessionStorage.getInstance().newMissionListModel

        if (newMissionListModel != null) {
            txt_campaignname.text = newMissionListModel?.task?.campaignName
            txt_rules.text = newMissionListModel?.task?.rules
            isBasicFormNeeded = newMissionListModel?.task?.needForm
            _id = newMissionListModel?.task?._id
            _campaignName = newMissionListModel?.task?.campaignName
            rewards = newMissionListModel?.task?.rewards.toString()
            rules = newMissionListModel?.task?.rules
            btnRulesNext.setOnClickListener {
                val intent = Intent(this, getTargetClass(isBasicFormNeeded))
                intent.putExtra("_id", _id)
                intent.putExtra("_campaignName", _campaignName)
                intent.putExtra("rewards", rewards)
                intent.putExtra("fromScreen", "rulesActivity")
                startActivity(intent)
                finish()
            }
        } else {
            Toast.makeText(this, "No info to show", Toast.LENGTH_LONG)
                .show()
        }


    }

    private fun getTargetClass(basicFormNeeded: Boolean?): Class<*> {
        return if (basicFormNeeded != null && basicFormNeeded == true) {
            DynamicFormActivity::class.java
        } else {
            ImageCaptureActivity::class.java
        }
    }
}
