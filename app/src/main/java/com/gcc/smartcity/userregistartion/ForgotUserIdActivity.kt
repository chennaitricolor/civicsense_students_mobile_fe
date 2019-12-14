package com.gcc.smartcity.userregistartion

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import bolts.Task
import com.gcc.smartcity.BaseActivity
import com.gcc.smartcity.BuildConfig
import com.gcc.smartcity.R
import com.gcc.smartcity.fontui.FontEditText
import com.gcc.smartcity.userregistartion.controller.RegistrationController
import com.gcc.smartcity.userregistartion.model.ForgotUserNameModel
import com.gcc.smartcity.userregistartion.model.userNameCheckModel
import kotlinx.android.synthetic.main.activity_forgot_user_id.*

class ForgotUserIdActivity : BaseActivity() {

    private var mRegistrationController: RegistrationController? = null
    private var isEmailValid: Boolean = false
    val emailPattern =
        "^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))\$"

    init {
        mRegistrationController = RegistrationController(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setView(R.layout.activity_forgot_user_id)
        forgotuserid_submit.setOnClickListener {
            showLoader(true)
            if (isEmailValid && forgotuserid_emailId?.text.toString().isNotEmpty()) {
                mRegistrationController?.forgotUserId(BuildConfig.HOST + "users/forgot-user-id?email=" + forgotuserid_emailId?.text)
                    ?.continueWith { task ->
                        validateTask(task)
                        showLoader(false)
                    }
            }
        }
        validateEmail()
    }

    fun validateTask(task: Task<Any>) {
        if (task.isFaulted) {
            showErrorDialog("Error", "Please give valid EmailID", "Ok")
        } else {

            val userNameModel = task.result as ForgotUserNameModel

            showErrorDialog("Your username", "" + userNameModel.userId, "Ok")
        }
    }

    fun validateEmail() {
        forgotuserid_emailId?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.matches((emailPattern).toRegex()) && s.isNotEmpty()) {
                    Log.d("success", "valid")
                    isEmailValid = true
                    forgotuserid_emailId?.setBackgroundResource(R.drawable.bg_border_edittext)
                } else if (s.isEmpty()) {
                    isEmailValid = false
                    forgotuserid_emailId?.setBackgroundResource(R.drawable.bg_border_edittext)
                } else {
                    Log.d("failure", "FAIL")
                    isEmailValid = false
                    forgotuserid_emailId?.setBackgroundResource(R.drawable.bg_border_edittext_wrong)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // other stuffs
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // other stuffs
            }
        })
    }

}
