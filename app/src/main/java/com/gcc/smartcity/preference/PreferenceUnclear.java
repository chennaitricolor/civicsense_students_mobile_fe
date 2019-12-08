package com.gcc.smartcity.preference;

import android.content.Context;
import android.content.SharedPreferences;

import javax.crypto.spec.SecretKeySpec;

public class PreferenceUnclear {
    private SharedPreferences mPreference;

    private SecretKeySpec secretKeySpec;
    private String KEY_PREF_NAME = "pref_unclear";

    public PreferenceUnclear(Context context) {
        mPreference = context.getSharedPreferences(KEY_PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setString(String key, String value) {
        mPreference.edit().putString(key, value).commit();
    }

    public String getString(String key, String defaultValue) {
        return mPreference.getString(key, defaultValue);
    }
}
