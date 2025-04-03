package com.example.michistema.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.michistema.R
import com.example.michistema.data.model.Device
import com.example.michistema.data.model.Response.deviceResponse

class DeviceAdapter(private var deviceList: List<Device>) :
    RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device_profile, parent, false)
        return DeviceViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val device = deviceList[position]
        holder.bind(device)
    }

    override fun getItemCount(): Int = deviceList.size

    fun updateList(newDevices: List<Device>) {
        deviceList = newDevices
        notifyDataSetChanged()
    }


    class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.tv_device_name)
        private val quantityTextView: TextView = itemView.findViewById(R.id.tv_device_quantity)

        fun bind(device: Device) {
            nameTextView.text = device.name
            quantityTextView.visibility = View.GONE // Ocultamos cantidad por item
        }
    }
}