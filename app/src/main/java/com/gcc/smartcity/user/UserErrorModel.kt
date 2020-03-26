package com.gcc.smartcity.user

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject

@JsonObject
class UserErrorModel {

    @JsonField
    var message: String? = null

}