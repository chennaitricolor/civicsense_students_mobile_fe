package com.gcc.smartcity.dashboard

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.birjuvachhani.locus.Locus
import com.gcc.smartcity.BuildConfig
import com.gcc.smartcity.R
import com.gcc.smartcity.loginandregistration.LoginActivity
import com.gcc.smartcity.navigationdrawer.NavigationDrawerActivity
import com.gcc.smartcity.utils.Logger
import com.gcc.smartcity.webview.WebViewActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashBoardActivity : NavigationDrawerActivity(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener, MissionAPIListener {

    private val TAG = this::class.java.simpleName

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mSettingsClient: SettingsClient
    private var currentLocationMarker: Marker? = null
    private var locationAccuracyCircle: Circle? = null
    private var initialMissionCall: Boolean = true
    private var mLatitude: String? = null
    private var mLongitude: String? = null

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onFail(message: String, shouldLogOut: Boolean) {
        dashboardMissionList.visibility = View.GONE
        if (shouldLogOut) {
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
        dashboardMissionList.visibility = View.VISIBLE
        showLoader(false)
        configureMissionList(missionModel)
    }

    override fun shouldShowNavigationDrawer(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun shouldShowToolbarBackButton(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setMainContentView(R.layout.activity_dashboard)

        Toast.makeText(this, "Fetching tasks in your location", Toast.LENGTH_SHORT)
            .show()

        val request = LocationRequest.create()
        Intent(this, DashBoardActivity::class.java).apply {
            putExtra("request", request)
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mSettingsClient = LocationServices.getSettingsClient(this)

        containmentZoneBanner.setOnClickListener {
            Logger.d("Containment Zones")
            val intent = WebViewActivity.newIntent(this, BuildConfig.WEBVIEWHOST + "hotzones")
            startActivity(intent)
        }

        reloadButton.setOnClickListener {
            Toast.makeText(this, "Fetching tasks in your location", Toast.LENGTH_SHORT)
                .show()
            getInstantLocation()
        }

        if (!BuildConfig.CONTAINMENTZONE) {
            containmentZoneBanner.visibility = View.GONE
        } else {
            containmentZoneBanner.visibility = View.VISIBLE
        }

    }

    private fun getInstantLocation() {
        Locus.getCurrentLocation(this) { result ->
            result.location?.let {
                populateMissionList(it.latitude.toString(), it.longitude.toString())
                showLoader(true)
            } ?: run {
                Toast.makeText(this, "Unable to fetch your location", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun populateMissionList(latitude: String, longitude: String) {
        DashboardController(this, this).getMissionData(latitude, longitude)
    }

    private fun Intent.clearStack() {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    private fun showLoader(status: Boolean) {
        if (status) {
            loaderContainer.visibility = View.VISIBLE
        } else {
            loaderContainer.visibility = View.GONE
        }
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

        startLocationUpdates()
    }


    private fun startLocationUpdates() {
        Locus.configure {
            shouldResolveRequest = true
            request {
                fastestInterval = 100
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                interval = 100
                maxWaitTime = 100
            }
        }
        Locus.startLocationUpdates(this) { result ->
            result.location?.let(::onLocationUpdate)
            result.error?.let(::onLocationError)
        }
    }

    private fun stopLocationUpdates() {
        Locus.stopLocationUpdates()
    }

    private fun onLocationUpdate(location: Location) {
        mLatitude = location.latitude.toString()
        mLongitude = location.longitude.toString()
        val currentLatLng = LatLng(location.latitude, location.longitude)
        drawLocationAccuracyCircle(location)
        placeMarkerOnMap(currentLatLng)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
        if (initialMissionCall) {
            initialMissionCall = false
            populateMissionList(mLatitude!!, mLongitude!!)
            showLoader(true)
        }
    }

    private fun onLocationError(error: Throwable?) {
        Log.e(TAG, "Error: ${error?.message}")
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

    private fun updateCameraPosition() {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocationMarker!!.position, 15f))
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    public override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }
}