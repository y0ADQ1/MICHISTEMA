package com.example.michistema.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.michistema.databinding.ItemDrinkerInfoBinding

data class DrinkerInfo(val label: String, val value: String)

class DrinkerInfoAdapter(
    public var drinkerInfoList: List<DrinkerInfo>
) : RecyclerView.Adapter<DrinkerInfoAdapter.DrinkerInfoViewHolder>() {

    class DrinkerInfoViewHolder(
        private val binding: ItemDrinkerInfoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(info: DrinkerInfo) {
            binding.etName.text = info.label
            binding.txtGetAgua.text = info.value
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrinkerInfoViewHolder {
        val binding = ItemDrinkerInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DrinkerInfoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DrinkerInfoViewHolder, position: Int) {
        val info = drinkerInfoList[position]
        holder.bind(info)
    }

    override fun getItemCount(): Int = drinkerInfoList.size

    fun updateData(newData: List<DrinkerInfo>) {
        drinkerInfoList = newData
        notifyDataSetChanged()
    }
}