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
import com.example.michistema.databinding.ActivityLitterBoxBinding
import com.example.michistema.databinding.DialogAddDeviceBinding
import com.example.michistema.model.DeviceCategory
import com.example.michistema.ui.adapter.LitterBoxInfo
import com.example.michistema.ui.adapter.LitterBoxInfoAdapter
import okhttp3.*
import okio.ByteString

class LitterBoxActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLitterBoxBinding
    private lateinit var webSocketManager: WebSocketManager
    private lateinit var adapter: LitterBoxInfoAdapter
    private lateinit var deviceCategory: DeviceCategory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLitterBoxBinding.inflate(layoutInflater)
        setContentView(binding.root)

        deviceCategory = intent.getParcelableExtra("device_category") ?: DeviceCategory(
            "Arenero Desconocido",
            R.drawable.noun_litter_box_6692365,
            R.layout.item_litter_box,
            "LitterBox"
        )

        binding.nameLitterBox.text = deviceCategory.name

        val initialData = listOf(
            LitterBoxInfo("Cleaning Interval:", deviceCategory.cleaningInterval ?: "N/A"),
            LitterBoxInfo("Estado de Gases:", "Unknown"),
            LitterBoxInfo("Estado de Humedad:", "Unknown")
            LitterBoxInfo("")
        )
        adapter = LitterBoxInfoAdapter(initialData)
        binding.recyclerViewLitterBoxInfo.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewLitterBoxInfo.adapter = adapter

        webSocketManager = WebSocketManager(this)
        val websocketUrl = "wss://tu-websocket-url.com"
        webSocketManager.startConnection(websocketUrl)

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnMore.setOnClickListener {
            val popupMenu = PopupMenu(this, binding.btnMore)
            popupMenu.menuInflater.inflate(R.menu.litter_box_options_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_modify_litter_box -> {
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

        dialogBinding.tvDialogTitle.text = "Modificar Arenero"

        dialogBinding.spinnerDeviceType.visibility = View.GONE

        dialogBinding.etDeviceName.setText(deviceCategory.name)

        dialogBinding.tvCleaningIntervalLabel.visibility = View.VISIBLE
        dialogBinding.timePickerCleaningInterval.visibility = View.VISIBLE
        dialogBinding.tvFoodAmountLabel.visibility = View.GONE
        dialogBinding.etFoodAmount.visibility = View.GONE
        dialogBinding.tvFoodAmountConverted.visibility = View.GONE

        deviceCategory.cleaningInterval?.let { interval ->
            val parts = interval.split(":")
            if (parts.size == 2) {
                val hour = parts[0].toIntOrNull() ?: 0
                val minute = parts[1].toIntOrNull() ?: 0
                dialogBinding.timePickerCleaningInterval.hour = hour
                dialogBinding.timePickerCleaningInterval.minute = minute
            }
        }

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

            val hour = dialogBinding.timePickerCleaningInterval.hour
            val minute = dialogBinding.timePickerCleaningInterval.minute
            val cleaningInterval = String.format("%02d:%02d", hour, minute)
            val updatedDevice = DeviceCategory(
                deviceName,
                R.drawable.noun_litter_box_6692365,
                R.layout.item_litter_box,
                "LitterBox",
                deviceCategory.environment,
                cleaningInterval
            )

            deviceCategory = updatedDevice
            binding.nameLitterBox.text = deviceCategory.name
            adapter.updateData(listOf(
                LitterBoxInfo("Cleaning Interval:", deviceCategory.cleaningInterval ?: "N/A"),
                LitterBoxInfo("Usage Status:", "Unknown"),
                LitterBoxInfo("Last Cleaned:", "Unknown")
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

    class WebSocketManager(private val activity: LitterBoxActivity) {

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
                        val newData = mutableListOf<LitterBoxInfo>()
                        val currentData = activity.adapter.litterBoxInfoList
                        if (text.contains("cleaning")) {
                            newData.add(LitterBoxInfo("Cleaning Interval:", text))
                            newData.add(currentData.find { it.label == "Usage Status:" } ?: LitterBoxInfo("Usage Status:", "Unknown"))
                            newData.add(currentData.find { it.label == "Last Cleaned:" } ?: LitterBoxInfo("Last Cleaned:", "Unknown"))
                        } else if (text.contains("usage")) {
                            newData.add(currentData.find { it.label == "Cleaning Interval:" } ?: LitterBoxInfo("Cleaning Interval:", activity.deviceCategory.cleaningInterval ?: "N/A"))
                            newData.add(LitterBoxInfo("Usage Status:", text))
                            newData.add(currentData.find { it.label == "Last Cleaned:" } ?: LitterBoxInfo("Last Cleaned:", "Unknown"))
                        } else if (text.contains("last_cleaned")) {
                            newData.add(currentData.find { it.label == "Cleaning Interval:" } ?: LitterBoxInfo("Cleaning Interval:", activity.deviceCategory.cleaningInterval ?: "N/A"))
                            newData.add(currentData.find { it.label == "Usage Status:" } ?: LitterBoxInfo("Usage Status:", "Unknown"))
                            newData.add(LitterBoxInfo("Last Cleaned:", text))
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