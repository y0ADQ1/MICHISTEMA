package com.example.michistema.ui.main

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.michistema.R
import com.example.michistema.databinding.ActivityDeviceDetailAreneroBinding
import com.example.michistema.utils.MessageSender
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class DeviceDetailAreneroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeviceDetailAreneroBinding
    private lateinit var webSocket: WebSocket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeviceDetailAreneroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val deviceId = intent.getIntExtra("device_id", -1)
        val deviceName = intent.getStringExtra("device_name") ?: "Nombre no disponible"
        val environmentId = intent.getIntExtra("environment_id", -1)
        val environmentName = intent.getStringExtra("environment_name") ?: "Desconocido"
        val userId = intent.getIntExtra("user_id", -1)

        iniciarWebSocket()

        if (deviceId != -1) {
            loadDeviceDetails(deviceId, deviceName)
        }

        // Botón para regresar
        val btnBack: Button = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            finish() // Regresa a la actividad anterior
        }

        // Botón para limpieza normal
        val limpiar = findViewById<Button>(R.id.btn1)
        limpiar.setOnClickListener {
            enviarMensaje("motor-limpieza", "normal")
        }

        // Botón para limpieza completa
        val limpiarCompleto = findViewById<Button>(R.id.btn2)
        limpiarCompleto.setOnClickListener {
            enviarMensaje("motor-limpieza", "completa")
        }

        // Botón para rellenar arena
        val relleno = findViewById<Button>(R.id.btn3)
        relleno.setOnClickListener {
            enviarMensaje("motor-limpieza", "relleno")
        }
    }

    private fun loadDeviceDetails(deviceId: Int, deviceName: String) {
        binding.txtDeviceId.text = "ID del Dispositivo: $deviceId"
        binding.txtDeviceName.text = "Nombre del Dispositivo: $deviceName"
    }

    private fun iniciarWebSocket() {
        val client = OkHttpClient.Builder()
            .readTimeout(3, TimeUnit.SECONDS)
            .build()

        val request = Request.Builder()
            .url("ws://189.244.34.160:3003")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                runOnUiThread {
                    println("WebSocket conectado")
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                runOnUiThread {
                    procesarMensaje(text)
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                runOnUiThread {
                    println("Error WebSocket: ${t.message}")
                }
            }
        })
    }

    private fun procesarMensaje(mensaje: String) {
        try {
            val json = JSONObject(mensaje)
            val topic = json.getString("topic")
            val message = json.getString("message")

            when (topic) {
                "sensor-mq2" -> binding.txtGases.text = "Gases: $message ppm"
                "sensor-dht" -> binding.txtHumedad.text = "Humedad: $message %"
                "sensor-ultrasonic" -> binding.txtProximidad.text = "Proximidad: $message cm"
                // Agrega más casos según otros sensores
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

    // Para cerrar el WebSocket cuando la actividad se destruye
    override fun onDestroy() {
        super.onDestroy()
        webSocket.close(1000, "Actividad destruida") // Cierra el WebSocket al destruir la actividad
    }
}
