package com.example.michistema.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.michistema.R
import com.example.michistema.data.model.Response.deviceResponse

class DeviceAdapter(private var deviceList: List<deviceResponse>) :
    RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {

    class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgDispositivo: ImageView = itemView.findViewById(R.id.img_dispositivo)
        val txtNombreDispositivo: TextView = itemView.findViewById(R.id.txt_nombre_dispositivo)
        val txtEstadoDispositivo: TextView = itemView.findViewById(R.id.txt_estado_dispositivo)
        val imgEstado: ImageView = itemView.findViewById(R.id.img_estado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dispositivo, parent, false)
        return DeviceViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val device = deviceList[position]

        holder.txtNombreDispositivo.text = device.name
        holder.txtEstadoDispositivo.text = if (device.active) "Conectado" else "Desconectado"
        holder.txtEstadoDispositivo.setTextColor(
            holder.itemView.context.getColor(
                if (device.active) android.R.color.holo_green_dark else android.R.color.holo_red_dark
            )
        )

    }

    override fun getItemCount() = deviceList.size

    fun updateList(newList: List<deviceResponse>) {
        deviceList = newList
        notifyDataSetChanged()
    }
}
