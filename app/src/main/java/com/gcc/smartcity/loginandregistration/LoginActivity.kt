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
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
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
import com.gcc.smartcity.loginandregistration.model.OTPModel
import com.gcc.smartcity.preference.SessionStorage
import com.gcc.smartcity.utils.Logger
import com.gcc.smartcity.utils.NetworkError
import com.google.android.gms.location.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : BaseActivity() {

    private var mLoginAndRegistrationController: LoginAndRegistrationController? = null
    private var loader: LinearLayout? = null
    private var loginScreen: RelativeLayout? = null
    private var mobileNumber: FontEditText? = null
    private var isMobileNumberValid: Boolean = false
    private val PERMISSION_ID = 42
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var firebaseRemoteConfig: FirebaseRemoteConfig

    init {
        mLoginAndRegistrationController = LoginAndRegistrationController(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isValidSession()) {
            showLoader(true)
            callLeaderBoardEndpoint()
        } else {
            setView(R.layout.activity_login)

            loader = findViewById(R.id.loader_layout)
            loginScreen = findViewById(R.id.login_screen)
            mobileNumber = findViewById(R.id.mobileNumber)

            buttonEffect(getOTP)

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

                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            })

            getOTP.setOnClickListener {
                hideSoftKeyBoard()
                if (mobileNumber?.text.toString().isNotEmpty() && isMobileNumberValid) {
                    sendOTP(mobileNumber?.text.toString())
                } else {
                    showErrorDialog(
                        getString(R.string.insufficientDetails),
                        getString(R.string.incorrectSignUpDetails),
                        getString(R.string.okButtonText)
                    )
                }
            }

            SignupBtnLogin.setOnClickListener {
                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
            }
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        getLastLocation()
        getFirebaseRemoteConfigData()

    }

    private fun isValidSession(): Boolean {
        return (SessionStorage.getInstance().userId != null
                && SessionStorage.getInstance().userId.trim() != ""
                && SessionStorage.getInstance().sessionCookies != null
                && SessionStorage.getInstance().sessionCookies != "")
    }

    private fun sendOTP(mobileNumber: String) {
        showLoader(true)
        mLoginAndRegistrationController?.doOTPCall(
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
                val intent = Intent(this, OTPVerifyActivity::class.java)
                intent.putExtra("mobilenumber", mobileNumber)
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

    private fun Intent.clearStack() {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
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
        showLoader(false)
        return null
    }

    private fun buttonEffect(button: View) {
        button.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.background.setColorFilter(
                        Color.parseColor("#d4993d"),
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

    override fun onResume() {
        super.onResume()
        showLoader(false)
    }
}
