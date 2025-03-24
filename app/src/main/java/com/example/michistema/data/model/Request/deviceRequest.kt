package com.example.michistema.data.model.Request

data class deviceRequest (
    val createdAt: String,
    val updatedAt: String,
    val id: Int,
    val name: String,
    val description: String,
    val code: String,
    val constant: Int,
    val active: Boolean
)