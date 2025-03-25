package com.example.michistema.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.michistema.data.model.Response.deviceResponse
import com.example.michistema.data.network.ApiService
import kotlinx.coroutines.launch
import retrofit2.Response

class DeviceViewModel : ViewModel() {

    private val _devices = MutableLiveData<List<deviceResponse>>()
    val devices: LiveData<List<deviceResponse>> get() = _devices

    fun getDevices() {
        viewModelScope.launch {
            val response = ApiService.create().getDevices()
            if (response.isSuccessful) {
                _devices.value = response.body() ?: emptyList()
            } else {
                // Manejo de errores (puedes agregar más detalles)
            }
        }
    }
}
