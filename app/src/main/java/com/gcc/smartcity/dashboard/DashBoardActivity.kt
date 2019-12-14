package com.gcc.smartcity.dashboard

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import com.gcc.smartcity.NavigationDrawerActivity
import com.gcc.smartcity.R
import com.gcc.smartcity.utils.AlertDialogBuilder
import com.gcc.smartcity.utils.OnDialogListener
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_dashboard.*


class DashBoardActivity : NavigationDrawerActivity(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener, OnDialogListener, MissionAPIListener {

    override fun onSuccess(missionModel: ArrayList<MissionModel>) {
        showLoader(false)
        configureMissionList(missionModel)
    }

    override fun onFail(message: String) {
        showLoader(false)
        showErrorDialog(
            getString(R.string.tryAgainLater),
            message,
            getString(R.string.okButtonText)
        )
    }

    override fun shouldShowNavigationDrawer(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun shouldShowToolbarBackButton(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMarkerClick(p0: Marker?) = false
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private var lastMockLocation: Location? = null
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false
    private var currentLocationMarker: Marker? = null
    private var numGoodReadings: Int = 0
    private val DEVELOPER_OPTIONS_REQUEST_CODE = 1010
    private var continueAppExecution: Boolean = true

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setMainContentView(R.layout.activity_dashboard)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (continueAppExecution) {
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    super.onLocationResult(p0)
                    lastLocation = p0.lastLocation
                    if (!isLocationPlausible(lastLocation) && continueAppExecution) {
                        AlertDialogBuilder.getInstance().showErrorDialog(
                            getString(R.string.turnOffMockLocationTitle),
                            getString(R.string.turnOffMockLocationDescription),
                            getString(R.string.cancelButtonText),
                            getString(R.string.gotoSettingsButtonText),
                            "mock_location_warning",
                            this@DashBoardActivity,
                            this@DashBoardActivity
                        )
                        continueAppExecution = false
                    } else if (isLocationPlausible(lastLocation)) {
                        placeMarkerOnMap(LatLng(lastLocation.latitude, lastLocation.longitude))
                    }
                }
            }

            createLocationRequest()
            showLoader(true)
            DashboardController(this, this).getMissionData()
        }
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
            numGoodReadings = Math.min(numGoodReadings + 1, 1000000)
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

        setMapStyle()
        if (continueAppExecution) {
            setUpMap()
        }
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

        map.isMyLocationEnabled = false

        map.setPadding(20, 200, 0, 15)

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                lastLocation = location
                if (!isLocationPlausible(lastLocation) && continueAppExecution) {
                    AlertDialogBuilder.getInstance().showErrorDialog(
                        getString(R.string.turnOffMockLocationTitle),
                        getString(R.string.turnOffMockLocationDescription),
                        getString(R.string.cancelButtonText),
                        getString(R.string.gotoSettingsButtonText),
                        "mock_location_warning",
                        this,
                        this
                    )
                    continueAppExecution = false
                } else if (isLocationPlausible(lastLocation)) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    placeMarkerOnMap(currentLatLng)
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
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
            null /* Looper */
        )
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.interval = 1000
        locationRequest.fastestInterval = 500
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                locationUpdateState = true
                startLocationUpdates()
            }
        } else if (requestCode == DEVELOPER_OPTIONS_REQUEST_CODE) {
            startLocationUpdates()
        }
    }

    // 2
    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    // 3
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

}