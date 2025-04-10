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
import com.example.michistema.ui.adapter.DeviceAdminAdapter
import com.example.michistema.ui.auth.LoginActivity
import com.example.michistema.ui.viewmodel.DeviceViewModel
import com.example.michistema.utils.PreferenceHelper
import com.example.michistema.utils.PreferenceHelper.set
import kotlinx.coroutines.launch
import retrofit2.Response

class DashboardAdminActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var deviceAdapter: DeviceAdminAdapter
    private val deviceViewModel: DeviceViewModel by viewModels()
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_admin)


        val etName = findViewById<EditText>(R.id.etName)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val etCode = findViewById<EditText>(R.id.etCode)
        val etConstant = findViewById<EditText>(R.id.etConstant)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        recyclerView = findViewById(R.id.recyclerViewDevices)
        recyclerView.layoutManager = LinearLayoutManager(this)

        deviceAdapter = DeviceAdminAdapter(
            emptyList(),
            onDisableClick = { device ->
                lifecycleScope.launch {
                    try {
                        val response = ApiService.create().disableDevice(device.id)
                        if (response.isSuccessful) {
                            Toast.makeText(this@DashboardAdminActivity,
                                "Dispositivo desactivado",
                                Toast.LENGTH_SHORT).show()
                            deviceViewModel.getDevices() // Refresh the list
                        } else {
                            Toast.makeText(this@DashboardAdminActivity,
                                "Error al desactivar",
                                Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@DashboardAdminActivity,
                            "Error: ${e.message}",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onEnableClick = { device ->
                lifecycleScope.launch {
                    try {
                        val response = ApiService.create().enableDevice(device.id)
                        if (response.isSuccessful) {
                            Toast.makeText(this@DashboardAdminActivity,
                                "Dispositivo activado",
                                Toast.LENGTH_SHORT).show()
                            deviceViewModel.getDevices() // Refresh the list
                        } else {
                            Toast.makeText(this@DashboardAdminActivity,
                                "Error al activar",
                                Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@DashboardAdminActivity,
                            "Error: ${e.message}",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onDeleteClick = { device ->
                lifecycleScope.launch {
                    try {
                        val baseUrl = "http://localhost:3333/v1/api"
                        val deleteUrl = "$baseUrl/delete/${device.id}"

                        Log.d("DashboardAdminActivity", "Deleting device with ID: ${device.id}")
                        Log.d("DashboardAdminActivity", "Using this route: $deleteUrl")
                        val response = ApiService.create().deleteDevice(device.id)
                        if (response.isSuccessful) {
                            Toast.makeText(this@DashboardAdminActivity,
                                "Dispositivo eliminado",
                                Toast.LENGTH_SHORT).show()
                            deviceViewModel.getDevices() // Refresh the list
                        } else {
                            Toast.makeText(this@DashboardAdminActivity,
                                "Error al eliminar",
                                Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@DashboardAdminActivity,
                            "Error: ${e.message}",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
        recyclerView.adapter = deviceAdapter
        deviceViewModel.getDevices()

        deviceViewModel.devices.observe(this, Observer { deviceResponses ->
            // Mapea cada item de deviceResponse a Device
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

            // Actualiza el adaptador con la lista mapeada
            deviceAdapter.updateList(devices)

            // Detén la animación de refresco una vez que los datos se cargan
            swipeRefreshLayout.isRefreshing = false
        })

        val btnLogout: ImageButton = findViewById(R.id.btn_logout)
        btnLogout.setOnClickListener {
            logout()
        }

        // Swipe to refresh handler
        swipeRefreshLayout.setOnRefreshListener {
            deviceViewModel.getDevices() // Refresh devices when swipe happens
        }

        btnSubmit.setOnClickListener {
            val name = etName.text.toString()
            val description = etDescription.text.toString().takeIf { it.isNotBlank() }
            val code = etCode.text.toString().takeIf { it.isNotBlank() }
            val constant = etConstant.text.toString().takeIf { it.isNotBlank() }?.toIntOrNull()

            if (name.isEmpty()) {
                Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val deviceRequest = deviceRequest(name, description, code, constant, true)

            lifecycleScope.launch {
                try {
                    val response: Response<deviceResponse> = ApiService.create().postDevice(deviceRequest)

                    if (response.isSuccessful) {
                        Toast.makeText(this@DashboardAdminActivity, "Dispositivo creado con éxito", Toast.LENGTH_SHORT).show()

                        etName.text.clear()
                        etDescription.text.clear()
                        etCode.text.clear()
                        etConstant.text.clear()

                        deviceViewModel.getDevices()
                    } else {
                        Toast.makeText(this@DashboardAdminActivity, "Error al crear el dispositivo", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@DashboardAdminActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun logout() {
        val preferences = PreferenceHelper.defaultPrefs(this)
        preferences["token"] = ""

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
