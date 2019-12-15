package com.gcc.smartcity.rewards

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject

@JsonObject
class RewardsDetailsModel {

    @JsonField
    var _id: String? = null

    @JsonField
    var title: String? = null

    @JsonField
    var description: String? = null

    @JsonField
    var gems: Int? = null

    @JsonField
    var validTill: String? = null

    @JsonField
    var photoId: String? = null
}