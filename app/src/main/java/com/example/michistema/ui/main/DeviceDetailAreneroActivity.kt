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

        iniciarWebSocket()

        if (deviceId != -1) {
            binding.txtDeviceId.text = "Codigo del Dispositivo: $deviceId"
            binding.txtDeviceName.text = "Nombre del Dispositivo: $deviceName"
        }

        findViewById<Button>(R.id.btnBack).setOnClickListener { finish() }

        findViewById<Button>(R.id.btn1).setOnClickListener {
            enviarMensaje("arenero-motor", "normal")
        }

        findViewById<Button>(R.id.btn2).setOnClickListener {
            enviarMensaje("arenero-motor", "completa")
        }

        findViewById<Button>(R.id.btn3).setOnClickListener {
            enviarMensaje("arenero-motor", "relleno")
        }
    }

    private fun iniciarWebSocket() {
        val client = OkHttpClient.Builder()
            .readTimeout(3, TimeUnit.SECONDS)
            .build()

        val request = Request.Builder()
            .url("ws://atenasoficial.com:3003")
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
                "sensor-mq2", "arenero-gas" -> {
                    binding.txtGases.text = "Gases: $message ppm"
                }
                "sensor-dht", "arenero-temperatura" -> {
                    binding.txtHumedad.text = "Humedad: $message %"
                }
                "sensor-ultrasonic", "arenero-ultrasonic" -> {
                    binding.txtProximidad.text = "Proximidad: $message cm"
                }
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

    override fun onDestroy() {
        super.onDestroy()
        webSocket.close(1000, "Actividad destruida")
    }
}
