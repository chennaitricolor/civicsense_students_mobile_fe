package com.gcc.smartcity.loginandregistration.controller

import android.content.Context
import bolts.Task
import com.android.volley.Request
import com.gcc.smartcity.BuildConfig
import com.gcc.smartcity.leaderboard.LeaderBoardErrorModel
import com.gcc.smartcity.leaderboard.LeaderBoardModel
import com.gcc.smartcity.location.LocationErrorModel
import com.gcc.smartcity.location.LocationModel
import com.gcc.smartcity.loginandregistration.model.*
import com.gcc.smartcity.network.JsonResponseParser
import com.gcc.smartcity.network.RequestExecutor
import com.gcc.smartcity.network.VolleyRequest
import com.gcc.smartcity.user.UserErrorModel
import com.gcc.smartcity.user.UserModel
import com.gcc.smartcity.utils.Logger
import org.json.JSONArray
import org.json.JSONObject

class LoginAndRegistrationController(private val mContext: Context) {

    fun doOTPCall(endpoint: String): Task<Any> {
        val parser = JsonResponseParser(OTPModel::class.java)
        val errorResponseParser = JsonResponseParser(OTPErrorModel::class.java)
        val otpRequest = VolleyRequest.newInstance<OTPModel>(Request.Method.GET, endpoint)
        otpRequest.setResponseParser(parser)
        otpRequest.setErrorResponseParser(errorResponseParser)
        return RequestExecutor.getInstance(mContext).makeRequestCall(otpRequest)
    }

    fun doLeaderBoardCall(endpoint: String): Task<Any> {
        val parser = JsonResponseParser(LeaderBoardModel::class.java)
        val errorResponseParser = JsonResponseParser(LeaderBoardErrorModel::class.java)
        val leaderBoardRequest = VolleyRequest.newInstance<LeaderBoardModel>(Request.Method.GET, endpoint)
        leaderBoardRequest.setResponseParser(parser)
        leaderBoardRequest.setErrorResponseParser(errorResponseParser)
        return RequestExecutor.getInstance(mContext).makeRequestCall(leaderBoardRequest)
    }

    fun doUserCall(endpoint: String): Task<Any> {
        val parser = JsonResponseParser(UserModel::class.java)
        val errorResponseParser = JsonResponseParser(UserErrorModel::class.java)
        val userDetailsRequest = VolleyRequest.newInstance<UserModel>(Request.Method.GET, endpoint)
        userDetailsRequest.setResponseParser(parser)
        userDetailsRequest.setErrorResponseParser(errorResponseParser)
        return RequestExecutor.getInstance(mContext).makeRequestCall(userDetailsRequest)
    }

    fun doUserLocationValidationCall(endpoint: String): Task<Any> {
        val parser = JsonResponseParser(LocationModel::class.java)
        val errorResponseParser = JsonResponseParser(LocationErrorModel::class.java)
        val userLocationValidationRequest = VolleyRequest.newInstance<LocationModel>(Request.Method.GET, endpoint)
        userLocationValidationRequest.setResponseParser(parser)
        userLocationValidationRequest.setErrorResponseParser(errorResponseParser)
        return RequestExecutor.getInstance(mContext).makeRequestCall(userLocationValidationRequest)
    }

    fun doLoginCall(endpoint: String, mobileNumber: String, OTP: Int, userPersona: String): Task<Any> {
        val parser = JsonResponseParser(LoginModel::class.java)
        val errorResponseParser = JsonResponseParser(LoginErrorModel::class.java)
        val loginRequest = VolleyRequest.newInstance<LoginModel>(Request.Method.POST, endpoint)
        val jsonObject = JSONObject()
        try {
            jsonObject.put("userId", mobileNumber)
            jsonObject.put("otp", OTP)
            jsonObject.put("persona", userPersona)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        loginRequest.setPayload(jsonObject.toString())
        loginRequest.setResponseParser(parser)
        loginRequest.setErrorResponseParser(errorResponseParser)
        return RequestExecutor.getInstance(mContext).makeRequestCall(loginRequest)
    }

    fun doSignUpCall(
        endpoint: String,
        name: String,
        userPersona: String,
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
            jsonObject.put("name", name)
            jsonObject.put("persona", userPersona)
            jsonObject.put("userId", phoneNumber)
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

    fun doProfileNameUpdate(endpoint: String, newProfileName: String): Task<Any> {
        val parser = JsonResponseParser(UserUpdateModel::class.java)
        val errorResponseParser = JsonResponseParser(UserUpdateErrorModel::class.java)
        val userUpdateRequest = VolleyRequest.newInstance<LoginModel>(Request.Method.PUT, endpoint)
        val jsonObject = JSONObject()
        val newValuesObject = JSONObject()
        try {
            newValuesObject.put("name", newProfileName)
            jsonObject.put("newValues", newValuesObject)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        userUpdateRequest.setPayload(jsonObject.toString())
        userUpdateRequest.setResponseParser(parser)
        userUpdateRequest.setErrorResponseParser(errorResponseParser)
        return RequestExecutor.getInstance(mContext).makeRequestCall(userUpdateRequest)
    }

}