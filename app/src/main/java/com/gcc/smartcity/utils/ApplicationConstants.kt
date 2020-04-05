package com.gcc.smartcity.utils

import com.gcc.smartcity.BuildConfig

class ApplicationConstants {
    var HOST: String = BuildConfig.HOST
    val LOGIN_POSTFIX = "user/login"
    val CSR_COOKIE_SESSION_KEY_NAME = "csr-api-be"

    companion object {
        val INPUTTYPE_String = "String"
        val INPUTTYPE_NUMBER = "Number"
        val INPUTTYPE_dropdown = "dropdown"
        var editTextId: Int = 1000
        var spinnerId: Int = 2000
    }
}