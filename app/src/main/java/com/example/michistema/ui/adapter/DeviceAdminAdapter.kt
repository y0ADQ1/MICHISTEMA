package com.example.michistema.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.michistema.R
import com.example.michistema.data.model.Device

class DeviceAdminAdapter(
    private var deviceList: List<Device>,
    private val onDisableClick: (Device) -> Unit,
    private val onEnableClick: (Device) -> Unit,
    private val onDeleteClick: (Device) -> Unit
) : RecyclerView.Adapter<DeviceAdminAdapter.DeviceAdminViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceAdminViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dispositivo, parent, false)
        return DeviceAdminViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceAdminViewHolder, position: Int) {
        val device = deviceList[position]
        holder.bind(device, onDisableClick, onEnableClick, onDeleteClick)
    }

    override fun getItemCount(): Int = deviceList.size

    fun updateList(newDevices: List<Device>) {
        deviceList = newDevices
        notifyDataSetChanged()
    }

    class DeviceAdminViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.txt_nombre_dispositivo)
        private val stateTextView: TextView = itemView.findViewById(R.id.txt_estado_dispositivo)
        private val stateIcon: ImageView = itemView.findViewById(R.id.img_estado)
        private val disableButton: Button = itemView.findViewById(R.id.btn_disable)
        private val enableButton: Button = itemView.findViewById(R.id.btn_aviable)
        private val deleteButton: Button = itemView.findViewById(R.id.btn_delete)

        fun bind(
            device: Device,
            onDisableClick: (Device) -> Unit,
            onEnableClick: (Device) -> Unit,
            onDeleteClick: (Device) -> Unit
        ) {
            nameTextView.text = device.name
            stateTextView.text = if (device.active) "Conectado" else "Desconectado"
            stateTextView.setTextColor(
                itemView.context.getColor(
                    if (device.active) android.R.color.holo_green_dark else android.R.color.holo_red_dark
                )
            )

            disableButton.setOnClickListener { onDisableClick(device) }
            enableButton.setOnClickListener { onEnableClick(device) }
            deleteButton.setOnClickListener { onDeleteClick(device) }
        }
    }
}
