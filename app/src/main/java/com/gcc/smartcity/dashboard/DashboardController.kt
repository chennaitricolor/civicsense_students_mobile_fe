package com.gcc.smartcity.dashboard

import android.content.Context

class DashboardController(context: Context) {


    fun getMissionData(): ArrayList<MissionModel> {
        var list = ArrayList<MissionModel>()
        var missionModel = MissionModel("Capture the potholes", "5")

        list.add(missionModel)
        missionModel = MissionModel("Capture the Water Stagnation", "3")

        list.add(missionModel)
        return list
    }

}