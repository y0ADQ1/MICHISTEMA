package com.example.michistema.data.model.Response

import com.example.michistema.data.model.User

data class RegisterResponse(
    val status: Int,
    val message: String,
    val user: User,
    val token: String,
    val tokenExpiration: String
)