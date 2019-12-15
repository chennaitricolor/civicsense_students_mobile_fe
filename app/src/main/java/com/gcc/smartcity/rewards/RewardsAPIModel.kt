package com.gcc.smartcity.rewards

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject
import java.util.*

@JsonObject
class RewardsAPIModel {

    @JsonField
    var success: Boolean? = false

    @JsonField
    var rewards: ArrayList<RewardsDetailsModel?>? = null

}