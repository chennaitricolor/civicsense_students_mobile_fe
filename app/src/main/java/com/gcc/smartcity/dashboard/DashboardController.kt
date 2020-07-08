package com.gcc.smartcity.dashboard

import android.content.Context
import bolts.Continuation
import bolts.Task
import com.android.volley.Request
import com.gcc.smartcity.BuildConfig
import com.gcc.smartcity.dashboard.model.MissionListErrorModel
import com.gcc.smartcity.dashboard.model.MissionListModel
import com.gcc.smartcity.network.JsonResponseParser
import com.gcc.smartcity.network.RequestExecutor
import com.gcc.smartcity.network.VolleyRequest
import com.gcc.smartcity.utils.Logger
import com.gcc.smartcity.utils.NetworkError

class DashboardController(
    private val mContext: Context,
    private var missionAPIListener: MissionAPIListener
) {
    private var list = ArrayList<MissionModel>()

    fun getMissionData(latitude: String, longitude: String): ArrayList<MissionModel> {

        doMissionListCall(
            BuildConfig.HOST + "user/tasks?coordinates=" + longitude + "&coordinates=" + latitude
        )

        return list
    }

    private fun doMissionListCall(url: String) {
        val volleyRequest = VolleyRequest.newInstance<MissionListModel>(Request.Method.GET, url)
        val jsonResponseParser = JsonResponseParser(MissionListModel::class.java)
        val errorResponseParser = JsonResponseParser(MissionListErrorModel::class.java)
        volleyRequest.setResponseParser(jsonResponseParser)
        volleyRequest.setErrorResponseParser(errorResponseParser)
        RequestExecutor.getInstance(mContext).makeRequestCall(volleyRequest).continueWithTask(
            Continuation<Any, Task<Any>> {
                if (!it.isFaulted) {
                    Logger.d("success", "got list")
                    val missionListModel = it.result as MissionListModel
                    if (missionListModel.success!!) {
                        if (missionListModel.tasks!!.isNotEmpty()) {
                            for (i in 0 until (missionListModel.tasks?.size ?: 0)) {
                                val missionModel = MissionModel(
                                    missionListModel.tasks?.get(i)?._id.toString(),
                                    missionListModel.tasks?.get(i)?.campaignName.toString(),
                                    missionListModel.tasks?.get(i)?.startDate.toString(),
                                    missionListModel.tasks?.get(i)?.endDate.toString(),
                                    missionListModel.tasks?.get(i)?.rewards,
                                    missionListModel.tasks?.get(i)?.rules
                                )
                                list.add(missionModel)
                            }
                            missionAPIListener.onSuccess(list)
                        } else {
                            missionAPIListener.onFail("There are no tasks in your area", false)
                        }
                    } else if (!missionListModel.success!!) {
                        missionAPIListener.onFail("Server down. Please try again later.", false)
                    }
                } else {
                    val error = it.error as NetworkError
                    missionAPIListener.onFail(
                        "No Tasks/Activities found in your area",
                        error.errorCode == 401
                    )
                    Logger.d("Failed", "Mission call failed")
                }
                null
            })
    }

}