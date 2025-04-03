package com.example.michistema.data.model.Request

data class UpdateUserRequest(
    val name: String? = null,
    val email: String? = null,
    val password: String? = null
)