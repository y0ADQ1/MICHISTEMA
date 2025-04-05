package com.example.michistema.data.model

data class DeviceCategory(
    val name: String,
    val iconResId: Int,
    val layoutResId: Int,
    val categoryType: String,
    val environment: String? = null,
    val cleaningInterval: String? = null,
    val foodAmount: String? = null
)