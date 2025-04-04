package com.example.michistema.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.michistema.R
import com.example.michistema.data.model.Device
import com.example.michistema.data.model.Request.deviceRequest
import com.example.michistema.data.model.Response.deviceResponse
import com.example.michistema.data.network.ApiService
import com.example.michistema.ui.adapter.DeviceProfileAdapter
import com.example.michistema.ui.auth.LoginActivity
import com.example.michistema.ui.viewmodel.DeviceViewModel
import com.example.michistema.utils.PreferenceHelper
import com.example.michistema.utils.PreferenceHelper.set
import kotlinx.coroutines.launch
import retrofit2.Response

class AllDevicesUserActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var deviceAdapter: DeviceProfileAdapter
    private val deviceViewModel: DeviceViewModel by viewModels()
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_devices_user)



        // Inicialización de los elementos de la interfaz

        val btnBack = findViewById<Button>(R.id.btnBack)
        btnBack.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Inicialización de SwipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)

        // Inicialización del RecyclerView
        recyclerView = findViewById(R.id.recyclerDevicees)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inicialización del adaptador
        deviceAdapter = DeviceProfileAdapter(
            emptyList(),
            onDisableClick = { device -> toggleDeviceState(device, false) },
            onEnableClick = { device -> toggleDeviceState(device, true) },
            onDeleteClick = { device -> deleteDevice(device) }
        )
        recyclerView.adapter = deviceAdapter

        // Cargar los dispositivos al iniciar
        deviceViewModel.getDevices()

        // Observador para dispositivos
        deviceViewModel.devices.observe(this, Observer { deviceResponses ->
            val devices = deviceResponses.map { response ->
                Device(
                    response.id,
                    response.name,
                    response.description,
                    response.code,
                    response.constant,
                    response.active
                )
            }

            // Actualizar la lista de dispositivos en el adaptador
            deviceAdapter.updateList(devices)

            // Detener el refresco
            swipeRefreshLayout.isRefreshing = false
        })

        // Configurar SwipeRefreshLayout para refrescar la lista
        swipeRefreshLayout.setOnRefreshListener {
            deviceViewModel.getDevices()
        }

        }

    // Función para habilitar/deshabilitar dispositivos
    private fun toggleDeviceState(device: Device, enable: Boolean) {
        lifecycleScope.launch {
            try {
                val response = if (enable) {
                    ApiService.create().enableDevice(device.id)
                } else {
                    ApiService.create().disableDevice(device.id)
                }

                if (response.isSuccessful) {
                    val message = if (enable) "Dispositivo activado" else "Dispositivo desactivado"
                    Toast.makeText(this@AllDevicesUserActivity, message, Toast.LENGTH_SHORT).show()
                    deviceViewModel.getDevices() // Refrescar la lista
                } else {
                    Toast.makeText(this@AllDevicesUserActivity, "Error al cambiar estado", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AllDevicesUserActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Función para eliminar un dispositivo
    private fun deleteDevice(device: Device) {
        lifecycleScope.launch {
            try {
                val response = ApiService.create().deleteDevice(device.id)

                if (response.isSuccessful) {
                    Toast.makeText(this@AllDevicesUserActivity, "Dispositivo eliminado", Toast.LENGTH_SHORT).show()
                    deviceViewModel.getDevices() // Refrescar la lista
                } else {
                    Toast.makeText(this@AllDevicesUserActivity, "Error al eliminar", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AllDevicesUserActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Función para hacer logout
    private fun logout() {
        val preferences = PreferenceHelper.defaultPrefs(this)
        preferences["token"] = "" // Limpiar el token en preferencias

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
