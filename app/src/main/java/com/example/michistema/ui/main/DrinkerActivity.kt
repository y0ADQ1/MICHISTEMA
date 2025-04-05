package com.example.michistema.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupMenu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.michistema.R
import com.example.michistema.databinding.ActivityDrinkerBinding
import com.example.michistema.databinding.DialogAddDeviceBinding
import com.example.michistema.model.DeviceCategory
import com.example.michistema.ui.adapter.DrinkerInfo
import com.example.michistema.ui.adapter.DrinkerInfoAdapter
import okhttp3.*
import okio.ByteString

class DrinkerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDrinkerBinding
    private lateinit var webSocketManager: WebSocketManager
    private lateinit var adapter: DrinkerInfoAdapter
    private lateinit var deviceCategory: DeviceCategory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDrinkerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        deviceCategory = intent.getParcelableExtra("device_category") ?: DeviceCategory(
            "Bebedero Desconocido",
            R.drawable.noun_water_dispenser_3516363,
            R.layout.item_water_dispenser,
            "WaterDispenser"
        )

        binding.nameDrinker.text = deviceCategory.name

        val initialData = listOf(
            DrinkerInfo("Agua:", "Unknown"),
            DrinkerInfo("Gato Cercano:", "Unknown")
        )
        adapter = DrinkerInfoAdapter(initialData)
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
                        showModifyDeviceDialog()
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

    private fun showModifyDeviceDialog() {
        val dialogBinding = DialogAddDeviceBinding.inflate(LayoutInflater.from(this))
        val dialog = AlertDialog.Builder(this, R.style.CustomDialogTheme)
            .setView(dialogBinding.root)
            .create()

        dialogBinding.tvDialogTitle.text = "Modificar Bebedero"

        dialogBinding.spinnerDeviceType.visibility = View.GONE

        dialogBinding.etDeviceName.setText(deviceCategory.name)

        dialogBinding.tvCleaningIntervalLabel.visibility = View.GONE
        dialogBinding.timePickerCleaningInterval.visibility = View.GONE
        dialogBinding.tvFoodAmountLabel.visibility = View.GONE
        dialogBinding.etFoodAmount.visibility = View.GONE
        dialogBinding.tvFoodAmountConverted.visibility = View.GONE

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnAdd.text = "Guardar"
        dialogBinding.btnAdd.setOnClickListener {
            val deviceName = dialogBinding.etDeviceName.text.toString().trim()
            if (deviceName.isEmpty()) {
                dialogBinding.etDeviceName.error = "Por favor, ingrese un nombre"
                return@setOnClickListener
            }

            val updatedDevice = DeviceCategory(
                deviceName,
                R.drawable.noun_water_dispenser_3516363,
                R.layout.item_water_dispenser,
                "WaterDispenser",
                deviceCategory.environment
            )

            deviceCategory = updatedDevice
            binding.nameDrinker.text = deviceCategory.name
            adapter.updateData(listOf(
                DrinkerInfo("Agua:", "Unknown"),
                DrinkerInfo("Gato Cercano:", "Unknown")
            ))

            val resultIntent = Intent()
            resultIntent.putExtra("original_device_name", intent.getParcelableExtra<DeviceCategory>("device_category")?.name)
            resultIntent.putExtra("updated_device", updatedDevice)
            setResult(Activity.RESULT_OK, resultIntent)

            dialog.dismiss()
        }

        dialog.show()
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
                            newData.add(currentData.find { it.label == "Gato Cercano:" } ?: DrinkerInfo("Gato Cercano:", "Unknown"))
                        } else if (text.contains("gato")) {
                            newData.add(currentData.find { it.label == "Agua:" } ?: DrinkerInfo("Agua:", "Unknown"))
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