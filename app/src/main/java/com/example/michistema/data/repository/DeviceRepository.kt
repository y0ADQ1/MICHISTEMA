package com.example.michistema.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.michistema.data.model.Response.deviceResponse

class DeviceRepository {

    private val devices = mutableListOf<deviceResponse>(
        deviceResponse("2024-01-01", "2024-01-10", 1, "Sensor de temperatura", "Detecta cambios de temperatura", "SENSOR_TEMP", 100, true),
        deviceResponse("2024-01-02", "2024-01-10", 2, "Cámara de seguridad", "Monitoreo en tiempo real", "CAM_SEC", 200, false)
    )

    private val devicesLiveData = MutableLiveData<List<deviceResponse>>(devices)

    fun getDevices(): LiveData<List<deviceResponse>> = devicesLiveData

    fun addDevice(device: deviceResponse) {
        devices.add(device)
        devicesLiveData.value = devices
    }
}
