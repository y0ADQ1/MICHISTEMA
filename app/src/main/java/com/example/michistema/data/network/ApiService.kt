package com.example.michistema.data.network

import com.example.michistema.data.model.Device
import com.example.michistema.data.model.DeviceCategory
import com.example.michistema.data.model.DeviceListCategory
import com.example.michistema.data.model.Environment
import com.example.michistema.data.model.Request.LoginRequest
import com.example.michistema.data.model.Response.LoginResponse
import com.example.michistema.data.model.Request.LogoutRequest
import com.example.michistema.data.model.Request.RegisterRequest
import com.example.michistema.data.model.Request.UpdateUserRequest
import com.example.michistema.data.model.Request.deviceRequest
import com.example.michistema.data.model.Response.LogoutResponse
import com.example.michistema.data.model.Response.RegisterResponse
import com.example.michistema.data.model.Response.deviceResponse
import com.example.michistema.data.model.Request.UserDeviceRequest
import com.example.michistema.data.model.Response.UserDeviceResponse
import com.example.michistema.data.model.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Auth endpoints
    @POST("auth/login") //login
    suspend fun postLogin(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("auth/register") //registrar
    suspend fun postRegister(@Body registerRequest: RegisterRequest): Response<RegisterResponse>

    @POST("auth/logout") //cerrar sesión
    suspend fun postLogout(@Body logoutRequest: LogoutRequest): Response<LogoutResponse>

    // Devices endpoints
    @PUT("device/disable/{id}") //desactivar dispositivo
    suspend fun disableDevice(@Path("id") id: Int): Response<Void>

    @PUT("device/enable/{id}") //activar dispositivo
    suspend fun enableDevice(@Path("id") id: Int): Response<Void>

    @DELETE("device/{id}") //eliminar dispositivo
    suspend fun deleteDevice(@Path("id") id: Int): Response<Void>

    @POST("device/") //crear dispositivo
    suspend fun postDevice(@Body deviceRequest: deviceRequest): Response<deviceResponse>



    //pa la listilla de filtrado de dispositivos
    @GET("device/") // Asegúrate de que sea la ruta correcta
    fun obtenerDispositivos(): Call<List<DeviceListCategory>>

    @GET("device/") //obtener todos los dispositivos en general
    suspend fun getDevices(): Response<List<deviceResponse>>

    @POST("device/user/{user_id}/device/{device_id}")
    suspend fun asignarDispositivo(
        @Path("user_id") userId: Int,
        @Path("device_id") deviceId: Int
    ): Response<UserDeviceResponse>

    @PUT("users/{userId}") //actualizar usuario
    suspend fun updateUser(
        @Path("userId") userId: String,
        @Body updateUserRequest: UpdateUserRequest,
        @Header("Authorization") token: String
    ): Response<User>

    // Environment endpoints
    @GET("environments/user/{userId}")
        suspend fun getEnvironmentsByUserId(
        @Path("userId") userId: Int
        ): Response<List<Environment>>

    companion object {
        fun create(): ApiService {
            return NetworkClient.retrofit.create(ApiService::class.java)
        }
    }
}

