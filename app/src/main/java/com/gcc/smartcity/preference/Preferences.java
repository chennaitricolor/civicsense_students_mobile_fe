package com.gcc.smartcity.preference;

import android.content.Context;

public enum Preferences {
    INSTANCE;

    public static final String PREF_NAME = "smartcity_pref";
    private static final String COOKIE = "cookie";
    private static final String LEADERBOARD_API_MODEL = "leaderboard_api_model";
    private static final String LEADERBOARD_STATUS = "leaderboard_status";
    private static final String USER_ID = "user_id";
    private static final String USER_NAME = "user_name";

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

    public String getLeaderBoardApiModel() {
        return mPreferenceHandle.getString(LEADERBOARD_API_MODEL, null);
    }

    public void setLeaderBoardApiModel(String adminApiModel) {
        mPreferenceHandle.setString(LEADERBOARD_API_MODEL, adminApiModel);
    }

    public String getUserId() {
        return mPreferenceHandle.getString(USER_ID, null);
    }

    public void setUserId(String userId) {
        mPreferenceHandle.setString(USER_ID, userId);
    }

    public String getUserName() {
        return mPreferenceHandle.getString(USER_NAME, null);
    }

    public void setUserName(String userName) {
        mPreferenceHandle.setString(USER_NAME, userName);
    }

    public void setLeaderBoardStatus(Boolean leaderBoardStatus) {
        mPreferenceHandle.setBoolean(LEADERBOARD_STATUS, leaderBoardStatus);
    }

    public Boolean getLeaderBoardStatus() {
        return mPreferenceHandle.getBoolean(LEADERBOARD_STATUS, false);
    }
}
