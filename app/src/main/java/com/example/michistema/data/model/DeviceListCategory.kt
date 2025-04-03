package com.example.michistema.data.model

import com.google.gson.annotations.SerializedName

data class DeviceListCategory(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)
