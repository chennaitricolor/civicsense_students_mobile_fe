package com.gcc.smartcity.dashboard.model

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject

@JsonObject
class FileUploadResponseModel {

    @JsonField
    var message: String? = null
    @JsonField
    var success: Boolean? = false
}