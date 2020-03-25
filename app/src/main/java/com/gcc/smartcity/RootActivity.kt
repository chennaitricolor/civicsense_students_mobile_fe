package com.gcc.smartcity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gcc.smartcity.intro.MainIntroActivity
import com.gcc.smartcity.loginandregistration.LoginActivity
import com.gcc.smartcity.preference.SessionStorage

open class RootActivity : AppCompatActivity() {
    private val REQUEST_CODE_INTRO = 108
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkForIntroSlidesFlag()
//        doRootCall()
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
        TODO("Not yet implemented")
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
