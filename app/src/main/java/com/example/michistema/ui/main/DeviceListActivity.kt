package com.example.michistema.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import com.example.michistema.R
import com.example.michistema.databinding.ActivityDeviceListBinding
import com.example.michistema.databinding.DialogAddDeviceBinding
import com.example.michistema.model.DeviceCategory
import com.example.michistema.ui.adapter.DeviceCategoryAdapter
import com.example.michistema.ui.adapter.SpinnerDeviceTypeAdapter

class DeviceListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeviceListBinding
    private lateinit var adapter: DeviceCategoryAdapter

    private val modifyDeviceLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { data ->
                val originalDeviceName = data.getStringExtra("original_device_name")
                val updatedDevice = data.getParcelableExtra<DeviceCategory>("updated_device")
                if (originalDeviceName != null && updatedDevice != null) {
                    adapter.updateDevice(originalDeviceName, updatedDevice)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDeviceListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val environmentName = intent.getStringExtra("environment_name") ?: "Mis Dispositivos"
        binding.tvTitle.text = "Dispositivos en: $environmentName"

        val deviceCategories = mutableListOf(
            DeviceCategory("Arenero 1", R.drawable.noun_litter_box_6692365, R.layout.item_litter_box, "LitterBox", environmentName),
            DeviceCategory("Arenero 2", R.drawable.noun_litter_box_6692365, R.layout.item_litter_box, "LitterBox", environmentName),
            DeviceCategory("Bebedero 1", R.drawable.noun_water_dispenser_3516363, R.layout.item_water_dispenser, "WaterDispenser", environmentName),
            DeviceCategory("Bebedero 2", R.drawable.noun_water_dispenser_3516363, R.layout.item_water_dispenser, "WaterDispenser", environmentName),
            DeviceCategory("Comedero 1", R.drawable.noun_cat_feeder_6692380, R.layout.item_cat_feeder, "CatFeeder", environmentName),
            DeviceCategory("Comedero 2", R.drawable.noun_cat_feeder_6692380, R.layout.item_cat_feeder, "CatFeeder", environmentName)
        )

        adapter = DeviceCategoryAdapter(
            onAddDeviceClick = {
                showAddDeviceDialog()
            }
        )

        adapter.setDeviceCategories(deviceCategories)

        binding.recyclerViewDeviceList.layoutManager = GridLayoutManager(this, 3)
        binding.recyclerViewDeviceList.adapter = adapter

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnFilter.setOnClickListener { view ->
            val popupMenu = PopupMenu(this, view)
            popupMenu.menuInflater.inflate(R.menu.filter_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.filter_all -> {
                        adapter.filterByCategory("Todos")
                        true
                    }
                    R.id.filter_litter_box -> {
                        adapter.filterByCategory("LitterBox")
                        true
                    }
                    R.id.filter_water_dispenser -> {
                        adapter.filterByCategory("WaterDispenser")
                        true
                    }
                    R.id.filter_cat_feeder -> {
                        adapter.filterByCategory("CatFeeder")
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

    private fun showAddDeviceDialog() {
        val dialogBinding = DialogAddDeviceBinding.inflate(LayoutInflater.from(this))
        val dialog = AlertDialog.Builder(this, R.style.CustomDialogTheme)
            .setView(dialogBinding.root)
            .create()

        val deviceTypes = resources.getStringArray(R.array.device_types)
        val spinnerAdapter = SpinnerDeviceTypeAdapter(this, deviceTypes)
        dialogBinding.spinnerDeviceType.adapter = spinnerAdapter

        dialogBinding.spinnerDeviceType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedDeviceType = parent.getItemAtPosition(position).toString()
                when (selectedDeviceType) {
                    "Arenero" -> {
                        dialogBinding.tvCleaningIntervalLabel.visibility = View.VISIBLE
                        dialogBinding.timePickerCleaningInterval.visibility = View.VISIBLE
                        dialogBinding.tvFoodAmountLabel.visibility = View.GONE
                        dialogBinding.etFoodAmount.visibility = View.GONE
                        dialogBinding.tvFoodAmountConverted.visibility = View.GONE
                    }
                    "Bebedero" -> {
                        dialogBinding.tvCleaningIntervalLabel.visibility = View.GONE
                        dialogBinding.timePickerCleaningInterval.visibility = View.GONE
                        dialogBinding.tvFoodAmountLabel.visibility = View.GONE
                        dialogBinding.etFoodAmount.visibility = View.GONE
                        dialogBinding.tvFoodAmountConverted.visibility = View.GONE
                    }
                    "Comedero" -> {
                        dialogBinding.tvCleaningIntervalLabel.visibility = View.GONE
                        dialogBinding.timePickerCleaningInterval.visibility = View.GONE
                        dialogBinding.tvFoodAmountLabel.visibility = View.VISIBLE
                        dialogBinding.etFoodAmount.visibility = View.VISIBLE
                        dialogBinding.tvFoodAmountConverted.visibility = View.VISIBLE
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

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

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnAdd.setOnClickListener {
            val deviceName = dialogBinding.etDeviceName.text.toString().trim()
            if (deviceName.isEmpty()) {
                dialogBinding.etDeviceName.error = "Por favor, ingrese un nombre"
                return@setOnClickListener
            }

            val selectedDeviceType = dialogBinding.spinnerDeviceType.selectedItem.toString()
            val newDevice = when (selectedDeviceType) {
                "Arenero" -> {
                    val hour = dialogBinding.timePickerCleaningInterval.hour
                    val minute = dialogBinding.timePickerCleaningInterval.minute
                    val cleaningInterval = String.format("%02d:%02d", hour, minute)
                    DeviceCategory(
                        deviceName,
                        R.drawable.noun_litter_box_6692365,
                        R.layout.item_litter_box,
                        "LitterBox",
                        intent.getStringExtra("environment_name"),
                        cleaningInterval
                    )
                }
                "Bebedero" -> DeviceCategory(
                    deviceName,
                    R.drawable.noun_water_dispenser_3516363,
                    R.layout.item_water_dispenser,
                    "WaterDispenser",
                    intent.getStringExtra("environment_name")
                )
                "Comedero" -> {
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
                    DeviceCategory(
                        deviceName,
                        R.drawable.noun_cat_feeder_6692380,
                        R.layout.item_cat_feeder,
                        "CatFeeder",
                        intent.getStringExtra("environment_name"),
                        null,
                        foodAmount
                    )
                }
                else -> null
            }

            newDevice?.let {
                adapter.addDevice(it)
            }
            dialog.dismiss()
        }

        dialog.show()
    }
}