package com.gcc.smartcity.loginandregistration

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import bolts.Task
import com.gcc.smartcity.BaseActivity
import com.gcc.smartcity.BuildConfig
import com.gcc.smartcity.R
import com.gcc.smartcity.fontui.FontEditText
import com.gcc.smartcity.loginandregistration.controller.LoginAndRegistrationController
import com.gcc.smartcity.loginandregistration.model.OTPModel
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : BaseActivity() {

    private var mLoginAndRegistrationController: LoginAndRegistrationController? = null
    private var name: FontEditText? = null
    private var mobileNumber: FontEditText? = null
    private var isMobileNumberValid: Boolean = false

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

        continueBtn.setOnClickListener {
            hideSoftKeyBoard()
            if (name?.text.toString().isNotEmpty() && mobileNumber?.text.toString()
                    .isNotEmpty() && isMobileNumberValid
            ) {
                sendOTP(
                    name?.text.toString(),
                    mobileNumber?.text.toString()
                )
            } else {
                showErrorDialog(
                    getString(R.string.insufficientDetails),
                    getString(R.string.incorrectSignUpDetails),
                    getString(R.string.okButtonText)
                )
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

    private fun sendOTP(name: String, mobileNumber: String) {
        showLoader(true)
        mLoginAndRegistrationController?.doOTPCall(
            BuildConfig.HOST + java.lang.String.format(
                "user/generate-otp?phoneNumber=%s",
                mobileNumber
            )
        )
            ?.continueWithTask { task ->
                afterOTPSent(task, name, mobileNumber)
            }
    }

    private fun afterOTPSent(
        task: Task<Any>,
        name: String,
        mobileNumber: String
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

    override fun onResume() {
        super.onResume()
        showLoader(false)
    }
}
