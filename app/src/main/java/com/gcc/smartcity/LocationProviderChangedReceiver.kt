package com.gcc.smartcity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.util.Log
import com.gcc.smartcity.utils.AlertDialogBuilder
import com.gcc.smartcity.utils.OnDialogListener

class LocationProviderChangedReceiver : BroadcastReceiver(), OnDialogListener {

    private var isGpsEnabled: Boolean = false
    private var isNetworkEnabled: Boolean = false

    override fun onReceive(context: Context, intent: Intent) {
        intent.action?.let { act ->
            if (act.matches("android.location.PROVIDERS_CHANGED".toRegex())) {
                val locationManager =
                    context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                isNetworkEnabled =
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

                Log.i(TAG, "Location Providers changed, is GPS Enabled: $isGpsEnabled")

                //Start your Activity if location was enabled:
                if (!(isGpsEnabled && isNetworkEnabled)) {
                    AlertDialogBuilder.getInstance().showErrorDialog(
                        "LOCATION TURNED OFF",
                        "Please turn ON location to continue using the app",
                        "CANCEL",
                        "OK",
                        "",
                        context,
                        this
                    )
                }
            }
        }
    }

    override fun onPositiveButtonClick(whichDialog: String?) {
    }

    override fun onNegativeButtonClick(whichDialog: String?) {
    }

    companion object {
        private const val TAG = "LocationProviderChanged"
    }


}