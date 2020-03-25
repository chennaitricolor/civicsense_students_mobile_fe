package com.gcc.smartcity.intro

import android.Manifest
import android.content.Intent
import android.os.Bundle
import com.gcc.smartcity.R
import com.heinrichreimersoftware.materialintro.app.IntroActivity
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide


class MainIntroActivity : IntroActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addSlide(
            SimpleSlide.Builder()
                .title("Welcome")
                .description("hi")
                .image(R.drawable.agent_x_logo)
                .background(R.color.agentx_text_light)
                .backgroundDark(R.color.agentx_text_dark)
                .scrollable(false)
                .build()
        )

        addSlide(
            SimpleSlide.Builder()
                .title("Permissions Required")
                .description("In order to access your location you must give permissions.")
                .image(R.drawable.agent_x_logo)
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
