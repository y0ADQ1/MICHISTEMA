package com.example.michistema.ui.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.michistema.R
import com.example.michistema.data.model.UserDevice
import com.example.michistema.ui.main.DeviceDetailAreneroActivity
import com.example.michistema.ui.main.DeviceDetailBebederoActivity
import com.example.michistema.ui.main.DeviceDetailComedorActivity

class DeviceAdapter(
    private var devices: List<UserDevice>,
    private val onDeviceClick: (UserDevice) -> Unit
) : RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {

    class DeviceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtDeviceName: TextView = view.findViewById(R.id.txtDeviceName)
        val txtDeviceStatus: TextView = view.findViewById(R.id.txtDeviceStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_device_profile, parent, false)
        return DeviceViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val userDevice = devices[position]

        // Manejar caso donde device sea null
        val device = userDevice.device
        holder.txtDeviceName.text = device?.name ?: "Dispositivo sin nombre"
        holder.txtDeviceStatus.text = if (device?.active == true) "Activo" else "Inactivo"

        // Establece el listener de clic en el ítem
        holder.itemView.setOnClickListener {
            if (device != null) { // Solo procede si device no es null
                val intent: Intent
                val deviceId = device.id

                // Lógica condicional basada en el deviceId
                if (deviceId == 1) {
                    intent = Intent(holder.itemView.context, DeviceDetailAreneroActivity::class.java)
                    intent.putExtra("device_id", deviceId)
                    intent.putExtra("device_name", device.name)
                } else if (deviceId == 2) {
                    intent = Intent(holder.itemView.context, DeviceDetailBebederoActivity::class.java)
                    intent.putExtra("device_id", deviceId)
                    intent.putExtra("device_name", device.name)
                } else {
                    intent = Intent(holder.itemView.context, DeviceDetailComedorActivity::class.java)
                    intent.putExtra("device_id", deviceId)
                    intent.putExtra("device_name", device.name)
                }

                // Inicia la actividad correspondiente
                holder.itemView.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = devices.size

    fun updateData(newDevices: List<UserDevice>) {
        devices = newDevices
        notifyDataSetChanged()
    }
}