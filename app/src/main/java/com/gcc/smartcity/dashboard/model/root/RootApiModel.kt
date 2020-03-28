package com.gcc.smartcity.dashboard.model.root

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject

@JsonObject
class RootApiModel {

    @JsonField
    public var links: Links? = null

    @JsonField
    public var aboutus: String? = null

    @JsonField
    public var termsAndCondition: String? = null

    @JsonField
    public var version: String? = null

}