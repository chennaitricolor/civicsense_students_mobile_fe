package com.gcc.smartcity.root

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject

@JsonObject
class RootErrorModel {

    @JsonField
    var message: String? = null

}