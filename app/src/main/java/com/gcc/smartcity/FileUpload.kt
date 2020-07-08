package com.gcc.smartcity

import android.content.Context
import android.graphics.Bitmap
import com.android.volley.DefaultRetryPolicy
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.VolleyError
import com.gcc.smartcity.dashboard.ImageUploadListener
import com.gcc.smartcity.dashboard.model.FileUploadResponseModel
import com.gcc.smartcity.network.RequestExecutor
import com.gcc.smartcity.network.VolleyMultipartRequest
import com.gcc.smartcity.utils.Logger
import com.google.gson.Gson
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.*

class FileUpload(
    private val mContext: Context,
    private val mImageUploadListener: ImageUploadListener
) {
    private var campaignId = ""
    private var campaignName = ""
    private var latitude = ""
    private var longitude = ""
    private var isMediaNeeded: Boolean? = null
    private var formValues: HashMap<String, String>? = null
    fun uploadScreenshotCall(
        mediaNeeded: Boolean?,
        mLatitude: String,
        mLongitude: String,
        url: String,
        bitmap: Bitmap?,
        mimeType: String,
        _id: String,
        _campaignName: String,
        hashMap: HashMap<String, String>?
    ) {
        isMediaNeeded = mediaNeeded
        campaignName = _campaignName
        campaignId = _id
        latitude = mLatitude
        longitude = mLongitude
        if (hashMap != null) {
            formValues = hashMap
        }
        RequestExecutor.getInstance(mContext)
            .addMultipartRequest(createVolleyRequestForUpload(url, bitmap, mimeType))
    }

    private fun createVolleyRequestForUpload(
        url: String,
        bitmap: Bitmap?,
        mimeType: String
    ): VolleyMultipartRequest {
        val multipartRequest: VolleyMultipartRequest = object : VolleyMultipartRequest(
            Method.POST,
            url,
            Response.Listener { response: NetworkResponse ->
                val resultResponse = String(response.data)
                val gson = Gson()
                val responseModel = gson.fromJson(
                    resultResponse,
                    FileUploadResponseModel::class.java
                )
                Logger.d("upload successful" + responseModel.message)
                if (responseModel.success!!) {
                    mImageUploadListener.onSuccess()
                } else {
                    mImageUploadListener.onFailure(responseModel.message!!)
                }
            },
            Response.ErrorListener { error: VolleyError ->
                error.printStackTrace()
                mImageUploadListener.onFailure("Server down. Please try after some time.")
            }
        ) {
            override fun getHeaders(): Map<String, String> {
                val map: HashMap<String, String> =
                    HashMap()
                map["Accept"] = "application/json"
                map["Authorization"] = "Basic dXNlcjpwYXNzd29yZA="
                map["region"] = BuildConfig.CITY
                return map
            }

            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> =
                    HashMap()
                params["campaignId"] = campaignId
                params["locationNm"] = campaignName
                params["location"] = "{\"coordinates\": [$longitude,$latitude]}"
                if (formValues != null) {
                    val formObject = JSONObject(formValues as Map<*, *>)
                    params["formData"] = formObject.toString()
                }
                return params
            }

            override fun getByteData(): MutableMap<String, DataPart>? {
                var params: MutableMap<String, DataPart>? = null
                if (isMediaNeeded!!) {
                    params = HashMap()
                    val dataPart = DataPart()
                    dataPart.content = getBytFromBitmap(bitmap!!, 15)
                    dataPart.type = mimeType
                    params["file"] = DataPart(
                        generateUniqueFileName() + ".jpg",
                        getBytFromBitmap(bitmap, 15),
                        "image/*"
                    )
                }
                return params
            }
        }
        multipartRequest.retryPolicy = DefaultRetryPolicy(
            0,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        return multipartRequest
    }

    private fun generateUniqueFileName(): String {
        var filename = ""
        val millis = System.currentTimeMillis()
        var datetime = Date().toGMTString()
        datetime = datetime.replace(" ", "")
        datetime = datetime.replace(":", "")
        filename = "Csr_" + datetime + "_" + millis
        return filename
    }

    private fun getBytFromBitmap(bitmap: Bitmap, quality: Int): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        //        bitmap.recycle();
        return stream.toByteArray()
    }

}