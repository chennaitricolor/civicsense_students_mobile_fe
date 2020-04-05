package com.gcc.smartcity.dashboard.model

data class DynamicFormData(
    var labelName: String,
    var type: String,
    var required: Boolean,
    var data: ArrayList<String>?
)