package com.example.michistema.data.model.Request

data class CreateEnvironmentRequest(
    val name: String,
    val color: String = "#FFFFFF", // Default color if not provided
    val active: Boolean = true,    // Default to true
    val userId: Int                // Add userId to link to the user
)