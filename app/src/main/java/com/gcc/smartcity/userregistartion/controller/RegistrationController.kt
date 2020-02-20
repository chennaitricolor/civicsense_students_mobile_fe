package com.gcc.smartcity.userregistartion.controller

import android.content.Context
import bolts.Task
import com.android.volley.Request
import com.gcc.smartcity.network.JsonResponseParser
import com.gcc.smartcity.network.RequestExecutor
import com.gcc.smartcity.network.VolleyRequest
import com.gcc.smartcity.userregistartion.model.*
import com.gcc.smartcity.utils.Logger
import org.json.JSONArray
import org.json.JSONObject

class RegistrationController(private val mContext: Context) {

    fun doOTPCall(endpoint: String): Task<Any> {
        val parser = JsonResponseParser(OTPModel::class.java)
        val errorResponseParser = JsonResponseParser(OTPErrorModel::class.java)
        val otpRequest = VolleyRequest.newInstance<OTPModel>(Request.Method.GET, endpoint)
        otpRequest.setResponseParser(parser)
        otpRequest.setErrorResponseParser(errorResponseParser)
        return RequestExecutor.getInstance(mContext).makeRequestCall(otpRequest)
    }

    fun doSignUpCall(
        endpoint: String,
        name: String,
        phoneNumber: String,
        OTP: Int,
        latitude: String,
        longitude: String
    ): Task<Any> {
        val parser = JsonResponseParser(SignUpModel::class.java)
        val errorResponseParser = JsonResponseParser(SignUpErrorModel::class.java)
        val loginRequest = VolleyRequest.newInstance<SignUpModel>(Request.Method.POST, endpoint)
        val jsonObject = JSONObject()
        val addressObject = JSONObject()
        val coordinatesArray = JSONArray()
        try {
            coordinatesArray.put(longitude)
            coordinatesArray.put(latitude)
            addressObject.put("coordinates", coordinatesArray)
            //jsonObject.put("address",addressObject)
            jsonObject.put("name", name)
            jsonObject.put("phoneNumber", phoneNumber)
            jsonObject.put("otp", OTP)
            jsonObject.put("currentLocation", addressObject)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        Logger.d("" + jsonObject.toString())
        loginRequest.setPayload(jsonObject.toString())
        loginRequest.setResponseParser(parser)
        loginRequest.setErrorResponseParser(errorResponseParser)
        return RequestExecutor.getInstance(mContext).makeRequestCall(loginRequest)
    }

    fun checkUserNameExistsCall(endpoint: String): Task<Any> {
        val parser = JsonResponseParser(UserNameCheckModel::class.java)
        val errorResponseParser = JsonResponseParser(UserNameErrorModel::class.java)
        val userNameCheckRequest =
            VolleyRequest.newInstance<UserNameCheckModel>(Request.Method.GET, endpoint)
        userNameCheckRequest.setResponseParser(parser)
        userNameCheckRequest.setErrorResponseParser(errorResponseParser)
        return RequestExecutor.getInstance(mContext).makeRequestCall(userNameCheckRequest)
    }

    fun forgotUserId(endpoint: String): Task<Any> {
        val parser = JsonResponseParser(ForgotUserNameModel::class.java)
        val userNameRequest =
            VolleyRequest.newInstance<ForgotUserNameModel>(Request.Method.GET, endpoint)
        userNameRequest.setResponseParser(parser)
        return RequestExecutor.getInstance(mContext).makeRequestCall(userNameRequest)
    }

}