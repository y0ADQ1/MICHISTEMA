package com.example.michistema.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.michistema.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Modificar el texto del TextView dinámicamente
        binding.textView.text = "¡Bienvenido de nuevo!"
    }
}
