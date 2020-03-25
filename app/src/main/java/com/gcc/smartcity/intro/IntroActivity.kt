package com.gcc.smartcity.intro

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.gcc.smartcity.R
import com.gcc.smartcity.loginandregistration.LoginActivity
import com.gcc.smartcity.preference.SessionStorage
import com.github.paolorotolo.appintro.AppIntro
import com.github.paolorotolo.appintro.AppIntroFragment

class IntroActivity : AppIntro() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addSlide(
            AppIntroFragment.newInstance(
                "Welcome",
                "hi",
                R.drawable.agent_x_logo,
                resources.getColor(R.color.agentx_text_dark)
            )
        )

        addSlide(
            AppIntroFragment.newInstance(
                "Permissions Required",
                "In order to access your location you must give permissions.",
                R.drawable.agent_x_logo,
                resources.getColor(R.color.agentx_text_dark)
            )
        )

        addSlide(
            AppIntroFragment.newInstance(
                "Permissions Required",
                "In order to access your camera you must give permissions.",
                R.drawable.agent_x_logo,
                resources.getColor(R.color.agentx_text_dark)
            )
        )

        addSlide(
            AppIntroFragment.newInstance(
                "Permissions Required",
                "In order to access your storage you must give permissions.",
                R.drawable.agent_x_logo,
                resources.getColor(R.color.agentx_text_dark)
            )
        )

        addSlide(TermsAndConditionsFragment())

        askForPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            ), 2
        )

        askForPermissions(
            arrayOf(
                Manifest.permission.CAMERA
            ), 3
        )

        askForPermissions(
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), 4
        )

//        setGoBackLock(true)
//        showSkipButton(false)
        isWizardMode = true
        setFadeAnimation()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        SessionStorage.getInstance().introSlidesVisibility = false
        val intent = Intent(this, LoginActivity::class.java)
        intent.clearStack()
        startActivity(intent)
        finish()
    }

    private fun Intent.clearStack() {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
}
