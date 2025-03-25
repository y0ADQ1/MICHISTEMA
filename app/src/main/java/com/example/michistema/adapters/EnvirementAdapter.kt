package com.example.michistema.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.michistema.databinding.ItemEnvirementBinding
import com.example.michistema.models.Envirement

class EnvirementAdapter(
    private val envirementList: List<Envirement>,
    private val onClick: (Envirement) -> Unit
) : RecyclerView.Adapter<EnvirementAdapter.EnvirementViewHolder>() {

    inner class EnvirementViewHolder(private val binding: ItemEnvirementBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(envirement: Envirement) {
            binding.tvEnvirement.text = envirement.name
            binding.root.setBackgroundColor(envirement.backgroundColor)
            binding.tvArrow.setOnClickListener { onClick(envirement) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EnvirementViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemEnvirementBinding.inflate(inflater, parent, false)
        return EnvirementViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EnvirementViewHolder, position: Int) {
        holder.bind(envirementList[position])
    }

    override fun getItemCount(): Int = envirementList.size
}
