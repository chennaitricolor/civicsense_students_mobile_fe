package com.gcc.smartcity.utils

import android.app.Activity
import android.view.WindowManager

class TouchUtility(private val activity: Activity) {

    fun enableUserInteraction(enable: Boolean) {
        if (enable) {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        } else {
            activity.window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        }
    }
}