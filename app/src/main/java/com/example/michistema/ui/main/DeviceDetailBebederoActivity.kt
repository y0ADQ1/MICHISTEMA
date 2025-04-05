package com.example.michistema.ui.main

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.michistema.R
import com.example.michistema.databinding.ActivityDeviceDetailBebederoBinding
import com.example.michistema.utils.MessageSender

class DeviceDetailBebederoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeviceDetailBebederoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeviceDetailBebederoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val btnBack: Button = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
        }

        // Recibir el ID y el nombre del dispositivo
        val deviceId = intent.getIntExtra("device_id", -1)
        val deviceName = intent.getStringExtra("device_name") ?: "Nombre no disponible"

        if (deviceId != -1) {
            loadDeviceDetails(deviceId, deviceName)

            // Simulación de estado (debes reemplazarlo con tus datos reales)
            val gatoPresente: Boolean? = null  // null si no hay datos
            val hayAgua: Boolean? = null       // null si no hay datos

            actualizarIndicadores(gatoPresente, hayAgua)
        }
    }

    private fun loadDeviceDetails(deviceId: Int, deviceName: String) {
        binding.txtDeviceId.text = "ID del Dispositivo: $deviceId"
        binding.txtDeviceName.text = "Nombre del Dispositivo: $deviceName"
    }

    private fun enviarMensaje(topic: String, payload: String) {
        val messageSender = MessageSender()
        messageSender.enviarMensaje(topic, payload,
            onResponse = { response ->
                println(response)
            },
            onError = { error ->
                println(error)
            })
    }

    private fun actualizarIndicadores(gatoPresente: Boolean?, hayAgua: Boolean?) {
        val gris = Color.GRAY
        val rojo = Color.RED
        val verde = Color.GREEN

        // Indicador de proximidad
        val colorProximidad = when (gatoPresente) {
            true -> rojo         // gato presente
            false -> verde       // sin gato
            null -> gris         // sin datos
        }
        binding.statusProximidad.setBackgroundColor(colorProximidad)

        // Indicador de agua
        val colorAgua = when (hayAgua) {
            true -> verde        // hay agua
            false -> rojo        // no hay agua
            null -> gris         // sin datos
        }
        binding.statusAgua.setBackgroundColor(colorAgua)
    }
}
