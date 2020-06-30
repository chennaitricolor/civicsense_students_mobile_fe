package com.gcc.smartcity.root

import android.content.Context
import bolts.Task
import com.android.volley.Request
import com.gcc.smartcity.dashboard.model.root.RootApiModel
import com.gcc.smartcity.network.JsonResponseParser
import com.gcc.smartcity.network.RequestExecutor
import com.gcc.smartcity.network.VolleyRequest

class RootController(private val mContext: Context) {

    fun doUserCall(endpoint: String): Task<Any> {
        val parser = JsonResponseParser(RootModel::class.java)
        val errorResponseParser = JsonResponseParser(RootErrorModel::class.java)
        val rootRequest = VolleyRequest.newInstance<RootModel>(Request.Method.GET, endpoint)
        rootRequest.setResponseParser(parser)
        rootRequest.setErrorResponseParser(errorResponseParser)
        return RequestExecutor.getInstance(mContext).makeRequestCall(rootRequest)
    }

    fun doRootCall(): Task<Any> {
        val parser = JsonResponseParser(RootApiModel::class.java)
        val errorResponseParser = JsonResponseParser(RootErrorModel::class.java)
        val rootRequest = VolleyRequest.newInstance<String>(
            Request.Method.GET,
            "https://api.dev.gccservice.in/api/v2/csr/"
        )
//        rootRequest.setResponseParser(parser)
        rootRequest.setErrorResponseParser(errorResponseParser)
        return RequestExecutor.getInstance(mContext).makeRequestCall(rootRequest)
    }

}