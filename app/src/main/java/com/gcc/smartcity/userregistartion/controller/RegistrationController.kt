package com.gcc.smartcity.userregistartion.controller

import android.content.Context
import bolts.Task
import com.android.volley.Request
import com.gcc.smartcity.network.JsonResponseParser
import com.gcc.smartcity.network.RequestExecutor
import com.gcc.smartcity.network.VolleyRequest
import com.gcc.smartcity.userregistartion.model.*
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

    fun doSignUpCall(endpoint: String, name: String, phoneNumber: String, password: String, userId: String, email: String, dateOfBirth: String, avatar: Int, OTP: Int, latitude: String, longitude: String): Task<Any> {
        val parser = JsonResponseParser(SignUpModel::class.java)
        val errorResponseParser = JsonResponseParser(SignUpErrorModel::class.java)
        val loginRequest = VolleyRequest.newInstance<SignUpModel>(Request.Method.POST, endpoint)
        val jsonObject = JSONObject()
        val addressObject = JSONObject()
        try {
            addressObject.put("coordinates",[latitude,longitude])
            jsonObject.put("address",addressObject)
            jsonObject.put("name", name)
            jsonObject.put("phoneNumber", phoneNumber)
            jsonObject.put("password", password)
            jsonObject.put("userId", userId)
            jsonObject.put("email", email)
            jsonObject.put("dateOfBirth", dateOfBirth)
            jsonObject.put("avatar", avatar)
            jsonObject.put("OTP", OTP)
            jsonObject.put("currentLocation", addressObject)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        loginRequest.setPayload(jsonObject.toString())
        loginRequest.setResponseParser(parser)
        loginRequest.setErrorResponseParser(errorResponseParser)
        return RequestExecutor.getInstance(mContext).makeRequestCall(loginRequest)
    }

    fun checkUserNameExistsCall(endpoint: String): Task<Any> {
        val parser = JsonResponseParser(userNameCheckModel::class.java)
        val errorResponseParser = JsonResponseParser(userNameErrorModel::class.java)
        val userNameCheckRequest = VolleyRequest.newInstance<userNameCheckModel>(Request.Method.GET, endpoint)
        userNameCheckRequest.setResponseParser(parser)
        userNameCheckRequest.setErrorResponseParser(errorResponseParser)
        return RequestExecutor.getInstance(mContext).makeRequestCall(userNameCheckRequest)
    }

}