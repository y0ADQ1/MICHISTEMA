package com.example.michistema.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.michistema.databinding.ActivityAddSandBoxBinding

class AddSandBoxActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddSandBoxBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSandBoxBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.npHours.minValue = 0
        binding.npHours.maxValue = 23
        binding.npMinutes.minValue = 0
        binding.npMinutes.maxValue = 59

        binding.btnSave.setOnClickListener {
            val sandBoxName = binding.etSandBoxName.text.toString()
            val hours = binding.npHours.value
            val minutes = binding.npMinutes.value

            finish()
        }
    }
}