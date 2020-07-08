package com.gcc.smartcity.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gcc.smartcity.BuildConfig
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
    private var isMediaNeeded: Boolean? = false
    private var newMissionListModel: NewMissionListModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rules)
        newMissionListModel = SessionStorage.getInstance().newMissionListModel

        if (newMissionListModel != null) {
            campaignName.text = newMissionListModel?.task?.campaignName
            descriptionText.text = newMissionListModel?.task?.description
            if (SessionStorage.getInstance().rootModel?.region?.regionsMap?.get(BuildConfig.CITY)?.rules!!) {
                rulesTitle.visibility = View.VISIBLE
                rulesText.text = newMissionListModel?.task?.rules
                rulesText.visibility = View.VISIBLE
            } else {
                rulesTitle.visibility = View.GONE
                rulesText.visibility = View.GONE
            }
            isBasicFormNeeded = newMissionListModel?.task?.needForm
            isMediaNeeded = newMissionListModel?.task?.needMedia
            _id = newMissionListModel?.task?._id
            _campaignName = newMissionListModel?.task?.campaignName
            rewards = newMissionListModel?.task?.rewards.toString()
            rules = newMissionListModel?.task?.rules
            btnRulesNext.setOnClickListener {
                val intent: Intent
                if (isBasicFormNeeded != null && isBasicFormNeeded == true) {
                    intent = Intent(this, DynamicFormActivity::class.java)
                    if (isMediaNeeded != null && isMediaNeeded == true) {
                        intent.putExtra("mediaNeeded", true)
                    } else {
                        intent.putExtra("mediaNeeded", false)
                    }
                } else {
                    intent = Intent(this, ImageCaptureActivity::class.java)
                }

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
}
