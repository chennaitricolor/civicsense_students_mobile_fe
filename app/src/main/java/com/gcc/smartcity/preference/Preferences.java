package com.gcc.smartcity.preference;


import android.content.Context;

/**
 * Preferences to store/retrieve values/model as string/JsonParcels
 */
public enum Preferences {
    INSTANCE;

    // Preference Name
    public static final String PREF_NAME = "smartcity_pref";
    private static final String COOKIE = "cookie";


    private UserPreferences mPreferenceHandle;
    private PreferenceUnclear mPreferenceUnclear;

    public void createPreferences(Context context) {
        mPreferenceHandle = new UserPreferences(context, PREF_NAME);
        mPreferenceUnclear = new PreferenceUnclear(context);
    }

    public void clearPreference() {
        if (mPreferenceHandle != null) {
            mPreferenceHandle.clearPreference();
        }
    }

    public void setCookieModel(String rootApiModel) {
        mPreferenceHandle.setString(COOKIE, rootApiModel);
    }

    public String getCookieModel() {
        return mPreferenceHandle.getString(COOKIE, null);
    }


}
