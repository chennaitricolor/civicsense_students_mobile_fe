package com.gcc.smartcity.dashboard

data class MissionModel(
    var _id: String,
    var campaignName: String,
    var startDate: String,
    var endDate: String,
    var rewards: Int?,
    var rules: String?
)