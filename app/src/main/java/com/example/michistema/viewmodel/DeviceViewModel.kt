package com.example.michistema.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.michistema.data.model.Environment
import com.example.michistema.data.model.Response.deviceResponse
import com.example.michistema.data.network.ApiService
import kotlinx.coroutines.launch
import retrofit2.Response

class DeviceViewModel : ViewModel() {

    // LiveData for the list of devices
    private val _devices = MutableLiveData<List<deviceResponse>>()
    val devices: LiveData<List<deviceResponse>> get() = _devices

    // LiveData for a single device's environment (optional, if needed)
    private val _environment = MutableLiveData<Environment?>()
    val environment: LiveData<Environment?> get() = _environment

    // Fetch all devices (assuming this uses a different endpoint like @GET("devices"))
    fun getDevices() {
        viewModelScope.launch {
            val response = ApiService.create().getDevices() // Assumes this exists in ApiService
            if (response.isSuccessful) {
                _devices.value = response.body() ?: emptyList()
            } else {
                // Handle error (e.g., log it or notify UI)
            }
        }
    }

    // Fetch environment data for a specific device using the provided route
    fun getDeviceEnvironment(userId: Int, deviceId: Int) {
        viewModelScope.launch {
            val response = ApiService.create().getDispositivoEntorno(userId, deviceId)
            if (response.isSuccessful) {
                _environment.value = response.body()
            } else {
                _environment.value = null // Handle error case
                // Optionally log or notify UI of failure
            }
        }
    }
}