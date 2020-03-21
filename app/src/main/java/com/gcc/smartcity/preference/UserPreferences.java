package com.gcc.smartcity.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import com.gcc.smartcity.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;

import javax.crypto.spec.SecretKeySpec;

/**
 * Shared Preferences to store the application/user data
 */
public class UserPreferences {

    private SharedPreferences mPreference;

    private SecretKeySpec secretKeySpec;

    public UserPreferences(Context context, String preferenceName) {
        mPreference = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);

        loadKey(context);
    }

    public void setString(String key, String value) {
        mPreference.edit().putString(key, value).commit();
    }

    public String getString(String key, String defaultValue) {
        return mPreference.getString(key, defaultValue);
    }

    public void setInt(String key, int value) {
        mPreference.edit().putInt(key, value).commit();
    }

    public int getInt(String key, int defaultValue) {
        return mPreference.getInt(key, defaultValue);
    }

    public void setBoolean(String key, boolean value) {
        mPreference.edit().putBoolean(key, value).commit();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return mPreference.getBoolean(key, defaultValue);
    }

    public void setEncryptedData(String key, String value) {
        String encryptedValue = null;
        if (value != null && secretKeySpec != null) {
            encryptedValue = Base64.encodeToString(Crypto.encryptAES(value, secretKeySpec), Base64.DEFAULT);
            setEncryptedDataWithCBC(key, value);
        }
        setString(key, encryptedValue);
    }

    public String getEncryptedData(String key, String defaultValue) {
        String value = getString(key, defaultValue);
        String decoded = "";
        if ((value != null) && (!value.equals(defaultValue))) {
            decoded = new String(Crypto.decryptAES(Base64.decode(value, Base64.DEFAULT), secretKeySpec));
        }
        return decoded;
    }

    public void setEncryptedDataWithCBC(String key, String value) {
        String encryptedValue = null;
        if (value != null && secretKeySpec != null) {
            encryptedValue = Base64.encodeToString(Crypto.encryptAESWithCBC(value, secretKeySpec), Base64.DEFAULT);
        }
        setString(key, encryptedValue);
    }

    public String getEncryptedDataWithCBC(String key, String defaultValue) {
        String value = getString(key, defaultValue);
        String decoded = "";
        if ((value != null) && (!value.equals(defaultValue))) {
            decoded = new String(Crypto.decryptWithCBC(Base64.decode(value, Base64.DEFAULT), secretKeySpec));
        }
        return decoded;
    }

    public void clearPreference() {
        mPreference.edit().clear().commit();
    }

    public void setStringSet(String key, Set<String> values) {
        mPreference.edit().putStringSet(key, values).commit();
    }

    public Set<String> getStringSet(String key) {
        return mPreference.getStringSet(key, null);
    }


    private void loadKey(Context context) {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.key)));
            String encryptionKey = bufferedReader.readLine();

            Log.d("LoadKey", "encryptionKey: " + encryptionKey);

            secretKeySpec = new SecretKeySpec(Base64.decode(encryptionKey, Base64.DEFAULT), "AES");
        } catch (Exception e) {
            Log.e("LoadKey", "EXCEPTION OCCURRED WHILE LOADING KEY" + e.getMessage().toString());
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}