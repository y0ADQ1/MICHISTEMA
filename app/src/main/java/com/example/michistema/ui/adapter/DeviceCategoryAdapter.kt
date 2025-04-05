package com.example.michistema.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.michistema.R
import com.example.michistema.databinding.ItemAdditionBinding
import com.example.michistema.databinding.ItemCatFeederBinding
import com.example.michistema.databinding.ItemLitterBoxBinding
import com.example.michistema.databinding.ItemWaterDispenserBinding
import com.example.michistema.model.DeviceCategory
import com.example.michistema.ui.main.CatFeederActivity
import com.example.michistema.ui.main.DrinkerActivity
import com.example.michistema.ui.main.LitterBoxActivity

class DeviceCategoryAdapter(
    private val onAddDeviceClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val fullDeviceCategories = mutableListOf<DeviceCategory>()
    private val filteredDeviceCategories = mutableListOf<DeviceCategory>()
    private val addDeviceItem = DeviceCategory(
        "Agregar Dispositivo",
        R.drawable.addition_icon,
        R.layout.item_addition,
        "AddDevice"
    )

    companion object {
        private const val TYPE_LITTER_BOX = 0
        private const val TYPE_WATER_DISPENSER = 1
        private const val TYPE_CAT_FEEDER = 2
        private const val TYPE_ADD_DEVICE = 3
    }

    fun setDeviceCategories(categories: List<DeviceCategory>) {
        fullDeviceCategories.clear()
        fullDeviceCategories.addAll(categories)
        updateFilteredList("Todos")
    }

    fun addDevice(device: DeviceCategory) {
        fullDeviceCategories.add(device)
        updateFilteredList("Todos")
    }

    fun updateDevice(originalDeviceName: String, updatedDevice: DeviceCategory) {
        val index = fullDeviceCategories.indexOfFirst { it.name == originalDeviceName }
        if (index != -1) {
            fullDeviceCategories[index] = updatedDevice
            updateFilteredList("Todos")
        }
    }

    fun filterByCategory(category: String) {
        updateFilteredList(category)
    }

    private fun updateFilteredList(category: String) {
        filteredDeviceCategories.clear()
        if (category == "Todos") {
            filteredDeviceCategories.addAll(fullDeviceCategories)
        } else {
            filteredDeviceCategories.addAll(fullDeviceCategories.filter { it.categoryType == category })
        }
        filteredDeviceCategories.add(addDeviceItem)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (filteredDeviceCategories[position].categoryType) {
            "LitterBox" -> TYPE_LITTER_BOX
            "WaterDispenser" -> TYPE_WATER_DISPENSER
            "CatFeeder" -> TYPE_CAT_FEEDER
            "AddDevice" -> TYPE_ADD_DEVICE
            else -> throw IllegalArgumentException("Invalid category type: ${filteredDeviceCategories[position].categoryType}")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_LITTER_BOX -> {
                val binding = ItemLitterBoxBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                LitterBoxViewHolder(binding)
            }
            TYPE_WATER_DISPENSER -> {
                val binding = ItemWaterDispenserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                WaterDispenserViewHolder(binding)
            }
            TYPE_CAT_FEEDER -> {
                val binding = ItemCatFeederBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CatFeederViewHolder(binding)
            }
            TYPE_ADD_DEVICE -> {
                val binding = ItemAdditionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AddDeviceViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val deviceCategory = filteredDeviceCategories[position]
        when (holder) {
            is LitterBoxViewHolder -> holder.bind(deviceCategory)
            is WaterDispenserViewHolder -> holder.bind(deviceCategory)
            is CatFeederViewHolder -> holder.bind(deviceCategory)
            is AddDeviceViewHolder -> holder.bind()
        }
    }

    override fun getItemCount(): Int = filteredDeviceCategories.size

    inner class LitterBoxViewHolder(private val binding: ItemLitterBoxBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(deviceCategory: DeviceCategory) {
            binding.nameTextView.text = deviceCategory.name
            binding.tvCleaningInterval.text = "Intervalo: ${deviceCategory.cleaningInterval ?: "N/A"}"
            binding.viewMoreButton.setOnClickListener {
                val intent = Intent(binding.root.context, LitterBoxActivity::class.java)
                intent.putExtra("device_category", deviceCategory)
                binding.root.context.startActivity(intent)
            }
        }
    }

    inner class WaterDispenserViewHolder(private val binding: ItemWaterDispenserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(deviceCategory: DeviceCategory) {
            binding.nameTextView.text = deviceCategory.name
            binding.viewMoreButton.setOnClickListener {
                val intent = Intent(binding.root.context, DrinkerActivity::class.java)
                intent.putExtra("device_category", deviceCategory)
                binding.root.context.startActivity(intent)
            }
        }
    }

    inner class CatFeederViewHolder(private val binding: ItemCatFeederBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(deviceCategory: DeviceCategory) {
            binding.nameTextView.text = deviceCategory.name
            binding.viewMoreButton.setOnClickListener {
                val intent = Intent(binding.root.context, CatFeederActivity::class.java)
                intent.putExtra("device_category", deviceCategory)
                binding.root.context.startActivity(intent)
            }
        }
    }

    inner class AddDeviceViewHolder(private val binding: ItemAdditionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.viewMoreButton.setOnClickListener { onAddDeviceClick() }
        }
    }
}