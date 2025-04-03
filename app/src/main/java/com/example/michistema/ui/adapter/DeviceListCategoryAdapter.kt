package com.example.michistema.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.michistema.R

class DeviceListCategoryAdapter(
    private var deviceNames: List<String> = listOf(),
    private val onViewMoreClick: (String) -> Unit // Modificado para manejar el nombre del dispositivo
) : RecyclerView.Adapter<DeviceListCategoryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewName: TextView =
            itemView.findViewById(R.id.device_name) // Asegúrate de que coincida con tu XML
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_list_device, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val deviceName = deviceNames[position] // Usamos el nombre del dispositivo directamente
        holder.textViewName.text = deviceName

        holder.itemView.setOnClickListener {
            onViewMoreClick(deviceName) // Pasamos el nombre del dispositivo
        }
    }

    override fun getItemCount(): Int = deviceNames.size

    // Nueva función para actualizar la lista de dispositivos
    fun setDeviceNames(newNames: List<String>) {
        deviceNames = newNames
        notifyDataSetChanged()
    }
}
