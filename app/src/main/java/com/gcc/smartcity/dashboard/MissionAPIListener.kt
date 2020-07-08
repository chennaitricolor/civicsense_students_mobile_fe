package com.gcc.smartcity.dashboard

interface MissionAPIListener {
    fun onSuccess(missionModel: ArrayList<MissionModel>)
    fun onFail(message: String, shouldLogOut: Boolean)
}