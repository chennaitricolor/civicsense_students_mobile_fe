package com.gcc.smartcity.network;

import com.gcc.smartcity.BuildConfig;
import com.gcc.smartcity.preference.SessionStorage;
import com.google.gson.Gson;

import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

public class PersistantCookieStore implements CookieStore {

    private CookieStore mCookieStore;
    private Gson mGson;

    public PersistantCookieStore() {

        mGson = new Gson();

        // Check if there is any cookie session key available in Session store
        // And if already available set them in the Cookie Store
        mCookieStore = new CookieManager().getCookieStore();

        String httpCookiesJson = SessionStorage.getInstance().getSessionCookies();

        if (httpCookiesJson != null) {

            HttpCookie cookie = mGson.fromJson(httpCookiesJson, HttpCookie.class);
            mCookieStore.add(URI.create(cookie.getDomain()), cookie);
        }

    }

    @Override
    public void add(URI uri, HttpCookie cookie) {

        // Add the session key to the Session Store and Cookie Store

        if (cookie.getName().equals(BuildConfig.COOKIEKEY)) {
            remove(URI.create(cookie.getDomain()), cookie);
            if (cookie.getMaxAge() <= 0)
                cookie.setMaxAge(1800);

            SessionStorage.getInstance().setSessionCookies(mGson.toJson(cookie));
        }

        mCookieStore.add(URI.create(cookie.getDomain()), cookie);

    }

    @Override
    public List<HttpCookie> get(URI uri) {
        return mCookieStore.get(uri);
    }

    @Override
    public List<HttpCookie> getCookies() {
        return mCookieStore.getCookies();
    }

    @Override
    public List<URI> getURIs() {
        return mCookieStore.getURIs();
    }

    @Override
    public boolean remove(URI uri, HttpCookie cookie) {
        return mCookieStore.remove(uri, cookie);
    }

    @Override
    public boolean removeAll() {
        return mCookieStore.removeAll();
    }
}
