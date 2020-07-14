package com.gcc.smartcity.loginandregistration

import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import bolts.Task
import com.birjuvachhani.locus.Locus
import com.gcc.smartcity.BaseActivity
import com.gcc.smartcity.BuildConfig
import com.gcc.smartcity.R
import com.gcc.smartcity.dashboard.DashBoardActivity
import com.gcc.smartcity.fontui.FontEditText
import com.gcc.smartcity.leaderboard.LeaderBoardModel
import com.gcc.smartcity.location.LocationErrorModel
import com.gcc.smartcity.location.LocationModel
import com.gcc.smartcity.loginandregistration.controller.LoginAndRegistrationController
import com.gcc.smartcity.loginandregistration.model.OTPModel
import com.gcc.smartcity.maintenance.MaintenanceActivity
import com.gcc.smartcity.preference.SessionStorage
import com.gcc.smartcity.utils.Logger
import com.gcc.smartcity.utils.NetworkError
import com.gcc.smartcity.webview.WebViewActivity
import com.google.firebase.crashlytics.internal.common.CommonUtils.hideKeyboard
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : BaseActivity() {

    private var mLoginAndRegistrationController: LoginAndRegistrationController? = null
    private var loginLoader: LinearLayout? = null
    private var loginScreen: RelativeLayout? = null
    private var mobileNumber: FontEditText? = null
    private var isMobileNumberValid: Boolean = false
    private var mLatitude: String? = null
    private var mLongitude: String? = null
    private var isValidLocation: Boolean = false
    private lateinit var spinnerList: ArrayList<Spinner>
    private lateinit var firebaseRemoteConfig: FirebaseRemoteConfig

    init {
        mLoginAndRegistrationController = LoginAndRegistrationController(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getFirebaseRemoteConfigData()

        setView(R.layout.activity_login)

        loginLoader = findViewById(R.id.login_loader_layout)
        loginScreen = findViewById(R.id.login_screen)
        mobileNumber = findViewById(R.id.mobileNumber)
        spinnerList = ArrayList()

        if (BuildConfig.PERSONA) {
            proceedButton.visibility = View.GONE
            val persona =
                SessionStorage.getInstance().rootModel.region?.regionsMap?.get(BuildConfig.CITY)?.persona
            if (!persona.isNullOrEmpty()) {
                setupDropDown(persona)
                personaContainer.visibility = View.VISIBLE

                if (persona.contains("HQIMS Volunteer")) {
                    hqimsBanner.visibility = View.VISIBLE
                } else {
                    hqimsBanner.visibility = View.GONE
                }
            }
        } else {
            personaContainer.visibility = View.GONE
            proceedButton.text = "Get OTP"
            proceedButton.visibility = View.VISIBLE
        }

        if (!BuildConfig.CONTAINMENTZONE) {
            containmentZoneBanner.visibility = View.GONE
        } else {
            containmentZoneBanner.visibility = View.VISIBLE
        }
        val mobileNumberPattern =
            "^[6-9]\\d{9}\$"

        hqimsBanner?.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.HQIMSHOST))
            ContextCompat.startActivity(this, browserIntent, null)
        }

        containmentZoneBanner?.setOnClickListener {
            val intent = WebViewActivity.newIntent(this, BuildConfig.WEBVIEWHOST + "hotzones")
            startActivity(intent)
        }

        persona_dropdown.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                when ((selectedItemView as AppCompatTextView).text) {
                    "Tap to select" -> {
                        proceedButton.visibility = View.GONE
                    }
                    "Citizen" -> {
                        proceedButton.text = "Get OTP"
                        proceedButton.visibility = View.VISIBLE
                    }
                    else -> {
                        proceedButton.text = "Next"
                        proceedButton.visibility = View.VISIBLE
                    }
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // your code here
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

        proceedButton.setOnClickListener {
            hideSoftKeyBoard()
            if (BuildConfig.PERSONA) {
                if (persona_dropdown.selectedItem.toString() == "Tap to select") {
                    Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    if (mobileNumber?.text.toString()
                            .isNotEmpty() && isMobileNumberValid && persona_dropdown.selectedItem.toString() == "Citizen"
                    ) {
                        sendOTP(
                            mobileNumber?.text.toString(),
                            persona_dropdown.selectedItem.toString()
                        )
                    } else if (mobileNumber?.text.toString()
                            .isNotEmpty() && isMobileNumberValid && persona_dropdown.selectedItem.toString() != "Citizen"
                    ) {
                        val intent = Intent(this, PasswordActivity::class.java)
                        intent.putExtra("mobilenumber", mobileNumber?.text.toString())
                        intent.putExtra("userPersona", persona_dropdown.selectedItem.toString())
                        startActivity(intent)
                    } else {
                        showErrorDialog(
                            getString(R.string.insufficientDetails),
                            getString(R.string.incorrectSignUpDetails),
                            getString(R.string.okButtonText)
                        )
                    }
                }
            } else {
                if (mobileNumber?.text.toString().isNotEmpty() && isMobileNumberValid) {
                    sendOTP(
                        mobileNumber?.text.toString(), null
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

        SignupBtnLogin.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        loginLoader?.visibility = View.VISIBLE
    }

    override fun onPause() {
        super.onPause()
        loginLoader?.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        getLocation()
    }

    private fun getLocation() {
        Locus.getCurrentLocation(this) { result ->
            result.location?.let {
                mLatitude = it.latitude.toString()
                mLongitude = it.longitude.toString()
                userLocationValidation()
            }
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v: View? = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    hideKeyboard(this, v)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private fun setupDropDown(dropdownData: ArrayList<String>?) {
        val dropDown = findViewById<Spinner>(R.id.persona_dropdown)
        dropdownData?.add(0, "Tap to select")
        val arrayAdapter = dropdownData?.toArray()?.let {
            ArrayAdapter(
                this, android.R.layout.simple_spinner_item, it
            )
        }
        arrayAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dropDown.adapter = arrayAdapter
        spinnerList.add(dropDown)
    }

    private fun userSessionValidation() {
        if (isValidSession()) {
            callLeaderBoardEndpoint()
        } else {
            loginLoader?.visibility = View.GONE
        }
    }

    private fun userLocationValidation() {
        if (mLatitude != null && mLongitude != null) {
            mLoginAndRegistrationController?.doUserLocationValidationCall(
                BuildConfig.HOST + java.lang.String.format(
                    "user/valid?coordinates=%s&coordinates=%s",
                    mLongitude, mLatitude
                )
            )?.continueWithTask { task ->
                afterUserLocationValidationCall(task)
            }
        } else {
            loginLoader?.visibility = View.GONE
            callMaintenanceActivity()
        }
    }

    private fun callMaintenanceActivity(userLocationValidationErrorMessage: String? = "We do not support your location yet") {
        val intent = Intent(this, MaintenanceActivity::class.java)
        if (!userLocationValidationErrorMessage.isNullOrBlank()) {
            intent.putExtra(
                "userLocationValidationErrorMessage",
                userLocationValidationErrorMessage
            )
            intent.putExtra(
                "reason",
                "userLocationInvalid"
            )
            intent.putExtra(
                "header",
                "Unsupported Location"
            )
        }
        intent.clearStack()
        startActivity(intent)
        finish()
    }

    private fun afterUserLocationValidationCall(task: Task<Any>): Task<Any>? {
        if (task.isFaulted) {
            val userLocationValidationErrorMessage =
                ((task.error as NetworkError).errorResponse as LocationErrorModel).message
            loginLoader?.visibility = View.GONE
            callMaintenanceActivity(userLocationValidationErrorMessage)
            task.makeVoid()
        } else {
            val locationModel = task.result as LocationModel
            isValidLocation = locationModel.success!!
            if (isValidLocation) {
                userSessionValidation()
            } else {
                callMaintenanceActivity()
            }
        }

        return null
    }

    private fun isValidSession(): Boolean {
        return (SessionStorage.getInstance().userId != null
                && SessionStorage.getInstance().userId.trim() != ""
                && SessionStorage.getInstance().sessionCookies != null
                && SessionStorage.getInstance().sessionCookies != "")
    }

    private fun sendOTP(mobileNumber: String, userPersona: String?) {
        loginLoader?.visibility = View.VISIBLE
        mLoginAndRegistrationController?.doOTPCall(
            BuildConfig.HOST + java.lang.String.format(
                "user/generate-otp?phoneNumber=%s",
                mobileNumber
            )
        )
            ?.continueWithTask { task ->
                afterOTPSent(task, mobileNumber, userPersona)
            }
    }

    private fun afterOTPSent(
        task: Task<Any>,
        mobileNumber: String,
        userPersona: String?
    ): Task<Any>? {
        if (task.isFaulted) {
            showErrorDialog(
                getString(R.string.unableToSendOTP),
                getString(R.string.tryAgainLater),
                getString(R.string.okButtonText)
            )
            task.makeVoid()
            loginLoader?.visibility = View.GONE
        } else {
            val otpModel = task.result as OTPModel
            if (otpModel.success!!) {
                val intent = Intent(this, OTPVerifyActivity::class.java)
                intent.putExtra("mobilenumber", mobileNumber)
                if (userPersona != null) {
                    intent.putExtra("userPersona", userPersona)
                }
                intent.putExtra("fromScreen", "loginScreen")
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

    private fun getFirebaseRemoteConfigData() {
        Log.d("Firebase----->>", "token " + FirebaseInstanceId.getInstance().token)
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            val newToken = it.token
            Log.e("Firebase", newToken)
        }.addOnFailureListener {
            Logger.d(it.toString())
        }
    }

    private fun callLeaderBoardEndpoint() {
        mLoginAndRegistrationController?.doLeaderBoardCall(BuildConfig.HOST + "user/leaderboard?type=local")
            ?.continueWithTask { task ->
                postLeaderBoard(task)
            }
    }

    private fun postLeaderBoard(task: Task<Any>): Task<Any>? {
        if (task.isFaulted) {
            if ((task.error as NetworkError).errorCode == 401) {
                Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_LONG)
                    .show()
                SessionStorage.getInstance().userId = null
                SessionStorage.getInstance().sessionCookies = null
                val intent = Intent(this, LoginActivity::class.java)
                intent.clearStack()
                startActivity(intent)
                finish()
                task.makeVoid()
            } else {
                SessionStorage.getInstance().leaderBoardStatus = false
                val intent = Intent(this, DashBoardActivity::class.java)
                intent.clearStack()
                startActivity(intent)
                finish()
                task.makeVoid()
            }
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
//        loginLoader?.visibility = View.GONE
        return null
    }
}
