package com.example.michistema.ui.main

import android.content.Intent
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

        setContentView(binding.root)
        val deviceId = intent.getIntExtra("device_id", -1)
        val deviceName = intent.getStringExtra("device_name") ?: "Nombre no disponible"
        val environmentId = intent.getIntExtra("environment_id", -1)
        val environmentName = intent.getStringExtra("environment_name") ?: "Desconocido"
        val userId = intent.getIntExtra("user_id", -1)

        if (deviceId != -1) {
            loadDeviceDetails(deviceId, deviceName)
        }

        // Botón para regresar (ajusta esto a la actividad previa que realmente quieres abrir)
        val btnBack: Button = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            finish() // Esto simplemente cierra esta actividad y vuelve a la anterior
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
}
