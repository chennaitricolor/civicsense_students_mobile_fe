package com.gcc.smartcity;

import android.content.Context;
import android.graphics.Bitmap;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gcc.smartcity.network.RequestExecutor;
import com.gcc.smartcity.network.VolleyMultipartRequest;
import com.gcc.smartcity.utils.Logger;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FileUpload {
    private String campaignId = "";
    private Context mContext;

    public FileUpload(Context context) {
        mContext = context;
    }

    public void uploadScreenshotCall(String url, Bitmap bitmap, String mimeType, String _id) {
        campaignId = _id;
        RequestExecutor.getInstance(mContext).addMultipartRequest(createVolleyRequestForUpload(url, bitmap, mimeType));
    }

    private VolleyMultipartRequest createVolleyRequestForUpload(String url, final Bitmap bitmap, final String mimeType) {

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                Logger.d("upload successful");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> map = new HashMap<>();
//                map.put("Authorization", "Basic " + Base64.encodeToString("denton:ap0ll0".getBytes(), Base64.DEFAULT));
                map.put("Accept", "application/json");
                return map;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("campaignId", campaignId);
                params.put("locationNm", "Zone 15 Sholinganallur");
                params.put("location", "{\"coordinates\": [79.619928, 10.752292]}");
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                DataPart dataPart = new VolleyMultipartRequest.DataPart();
                dataPart.setContent(getBytFromBitmap(bitmap, 15));
                dataPart.setType(mimeType);
                params.put("file", new DataPart(generateUniqueFileName() + ".jpg", getBytFromBitmap(bitmap, 15), "image/*"));

                return params;
            }
        };

        return multipartRequest;
    }

    private String generateUniqueFileName() {
        String filename = "";
        long millis = System.currentTimeMillis();
        String datetime = new Date().toGMTString();
        datetime = datetime.replace(" ", "");
        datetime = datetime.replace(":", "");
        filename = "Csr_" + datetime + "_" + millis;
        return filename;
    }

    private byte[] getBytFromBitmap(Bitmap bitmap, int quality) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        byte[] byteArray = stream.toByteArray();
//        bitmap.recycle();
        return byteArray;
    }

}



