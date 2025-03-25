package com.example.michistema.ui.main

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.michistema.R
import okhttp3.*
import okio.ByteString

class DrinkerActivity : AppCompatActivity() {

    private lateinit var webSocketManager: WebSocketManager
    private lateinit var txtAgua: TextView
    private lateinit var txtGato: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_drinker)

        txtAgua = findViewById(R.id.txt_get_agua)
        txtGato = findViewById(R.id.txt_get_gato)

        webSocketManager = WebSocketManager(this)

        val websocketUrl = "wss://tu-websocket-url.com"
        webSocketManager.startConnection(websocketUrl)

        val btnBack: Button = findViewById(R.id.btn_back)
        btnBack.setOnClickListener {
            onBackPressed()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cerrar WebSocket cuando la actividad se destruye
        webSocketManager.closeConnection()
    }

    class WebSocketManager(private val activity: DrinkerActivity) {

        private val client = OkHttpClient()
        private var webSocket: WebSocket? = null

        fun startConnection(url: String) {
            val request = Request.Builder().url(url).build()
            val listener = object : WebSocketListener() {

                override fun onOpen(webSocket: WebSocket, response: Response) {
                    Log.d("WebSocket", "WebSocket abierto: ${response.message}")
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    Log.d("WebSocket", "Mensaje recibido: $text")
                    activity.runOnUiThread {
                        if (text.contains("agua")) {
                            activity.txtAgua.text = text
                        } else if (text.contains("gato")) {
                            activity.txtGato.text = text
                        }
                    }
                }

                override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                    Log.d("WebSocket", "Mensaje recibido en bytes: ${bytes.hex()}")
                }

                override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                    Log.d("WebSocket", "WebSocket cerrándose: $reason")
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    Log.d("WebSocket", "WebSocket cerrado: $reason")
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    Log.e("WebSocket", "Error en WebSocket: ", t)
                }
            }

            // Establecer la conexión WebSocket
            webSocket = client.newWebSocket(request, listener)
        }

        fun closeConnection() {
            webSocket?.close(1000, "Adiós!")
        }
    }
}
