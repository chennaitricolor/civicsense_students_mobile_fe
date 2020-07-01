package com.gcc.smartcity.loginandregistration

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import bolts.Task
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.gcc.smartcity.BaseActivity
import com.gcc.smartcity.BuildConfig
import com.gcc.smartcity.R
import com.gcc.smartcity.fontui.FontEditText
import com.gcc.smartcity.loginandregistration.controller.LoginAndRegistrationController
import com.gcc.smartcity.loginandregistration.model.OTPModel
import com.gcc.smartcity.preference.SessionStorage
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.personaContainer
import kotlinx.android.synthetic.main.activity_sign_up.persona_dropdown

class SignUpActivity : BaseActivity() {

    private var mLoginAndRegistrationController: LoginAndRegistrationController? = null
    private var name: FontEditText? = null
    private var mobileNumber: FontEditText? = null
    private var isMobileNumberValid: Boolean = false
    private lateinit var spinnerList: ArrayList<Spinner>

    init {
        mLoginAndRegistrationController = LoginAndRegistrationController(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setView(R.layout.activity_sign_up)

        name = findViewById(R.id.Name)
        mobileNumber = findViewById(R.id.registerMobileNumber)

        val mobileNumberPattern =
            "^[6-9]\\d{9}\$"
        spinnerList = ArrayList()


        if (BuildConfig.PERSONA) {
            val persona = SessionStorage.getInstance().rootModel.region?.regionsMap?.get(BuildConfig.CITY)?.persona
            if (!persona.isNullOrEmpty()) {
                setupDropDown(persona)
                personaContainer.visibility = View.VISIBLE
            }
        } else {
            personaContainer.visibility = View.GONE
        }


        buttonEffect(continueBtn)

        continueBtn.setOnClickListener {
            hideSoftKeyBoard()
            if (persona_dropdown.selectedItem == "Please select role") {
                Toast.makeText(this, "Please select your role", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (name?.text.toString().isNotEmpty() && mobileNumber?.text.toString()
                        .isNotEmpty() && isMobileNumberValid
                ) {
                    sendOTP(
                        name?.text.toString(),
                        mobileNumber?.text.toString(),
                        persona_dropdown.selectedItem.toString()
                    )
                } else {
                    showErrorDialog(
                        getString(R.string.insufficientDetails),
                        getString(R.string.incorrectSignUpDetails),
                        getString(R.string.okButtonText)
                    )
                }
            }
        }

        mobileNumber?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.matches((mobileNumberPattern).toRegex()) && s.isNotEmpty()) {
                    Log.d("success", "valid")
                    isMobileNumberValid = true
                    mobileNumber?.setBackgroundResource(R.drawable.bg_border_edittext)
                } else {
                    Log.d("failure", "FAIL")
                    isMobileNumberValid = false
                    mobileNumber?.setBackgroundResource(R.drawable.bg_border_edittext_wrong)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }

    private fun sendOTP(name: String, mobileNumber: String, userPersona: String) {
        showLoader(true)
        mLoginAndRegistrationController?.doOTPCall(
            BuildConfig.HOST + java.lang.String.format(
                "user/generate-otp?phoneNumber=%s",
                mobileNumber
            )
        )
            ?.continueWithTask { task ->
                afterOTPSent(task, name, mobileNumber, userPersona)
            }
    }

    private fun afterOTPSent(
        task: Task<Any>,
        name: String,
        mobileNumber: String,
        userPersona: String
    ): Task<Any>? {
        if (task.isFaulted) {
            showErrorDialog(
                getString(R.string.unableToSendOTP),
                getString(R.string.tryAgainLater),
                getString(R.string.okButtonText)
            )
            task.makeVoid()
            showLoader(false)
        } else {
            val otpModel = task.result as OTPModel
            if (otpModel.success!!) {
                val intent = Intent(this, OTPVerifyActivity::class.java)
                intent.putExtra("name", name)
                intent.putExtra("mobilenumber", mobileNumber)
                intent.putExtra("userPersona", userPersona)
                intent.putExtra("fromScreen", "signUpScreen")
                startActivity(intent)
            } else {
                showErrorDialog(
                    getString(R.string.unableToSendOTP),
                    getString(R.string.tryAgainLater),
                    getString(R.string.okButtonText)
                )
            }
        }

        return null
    }

    private fun buttonEffect(button: View) {
        button.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.background.setColorFilter(
                        Color.parseColor("#7aa133"),
                        PorterDuff.Mode.SRC_ATOP
                    )
                    v.invalidate()
                }
                MotionEvent.ACTION_UP -> {
                    v.background.clearColorFilter()
                    v.invalidate()
                }
            }
            false
        }
    }

    private fun setupDropDown(dropdownData: ArrayList<String>?) {
        val dropDown = findViewById<Spinner>(R.id.persona_dropdown)
        dropdownData?.add(0, "Please select role")
        val arrayAdapter = dropdownData?.toArray()?.let {
            ArrayAdapter(
                this, android.R.layout.simple_spinner_item, it
            )
        }
        arrayAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dropDown.adapter = arrayAdapter
        spinnerList.add(dropDown)
    }

    override fun onResume() {
        super.onResume()
        showLoader(false)
    }
}
