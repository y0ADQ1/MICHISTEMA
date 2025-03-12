package com.example.michistema

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.michistema.databinding.ItemSandBoxBinding
import com.example.michistema.models.SandBoxItem

class SandBoxAdapter(
    private val items: List<SandBoxItem>,
    private val onClick: (SandBoxItem) -> Unit
) : RecyclerView.Adapter<SandBoxAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemSandBoxBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSandBoxBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        Log.d("SandBoxAdapter", "Binding item: ${item.name}")
        with(holder.binding) {
            nameTextView.text = item.name
            iconImageView.setImageResource(item.icon)
            viewMoreButton.setOnClickListener { onClick(item) }
        }
    }
    override fun getItemCount() = items.size
}