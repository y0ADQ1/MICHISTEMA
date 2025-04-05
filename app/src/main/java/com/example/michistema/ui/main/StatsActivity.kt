package com.example.michistema.ui.main

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.michistema.databinding.ActivityStatsBinding // Asegúrate de que el nombre del binding sea correcto
import com.example.michistema.R

class StatsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializar ViewBinding
        binding = ActivityStatsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar los insets para el diseño edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configurar el RecyclerView
        setupRecyclerView()

        // Configurar el botón "Volver"
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Configurar el botón "Actualizar"
        binding.btnUpStats.setOnClickListener {
            // Aquí puedes añadir la lógica para actualizar las estadísticas
            android.widget.Toast.makeText(this, "Estadísticas actualizadas", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        // Configurar el LayoutManager
        binding.rvStats.layoutManager = LinearLayoutManager(this)

        // Ejemplo de datos
        val statsList = listOf(
            StatItem("Usos del Arenero:", "15"),
            StatItem("Usos del Comedor:", "8"),
            StatItem("Usos del Bebedero:", "20"),
            StatItem("Humedad:", "45%"),
            StatItem("Gases:", "12 ppm")
        )

        // Configurar el Adapter
        binding.rvStats.adapter = StatsAdapter(statsList)
    }
}

// Data class para los ítems
data class StatItem(val label: String, val value: String)

// Adapter para el RecyclerView
class StatsAdapter(private val statsList: List<StatItem>) :
    androidx.recyclerview.widget.RecyclerView.Adapter<StatsAdapter.ViewHolder>() {

    class ViewHolder(itemView: android.view.View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val label: android.widget.TextView = itemView.findViewById(R.id.tv_statLabel)
        val value: android.widget.TextView = itemView.findViewById(R.id.tv_statValue)
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_stat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val stat = statsList[position]
        holder.label.text = stat.label
        holder.value.text = stat.value
    }

    override fun getItemCount(): Int = statsList.size
}