package com.gcc.smartcity.loginandregistration.model

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject

@JsonObject
class OTPModel {

    @JsonField
    var success: Boolean? = false

}