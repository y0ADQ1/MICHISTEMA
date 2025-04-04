package com.example.michistema.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.michistema.databinding.ItemEnvironmentBinding
import com.example.michistema.data.model.Environment
import com.example.michistema.ui.main.EnvironmentDetailActivity

class EnvironmentAdapter(
    private var environments: List<Environment>,
    private val onClick: (Environment) -> Unit
) : RecyclerView.Adapter<EnvironmentAdapter.EnvironmentViewHolder>() {

    class EnvironmentViewHolder(private val binding: ItemEnvironmentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(environment: Environment, onClick: (Environment) -> Unit) {
            binding.textViewEnvironmentName.text = environment.name
            binding.root.setOnClickListener {
                // Aquí se llama al onClick que fue pasado como parámetro
                onClick(environment)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EnvironmentViewHolder {
        val binding = ItemEnvironmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EnvironmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EnvironmentViewHolder, position: Int) {
        holder.bind(environments[position], onClick)
    }

    override fun getItemCount(): Int = environments.size

    fun updateEnvironments(newEnvironments: List<Environment>) {
        environments = newEnvironments
        notifyDataSetChanged()
    }
}
