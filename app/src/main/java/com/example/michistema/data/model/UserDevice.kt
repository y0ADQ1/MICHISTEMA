package com.example.michistema.data.model


data class UserDevice(
    val assigned_at: AssignedAt,
    val id: Int,
    val user_id: String,
    val device_id: String,
    val active: Boolean,
    val updatedAt: String,
    val createdAt: String
)


data class AssignedAt(
    val `val`: String
)