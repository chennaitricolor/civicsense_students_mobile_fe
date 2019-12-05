package com.gcc.smartcity.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by bgnanaraj on 9/26/2017.
 */

public class VolleyImageLoader {
    private static RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static VolleyImageLoader sInstance;

    public static synchronized VolleyImageLoader getInstance(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
        if (sInstance == null) {
            sInstance = new VolleyImageLoader();
        }
        return sInstance;
    }


   public ImageLoader setImageLoader(){
       mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
           private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);
           public void putBitmap(String url, Bitmap bitmap) {
               mCache.put(url, bitmap);
           }
           public Bitmap getBitmap(String url) {
               return mCache.get(url);
           }
       });
       return mImageLoader;
   }
}
