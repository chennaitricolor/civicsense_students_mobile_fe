package com.gcc.smartcity

import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationListener


class MyLocationListener(var mContext: Context) : LocationListener {
//    private var currentBestLocation: Location? = null
//    private var locationManager : LocationManager? = null
//    locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
    override fun onLocationChanged(location: Location?) {
//        makeUseOfNewLocation(location)
//
//        if(currentBestLocation == null){
//            currentBestLocation = location;
//        }
//
    }
//
//    private fun getLastBestLocation(): Location? {
//        val locationGPS: Location =
//            mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
//        val locationNet: Location =
//            mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
//        var GPSLocationTime: Long = 0
//        if (null != locationGPS) {
//            GPSLocationTime = locationGPS.time
//        }
//        var NetLocationTime: Long = 0
//        if (null != locationNet) {
//            NetLocationTime = locationNet.time
//        }
//        return if (0 < GPSLocationTime - NetLocationTime) {
//            locationGPS
//        } else {
//            locationNet
//        }
//    }
}