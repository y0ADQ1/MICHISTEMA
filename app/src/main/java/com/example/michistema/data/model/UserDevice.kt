package com.example.michistema.data.model


data class UserDevice(
    val id: Int,
    val assigned_at: String,
    val config_id: Int?,
    val user_id: Int,
    val device_id: Int,
    val environment_id: Int?,
    val active: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val device: Device
)


