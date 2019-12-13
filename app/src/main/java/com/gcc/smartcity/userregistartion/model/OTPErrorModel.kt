package com.gcc.smartcity.userregistartion.model

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject

@JsonObject
class OTPErrorModel {

    @JsonField
    var message: String? = null

}