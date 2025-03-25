package com.example.michistema.ui.auth

import android.os.Bundle
import android.graphics.Color
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.michistema.databinding.ActivityHomePageBinding
import com.example.michistema.adapters.EnvirementAdapter
import com.example.michistema.models.Envirement
import android.content.Intent

class HomePageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomePageBinding
    private lateinit var envirementList: MutableList<Envirement>
    private lateinit var adapter: EnvirementAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupButtons()
    }

    private fun setupRecyclerView() {
        envirementList = mutableListOf(
            Envirement(1, "Entorno 1", Color.parseColor("#FFC0CB")),
            Envirement(2, "Entorno 2", Color.parseColor("#ADD8E6")),
            Envirement(3, "Entorno 3", Color.parseColor("#90EE90"))
        )

        adapter = EnvirementAdapter(envirementList) { envirement ->
            println("Seleccionado: ${envirement.name}")
        }

        binding.recyclerViewHomePage.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewHomePage.adapter = adapter
    }

    private fun setupButtons() {
        binding.btnMyEnvirement.setOnClickListener {
            val intent = Intent(this, AddEnvironmentActivity::class.java)
            startActivity(intent)
        }
    }
}
