package com.example.michistema.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.michistema.data.model.Response.deviceResponse
import com.example.michistema.data.network.ApiService

class DeviceRepository(private val apiService: ApiService) {

    private val _devices = MutableLiveData<List<deviceResponse>>()
    val devices: LiveData<List<deviceResponse>> get() = _devices

    suspend fun fetchDevices() {
        try {
            val response = apiService.getDevices()
            if (response.isSuccessful) {
                _devices.postValue(response.body())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
