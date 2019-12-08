package com.gcc.smartcity;

import android.app.Application;

import com.gcc.smartcity.preference.Preferences;

public class SmartCityApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Preferences.INSTANCE.createPreferences(this);
    }
}
