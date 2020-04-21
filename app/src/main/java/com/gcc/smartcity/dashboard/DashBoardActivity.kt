package com.gcc.smartcity.dashboard

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import androidx.core.app.ActivityCompat
import bolts.Task
import com.gcc.smartcity.BuildConfig
import com.gcc.smartcity.navigationdrawer.NavigationDrawerActivity
import com.gcc.smartcity.R
import com.gcc.smartcity.loginandregistration.LoginActivity
import com.gcc.smartcity.utils.Logger
import com.gcc.smartcity.utils.NetworkError
import com.gcc.smartcity.utils.OnDialogListener
import com.gcc.smartcity.webview.WebViewActivity
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.activity_dashboard.containmentZoneBanner
import kotlinx.android.synthetic.main.activity_login.*
import kotlin.math.min

class DashBoardActivity : NavigationDrawerActivity(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener, OnDialogListener, MissionAPIListener {

    override fun onFail(message: String, task: Task<Any>) {
        val error = task.error as NetworkError
        if (error.errorCode == 401) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.clearStack()
            startActivity(intent)
            finish()
        } else {
            showLoader(false)
            showErrorDialog(
                getString(R.string.tryAgainLater),
                message,
                getString(R.string.okButtonText)
            )
        }
    }

    override fun onSuccess(missionModel: ArrayList<MissionModel>) {
        showLoader(false)
        configureMissionList(missionModel)
    }

    override fun shouldShowNavigationDrawer(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun shouldShowToolbarBackButton(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mSettingsClient: SettingsClient
    private lateinit var lastLocation: Location
    private var lastMockLocation: Location? = null
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false
    private var currentLocationMarker: Marker? = null
    private var numGoodReadings: Int = 0
    private val DEVELOPER_OPTIONS_REQUEST_CODE = 1010
    private var locationAccuracyCircle: Circle? = null
    private var hasMissionListPopulated: Boolean = true


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setMainContentView(R.layout.activity_dashboard)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mSettingsClient = LocationServices.getSettingsClient(this)

        buttonEffect(containmentZoneBanner)

        containmentZoneBanner?.setOnClickListener {
            Logger.d("Containment Zones")
            val intent = WebViewActivity.newIntent(this, BuildConfig.WEBVIEWHOST + "hotzones")
            startActivity(intent)
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                lastLocation = p0.lastLocation
//                drawLocationAccuracyCircle(lastLocation)
//                placeMarkerOnMap(LatLng(lastLocation.latitude, lastLocation.longitude))
                if (hasMissionListPopulated) {
                    populateMissionList(
                        lastLocation.latitude.toString(),
                        lastLocation.longitude.toString()
                    )
                    showLoader(true)
                }
            }
        }
        createLocationRequest()
    }

    private fun populateMissionList(latitude: String, longitude: String) {
        hasMissionListPopulated = false
        DashboardController(this, this).getMissionData(latitude, longitude)
    }

    private fun Intent.clearStack() {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    private fun showLoader(status: Boolean) {
        if (status) {
            loader_layout.visibility = View.VISIBLE
            mapLayout.visibility = View.GONE
            rlList.visibility = View.GONE
        } else {
            loader_layout.visibility = View.GONE
            mapLayout.visibility = View.VISIBLE
            rlList.visibility = View.VISIBLE
        }
    }

    private fun isLocationPlausible(location: Location?): Boolean {
        if (location == null) return false
        val isMock = location.isFromMockProvider
        if (isMock) {
            lastMockLocation = location
            numGoodReadings = 0
        } else {
            numGoodReadings = min(numGoodReadings + 1, 1000000)
        } // Prevent overflow
        // We only clear that incident record after a significant show of good behavior
        if (numGoodReadings >= 20) lastMockLocation = null
        // If there's nothing to compare against, we have to trust it
        if (lastMockLocation == null) return true
        // And finally, if it's more than 1km away from the last known mock, we'll trust it
        val d = location.distanceTo(lastMockLocation).toDouble()
        return d > 1000
    }

    private fun configureMissionList(list: ArrayList<MissionModel>) {

        val missionListAdapter =
            MissionListAdapter(this, list)

        dashboardMissionList.adapter = missionListAdapter
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.setOnMarkerClickListener(this)

        setUpMap()
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        Handler().postDelayed({
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    marker.position,
                    15f
                )
            )
        }, 300)
        return true
    }


    private fun setMapStyle() {
        map.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                this,
                R.raw.map_custom_style
            )
        )
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        setMapStyle()

        map.isMyLocationEnabled = false

        map.uiSettings.isTiltGesturesEnabled = false
        map.uiSettings.isRotateGesturesEnabled = false
        map.uiSettings.isMapToolbarEnabled = false
        map.uiSettings.isZoomControlsEnabled = false
        map.uiSettings.isMyLocationButtonEnabled = true

        map.setPadding(20, 200, 0, 15)

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                drawLocationAccuracyCircle(location)
                placeMarkerOnMap(currentLatLng)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                if (hasMissionListPopulated) {
                    populateMissionList(location.latitude.toString(), location.longitude.toString())
                }
            }
        }
    }

    private fun placeMarkerOnMap(location: LatLng) {
        currentLocationMarker?.remove()
        val markerOptions = MarkerOptions()
            .flat(true)
            .icon(
                BitmapDescriptorFactory
                    .fromResource(R.drawable.ic_locationmarker)
            )
            .position(location)
        currentLocationMarker = map.addMarker(markerOptions)
//        updateCameraPosition()
    }

    private fun updateCameraPosition() {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocationMarker!!.position, 15f))
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            locationUpdateState = true
            startLocationUpdates()
        }
        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    e.startResolutionForResult(
                        this@DashBoardActivity,
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    private fun drawLocationAccuracyCircle(location: Location) {
        if (location.accuracy < 0) {
            return
        }

        val latLng = LatLng(location.latitude, location.longitude)

        locationAccuracyCircle?.let {
            it.center = latLng
        } ?: run {
            this.locationAccuracyCircle = map.addCircle(
                CircleOptions()
                    .center(latLng)
                    .fillColor(Color.argb(34, 0, 0, 0))
                    .strokeColor(Color.argb(64, 0, 0, 0))
                    .strokeWidth(1.0f)
                    .radius(location.accuracy.toDouble())
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                locationUpdateState = true
                startLocationUpdates()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        if (!locationUpdateState) {
            return
        }
        fusedLocationClient.removeLocationUpdates(locationCallback)
            .addOnCompleteListener(this) {
                locationUpdateState = false
            }
    }

    public override fun onResume() {
        super.onResume()
        if (!locationUpdateState) {
            startLocationUpdates()
        }
    }

    override fun onPositiveButtonClick(whichDialog: String?) {
        startActivityForResult(
            Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS),
            DEVELOPER_OPTIONS_REQUEST_CODE
        )
    }

    override fun onNegativeButtonClick(whichDialog: String?) {
        this.finishAffinity()
    }

    private fun buttonEffect(button: View) {
        button.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.background.setColorFilter(
                        Color.parseColor("#F06935"),
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
}