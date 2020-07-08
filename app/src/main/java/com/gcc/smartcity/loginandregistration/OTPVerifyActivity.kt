package com.gcc.smartcity.loginandregistration

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import bolts.Task
import com.birjuvachhani.locus.Locus
import com.gcc.smartcity.BaseActivity
import com.gcc.smartcity.BuildConfig
import com.gcc.smartcity.R
import com.gcc.smartcity.dashboard.DashBoardActivity
import com.gcc.smartcity.fontui.FontEditText
import com.gcc.smartcity.leaderboard.LeaderBoardModel
import com.gcc.smartcity.loginandregistration.controller.LoginAndRegistrationController
import com.gcc.smartcity.loginandregistration.model.LoginErrorModel
import com.gcc.smartcity.loginandregistration.model.LoginModel
import com.gcc.smartcity.loginandregistration.model.SignUpErrorModel
import com.gcc.smartcity.loginandregistration.model.SignUpModel
import com.gcc.smartcity.preference.SessionStorage
import com.gcc.smartcity.user.UserModel
import com.gcc.smartcity.utils.AlertDialogBuilder
import com.gcc.smartcity.utils.Logger
import com.gcc.smartcity.utils.NetworkError
import com.gcc.smartcity.utils.OnSingleBtnDialogListener
import kotlinx.android.synthetic.main.activity_otpverify.*

class OTPVerifyActivity : BaseActivity(), OnSingleBtnDialogListener {

    private var mLoginAndRegistrationController: LoginAndRegistrationController? = null
    private var otpField: FontEditText? = null
    private var isOTPValid: Boolean = false
    private var userPersona: String? = null
    lateinit var name: String
    private lateinit var userMobileNumber: String
    private lateinit var fromScreen: String
    private var mLatitude: String? = null
    private var mLongitude: String? = null

