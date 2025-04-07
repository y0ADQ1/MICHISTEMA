package com.example.michistema.ui.main

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.michistema.R
import com.example.michistema.databinding.ActivityDeviceDetailBebederoBinding
import com.example.michistema.utils.MessageSender
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class DeviceDetailBebederoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeviceDetailBebederoBinding
    private lateinit var webSocket: WebSocket  // Declaración de la variable webSocket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeviceDetailBebederoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener datos del intent
        val deviceId = intent.getIntExtra("device_id", -1)
        val deviceName = intent.getStringExtra("device_name") ?: "Nombre no disponible"

        if (deviceId != -1) {
            loadDeviceDetails(deviceId, deviceName)
        }

        // Conectar WebSocket
        iniciarWebSocket()

        // Botón para regresar
        findViewById<Button>(binding.btnBack.id).setOnClickListener {
            finish()
        }
    }

    private fun loadDeviceDetails(deviceId: Int, deviceName: String) {
        binding.txtDeviceId.text = "ID del Dispositivo: $deviceId"
        binding.txtDeviceName.text = "Nombre del Dispositivo: $deviceName"
    }

    private fun enviarMensaje(topic: String, payload: String) {
        val messageSender = MessageSender()
        messageSender.enviarMensaje(topic, payload,
            onResponse = { response -> println("Respuesta: $response") },
            onError = { error -> println("Error: $error") })
    }

    private fun iniciarWebSocket() {
        val client = OkHttpClient.Builder()
            .readTimeout(3, TimeUnit.SECONDS)
            .build()

        val request = Request.Builder()
            .url("ws://atenasoficial.com:3003") // Cambia si tu IP/host es otro
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                runOnUiThread {
                    println("WebSocket del Comedero Bebedero abierto")
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
                "bebedero-agua" -> {
                    binding.txtAgua.text = "Estado del Bebedero"
                    binding.txtAguavalor.text = message
                }
                else -> {
                    println("Topic desconocido")
                }
            }
        } catch (e: Exception) {
            println("Error al procesar JSON: ${e.message}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        webSocket.close(1000, "Actividad del comedero destruida")
    }
}
