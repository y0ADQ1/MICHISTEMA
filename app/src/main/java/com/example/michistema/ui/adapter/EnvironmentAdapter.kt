package com.example.michistema.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.example.michistema.R
import com.example.michistema.databinding.ItemEnvironmentBinding

class EnvironmentAdapter(
    private var environments: List<String>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<EnvironmentAdapter.EnvironmentViewHolder>() {

    class EnvironmentViewHolder(
        private val binding: ItemEnvironmentBinding,
        private val onItemClick: (String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(environment: String) {
            binding.tvRoomName.text = environment
            binding.root.setOnClickListener {
                val animation = AnimationUtils.loadAnimation(binding.root.context, R.anim.scale_animation)
                binding.root.startAnimation(animation)
                onItemClick(environment)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EnvironmentViewHolder {
        val binding = ItemEnvironmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EnvironmentViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: EnvironmentViewHolder, position: Int) {
        val environment = environments[position]
        holder.bind(environment)
    }

    override fun getItemCount(): Int = environments.size

    fun updateEnvironments(newEnvironments: List<String>) {
        environments = newEnvironments
        notifyDataSetChanged()
    }
}