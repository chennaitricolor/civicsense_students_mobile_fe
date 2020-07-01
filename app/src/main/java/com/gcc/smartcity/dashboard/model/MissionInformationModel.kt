package com.gcc.smartcity.dashboard.model

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject
import java.util.*

@JsonObject
class MissionInformationModel {

    @JsonField
    var _id: String? = null

    @JsonField
    var campaignName: String? = null

    @JsonField
    var startDate: String? = null

    @JsonField
    var endDate: String? = null

    @JsonField
    var description: String? = null

    @JsonField
    var rewards: Int? = null

    @JsonField
    var rules: String? = null

    @JsonField
    var needForm: Boolean? = null

    @JsonField
    var needMedia: Boolean? = null

    @JsonField
    var formFields: ArrayList<FormFieldsModel?>? = null
}