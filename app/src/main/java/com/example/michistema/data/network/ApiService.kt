package com.example.michistema.data.network

import com.example.michistema.data.model.Request.LoginRequest
import com.example.michistema.data.model.Response.LoginResponse
import com.example.michistema.data.model.Request.LogoutRequest
import com.example.michistema.data.model.Request.RegisterRequest
import com.example.michistema.data.model.Request.deviceRequest
import com.example.michistema.data.model.Response.LogoutResponse // Importar LogoutResponse
import com.example.michistema.data.model.Response.RegisterResponse
import com.example.michistema.data.model.Response.deviceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {


    //auth
    @POST("auth/login")
    suspend fun postLogin(@Body loginRequest: LoginRequest): Response<LoginResponse>
    @POST("auth/register")
    suspend fun postRegister(@Body registerRequest: RegisterRequest): Response<RegisterResponse>
    @POST("auth/logout")
    suspend fun postLogout(@Body logoutRequest: LogoutRequest): Response<LogoutResponse>

    //devices
    @POST("device/devices")
    suspend fun postDevice(@Body deviceRequest: deviceRequest): Response<deviceResponse>


    companion object {
        fun create(): ApiService {
            return NetworkClient.retrofit.create(ApiService::class.java)
        }
    }
}
