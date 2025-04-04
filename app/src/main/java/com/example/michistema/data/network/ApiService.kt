package com.example.michistema.data.network

import com.example.michistema.data.model.Device
import com.example.michistema.data.model.DeviceCategory
import com.example.michistema.data.model.DeviceListCategory
import com.example.michistema.data.model.Environment
import com.example.michistema.data.model.Request.CreateEnvironmentRequest
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
import com.example.michistema.data.model.Response.CreateEnvironmentResponse
import com.example.michistema.data.model.Response.UserDeviceResponse
import com.example.michistema.data.model.User
import com.example.michistema.data.model.UserDevice
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


    @GET("device/") // Asegúrate de que sea la ruta correcta
    fun obtenerDispositivos(): Call<List<DeviceListCategory>>

    @GET("device/") //obtener todos los dispositivos en general
    suspend fun getDevices(): Response<List<deviceResponse>>

    @GET("device/{user_id}/device/{device_id}/entorno")
    suspend fun getDispositivoEntorno(
        @Path("user_id") userId: Int,
        @Path("device_id") deviceId: Int
    ): Response<Environment>




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


        //crear entorno vinculado a un usuario
        @POST("environments")
        suspend fun createEnvironment(
            @Header("Authorization") token: String,
            @Body request: CreateEnvironmentRequest
        ): Response<CreateEnvironmentResponse>


    @GET("users/{userId}/devices")
    suspend fun getDevicesByUserId(@Path("userId") userId: Int): Response<List<Device>>

    @POST("device/asignar/{userId}/{deviceId}/{environmentId}")
    suspend fun asignarDispositivoConEntorno(
        @Path("userId") userId: Int,
        @Path("deviceId") deviceId: Int,
        @Path("environmentId") environmentId: Int
    ): Response<UserDeviceResponse>

    @DELETE("environments/{id}")
    suspend fun deleteEnvironment(@Path("id") id: Int): Response<Void>

    @GET("environments/{id}/user-devices")
    suspend fun getUserDevices(@Path("id") id: Int): Response<List<UserDevice>>

    companion object {
        fun create(): ApiService {
            return NetworkClient.retrofit.create(ApiService::class.java)
        }
    }
}

