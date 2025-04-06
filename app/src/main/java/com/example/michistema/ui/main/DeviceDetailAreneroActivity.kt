package com.example.michistema.ui.main

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.michistema.R
import com.example.michistema.databinding.ActivityDeviceDetailAreneroBinding
import com.example.michistema.utils.MessageSender

class DeviceDetailAreneroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeviceDetailAreneroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeviceDetailAreneroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val btnBack: Button = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
        }

        // Botón para limpiar la vista
        val limpiar = findViewById<Button>(R.id.btn1)
        limpiar.setOnClickListener {
            enviarMensaje("motor-limpieza", "normal")
        }

        // Nuevo botón para enviar otro topic
        val limpiarCompleto = findViewById<Button>(R.id.btn2)
        limpiarCompleto.setOnClickListener {
            enviarMensaje("motor-limpieza", "completa")
        }

        val relleno = findViewById<Button>(R.id.btn3)
        relleno.setOnClickListener {
            enviarMensaje("motor-limpieza", "relleno")
        }

        // Recibir el ID y el nombre del dispositivo
        val deviceId = intent.getIntExtra("device_id", -1)
        val deviceName = intent.getStringExtra("device_name") ?: "Nombre no disponible"

        if (deviceId != -1) {
            loadDeviceDetails(deviceId, deviceName)

            // Simulación de estado (reemplaza con datos reales)
            val gatoPresente: Boolean? = null  // null si no hay datos
            actualizarIndicadorProximidad(gatoPresente)
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
                // Aquí puedes manejar la respuesta del servidor si es necesario
                println(response)
            },
            onError = { error ->
                // Aquí puedes manejar el error si es necesario
                println(error)
            })
    }

    private fun actualizarIndicadorProximidad(gatoPresente: Boolean?) {
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
    }
}