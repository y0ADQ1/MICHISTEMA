package com.example.michistema

import android.util.Log
import okhttp3.*
import okio.ByteString

class WebSocketManager {

    private val client = OkHttpClient() // Cliente OkHttp para WebSocket
    private var webSocket: WebSocket? = null

    // Método para iniciar la conexión WebSocket
    fun startConnection(url: String) {
        val request = Request.Builder().url(url).build()
        val listener = object : WebSocketListener() {

            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("WebSocket", "WebSocket abierto: ${response.message}")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("WebSocket", "Mensaje recibido: $text") // Imprimir el mensaje en texto
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

    // Método para cerrar la conexión WebSocket
    fun closeConnection() {
        webSocket?.close(1000, "Adiós!") // Cerrar la conexión con código 1000 (cierre normal)
    }
}
