package com.gcc.smartcity.leaderboard

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject

@JsonObject
class LeaderBoardErrorModel {

    @JsonField
    var message: String? = null

}