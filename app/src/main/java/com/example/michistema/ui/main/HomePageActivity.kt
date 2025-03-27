package com.example.michistema.ui.main

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.michistema.databinding.ActivityHomePageBinding
import com.example.michistema.ui.adapter.EnvironmentAdapter

class HomePageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomePageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val environments = listOf(
            "Sala de estar 1 - Sala común",
            "Cocina 1 - Papu Cocina",
            "Sala de estar 2 - Papu Sala",
            "Sala de estar 2 - Papu Sala"
        )

        val adapter = EnvironmentAdapter(environments) { environment ->
            Toast.makeText(this, "Clicked: $environment", Toast.LENGTH_SHORT).show()
        }
        binding.recyclerViewHomePage.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewHomePage.adapter = adapter
    }
}