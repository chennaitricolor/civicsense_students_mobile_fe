package com.gcc.smartcity.dashboard.form

import android.content.Context
import bolts.Task
import com.android.volley.Request
import com.gcc.smartcity.dashboard.model.NewMissionListErrorModel
import com.gcc.smartcity.dashboard.model.NewMissionListModel
import com.gcc.smartcity.network.JsonResponseParser
import com.gcc.smartcity.network.RequestExecutor
import com.gcc.smartcity.network.VolleyRequest

class FormDataController(private val mContext: Context) {

    fun postInfoToServer(url: String): Task<Any>? {
        val parser = JsonResponseParser(NewMissionListModel::class.java)
        val errorResponseParser = JsonResponseParser(NewMissionListErrorModel::class.java)
        val request = VolleyRequest.newInstance<NewMissionListModel>(Request.Method.GET, url)
        request.setResponseParser(parser)
        request.setErrorResponseParser(errorResponseParser)
        return RequestExecutor.getInstance(mContext).makeRequestCall(request)
    }

}