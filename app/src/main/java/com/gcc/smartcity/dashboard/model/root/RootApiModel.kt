package com.gcc.smartcity.dashboard.model.root

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject

@JsonObject
class RootApiModel {

    @JsonField
    var links: Links? = null

    @JsonField
    var aboutus: String? = null

    @JsonField
    var termsAndCondition: String? = null

    @JsonField
    var version: String? = null

    @JsonField
    var region:JsonObject?=null

}