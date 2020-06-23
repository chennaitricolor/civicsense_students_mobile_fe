package com.gcc.smartcity.intro

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import com.gcc.smartcity.R
import com.heinrichreimersoftware.materialintro.app.IntroActivity
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide


class MainIntroActivity : IntroActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addSlide(
            SimpleSlide.Builder()
                .title("WELCOME!")
                .description("Hello there! This is Greater Chennai Corporation's official CORONA MONITORING APP.")
                .image(R.drawable.app_logo)
                .background(R.color.agentx_text_light)
                .backgroundDark(R.color.agentx_text_dark)
                .scrollable(false)
                .build()
        )

        addSlide(
            SimpleSlide.Builder()
                .title("PERMISSIONS REQUIRED")
                .description("We need a few permissions to ensure that the app is working properly. Please grant them by clicking the button below.")
                .image(R.drawable.app_logo)
                .background(R.color.agentx_text_light)
                .backgroundDark(R.color.agentx_text_dark)
                .permissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                .scrollable(false)
                .build()
        )

        addSlide(
            FragmentSlide.Builder()
                .fragment(TermsAndConditionsFragment())
                .background(R.color.agentx_text_light)
                .build()
        )

        buttonBackFunction = BUTTON_BACK_FUNCTION_BACK
    }
}
