package com.gcc.smartcity.rating

import android.content.ComponentName
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gcc.smartcity.R
import com.gcc.smartcity.preference.SessionStorage
import kotlinx.android.synthetic.main.activity_rating.*


class RatingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating)
        ratingView.setRatingChangeListener { previousRating, newRating ->
            emotionView.setRating(previousRating, newRating)
            gradientBackgroundView.changeBackground(previousRating, newRating)
        }
        val content = SpannableString("SKIP")
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        skipButton.text = content
        skipButton.setOnClickListener {
            finish()
        }
        submitButton.setOnClickListener {
            SessionStorage.getInstance().ratingStatus = true
            when (ratingView.getCurrentRating()) {
                1, 2, 3 -> {
                    Toast.makeText(
                        this,
                        "We will improve our app in the next upgrade",
                        Toast.LENGTH_LONG
                    )
                        .show()
                    finish()
                }
                4, 5 -> {
                    Toast.makeText(
                        this,
                        "Please leave us a review",
                        Toast.LENGTH_LONG
                    )
                        .show()
                    // you can also use BuildConfig.APPLICATION_ID
                    val appId: String = this.packageName
                    val rateIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$appId")
                    )
                    var marketFound = false
                    // find all applications able to handle our rateIntent
                    val otherApps: List<ResolveInfo> = this.packageManager
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
                                this.startActivity(rateIntent)
                                marketFound = true
                            } catch (e: Exception) {
                                Toast.makeText(
                                    this,
                                    getString(R.string.tryAgainLater),
                                    Toast.LENGTH_SHORT
                                )
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
                            this.startActivity(webIntent)
                        } catch (e: Exception) {
                            Toast.makeText(
                                this,
                                getString(R.string.tryAgainLater),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    finish()
                }
            }
        }
    }
}