package com.gcc.smartcity;

import android.content.Context;
import android.graphics.Bitmap;

import com.android.volley.Request;
import com.gcc.smartcity.dashboard.ImageUploadListener;
import com.gcc.smartcity.dashboard.model.FileUploadResponseModel;
import com.gcc.smartcity.network.RequestExecutor;
import com.gcc.smartcity.network.VolleyMultipartRequest;
import com.gcc.smartcity.utils.Logger;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FileUpload {
    private String campaignId = "";
    private String campaignName = "";
    private String latitude = "";
    private String longitude = "";
    private Boolean isMediaNeeded;
    private Context mContext;
    private ImageUploadListener mImageUploadListener;
    private HashMap<String, String> formValues;

    public FileUpload(Context context, ImageUploadListener imageUploadListener) {
        mContext = context;
        mImageUploadListener = imageUploadListener;
    }

    public void uploadScreenshotCall(Boolean mediaNeeded, String mLatitude, String mLongitude, String url, Bitmap bitmap, String mimeType, String _id, String _campaignName, HashMap<String, String> hashMap) {
        isMediaNeeded = mediaNeeded;
        campaignName = _campaignName;
        campaignId = _id;
        latitude = mLatitude;
        longitude = mLongitude;
        if (hashMap != null) {
            formValues = hashMap;
        }
        RequestExecutor.getInstance(mContext).addMultipartRequest(createVolleyRequestForUpload(url, bitmap, mimeType));
    }

    private VolleyMultipartRequest createVolleyRequestForUpload(String url, final Bitmap bitmap, final String mimeType) {

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, response -> {
            String resultResponse = new String(response.data);
            Gson gson = new Gson();
            FileUploadResponseModel responseMode = gson.fromJson(resultResponse, FileUploadResponseModel.class);
            Logger.d("upload successful" + responseMode.getMessage());
            if (responseMode.getSuccess()) {
                mImageUploadListener.onSuccess();

            } else {
                mImageUploadListener.onFailure(responseMode.getMessage());
            }
        }, error -> {
            error.printStackTrace();
            mImageUploadListener.onFailure("Server down. Please try after some time.");
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> map = new HashMap<>();
                map.put("Accept", "application/json");
                return map;
            }

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("campaignId", campaignId);
                params.put("locationNm", campaignName);
                params.put("location", "{\"coordinates\": [" + longitude + "," + latitude + "]}");
                if (formValues != null) {
                    JSONObject formObject = new JSONObject(formValues);
                    params.put("formData", String.valueOf(formObject));
                }
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = null;
                if (isMediaNeeded) {
                    params = new HashMap<>();
                    DataPart dataPart = new VolleyMultipartRequest.DataPart();
                    dataPart.setContent(getBytFromBitmap(bitmap, 15));
                    dataPart.setType(mimeType);
                    params.put("file", new DataPart(generateUniqueFileName() + ".jpg", getBytFromBitmap(bitmap, 15), "image/*"));
                }
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



