package com.example.michistema.ui.main

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.michistema.databinding.ActivityDeviceDetailComedorBinding
import com.example.michistema.utils.MessageSender
import okhttp3.*
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class DeviceDetailComedorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeviceDetailComedorBinding
    private lateinit var webSocket: WebSocket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeviceDetailComedorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val deviceId = intent.getIntExtra("device_id", -1)
        val deviceName = intent.getStringExtra("device_name") ?: "Nombre no disponible"

        if (deviceId != -1) {
            loadDeviceDetails(deviceId, deviceName)
        }

        iniciarWebSocket()

        findViewById<Button>(binding.btnBack.id).setOnClickListener {
            finish()
        }

        findViewById<Button>(binding.btn1.id).setOnClickListener {
            enviarMensaje("comedero-servo", "fill")
        }

        findViewById<Button>(binding.btn2.id).setOnClickListener {
            enviarMensaje("comedero-servo", "stop")
        }
    }

    private fun loadDeviceDetails(deviceId: Int, deviceName: String) {
        binding.txtDeviceId.text = "Codigo del Dispositivo: $deviceId"
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
            .url("ws://atenasoficial.com:3003")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                runOnUiThread {
                    println("WebSocket del Comedero conectado")
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
                "comedero-infrarojo" -> {
                    binding.txtProximidad.text = "El gato esta comiendo: "
                    binding.txtProximidadvalor.text = "$message"
                }
                "comedero-ultrasonico-almacenamiento" -> {
                    binding.txtPeso.text = "Estado Actual del almacén: "
                    binding.txtPesovalor.text = "$message"
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
