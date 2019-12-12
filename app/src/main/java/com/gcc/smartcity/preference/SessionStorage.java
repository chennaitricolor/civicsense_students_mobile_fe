package com.gcc.smartcity.preference;

import com.gcc.smartcity.utils.Logger;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SessionStorage {

    private static Gson GSON = new Gson();

    private static volatile SessionStorage sInstance;

    public static synchronized SessionStorage getInstance() {
//        db = new DatabaseHandler();
        if (sInstance == null) {
            sInstance = new SessionStorage();
        }
        return sInstance;
    }

    private SessionStorage() {

    }

    public String getSessionCookies() {
        return Preferences.INSTANCE.getCookieModel();
    }

    public void setSessionCookies(String sessionCookies) {
        Preferences.INSTANCE.setCookieModel(sessionCookies);

    }

    public String getStringfromObject(Object object) {
        if (object == null) {
            throw new IllegalArgumentException("object is null");
        }
        return GSON.toJson(object);
    }

    public <T> T getObjectfromString(String value, Class<T> a) {
        String gson = value;
        if (gson == null) {
            return null;
        } else {
            try {
                return GSON.fromJson(gson, a);
            } catch (Exception e) {
                throw new IllegalArgumentException("Object storaged with key is instanceof other class");
            }
        }
    }


    private String getStringFromArrayList(ArrayList list) {
        String inputString = GSON.toJson(list);
        return inputString;
    }

    private <T> ArrayList<T> getArrayListFromString(String str, Class<T[]> clz) {
        Logger.d("DB VALUE::: " + str);

        T[] result = new Gson().fromJson(str, clz);

        if (result == null) {
            return null;
        }
        return new ArrayList<T>(Arrays.asList(result));

    }

    public static <T> List<T> stringToArray(String s, Class<T> clazz) {
        T arr = new Gson().fromJson(s, clazz);
        return Arrays.asList(arr); //or return Arrays.asList(new Gson().fromJson(s, clazz)); for a one-liner
    }

}