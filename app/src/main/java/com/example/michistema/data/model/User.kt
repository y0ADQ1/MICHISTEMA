package com.example.michistema.data.model


data class User(
    val id: Int,
    val name: String,
    val email: String,
    val activo: Boolean,
    val role_id: Int
)