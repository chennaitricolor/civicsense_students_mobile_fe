package com.gcc.smartcity.root

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import bolts.Task
import com.gcc.smartcity.BuildConfig
import com.gcc.smartcity.R
import com.gcc.smartcity.dashboard.model.root.*
import com.gcc.smartcity.intro.MainIntroActivity
import com.gcc.smartcity.loginandregistration.LoginActivity
import com.gcc.smartcity.maintenance.MaintenanceActivity
import com.gcc.smartcity.preference.SessionStorage
import org.json.JSONArray
import org.json.JSONObject

class RootActivity : AppCompatActivity() {
    private val REQUEST_CODE_INTRO = 108
    private var mRootController: RootController? = null

    init {
        mRootController = RootController(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)
        //  doUserCall()
        doRootCall()

    }

    private fun checkForIntroSlidesFlag() {
        if (SessionStorage.getInstance().introSlidesVisibility) {
            val i = Intent(this, MainIntroActivity::class.java)
            startActivityForResult(i, REQUEST_CODE_INTRO)
        } else {
            callNextActivity(LoginActivity::class.java)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_INTRO) {
            if (resultCode == RESULT_OK) {
                SessionStorage.getInstance().introSlidesVisibility = false
                callNextActivity(LoginActivity::class.java)
            } else {
                finish()
            }
        }
    }

    private fun doRootCall() {
        mRootController?.doRootCall()?.continueWith { it ->
            afterRootCall(it)
        }
    }

    private fun doUserCall() {
        mRootController?.doUserCall(
            BuildConfig.HOST + java.lang.String.format(
                "user/"
            )
        )
    }

    private fun afterRootCall(task: Task<Any>) {
        if (!task.isFaulted) {
            val rootString: String = (task.result).toString()
            if (!rootString.equals("null", true)) {
                val rootApiModel: RootApiModel = getObject(rootString)

                SessionStorage.getInstance().rootModel = rootApiModel

//                if (rootApiModel.region?.regionsMap?.get(BuildConfig.CITY)?.minimumAndroidVersion != null && VersionCheckUtils.compareInstalledVersionNameWith(
//                        rootApiModel.region?.regionsMap?.get(BuildConfig.CITY)?.minimumAndroidVersion
//                    ) == -1
//                ) {
//                    callNextActivity(ForceAppUpdateActivity::class.java)
//                } else {
                checkForIntroSlidesFlag()
//                }
            } else {
                callNextActivity(MaintenanceActivity::class.java)
            }
        } else {
            callNextActivity(MaintenanceActivity::class.java)
        }
    }

    private fun callNextActivity(activityClass: Class<*>) {
        val i = Intent(this, activityClass)
        intent.clearStack()
        startActivity(i)
        finish()
    }

    private fun Intent.clearStack() {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    private fun getObject(json: String): RootApiModel {
        val rootApiModel = RootApiModel()
        val jsonObject = JSONObject(json)
        val linkObj = jsonObject.optJSONObject("links")
        val links = Links()
        links.howToPlay = linkObj?.optString("howToPlay")
        rootApiModel.links = links
        val contactUsObj = jsonObject.optJSONObject("contactus")
        val email = contactUsObj?.optString("email")
        val phone = contactUsObj?.optInt("phone")
        val contactUs = ContactUs(email, phone)
        rootApiModel.contactUs = contactUs
        val aboutus = jsonObject.optString("aboutus")
        rootApiModel.aboutus = aboutus
        val termsAndConditionObj = jsonObject.optString("termsAndCondition")
        rootApiModel.termsAndConditions = termsAndConditionObj
        val contributorsList = ArrayList<Contributors>()
        val contributorsArr = jsonObject.optJSONArray("contributors")
        contributorsArr.let {
            for (i in 0 until it?.length()!!) {
                val obj = it.get(i) as JSONObject
                val name = obj.optString("name")
                val contributorsEmail = obj.optString("email")
                val meta = obj.optString("meta")
                val contributors = Contributors(name, contributorsEmail, meta)
                contributorsList.add(contributors)
            }
        }
        rootApiModel.contributors = contributorsList
        val meta: String = jsonObject.optString("meta")
        val default: String = jsonObject.optString("default")
        rootApiModel.meta = meta
        rootApiModel.default = default
        val regionsMap = HashMap<String, RegionModel>()
        val regionsObj = jsonObject.optJSONObject("region")
        regionsObj.let {
            val iterator = it?.keys()
            while (iterator?.hasNext()!!) {
                val key = iterator.next()
                val cityObj = it.optJSONObject(key)
                val rulesBoolean = cityObj?.optBoolean("rules")
                val termsAndConditionString = cityObj?.optString("termsAndCondition")
                val minAndroidVersionString = cityObj?.optString("minimumAndroidVersion")
                val arr: JSONArray? = cityObj?.optJSONArray("userPersona")
                val personaList = ArrayList<String>()
                for (i in 0 until arr?.length()!!) {
                    personaList.add(arr.optString(i))
                }
                val regionModel =
                    RegionModel(termsAndConditionString, minAndroidVersionString, personaList, rulesBoolean)
                regionsMap[key] = regionModel
            }
        }
        val regions = Regions(regionsMap)
        rootApiModel.region = regions
        return rootApiModel
    }
}
