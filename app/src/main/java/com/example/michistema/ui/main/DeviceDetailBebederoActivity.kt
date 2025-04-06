package com.example.michistema.ui.main

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.michistema.R
import com.example.michistema.databinding.ActivityDeviceDetailBebederoBinding
import com.example.michistema.utils.MessageSender
import org.json.JSONObject

class DeviceDetailBebederoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeviceDetailBebederoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeviceDetailBebederoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener datos del intent
        val deviceId = intent.getIntExtra("device_id", -1)
        val deviceName = intent.getStringExtra("device_name") ?: "Nombre no disponible"
        val environmentId = intent.getIntExtra("environment_id", -1)
        val environmentName = intent.getStringExtra("environment_name") ?: "Desconocido"
        val userId = intent.getIntExtra("user_id", -1)

        if (deviceId != -1) {
            loadDeviceDetails(deviceId, deviceName)
        }

        // Botón para regresar a la actividad anterior
        val btnBack: Button = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            finish() // Simplemente vuelve a la actividad anterior
        }
    }

    private fun loadDeviceDetails(deviceId: Int, deviceName: String) {
        binding.txtDeviceId.text = "ID del Dispositivo: $deviceId"
        binding.txtDeviceName.text = "Nombre del Dispositivo: $deviceName"
    }

    private fun procesarMensaje(mensaje: String) {
        try {
            val json = JSONObject(mensaje)
            val topic = json.getString("topic")
            val message = json.getString("message") // El valor de la distancia en cm

            // Convertimos el mensaje a un valor numérico (float o double)
            val distancia = message.toFloatOrNull() // Convertimos a float, si no es posible, regresa null

            // Solo procesamos si la distancia es válida
            if (distancia != null) {
                when (topic) {
                    "ultrasonic-water" -> {
                        // Si la distancia es menor a 5 cm, cambiamos el color a verde, si no, a rojo
                        if (distancia < 5) {
                            binding.statusProximidad.setBackgroundResource(R.drawable.circle_green)
                        } else {
                            binding.statusProximidad.setBackgroundResource(R.drawable.circle_red)
                        }
                    }
                    "sensor-water" -> {
                        // Ejemplo para otro sensor, si quieres añadir más lógica
                        if (message == "normal") {
                            binding.statusAgua.setBackgroundResource(R.drawable.circle_green)
                        } else {
                            binding.statusAgua.setBackgroundResource(R.drawable.circle_red)
                        }
                    }
                    else -> {
                        println("Topic desconocido")
                    }
                }
            } else {
                println("La distancia recibida no es válida: $message")
            }
        } catch (e: Exception) {
            println("Error al procesar JSON: ${e.message}")
        }
    }

    private fun enviarMensaje(topic: String, payload: String) {
        val messageSender = MessageSender()
        messageSender.enviarMensaje(topic, payload,
            onResponse = { response ->
                println("Respuesta: $response")
            },
            onError = { error ->
                println("Error: $error")
            })
    }
}
