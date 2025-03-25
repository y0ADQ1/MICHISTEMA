package com.example.michistema.ui.auth

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.michistema.R
import com.example.michistema.databinding.ActivityAddEnvironmentBinding
import com.example.michistema.viewmodel.AddEnvironmentViewModel

class AddEnvironmentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddEnvironmentBinding
    private val viewModel: AddEnvironmentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddEnvironmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val environments = resources.getStringArray(R.array.environments)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, environments)
        binding.spinnerEnvironments.adapter = adapter

        viewModel.selectedEnvironment.observe(this, Observer { environment ->

        })

        viewModel.secondaryName.observe(this, Observer { binding.etSecondaryName.setText(it)})

        binding.btnChooseColor.setOnClickListener {
            viewModel.setSelectedColor("#FF0000")
        }

        binding.btnSaveEnvironment.setOnClickListener {
            val selectedEnv = binding.spinnerEnvironments.selectedItem.toString()
            val secondaryName = binding.etSecondaryName.text.toString()
            viewModel.setSelectedEnvironment(selectedEnv)
            viewModel.setSecondaryName(secondaryName)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}