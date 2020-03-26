package com.gcc.smartcity.user

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject

@JsonObject
class UserModel {

    @JsonField
    var rewards: Int? = null

    @JsonField
    var _id: Long? = null

    @JsonField
    var defaultLocation: String? = null

    @JsonField
    var name: String? = null
}