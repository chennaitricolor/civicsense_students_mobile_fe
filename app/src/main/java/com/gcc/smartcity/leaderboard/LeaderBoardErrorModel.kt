package com.gcc.smartcity.leaderboard

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject
import java.util.*

@JsonObject
class LeaderBoardErrorModel {

    @JsonField
    var message: String? = null

}