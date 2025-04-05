package com.example.michistema.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.michistema.R
import com.example.michistema.databinding.ActivityDeviceDetailComedorBinding
import com.example.michistema.utils.MessageSender



class DeviceDetailComedorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeviceDetailComedorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeviceDetailComedorBinding.inflate(layoutInflater)
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
        }

        val btnMotor1: Button = findViewById(R.id.btn1)
        btnMotor1.setOnClickListener {
            enviarMensaje("fill-food", "fill")
        }

        val btnMotor2: Button = findViewById(R.id.btn2)
        btnMotor2.setOnClickListener {
            enviarMensaje("fill-food", "stop")
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
