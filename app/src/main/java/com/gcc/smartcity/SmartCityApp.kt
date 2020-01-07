package com.gcc.smartcity

import android.app.Application
import com.gcc.smartcity.preference.Preferences

class SmartCityApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Preferences.INSTANCE.createPreferences(this)
    }
}