    init {
        mLoginAndRegistrationController = LoginAndRegistrationController(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getLocation()

        setView(R.layout.activity_otpverify)

        otpField = findViewById(R.id.otpfield)

        if (intent.extras != null) {
            if (intent.hasExtra("userPersona")) {
                userPersona = intent.extras!!.getString("userPersona").toString()
            }
            userMobileNumber = intent.extras!!.getString("mobilenumber").toString()
            fromScreen = intent.extras!!.getString("fromScreen").toString()
            if (fromScreen == "signUpScreen") {
                name = intent.extras!!.getString("name").toString()
            }
        }

        val otpPattern =
            "^[0-9]\\d{3}\$"

        otpField?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.matches((otpPattern).toRegex()) && s.isNotEmpty()) {
                    Log.d("success", "valid")
                    isOTPValid = true
                    otpField?.setBackgroundResource(R.drawable.bg_border_edittext)
                } else {
                    Log.d("failure", "FAIL")
                    isOTPValid = false
                    otpField?.setBackgroundResource(R.drawable.bg_border_edittext_wrong)
                }
            }

            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
            }
        })

        VerifyBTN.setOnClickListener {
            if (otpField?.text.toString().isNotEmpty() && isOTPValid) {
                hideSoftKeyBoard()
                if (mLatitude != null && mLongitude != null) {
                    if (fromScreen == "signUpScreen") {
                        doRegistration(
                            name,
                            userMobileNumber,
                            userPersona,
                            otpField?.text.toString()
                        )
                    } else {
                        doLogin(userMobileNumber, userPersona, otpField?.text.toString())
                    }
                    showLoader(true)
                } else {
                    getLocation()
                }
            } else {
                Toast.makeText(this, "Please enter the OTP to proceed", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun getLocation() {
        Locus.getCurrentLocation(this) { result ->
            result.location?.let {
                mLatitude = it.latitude.toString()
                mLongitude = it.longitude.toString()
            }
        }
    }

    private fun doRegistration(
        name: String,
        mobileNumber: String,
        userPersona: String?,
        otp: String
    ) {
        mLoginAndRegistrationController?.doSignUpCall(
            BuildConfig.HOST + "user/signup",
            name,
            userPersona,
            mobileNumber,
            otp.toInt(),
            mLatitude!!,
            mLongitude!!
        )
            ?.continueWithTask { task ->
                afterRegistrationCall(mobileNumber, task)
            }
    }

    private fun doLogin(mobileNumber: String, userPersona: String?, otp: String) {
        mLoginAndRegistrationController?.doLoginCall(
            BuildConfig.HOST + "user/login",
            mobileNumber,
            otp.toInt(),
            userPersona
        )
            ?.continueWithTask { task ->
                afterLoginCall(mobileNumber, task)
            }
    }

    private fun afterRegistrationCall(mobileNumber: String, task: Task<Any>): Task<Any>? {
        if (task.isFaulted) {
            val signUpErrorMessage =
                ((task.error as NetworkError).errorResponse as SignUpErrorModel).message
            AlertDialogBuilder.getInstance().showErrorDialog(
                getString(R.string.tryAgainLater),
                signUpErrorMessage,
                getString(R.string.okButtonText),
                this,
                this
            )
            task.makeVoid()
            showLoader(false)
        } else {
            val signUpModel = task.result as SignUpModel
            if (signUpModel.success!!) {
                SessionStorage.getInstance().userId = mobileNumber
                try {
                    callUserEndpoint()
                    callLeaderBoardEndpoint()
                } catch (ex: Exception) {
                    Logger.d(ex.toString())
                }
            } else {
                showErrorDialog(
                    getString(R.string.unableToSignUp),
                    signUpModel.message,
                    getString(R.string.okButtonText)
                )
                showLoader(false)
            }
        }

        return null
    }

    private fun callUserEndpoint() {
        mLoginAndRegistrationController?.doUserCall(BuildConfig.HOST + "user")
            ?.continueWithTask { task ->
                postUserCall(task)
            }
    }

    private fun afterLoginCall(mobileNumber: String, task: Task<Any>): Task<Any>? {
        if (task.isFaulted) {
            val loginErrorMessage =
                ((task.error as NetworkError).errorResponse as LoginErrorModel).message
            showErrorDialog(
                getString(R.string.signInErrorTitle),
                loginErrorMessage,
                getString(R.string.okButtonText)
            )
            task.makeVoid()
            showLoader(false)
        } else {
            val loginModel = task.result as LoginModel
            if (loginModel.success!!) {
                SessionStorage.getInstance().userId = mobileNumber
                try {
                    callUserEndpoint()
                    callLeaderBoardEndpoint()
                } catch (ex: Exception) {
                    Logger.d(ex.toString())
                }
            } else {
                showErrorDialog(
                    getString(R.string.signInErrorTitle),
                    getString(R.string.useCorrectCredentialMessage),
                    getString(R.string.okButtonText)
                )
                showLoader(false)
            }
        }

        return null
    }

    private fun callLeaderBoardEndpoint() {
        mLoginAndRegistrationController?.doLeaderBoardCall(BuildConfig.HOST + "user/leaderboard?type=local")
            ?.continueWithTask { task ->
                postLeaderBoardCall(task)
            }
    }

    private fun postLeaderBoardCall(task: Task<Any>): Task<Any>? {
        if (task.isFaulted) {
            SessionStorage.getInstance().leaderBoardStatus = false
            val intent = Intent(this, DashBoardActivity::class.java)
            intent.clearStack()
            startActivity(intent)
            finish()
            task.makeVoid()
        } else {
            val leaderBoardModel = task.result as LeaderBoardModel
            if (leaderBoardModel.success!!) {
                SessionStorage.getInstance().leaderBoardModel = leaderBoardModel
                SessionStorage.getInstance().leaderBoardStatus = true
                val intent = Intent(this, DashBoardActivity::class.java)
                intent.clearStack()
                startActivity(intent)
                finish()
            } else {
                SessionStorage.getInstance().leaderBoardStatus = false
                val intent = Intent(this, DashBoardActivity::class.java)
                intent.clearStack()
                startActivity(intent)
                finish()
            }
        }

        return null
    }

    private fun postUserCall(task: Task<Any>): Task<Any>? {
        if (task.isFaulted) {
            task.makeVoid()
        } else {
            val userModel = task.result as UserModel
            try {
                SessionStorage.getInstance().userModel = userModel
            } catch (ex: Exception) {
                Logger.d(ex.toString())
            }
        }
        return null
    }

    override fun onSingleButtonClicked() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.clearStack()
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, LoginActivity::class.java)
        intent.clearStack()
        startActivity(intent)
        finish()
    }
}