package com.gcc.smartcity.dashboard

import bolts.Task

interface MissionAPIListener {
    fun onSuccess(missionModel: ArrayList<MissionModel>)
    fun onFail(message: String, task: Task<Any>)
}