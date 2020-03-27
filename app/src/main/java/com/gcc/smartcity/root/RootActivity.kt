package com.gcc.smartcity.root

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import bolts.Task
import com.gcc.smartcity.BuildConfig
import com.gcc.smartcity.dashboard.DashBoardActivity
import com.gcc.smartcity.forceupdate.ForceAppUpdateActivity
import com.gcc.smartcity.intro.MainIntroActivity
import com.gcc.smartcity.loginandregistration.LoginActivity
import com.gcc.smartcity.preference.SessionStorage
import com.gcc.smartcity.utils.VersionCheckUtil

class RootActivity : AppCompatActivity() {
    private val REQUEST_CODE_INTRO = 108
    private var mRootController: RootController? = null

    init {
        mRootController = RootController(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        doRootCall()
        checkForIntroSlidesFlag()
    }



    private fun checkForIntroSlidesFlag() {
        if (SessionStorage.getInstance().introSlidesVisibility) {
//            callNextActivity(MainIntroActivity::class.java)
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
        mRootController?.doRootCall(
            BuildConfig.HOST + java.lang.String.format(
                "user/"
            )
        )?.continueWithTask { task ->
                afterRootCall(task)
            }
    }

    private fun afterRootCall(task: Task<Any>): Task<Any>? {
//        if (task.isFaulted) {
//            showErrorDialog(
//                getString(R.string.unableToGetRootModel),
//                getString(R.string.tryAgainLater),
//                getString(R.string.okButtonText)
//            )
//            task.makeVoid()
//            showLoader(false)
//        } else {
//            val rootModel = task.result as RootModel
//            if (rootModel.success!!) {
//              SessionStorage.getInstance().rootModel = rootModel
//        if (rootModel.version.min_supported_android != null && VersionCheckUtil.compareInstalledVersionNameWith(
//                rootModel.version.min_supported_android
//            ) == -1
//        ) {
//            callNextActivity(ForceAppUpdateActivity::class.java)
//        }
//            } else {
//                showErrorDialog(
//                    getString(R.string.unableToGetRootModel),
//                    getString(R.string.tryAgainLater),
//                    getString(R.string.okButtonText)
//                )
//            }
//        }

        return null
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
