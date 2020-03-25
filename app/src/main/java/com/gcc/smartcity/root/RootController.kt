package com.gcc.smartcity.root

import android.content.Context
import bolts.Task
import com.android.volley.Request
import com.gcc.smartcity.network.JsonResponseParser
import com.gcc.smartcity.network.RequestExecutor
import com.gcc.smartcity.network.VolleyRequest

class RootController(private val mContext: Context) {

    fun doRootCall(endpoint: String): Task<Any> {
        val parser = JsonResponseParser(RootModel::class.java)
        val errorResponseParser = JsonResponseParser(RootErrorModel::class.java)
        val rootRequest = VolleyRequest.newInstance<RootModel>(Request.Method.GET, endpoint)
        rootRequest.setResponseParser(parser)
        rootRequest.setErrorResponseParser(errorResponseParser)
        return RequestExecutor.getInstance(mContext).makeRequestCall(rootRequest)
    }

}