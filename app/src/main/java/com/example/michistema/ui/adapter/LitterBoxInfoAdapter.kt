package com.example.michistema.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.michistema.databinding.ItemLitterBoxInfoBinding

class LitterBoxInfoAdapter(
    var litterBoxInfoList: List<LitterBoxInfo>
) : RecyclerView.Adapter<LitterBoxInfoAdapter.LitterBoxInfoViewHolder>() {

    class LitterBoxInfoViewHolder(
        private val binding: ItemLitterBoxInfoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(info: LitterBoxInfo) {
            binding.tvLabel.text = info.label
            binding.tvValue.text = info.value
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LitterBoxInfoViewHolder {
        val binding = ItemLitterBoxInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LitterBoxInfoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LitterBoxInfoViewHolder, position: Int) {
        val info = litterBoxInfoList[position]
        holder.bind(info)
    }

    override fun getItemCount(): Int = litterBoxInfoList.size

    fun updateData(newData: List<LitterBoxInfo>) {
        litterBoxInfoList = newData
        notifyDataSetChanged()
    }
}