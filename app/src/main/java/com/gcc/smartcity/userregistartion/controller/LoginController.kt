package com.gcc.smartcity.userregistartion.controller

import android.content.Context
import bolts.Task
import com.android.volley.Request
import com.gcc.smartcity.network.JsonResponseParser
import com.gcc.smartcity.network.RequestExecutor
import com.gcc.smartcity.network.VolleyRequest
import com.gcc.smartcity.userregistartion.model.LoginErrorModel
import com.gcc.smartcity.userregistartion.model.LoginModel
import org.json.JSONObject

class LoginController(private val mContext: Context) {

    fun doLoginCall(endpoint: String, username: String, password: String): Task<Any> {
        val parser = JsonResponseParser(LoginModel::class.java)
        val errorResponseParser = JsonResponseParser(LoginErrorModel::class.java)
        val loginRequest = VolleyRequest.newInstance<LoginModel>(Request.Method.POST, endpoint)
        val jsonObject = JSONObject()
        try {
            jsonObject.put("password", password)
            jsonObject.put("userId", username)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        loginRequest.setPayload(jsonObject.toString())
        loginRequest.setResponseParser(parser)
        loginRequest.setErrorResponseParser(errorResponseParser)
        return RequestExecutor.getInstance(mContext).makeRequestCall(loginRequest)
    }

}