package com.example.michistema.data.model.Response

import com.example.michistema.data.model.UserDevice

data class UserDeviceResponse(
    val message: String,
    val userDevice: UserDevice
)