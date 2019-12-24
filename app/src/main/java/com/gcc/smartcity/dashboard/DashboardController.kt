package com.gcc.smartcity.dashboard

import android.content.Context
import bolts.Continuation
import bolts.Task
import com.android.volley.Request
import com.gcc.smartcity.BuildConfig
import com.gcc.smartcity.dashboard.model.MissionListErrorModel
import com.gcc.smartcity.dashboard.model.MissionListModel
import com.gcc.smartcity.leaderboard.LeaderBoardErrorModel
import com.gcc.smartcity.network.JsonResponseParser
import com.gcc.smartcity.network.RequestExecutor
import com.gcc.smartcity.network.VolleyRequest
import com.gcc.smartcity.utils.Logger

class DashboardController(
    private val mContext: Context,
    private var missionAPIListener: MissionAPIListener
) {
    private var list = ArrayList<MissionModel>()

    fun getMissionData(latitude: String, longitude: String): ArrayList<MissionModel> {

        doMissionListCall(
            BuildConfig.HOST + "user/tasks?coordinates=" + longitude + "&coordinates=" + latitude
        )

//        doMissionListCall(
//            BuildConfig.HOST + "user/tasks?coordinates=80.304340&coordinates=13.161376"
//        )
//
//        var missionModel = MissionModel("gdsvgdsgdw3r3fw", "Capture the Water Stagnation", "gadg","agdag",50)
//
//        list.add(missionModel)
////        missionModel = MissionModel("Capture the Water Stagnation", "3")
////
////        list.add(missionModel)
        return list
    }
//
//    fun doMissionListCall(endpoint: String): Task<Any> {
//        val parser = JsonResponseParser(MissionListModel::class.java)
//        val errorResponseParser = JsonResponseParser(LoginErrorModel::class.java)
//        val loginRequest = VolleyRequest.newInstance<LoginModel>(Request.Method.POST, endpoint)
//        val jsonObject = JSONObject()
//        try {
//            jsonObject.put("coordinates", latitude)
//            jsonObject.put("coordinates", longitude)
//        } catch (ex: Exception) {
//            ex.printStackTrace()
//        }
//        loginRequest.setPayload(jsonObject.toString())
//        loginRequest.setResponseParser(parser)
//        loginRequest.setErrorResponseParser(errorResponseParser)
//        return RequestExecutor.getInstance(mContext).makeRequestCall(loginRequest)
//    }

    private fun doMissionListCall(url: String) {
        val volleyRequest = VolleyRequest.newInstance<MissionListModel>(Request.Method.GET, url)
        val jsonResponseParser = JsonResponseParser<MissionListModel>(MissionListModel::class.java)
        val errorResponseParser = JsonResponseParser(MissionListErrorModel::class.java)
        volleyRequest.setResponseParser(jsonResponseParser)
        volleyRequest.setErrorResponseParser(errorResponseParser)
        RequestExecutor.getInstance(mContext).makeRequestCall(volleyRequest).continueWithTask(
            Continuation<Any, Task<Any>> {
                if (!it.isFaulted) {
                    Logger.d("success", "got list")
                    val missionListModel = it.result as MissionListModel
                    if (missionListModel.success!! && missionListModel.tasks!!.isNotEmpty()) {
                        for (i in 0 until (missionListModel.tasks?.size ?: 0)) {
                            val missionModel = MissionModel(
                                missionListModel.tasks?.get(i)?._id.toString(),
                                missionListModel.tasks?.get(i)?.campaignName.toString(),
                                missionListModel.tasks?.get(i)?.startDate.toString(),
                                missionListModel.tasks?.get(i)?.endDate.toString(),
                                missionListModel.tasks?.get(i)?.rewards!!
                            )
                            list.add(missionModel)
                        }
                        missionAPIListener.onSuccess(list)
                    } else if (missionListModel.success!! && missionListModel.tasks!!.isEmpty()) {
                        missionAPIListener.onFail("There are no tasks in your area")
                    }
                } else {
                    val missionListErrorModel = it.result as MissionListErrorModel
                    Logger.d("failed", "unable to fetch mission list")
                    missionAPIListener.onFail(missionListErrorModel.message.toString())
                }
                null
            })
    }

}