package com.gcc.smartcity.leaderboard

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject
import java.util.*

@JsonObject
class LeaderBoardModel {

    @JsonField
    var success: Boolean? = false

    @JsonField
    var leaderboard: ArrayList<LeaderBoardDetailsModel?>? = null

    @JsonField
    var userRank: Int? = null
}