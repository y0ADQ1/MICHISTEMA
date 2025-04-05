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
import com.example.michistema.databinding.ActivityCatFeederBinding
import com.example.michistema.databinding.DialogAddDeviceBinding
import com.example.michistema.model.DeviceCategory
import com.example.michistema.ui.adapter.LitterBoxInfo
import com.example.michistema.ui.adapter.LitterBoxInfoAdapter
import okhttp3.*
import okio.ByteString

class CatFeederActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCatFeederBinding
    private lateinit var webSocketManager: WebSocketManager
    private lateinit var adapter: LitterBoxInfoAdapter
    private lateinit var deviceCategory: DeviceCategory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCatFeederBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the DeviceCategory from the Intent
        deviceCategory = intent.getParcelableExtra("device_category") ?: DeviceCategory(
            "Comedero Desconocido",
            R.drawable.noun_cat_feeder_6692380,
            R.layout.item_cat_feeder,
            "CatFeeder"
        )

        // Set the Comedero name
        binding.nameCatFeeder.text = deviceCategory.name

        // Initialize the RecyclerView with initial data
        val initialData = listOf(
            LitterBoxInfo("Food Amount:", deviceCategory.foodAmount ?: "N/A"),
            LitterBoxInfo("Feeding Status:", "Unknown"),
            LitterBoxInfo("Last Fed:", "Unknown")
        )
        adapter = LitterBoxInfoAdapter(initialData)
        binding.recyclerViewCatFeederInfo.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewCatFeederInfo.adapter = adapter

        // Initialize WebSocket
        webSocketManager = WebSocketManager(this)
        val websocketUrl = "wss://tu-websocket-url.com" // Replace with your WebSocket URL
        webSocketManager.startConnection(websocketUrl)

        // Back button
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        // More button with popup menu
        binding.btnMore.setOnClickListener {
            val popupMenu = PopupMenu(this, binding.btnMore)
            popupMenu.menuInflater.inflate(R.menu.cat_feeder_options_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_modify_cat_feeder -> {
                        showModifyDeviceDialog()
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }

        // Handle window insets
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

        // Update dialog title to indicate modification
        dialogBinding.tvDialogTitle.text = "Modificar Comedero"

        // Hide the Spinner since we're only modifying a Comedero
        dialogBinding.spinnerDeviceType.visibility = View.GONE

        // Pre-fill the dialog with the current device's details
        dialogBinding.etDeviceName.setText(deviceCategory.name)

        // Show fields specific to Comedero
        dialogBinding.tvCleaningIntervalLabel.visibility = View.GONE
        dialogBinding.timePickerCleaningInterval.visibility = View.GONE
        dialogBinding.tvFoodAmountLabel.visibility = View.VISIBLE
        dialogBinding.etFoodAmount.visibility = View.VISIBLE
        dialogBinding.tvFoodAmountConverted.visibility = View.VISIBLE

        // Pre-fill the food amount
        deviceCategory.foodAmount?.let { amount ->
            val grams = if (amount.endsWith(" kg")) {
                (amount.removeSuffix(" kg").toIntOrNull() ?: 0) * 1000
            } else {
                amount.removeSuffix(" g").toIntOrNull() ?: 0
            }
            dialogBinding.etFoodAmount.setText(grams.toString())
        }

        // Convert food amount for Comedero
        dialogBinding.etFoodAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()
                if (input.isNotEmpty()) {
                    val grams = input.toIntOrNull() ?: 0
                    dialogBinding.tvFoodAmountConverted.text = if (grams >= 1000) {
                        val kg = grams / 1000
                        "$kg kg"
                    } else {
                        "$grams g"
                    }
                } else {
                    dialogBinding.tvFoodAmountConverted.text = ""
                }
            }
        })

        // Cancel button
        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        // Save button (reusing the "Add" button for saving changes)
        dialogBinding.btnAdd.text = "Guardar"
        dialogBinding.btnAdd.setOnClickListener {
            val deviceName = dialogBinding.etDeviceName.text.toString().trim()
            if (deviceName.isEmpty()) {
                dialogBinding.etDeviceName.error = "Por favor, ingrese un nombre"
                return@setOnClickListener
            }

            // Create the updated device (always a Comedero)
            val foodAmountInput = dialogBinding.etFoodAmount.text.toString()
            val foodAmount = if (foodAmountInput.isNotEmpty()) {
                val grams = foodAmountInput.toIntOrNull() ?: 0
                if (grams >= 1000) {
                    val kg = grams / 1000
                    "$kg kg"
                } else {
                    "$grams g"
                }
            } else {
                "0 g"
            }
            val updatedDevice = DeviceCategory(
                deviceName,
                R.drawable.noun_cat_feeder_6692380,
                R.layout.item_cat_feeder,
                "CatFeeder",
                deviceCategory.environment,
                null,
                foodAmount
            )

            // Update the current activity's UI
            deviceCategory = updatedDevice
            binding.nameCatFeeder.text = deviceCategory.name
            adapter.updateData(listOf(
                LitterBoxInfo("Food Amount:", deviceCategory.foodAmount ?: "N/A"), // Fixed: Changed deviceCategoryfoodAmount to deviceCategory.foodAmount
                LitterBoxInfo("Feeding Status:", "Unknown"),
                LitterBoxInfo("Last Fed:", "Unknown")
            ))

            // Send the updated device back to DeviceListActivity
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

    class WebSocketManager(private val activity: CatFeederActivity) {

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
                        if (text.contains("food_amount")) {
                            newData.add(LitterBoxInfo("Food Amount:", text))
                            newData.add(currentData.find { it.label == "Feeding Status:" } ?: LitterBoxInfo("Feeding Status:", "Unknown"))
                            newData.add(currentData.find { it.label == "Last Fed:" } ?: LitterBoxInfo("Last Fed:", "Unknown"))
                        } else if (text.contains("feeding")) {
                            newData.add(currentData.find { it.label == "Food Amount:" } ?: LitterBoxInfo("Food Amount:", activity.deviceCategory.foodAmount ?: "N/A"))
                            newData.add(LitterBoxInfo("Feeding Status:", text))
                            newData.add(currentData.find { it.label == "Last Fed:" } ?: LitterBoxInfo("Last Fed:", "Unknown"))
                        } else if (text.contains("last_fed")) {
                            newData.add(currentData.find { it.label == "Food Amount:" } ?: LitterBoxInfo("Food Amount:", activity.deviceCategory.foodAmount ?: "N/A"))
                            newData.add(currentData.find { it.label == "Feeding Status:" } ?: LitterBoxInfo("Feeding Status:", "Unknown"))
                            newData.add(LitterBoxInfo("Last Fed:", text))
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