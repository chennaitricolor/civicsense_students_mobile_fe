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
import com.gcc.smartcity.fontui.FontTextView
import com.gcc.smartcity.leaderboard.LeaderBoardModel
import com.gcc.smartcity.leaderboard.LeaderBoardRecyclerViewModel
import com.gcc.smartcity.preference.SessionStorage
import com.gcc.smartcity.userregistartion.controller.LoginController
import com.gcc.smartcity.userregistartion.model.LoginErrorModel
import com.gcc.smartcity.userregistartion.model.LoginModel
import com.gcc.smartcity.utils.Logger
import com.gcc.smartcity.utils.NetworkError
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
    private val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var forgotUserId: FontTextView? = null
    private var leaderBoardAdapterData = ArrayList<LeaderBoardRecyclerViewModel>()

    //    override fun onStart() {
////        super.onStart()
////        val br: BroadcastReceiver = LocationProviderChangedReceiver()
////        val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
////        registerReceiver(br, filter)
////    }

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
        forgotUserId = findViewById(R.id.forgotusername)

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
                showLoader(true)
                callLogin(loginEmail?.text.toString(), loginPassword?.text.toString())
            }
        }

        SignupBtnLogin.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            getLastLocation()
            startActivity(intent)
        }

        forgotUserId?.setOnClickListener {
            val intent = Intent(this, ForgotUserIdActivity::class.java)
            startActivity(intent)
        }
    }

    private fun callLogin(username: String, password: String) {
        mLoginController?.doLoginCall(BuildConfig.HOST + "user/login", username, password)
            ?.continueWithTask { task ->
                showLoader(false)
                postLogin(task)
            }
    }

    private fun postLogin(task: Task<Any>): Task<Any>? {
        if (task.isFaulted) {
            val loginErrorMessage =
                ((task.error as NetworkError).errorResponse as LoginErrorModel).message
            showErrorDialog(
                getString(R.string.signInErrorTitle),
                loginErrorMessage,
                getString(R.string.okButtonText)
            )
            task.makeVoid()
        } else {
            val loginModel = task.result as LoginModel
            Logger.d("HERE IN POST LOGIN")
            if (loginModel.success!!) {
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

//    private fun showLoader(status: Boolean) {
//        if (status) {
//            loader?.visibility = View.VISIBLE
//            loginScreen?.visibility = View.GONE
//            TouchUtility(this).enableUserInteraction(!status)
//        } else {
//            loader?.visibility = View.GONE
//            loginScreen?.visibility = View.VISIBLE
//            TouchUtility(this).enableUserInteraction(!status)
//        }
//    }

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
}
