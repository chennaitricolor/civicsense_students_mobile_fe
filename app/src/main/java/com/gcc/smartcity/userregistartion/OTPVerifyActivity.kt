package com.gcc.smartcity.userregistartion

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.gcc.smartcity.BaseActivity
import com.gcc.smartcity.R
import com.gcc.smartcity.dashboard.DashBoardActivity
import com.gcc.smartcity.fontui.FontEditText
import kotlinx.android.synthetic.main.activity_otpverify.*


class OTPVerifyActivity : BaseActivity() {


    private var mobileNumber: FontEditText? = null
    private var isMobileNumberValid: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setView(R.layout.activity_otpverify)
        setInitialOTPLayout()
        mobileNumber = findViewById(R.id.mobilenumber)
        val mobileNumberPattern =
            "^[6-9]\\d{9}\$"
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

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // other stuffs
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // other stuffs
            }
        })

        buttonEffect(VerifyBTN)

        VerifyBTN.setOnClickListener {
            if (VerifyBTN.text == "Get OTP") {
                if (isMobileNumberValid && mobileNumber?.text.toString().isNotEmpty()) {
                    setMobileOtpLayout(mobileNumber?.text.toString())
                    mobileNumber?.text?.clear()
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Please enter the correct mobile number",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                val intent = Intent(this, DashBoardActivity::class.java)
                startActivity(intent)
            }
        }

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

    private fun setInitialOTPLayout() {
        setTextToLayout(
            "We will send you an <b>One Time Password</b>\n" +
                    "to verify this mobile number", "Mobile", "Get OTP"
        )
    }

    private fun setTextToLayout(desc: String, title: String, btnTxt: String) {
        otpDesc.text = Html.fromHtml(desc)
        titleTxt.text = title
        VerifyBTN.text = btnTxt
    }

    private fun setMobileOtpLayout(mobileNumber: String) {
        setTextToLayout(
            "Enter the OTP send to <b>+91-$mobileNumber</b>\n", "OTP", "Verify & Proceed"
        )
    }
}