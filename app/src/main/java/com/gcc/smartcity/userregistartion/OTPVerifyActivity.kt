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
import com.gcc.smartcity.preference.SessionStorage
import com.gcc.smartcity.userregistartion.controller.LoginController
import com.gcc.smartcity.userregistartion.controller.RegistrationController
import com.gcc.smartcity.userregistartion.model.LoginErrorModel
import com.gcc.smartcity.userregistartion.model.LoginModel
import com.gcc.smartcity.userregistartion.model.SignUpModel
import com.gcc.smartcity.utils.Logger
import com.gcc.smartcity.utils.NetworkError
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_otpverify.*


class OTPVerifyActivity : BaseActivity() {

    private var mLoginController: LoginController? = null
    private var mRegistrationController: RegistrationController? = null
    private var otpField: FontEditText? = null
    lateinit var name: String
    lateinit var userMobileNumber: String
    lateinit var fromScreen: String
    lateinit var mLatitude: String
    lateinit var mLongitude: String
    private val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    init {
        mLoginController = LoginController(this)
        mRegistrationController = RegistrationController(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setView(R.layout.activity_otpverify)
        otpField = findViewById(R.id.otpfield)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (intent.extras != null) {
            name = intent.extras!!.getString("name").toString()
            userMobileNumber = intent.extras!!.getString("mobilenumber").toString()
            fromScreen = intent.extras!!.getString("fromScreen").toString()
        }

        buttonEffect(VerifyBTN)

        VerifyBTN.setOnClickListener {
            if (otpField?.text.toString().isNotEmpty()) { //check if the right otp is entered or not and then call registration
                if(fromScreen == "signUpScreen") {
                    doRegistration(name, userMobileNumber, otpField?.text.toString())
                } else {
                    doLogin(userMobileNumber, otpField?.text.toString())
                }
            }
            showLoader(true)
        }
        getLastLocation()
    }

    private fun doRegistration(name: String, mobileNumber: String, otp: String) {
        mRegistrationController?.doSignUpCall(
            BuildConfig.HOST + "user/signup",
            name,
            mobileNumber,
            otp.toInt(),
            "12.888370",
            "80.227051"
        )
            ?.continueWithTask { task ->
                afterRegistrationCall(mobileNumber, task)
            }
    }

    private fun doLogin(mobileNumber: String, otp: String) {
        mLoginController?.doLoginCall(
            BuildConfig.HOST + "user/login",
            mobileNumber,
            otp.toInt()
        )
            ?.continueWithTask { task ->
                afterLoginCall(mobileNumber, task)
            }
    }

    private fun afterRegistrationCall(mobileNumber: String, task: Task<Any>): Task<Any>? {
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
                Toast.makeText(this, "SignedUp successfully", Toast.LENGTH_SHORT).show()
                SessionStorage.getInstance().userId = mobileNumber
                try {
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
            }
            showLoader(false)
        }

        return null
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
            if (!loginModel.success!!) {
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
        mLoginController?.doLeaderBoardCall(BuildConfig.HOST + "user/leaderboard?type=local")
            ?.continueWithTask { task ->
                postLeaderBoard(task)
            }
    }

    private fun postLeaderBoard(task: Task<Any>): Task<Any>? {
        if (task.isFaulted) {
            SessionStorage.getInstance().leaderBoardStatus = false
            val intent = Intent(this, DashBoardActivity::class.java)
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
                    startActivity(intent)
                    finish()
                } catch (ex: Exception) {
                    Logger.d(ex.toString())
                }
            } else {
                SessionStorage.getInstance().leaderBoardStatus = false
                val intent = Intent(this, DashBoardActivity::class.java)
                startActivity(intent)
                finish()
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