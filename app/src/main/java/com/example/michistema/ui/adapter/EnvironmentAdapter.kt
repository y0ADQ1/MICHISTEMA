package com.example.michistema.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.michistema.databinding.ItemEnvironmentBinding
import com.example.michistema.data.model.Environment

class EnvironmentAdapter(
    private var environments: List<Environment>,
    private val onClick: (Environment) -> Unit
) : RecyclerView.Adapter<EnvironmentAdapter.EnvironmentViewHolder>() {

    class EnvironmentViewHolder(private val binding: ItemEnvironmentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(environment: Environment, onClick: (Environment) -> Unit) {
            binding.textViewEnvironmentName.text = environment.name

            // Aplica el color de fondo al CardView completo
            environment.color?.let {
                try {
                    binding.root.setCardBackgroundColor(Color.parseColor(it))
                } catch (e: IllegalArgumentException) {
                    binding.root.setCardBackgroundColor(Color.GRAY) // Color por defecto si falla
                }
            }

            binding.root.setOnClickListener {
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
        notifyItemRangeChanged(0, environments.size)
    }
}
