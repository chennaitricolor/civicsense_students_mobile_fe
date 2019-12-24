package com.gcc.smartcity.dashboard

interface ImageUploadListener {

    fun onSuccess()
    fun onFailure(message:String)
}