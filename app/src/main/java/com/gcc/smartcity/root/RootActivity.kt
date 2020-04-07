package com.gcc.smartcity.root

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import bolts.Task
import com.gcc.smartcity.BuildConfig
import com.gcc.smartcity.R
import com.gcc.smartcity.dashboard.model.root.RootApiModel
import com.gcc.smartcity.forceupdate.ForceAppUpdateActivity
import com.gcc.smartcity.intro.MainIntroActivity
import com.gcc.smartcity.loginandregistration.LoginActivity
import com.gcc.smartcity.maintenance.MaintenanceActivity
import com.gcc.smartcity.preference.SessionStorage
import com.gcc.smartcity.utils.VersionCheckUtils

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

            val rootApiModel: RootApiModel = task.result as RootApiModel

            SessionStorage.getInstance().rootModel = rootApiModel

            if (rootApiModel.version != null && VersionCheckUtils.compareInstalledVersionNameWith(
                    rootApiModel.version!!
                ) == -1
            ) {
                callNextActivity(ForceAppUpdateActivity::class.java)
            } else {
                checkForIntroSlidesFlag()
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
}
