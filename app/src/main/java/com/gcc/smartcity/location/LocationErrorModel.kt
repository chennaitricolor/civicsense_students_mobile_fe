package com.gcc.smartcity.location

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject

@JsonObject
class LocationErrorModel {

    @JsonField
    var message: String? = null

}