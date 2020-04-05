package com.gcc.smartcity.dashboard.model

import android.widget.EditText
import android.widget.Spinner


data class DynamicFormUserInputData(
    var id: Int,
    var label: String,
    var reqiured: Boolean,
    var isEdittext:Boolean,
    var editText: EditText,
    var spinner: Spinner
)