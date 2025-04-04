package com.example.michistema.data.model.Response

import com.example.michistema.data.model.Environment

data class CreateEnvironmentResponse(
    val id: Int,
    val name: String,
    val description: String,
    val createdAt: String,
    val message: String,
    val environment: Environment
)
