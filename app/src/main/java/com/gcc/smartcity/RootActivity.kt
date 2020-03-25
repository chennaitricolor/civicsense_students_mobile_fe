package com.gcc.smartcity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gcc.smartcity.intro.IntroActivity
import com.gcc.smartcity.loginandregistration.LoginActivity
import com.gcc.smartcity.preference.SessionStorage

open class RootActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkForIntroSlidesFlag()
//        doRootCall()
    }

    private fun checkForIntroSlidesFlag() {
        if (SessionStorage.getInstance().introSlidesVisibility) {
            callNextActivity(IntroActivity::class.java)
        } else {
            callNextActivity(LoginActivity::class.java)
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
