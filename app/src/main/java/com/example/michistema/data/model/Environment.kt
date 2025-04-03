package com.example.michistema.data.model
import com.google.gson.annotations.SerializedName

data class Environment(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("color") val color: String?,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("active") val active: Boolean,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String
)