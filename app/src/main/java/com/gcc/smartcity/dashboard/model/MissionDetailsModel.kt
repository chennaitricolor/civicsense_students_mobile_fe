package com.gcc.smartcity.dashboard.model

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject

@JsonObject
class MissionDetailsModel {

    @JsonField
    var _id: Long? = null

    @JsonField
    var campaignName: String? = null

    @JsonField
    var startDate: String? = null

    @JsonField
    var endDate: String? = null

    @JsonField
    var description: String? = null

    @JsonField
    var rewards: Int? = null

}