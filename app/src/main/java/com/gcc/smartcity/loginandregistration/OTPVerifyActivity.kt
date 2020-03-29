package com.gcc.smartcity.loginandregistration

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
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import bolts.Task
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
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_otpverify.*

class OTPVerifyActivity : BaseActivity(), OnSingleBtnDialogListener {

    private var mLoginAndRegistrationController: LoginAndRegistrationController? = null
    private var otpField: FontEditText? = null
    lateinit var name: String
    lateinit var userMobileNumber: String
    lateinit var fromScreen: String
    private var mLatitude: String? = null
    private var mLongitude: String? = null
    private val PERMISSION_ID = 42
    private lateinit var lastLocation: Location
    private lateinit var locationCallback: LocationCallback
    private lateinit var mLocationRequest: LocationRequest
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    init {
        mLoginAndRegistrationController = LoginAndRegistrationController(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setView(R.layout.activity_otpverify)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                lastLocation = p0.lastLocation
                mLatitude = lastLocation.latitude.toString()
                mLongitude = lastLocation.longitude.toString()
            }
        }

        getLastLocation()

        otpField = findViewById(R.id.otpfield)

        if (intent.extras != null) {
            userMobileNumber = intent.extras!!.getString("mobilenumber").toString()
            fromScreen = intent.extras!!.getString("fromScreen").toString()
            if (fromScreen == "signUpScreen") {
                name = intent.extras!!.getString("name").toString()
            }
        }

        buttonEffect(VerifyBTN)

        VerifyBTN.setOnClickListener {
            if (otpField?.text.toString().isNotEmpty()) {
                if (fromScreen == "signUpScreen") {
                    doRegistration(name, userMobileNumber, otpField?.text.toString())
                } else {
                    doLogin(userMobileNumber, otpField?.text.toString())
                }
            }
            showLoader(true)
        }
    }

    private fun doRegistration(name: String, mobileNumber: String, otp: String) {
        if (mLatitude != null && mLongitude != null) {
            mLoginAndRegistrationController?.doSignUpCall(
                BuildConfig.HOST + "user/signup",
                name,
                mobileNumber,
                otp.toInt(),
                mLatitude!!,
                mLongitude!!
            )
                ?.continueWithTask { task ->
                    afterRegistrationCall(mobileNumber, task)
                }
        } else {
            getLastLocation()
            Toast.makeText(this, getString(R.string.unableToGetYourLocation), Toast.LENGTH_LONG)
                .show()
        }

    }

    private fun doLogin(mobileNumber: String, otp: String) {
        if (mLatitude != null && mLongitude != null) {
            mLoginAndRegistrationController?.doSignUpCall(
                BuildConfig.HOST + "user/signup",
                "Guest",
                mobileNumber,
                otp.toInt(),
                mLatitude!!,
                mLongitude!!
            )
                ?.continueWithTask { task ->
                    afterRegistrationCall(mobileNumber, task)
                }
        } else {
            getLastLocation()
            Toast.makeText(this, getString(R.string.unableToGetYourLocation), Toast.LENGTH_LONG)
                .show()
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
            showLoader(false)
            task.makeVoid()
        } else {
            val loginModel = task.result as LoginModel
            Logger.d("HERE IN POST LOGIN")
            if (loginModel.success!!) {
                SessionStorage.getInstance().userId = mobileNumber
                try {
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
                postLeaderBoard(task)
            }
    }

    private fun Intent.clearStack() {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    private fun postLeaderBoard(task: Task<Any>): Task<Any>? {
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
                try {
                    SessionStorage.getInstance().leaderBoardModel = leaderBoardModel
                    SessionStorage.getInstance().leaderBoardStatus = true
                    val intent = Intent(this, DashBoardActivity::class.java)
                    intent.clearStack()
                    startActivity(intent)
                    finish()
                } catch (ex: Exception) {
                    Logger.d(ex.toString())
                }
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
        mLocationRequest = LocationRequest()
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

    override fun onSingleButtonClicked() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.clearStack()
        startActivity(intent)
        finish()
    }
}