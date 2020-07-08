package com.gcc.smartcity.loginandregistration

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import bolts.Task
import com.birjuvachhani.locus.Locus
import com.gcc.smartcity.BaseActivity
import com.gcc.smartcity.BuildConfig
import com.gcc.smartcity.R
import com.gcc.smartcity.dashboard.DashBoardActivity
import com.gcc.smartcity.leaderboard.LeaderBoardModel
import com.gcc.smartcity.loginandregistration.controller.LoginAndRegistrationController
import com.gcc.smartcity.loginandregistration.model.LoginErrorModel
import com.gcc.smartcity.loginandregistration.model.LoginModel
import com.gcc.smartcity.preference.SessionStorage
import com.gcc.smartcity.user.UserModel
import com.gcc.smartcity.utils.Logger
import com.gcc.smartcity.utils.NetworkError
import kotlinx.android.synthetic.main.activity_password.*


class PasswordActivity : BaseActivity() {

    private var mLatitude: String? = null
    private var mLongitude: String? = null
    private lateinit var userMobileNumber: String
    private lateinit var userPersona: String
    private var mLoginAndRegistrationController: LoginAndRegistrationController? = null

    init {
        mLoginAndRegistrationController = LoginAndRegistrationController(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getLocation()

        setView(R.layout.activity_password)

        if (intent.extras != null) {
            userMobileNumber = intent.extras!!.getString("mobilenumber").toString()
            userPersona = intent.extras!!.getString("userPersona").toString()
        }

        show_pass_btn.setOnClickListener {
            showHidePass(it)
        }

        loginButton.setOnClickListener {
            if (passwordField?.text.toString().isNotEmpty()) {
                hideSoftKeyBoard()
                if (mLatitude != null && mLongitude != null) {
                    doLogin(userMobileNumber, userPersona, passwordField.text.toString())
                    showLoader(true)
                } else {
                    getLocation()
                }
            } else {
                Toast.makeText(this, "Please enter your password", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun showHidePass(view: View) {
        if (view.id === R.id.show_pass_btn) {
            if (passwordField.transformationMethod == PasswordTransformationMethod.getInstance()
            ) {
                (view as ImageView).setImageResource(R.drawable.ic_visibility_off)
                //Show Password
                passwordField.transformationMethod = HideReturnsTransformationMethod.getInstance()
                passwordField.placeCursorToEnd()
            } else {
                (view as ImageView).setImageResource(R.drawable.ic_visibility)
                //Hide Password
                passwordField.transformationMethod = PasswordTransformationMethod.getInstance()
                passwordField.placeCursorToEnd()
            }
        }
    }

    private fun EditText.placeCursorToEnd() {
        this.setSelection(this.text.length)
    }

    private fun getLocation() {
        Locus.getCurrentLocation(this) { result ->
            result.location?.let {
                mLatitude = it.latitude.toString()
                mLongitude = it.longitude.toString()
            }
        }
    }

    private fun doLogin(mobileNumber: String, userPersona: String, password: String) {
        mLoginAndRegistrationController?.doLoginCallWithPassword(
            BuildConfig.HOST + "persona/login",
            mobileNumber,
            userPersona,
            password
        )?.continueWithTask { task ->
            afterLoginCall(mobileNumber, task)
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

    private fun callUserEndpoint() {
        mLoginAndRegistrationController?.doUserCall(BuildConfig.HOST + "user")
            ?.continueWithTask { task ->
                postUserCall(task)
            }
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
}