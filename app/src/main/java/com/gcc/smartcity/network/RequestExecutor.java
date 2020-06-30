package com.gcc.smartcity.network;

import android.content.Context;
import android.os.Build;
import android.util.Base64;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.Volley;
import com.gcc.smartcity.BuildConfig;
import com.gcc.smartcity.utils.Logger;
import com.gcc.smartcity.utils.NetworkError;

import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import bolts.Task;
import bolts.TaskCompletionSource;

public class RequestExecutor {

    private RequestExecutor() {
        // Private constructor to avoid instantiation
    }

    private static RequestExecutor sInstance, sInstanceWithRequestQueue;

    public static synchronized RequestExecutor getInstance(Context context) {//  synchronized (sInstance) {
        if (sInstance == null) {
            sInstance = new RequestExecutor(context);
        }
        return sInstance;
    }

    public static synchronized RequestExecutor getInstance(Context context, RequestQueue requestQueue) {//  synchronized (sInstance) {
        if (sInstanceWithRequestQueue == null) {
            sInstanceWithRequestQueue = new RequestExecutor(context, requestQueue);
        }
        return sInstanceWithRequestQueue;
    }

    private Context mContext;
    private RequestQueue mRequestQueue;

    private RequestExecutor(Context context) {
        mContext = context;
        mRequestQueue = Volley.newRequestQueue(mContext);
//      mRequestQueue = Volley.newRequestQueue(context, new CustomHurlStack(null, ClientSSLSocketFactory.getSocketFactory(mContext)));
        mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext(), getHurlStack());
    }

    private HttpStack getHurlStack() {
        HttpStack httpStack = null;
        try {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, null, null);
            SSLSocketFactory noSSLv3Factory = null;
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                noSSLv3Factory = new TLSSocketFactory(context.getSocketFactory());
            } else {
                noSSLv3Factory = context.getSocketFactory();
            }
            httpStack = new OkHttpHurlStack(noSSLv3Factory);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return httpStack;
    }

    public RequestExecutor(Context context, RequestQueue requestQueue) {
        mContext = context;
        mRequestQueue = requestQueue;
        // mRequestQueue = Volley.newRequestQueue(mContext);
    }


    public void makeRequestCall(VolleyRequest<?> volleyRequest, OnServiceListener onServiceListener) {
        volleyRequest = addAuthHeaders(volleyRequest);
        volleyRequest.setOnServiceListener(onServiceListener);
        mRequestQueue.add(volleyRequest);
    }

    public Task<Object> makeRequestCall(VolleyRequest<?> volleyRequest) {
        final TaskCompletionSource<Object> task = new TaskCompletionSource<>();
        volleyRequest = addAuthHeaders(volleyRequest);
        volleyRequest.setBodyContentType("application/json");
        volleyRequest.setOnServiceListener(new OnServiceListener() {
            @Override
            public void onSuccess(int serviceIdentifier, final Object response) {
                Logger.d("Task is successfully completed" + response.toString());
                task.setResult(response);
            }

            @Override
            public void onError(int serviceIdentifier, NetworkError networkError) {
                Logger.d("Task is completed with error");
                task.setError(networkError);
            }
        });

        mRequestQueue.add(volleyRequest);
        return task.getTask();
    }

    public Task<Object> makeRequestCallWithCustomHeaders(VolleyRequest<?> volleyRequest, Map<String, String> headers) {
        final TaskCompletionSource<Object> task = new TaskCompletionSource<>();
        if (headers != null) {
            headers.put("X-Application-Id", "mobile-android");
        }
        volleyRequest.setHeaders(headers);
        volleyRequest.setOnServiceListener(new OnServiceListener() {
            @Override
            public void onSuccess(int serviceIdentifier, final Object response) {
                Logger.d("Task is successfully completed");
                task.setResult(response);
            }

            @Override
            public void onError(int serviceIdentifier, NetworkError networkError) {
                Logger.d("Task is completed with error");
                task.setError(networkError);
            }
        });

        mRequestQueue.add(volleyRequest);
        return task.getTask();

    }

    public Task<Object> makeRequestCallForPDFDownloadWithCustomHeaders(ByteArrayRequest request, Map<String, String> headers) {
        final TaskCompletionSource<Object> task = new TaskCompletionSource<>();
        headers.put("X-Application-Id", "mobile-android");
        request.setHeaders(headers);
        request.setOnServiceListener(new OnServiceListener() {
            @Override
            public void onSuccess(int serviceIdentifier, final Object response) {
                Logger.d("Task is successfully completed");
                task.setResult(response);
            }

            @Override
            public void onError(int serviceIdentifier, NetworkError networkError) {
                Logger.d("Task is completed with error");
                task.setError(networkError);
            }
        });

        mRequestQueue.add(request);
        return task.getTask();
    }

    private VolleyRequest<?> addAuthHeaders(VolleyRequest<?> volleyRequest) {
        Map<String, String> authHeaders = new HashMap<>();
        authHeaders.put("Accept", "application/json");
        if(BuildConfig.AUTHORIZATION) {
            authHeaders.put("Authorization", "Basic dXNlcjpwYXNzd29yZA=");
        }

        volleyRequest.setHeaders(authHeaders);

        return volleyRequest;
    }

    public void addMultipartRequest(VolleyMultipartRequest multipartRequest) {
        mRequestQueue.add(multipartRequest);
    }

    public RequestQueue getmRequestQueue() {
        return mRequestQueue;
    }
}
