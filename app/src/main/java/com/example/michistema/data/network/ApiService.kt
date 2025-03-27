package com.example.michistema.data.network

import com.example.michistema.data.model.Request.LoginRequest
import com.example.michistema.data.model.Response.LoginResponse
import com.example.michistema.data.model.Request.LogoutRequest
import com.example.michistema.data.model.Request.RegisterRequest
import com.example.michistema.data.model.Request.deviceRequest
import com.example.michistema.data.model.Response.LogoutResponse
import com.example.michistema.data.model.Response.RegisterResponse
import com.example.michistema.data.model.Response.deviceResponse
import com.example.michistema.data.model.Response.environmentsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiService {

    // Auth endpoints
    @POST("auth/login")
    suspend fun postLogin(@Body loginRequest: LoginRequest): Response<LoginResponse>
    @POST("auth/register")
    suspend fun postRegister(@Body registerRequest: RegisterRequest): Response<RegisterResponse>
    @POST("auth/logout")
    suspend fun postLogout(@Body logoutRequest: LogoutRequest): Response<LogoutResponse>

    // Devices endpoints
    @POST("device/")
    suspend fun postDevice(@Body deviceRequest: deviceRequest): Response<deviceResponse>
    @GET("device/")
    suspend fun getDevices(): Response<List<deviceResponse>>
    @PUT("disable/")
    suspend fun putDisable(@Body deviceRequest: deviceRequest): Response<deviceResponse>
    @DELETE("device/")
    suspend fun deleteDevice(@Body deviceRequest: deviceRequest): Response<deviceResponse>

    // Eenvironments endpoints
    @GET("environments/")
    suspend fun getEnvironments(): Response<List<environmentsResponse>>



    companion object {
        fun create(): ApiService {
            return NetworkClient.retrofit.create(ApiService::class.java)
        }
    }
}
