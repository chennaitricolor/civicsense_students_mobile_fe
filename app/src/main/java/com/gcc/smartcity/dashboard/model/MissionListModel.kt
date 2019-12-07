package com.gcc.smartcity.dashboard.model

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject
import java.util.*

@JsonObject
class MissionListModel {

    @JsonField
    var success: Boolean? = false

    @JsonField
    var tasks: ArrayList<MissionDetailsModel?>? = null

}