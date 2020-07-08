package com.gcc.smartcity.dashboard.model

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject

@JsonObject
class NewMissionListModel {

    @JsonField
    var success: Boolean? = false

    @JsonField
    var task: MissionInformationModel? = null

}