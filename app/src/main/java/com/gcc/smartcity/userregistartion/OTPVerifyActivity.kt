package com.gcc.smartcity.userregistartion

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import bolts.Task
import com.gcc.smartcity.BaseActivity
import com.gcc.smartcity.BuildConfig
import com.gcc.smartcity.R
import com.gcc.smartcity.fontui.FontEditText
import com.gcc.smartcity.userregistartion.controller.RegistrationController
import com.gcc.smartcity.userregistartion.model.OTPModel
import com.gcc.smartcity.userregistartion.model.SignUpModel
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_otpverify.*


class OTPVerifyActivity : BaseActivity() {

    private var mRegistrationController: RegistrationController? = null
    private var mobileNumber: FontEditText? = null
    private var isMobileNumberValid: Boolean = false
    lateinit var name: String
    lateinit var email: String
    lateinit var password: String
    lateinit var dob: String
    lateinit var username: String
    lateinit var userMobileNumber: String
    lateinit var mLatitude: String
    lateinit var mLongitude: String
    private val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    init {
        mRegistrationController = RegistrationController(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setView(R.layout.activity_otpverify)
        setInitialOTPLayout()
        mobileNumber = findViewById(R.id.mobilenumber)
        val mobileNumberPattern =
            "^[6-9]\\d{9}\$"

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (intent.extras != null) {
            name = intent.extras!!.getString("name").toString()
            email = intent.extras!!.getString("email").toString()
            password = intent.extras!!.getString("password").toString()
            dob = intent.extras!!.getString("dob").toString()
            username = intent.extras!!.getString("username").toString()
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

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // other stuffs
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // other stuffs
            }
        })

        buttonEffect(VerifyBTN)

        VerifyBTN.setOnClickListener {
            hideSoftKeyBoard()
            if (VerifyBTN.text == "Get OTP") {
                if (isMobileNumberValid && mobileNumber?.text.toString().isNotEmpty()) {
                    userMobileNumber = mobileNumber?.text.toString()
                    sendOTP(mobileNumber?.text.toString())
                    setMobileOtpLayout(mobileNumber?.text.toString())
                    mobileNumber?.text?.clear()
                } else {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.incorrectMobileNumberMessage),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                if (mobileNumber?.text.toString().isNotEmpty()) {
                    doRegistration(userMobileNumber, mobileNumber?.text.toString())
                }
            }
            showLoader(true)
        }
        getLastLocation()
    }

    private fun doRegistration(mobileNumber: String, otp: String) {
        mRegistrationController?.doSignUpCall(
            BuildConfig.HOST + "user",
            name,
            mobileNumber,
            password,
            username,
            email,
            dob,
            6,
            otp.toInt(),
            "12.888370",
            "80.227051"
        )
            ?.continueWithTask { task ->
                afterRegistrationCall(task)
            }
    }

    private fun afterRegistrationCall(task: Task<Any>): Task<Any>? {
        if (task.isFaulted) {
            showErrorDialog(
                getString(R.string.unableToSignUp),
                getString(R.string.tryAgainLater),
                getString(R.string.okButtonText)
            )
            task.makeVoid()
            showLoader(false)
        } else {
            val signUpModel = task.result as SignUpModel
            if (signUpModel.success!!) {
                showErrorDialog(
                    getString(R.string.successfulSignUp),
                    getString(R.string.loginRedirectMessage),
                    getString(R.string.okButtonText)
                )
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            } else {
                showErrorDialog(
                    getString(R.string.unableToSignUp),
                    signUpModel.message,
                    getString(R.string.okButtonText)
                )
            }
            showLoader(false)
        }

        return null
    }

    private fun sendOTP(mobileNumber: String) {
        mRegistrationController?.doOTPCall(
            BuildConfig.HOST + java.lang.String.format(
                "user/generate-otp?phoneNumber=%s",
                mobileNumber
            )
        )
            ?.continueWithTask { task ->
                afterOTPSent(task, mobileNumber)
            }
    }

    private fun afterOTPSent(task: Task<Any>, mobileNumber: String): Task<Any>? {
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
                setMobileOtpLayout(mobileNumber)
            } else {
                showErrorDialog(
                    getString(R.string.unableToSendOTP),
                    getString(R.string.tryAgainLater),
                    getString(R.string.okButtonText)
                )
            }
            showLoader(false)
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

    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        Log.d("Old Latitude", location.latitude.toString())
                        Log.d("Old Longitude", location.longitude.toString())
                        mLatitude = location.latitude.toString()
                        mLongitude = location.longitude.toString()
                    }
                }
            } else {
                Toast.makeText(this, getString(R.string.turnOnLocationMessage), Toast.LENGTH_LONG)
                    .show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            Log.d("New Latitude", mLastLocation.latitude.toString())
            Log.d("New Longitude", mLastLocation.longitude.toString())
            mLatitude = mLastLocation.latitude.toString()
            mLongitude = mLastLocation.longitude.toString()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }
}