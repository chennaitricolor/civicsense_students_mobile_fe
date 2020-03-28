package com.gcc.smartcity.forceupdate

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gcc.smartcity.BuildConfig
import com.gcc.smartcity.R
import com.gcc.smartcity.preference.SessionStorage
import kotlinx.android.synthetic.main.activity_force_app_update.*
import java.lang.StringBuilder


class ForceAppUpdateActivity : AppCompatActivity() {

    private var mTvTitle: TextView? = null
    private var mTvBody: TextView? = null
    private var mTvInstalledVersion: TextView? = null
    private var sessionStorage: SessionStorage? = null
    private var version: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.requestFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_force_app_update)
        sessionStorage = SessionStorage.getInstance()
        mTvTitle = findViewById(R.id.forceAppUpdate_tvTitle)
        mTvInstalledVersion = findViewById(R.id.forceAppUpdate_installed_version)
        mTvBody = findViewById(R.id.forceAppUpdate_tvMsg)
        forceAppUpdate_tvUpdate.setOnClickListener {
            openPlayStore(this)
        }
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun updateUI() {
        version =  sessionStorage?.rootModel?.version
        mTvInstalledVersion!!.text = StringBuilder(getString(R.string.forceupdate_current_version) + " " + BuildConfig.VERSION_NAME)
        mTvTitle?.text = "Update Available"
        mTvBody?.text = StringBuilder("This version of the app is outdated. Please update to the latest version"+ " (" + version + ")")
    }

    private fun openPlayStore(context: Context) {
        // you can also use BuildConfig.APPLICATION_ID
        val appId: String = context.packageName
        val rateIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("market://details?id=$appId")
        )
        var marketFound = false
        // find all applications able to handle our rateIntent
        val otherApps: List<ResolveInfo> = context.packageManager
            .queryIntentActivities(rateIntent, 0)
        for (otherApp in otherApps) { // look for Google Play application
            if (otherApp.activityInfo.applicationInfo.packageName
                == "com.android.vending"
            ) {
                val otherAppActivity = otherApp.activityInfo
                val componentName = ComponentName(
                    otherAppActivity.applicationInfo.packageName,
                    otherAppActivity.name
                )
                // make sure it does NOT open in the stack of your activity
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                // task reparenting if needed
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                // if the Google Play was already open in a search result
                //  this make sure it still go to the app page you requested
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                // this make sure only the Google Play app is allowed to
                // intercept the intent
                rateIntent.component = componentName
                try {
                    context.startActivity(rateIntent)
                    marketFound = true
                } catch (e: Exception) {
                    Toast.makeText(this, getString(R.string.tryAgainLater), Toast.LENGTH_SHORT)
                        .show()
                }
                break
            }
        }
        // if GP not present on device, open web browser
        if (!marketFound) {
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$appId")
            )
            try {
                context.startActivity(webIntent)
            } catch (e: Exception) {
                Toast.makeText(this, getString(R.string.tryAgainLater), Toast.LENGTH_SHORT).show()
            }
        }
    }
}
