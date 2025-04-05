
package com.example.michistema.ui.main

import android.os.Bundle
import android.util.Log
import android.widget.PopupMenu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.michistema.R
import com.example.michistema.databinding.ActivityDrinkerBinding
import com.example.michistema.ui.adapter.DrinkerInfo
import com.example.michistema.ui.adapter.DrinkerInfoAdapter
import okhttp3.*
import okio.ByteString

class DrinkerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDrinkerBinding
    private lateinit var webSocketManager: WebSocketManager
    private lateinit var adapter: DrinkerInfoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDrinkerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerViewDrinkerInfo.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewDrinkerInfo.adapter = adapter

        webSocketManager = WebSocketManager(this)

        val websocketUrl = "wss://tu-websocket-url.com"
        webSocketManager.startConnection(websocketUrl)

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnMore.setOnClickListener {
            val popupMenu = PopupMenu(this, binding.btnMore)
            popupMenu.menuInflater.inflate(R.menu.drinker_options_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_modify_drinker -> {
                        Log.d("DrinkerActivity", "Modificar el bebedero clicked")
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onDestroy() {
        super.onDestroy()
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
                        val newData = mutableListOf<DrinkerInfo>()
                        val currentData = activity.adapter.drinkerInfoList
                        if (text.contains("agua")) {
                            newData.add(DrinkerInfo("Agua:", text))
                            newData.add(currentData.find { it.label == "Gato Cercano:" } ?: DrinkerInfo("Gato Cercano:", "siono"))
                        } else if (text.contains("gato")) {
                            newData.add(currentData.find { it.label == "Agua:" } ?: DrinkerInfo("Agua:", "siono"))
                            newData.add(DrinkerInfo("Gato Cercano:", text))
                        }
                        activity.adapter.updateData(newData)
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

            webSocket = client.newWebSocket(request, listener)
        }

        fun closeConnection() {
            webSocket?.close(1000, "Adiós!")
        }
    }
}



