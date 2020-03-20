package com.gcc.smartcity.leaderboard

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject

@JsonObject
class LeaderBoardDetailsModel {

    @JsonField
    var _id: String? = null

    @JsonField
    var name: String? = null

    @JsonField
    var rewards: Int? = null

}