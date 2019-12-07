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
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
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
import com.gcc.smartcity.userregistartion.controller.LoginController
import com.gcc.smartcity.userregistartion.model.LoginModel
import com.gcc.smartcity.utils.Logger
import com.gcc.smartcity.utils.TouchUtility
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {

    private var mLoginController: LoginController? = null
    private var loginEmail: FontEditText? = null
    private var isLoginEmailValid: Boolean = false
    private var loginPassword: FontEditText? = null
    private var isLoginPasswordStrengthValid: Boolean = false
    private var loader: LinearLayout? = null
    private var loginScreen: RelativeLayout? = null
    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    //    override fun onStart() {
//        super.onStart()
//        val br: BroadcastReceiver = LocationProviderChangedReceiver()
//        val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
//        registerReceiver(br, filter)
//    }
    init {
        mLoginController = LoginController(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setView(R.layout.activity_login)

        loginEmail = findViewById(R.id.emailId)
        loginPassword = findViewById(R.id.password)
        loader = findViewById(R.id.loader_layout)
        loginScreen = findViewById(R.id.login_screen)

        showLoader(false)
        showVisiblePasswordButton(false)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        buttonEffect(LoginBtn)

        val passwordPattern =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$"

        loginPassword?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.toString().isNotEmpty()) {
                    showVisiblePasswordButton(true)
                } else {
                    showVisiblePasswordButton(false)
                }

                if (s.matches((passwordPattern).toRegex()) && s.isNotEmpty()) {
                    Log.d("success", "valid")
                    isLoginPasswordStrengthValid = true
                    loginPassword?.setBackgroundResource(R.drawable.bg_border_edittext)
                } else if (s.isEmpty()) {
                    isLoginPasswordStrengthValid = false
                    loginPassword?.setBackgroundResource(R.drawable.bg_border_edittext)
                } else {
                    Log.d("failure", "FAIL")
                    isLoginPasswordStrengthValid = false
                    loginPassword?.setBackgroundResource(R.drawable.bg_border_edittext_wrong)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // other stuffs
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // other stuffs
            }
        })

        LoginBtn.setOnClickListener {
            if (loginEmail?.text!!.isNotEmpty() && loginPassword?.text!!.isNotEmpty()) {
                callLogin(loginEmail?.text.toString(), loginPassword?.text.toString())
            }
        }

        SignupBtnLogin.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            showLoader(true)
            getLastLocation()
            startActivity(intent)
        }
    }

    private fun callLogin(username: String, password: String) {
        mLoginController?.doLoginCall(BuildConfig.HOST + "user/login", username, password)
            ?.continueWithTask { task ->
                postLogin(task)
            }
    }

    private fun postLogin(task: Task<Any>): Task<Any>? {
        if (task.isFaulted) {
//            isUserInteractionEnable(true)
//            val networkError = task.error as NetworkError
            showErrorDialog("Sign In Error", "Please try again.", "OK")
            task.makeVoid()
        } else {
            val loginModel = task.result as LoginModel
            Logger.d("HERE IN POST LOGIN")
            if(loginModel.success!!) {
                try {
                    val intent = Intent(this, DashBoardActivity::class.java)
                    startActivity(intent)
                    finish()
                } catch (ex: Exception) {
                    Logger.d(ex.toString())
                }
            } else {
                Toast.makeText(this, "Please use the correct credentials", Toast.LENGTH_LONG).show()
            }
        }

        return null
    }

    private fun isUserInteractionEnable(status: Boolean) {
        if (status) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        }
//        makeSignInProgressVisible(!status)
    }

//    private fun makeSignInProgressVisible(status: Boolean) {
//        if (status) {
//            login_pgSignin.visibility = View.VISIBLE
//            login_llSignin.setBackgroundResource(R.drawable.bg_rounded_sigin_grey)
//        } else {
//            login_pgSignin.visibility = View.GONE
//            login_llSignin.setBackgroundResource(R.drawable.bg_rounded_sigin_dark)
//        }
//    }

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

    override fun onResume() {
        super.onResume()
        showLoader(false)
    }

    private fun showVisiblePasswordButton(status: Boolean) {
        if (status) {
            login_RDBTNPasswordshow.visibility = View.VISIBLE
        } else {
            login_RDBTNPasswordshow.visibility = View.INVISIBLE

        }
        login_RDBTNPasswordshow.setOnCheckedChangeListener { _, b ->
            if (b) {
                loginPassword?.transformationMethod = PasswordTransformationMethod.getInstance()
                loginPassword?.setSelection(loginPassword!!.text?.length!!)
            } else {
                loginPassword?.transformationMethod = HideReturnsTransformationMethod.getInstance()
                loginPassword?.setSelection(loginPassword!!.text?.length!!)
            }
        }
    }

    private fun showLoader(status: Boolean) {
        if (status) {
            loader?.visibility = View.VISIBLE
            loginScreen?.visibility = View.GONE
            TouchUtility(this).enableUserInteraction(!status)
        } else {
            loader?.visibility = View.GONE
            loginScreen?.visibility = View.VISIBLE
            TouchUtility(this).enableUserInteraction(!status)
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
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
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
}
