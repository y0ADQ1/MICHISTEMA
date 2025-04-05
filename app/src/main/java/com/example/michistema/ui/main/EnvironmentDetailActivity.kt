package com.example.michistema.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.michistema.data.network.ApiService
import com.example.michistema.databinding.ActivityEnvironmentDetailBinding
import com.example.michistema.ui.adapters.DeviceAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import android.widget.EditText

class EnvironmentDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEnvironmentDetailBinding
    private lateinit var deviceAdapter: DeviceAdapter
    private val apiService = ApiService.create()
    private var environmentId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnvironmentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        deviceAdapter = DeviceAdapter(emptyList()) { device ->
            val intent = Intent(this, DeviceDetailAreneroActivity::class.java).apply {
                putExtra("device_id", device.device.id)
                putExtra("device_name", device.device.name)
            }
            startActivity(intent)
        }

        binding.recyclerDevices.apply {
            layoutManager = GridLayoutManager(this@EnvironmentDetailActivity, 3)
            adapter = deviceAdapter
        }

        val environmentName = intent.getStringExtra("environment_name") ?: "Nombre no disponible"
        environmentId = intent.getIntExtra("environment_id", -1)
        val userId = intent.getIntExtra("user_id", -1)

        binding.txtEnvironmentName.text = environmentName

        Log.d("EnvironmentDetail", "Received: environmentId=$environmentId, userId=$userId, environmentName=$environmentName")

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnAgregarDispositivo.setOnClickListener {
            if (userId == -1) {
                Toast.makeText(this, "Usuario no identificado. Por favor, inicia sesión nuevamente.", Toast.LENGTH_LONG).show()
                Log.e("EnvironmentDetail", "Invalid userId: $userId")
                return@setOnClickListener
            }
            showDeviceIdInputDialog(userId)
        }

        // Listener para el botón de eliminar entorno
        binding.btnEliminarEntorno.setOnClickListener {
            if (environmentId != -1) {
                showDeleteConfirmationDialog()
            } else {
                Toast.makeText(this, "ID de entorno inválido", Toast.LENGTH_SHORT).show()
                Log.e("EnvironmentDetail", "Invalid environmentId: $environmentId")
            }
        }

        if (environmentId != -1) {
            loadDevices(environmentId)
        } else {
            Toast.makeText(this, "ID de entorno inválido", Toast.LENGTH_SHORT).show()
            Log.e("EnvironmentDetail", "Invalid environmentId: $environmentId")
        }
    }

    override fun onResume() {
        super.onResume()
        if (environmentId != -1) {
            loadDevices(environmentId)
        }
    }

    private fun loadDevices(environmentId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getUserDevices(environmentId)
                if (response.isSuccessful) {
                    val userDevices = response.body() ?: emptyList()
                    withContext(Dispatchers.Main) {
                        deviceAdapter.updateData(userDevices)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@EnvironmentDetailActivity,
                            "Error al cargar dispositivos: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    Log.e("API_ERROR", "Error al obtener dispositivos: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@EnvironmentDetailActivity,
                        "Excepción al cargar dispositivos: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Log.e("API_ERROR", "Excepción: ${e.message}")
            }
        }
    }

    private fun showDeviceIdInputDialog(userId: Int) {
        // Llamar a la API para obtener los dispositivos disponibles
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getDevices()
                if (response.isSuccessful) {
                    val devices = response.body() ?: emptyList()
                    withContext(Dispatchers.Main) {
                        // Crear una lista de nombres con ID de dispositivo
                        val deviceNames = devices.map { "${it.name}" }

                        // Crear un AlertDialog con una lista de dispositivos
                        AlertDialog.Builder(this@EnvironmentDetailActivity)
                            .setTitle("Seleccionar Dispositivo")
                            .setItems(deviceNames.toTypedArray()) { _, position ->
                                val selectedDevice = devices[position]
                                assignDeviceToUser(userId, selectedDevice.id) // Vincular el dispositivo seleccionado
                            }
                            .setNegativeButton("Cancelar", null)
                            .show()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@EnvironmentDetailActivity, "Error al cargar dispositivos", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EnvironmentDetailActivity, "Excepción al cargar dispositivos: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                Log.e("API_ERROR", "Excepción: ${e.message}")
            }
        }
    }


    private fun assignDeviceToUser(userId: Int, deviceId: Int) {
        if (environmentId == -1) {
            Toast.makeText(this, "ID de entorno inválido", Toast.LENGTH_SHORT).show()
            Log.e("EnvironmentDetail", "No se pudo obtener environmentId para asignación")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.asignarDispositivoConEntorno(userId, deviceId, environmentId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@EnvironmentDetailActivity,
                            "Dispositivo vinculado exitosamente al entorno",
                            Toast.LENGTH_SHORT
                        ).show()
                        loadDevices(environmentId)
                    } else {
                        val errorMessage = when (response.code()) {
                            404 -> "El dispositivo o entorno no existe"
                            400 -> "Solicitud inválida, verifica los datos"
                            403 -> "No tienes permiso para esta acción"
                            else -> "Error al vincular: ${response.errorBody()?.string() ?: response.message()}"
                        }
                        Toast.makeText(
                            this@EnvironmentDetailActivity,
                            errorMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@EnvironmentDetailActivity,
                        "Excepción al vincular: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                Log.e("API_ERROR", "Excepción: ${e.message}")
            }
        }
    }

    // Método para mostrar un diálogo de confirmación antes de eliminar
    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Entorno")
            .setMessage("¿Estás seguro de que deseas eliminar este entorno? Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                deleteEnvironment()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteEnvironment() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.deleteEnvironment(environmentId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@EnvironmentDetailActivity,
                            "Entorno eliminado exitosamente",
                            Toast.LENGTH_SHORT
                        ).show()
                        // Regresar a la actividad anterior
                        val intent = Intent(this@EnvironmentDetailActivity, HomePageActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val errorMessage = when (response.code()) {
                            404 -> "El entorno no existe"
                            403 -> "No tienes permiso para eliminar este entorno"
                            else -> "Error al eliminar: ${response.errorBody()?.string() ?: response.message()}"
                        }
                        Toast.makeText(
                            this@EnvironmentDetailActivity,
                            errorMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@EnvironmentDetailActivity,
                        "Excepción al eliminar: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                Log.e("API_ERROR", "Excepción: ${e.message}")
            }
        }
    }
}