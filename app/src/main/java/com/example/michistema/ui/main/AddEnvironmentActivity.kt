package com.example.michistema.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.michistema.databinding.ActivityAddEnvironmentBinding
import com.example.michistema.ui.adapter.EnvironmentSpinnerAdapter
import com.example.michistema.viewmodel.AddEnvironmentViewModel
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.listener.ColorListener
import com.github.dhaval2404.colorpicker.model.ColorShape

class AddEnvironmentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEnvironmentBinding
    private lateinit var viewModel: AddEnvironmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEnvironmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(AddEnvironmentViewModel::class.java)

        val environments = listOf("Recámara", "Sala de estar", "Cuarto de lavado", "Patio", "Cocina")
        val adapter = EnvironmentSpinnerAdapter(this, environments)
        binding.spinnerEnvironments.adapter = adapter

        binding.spinnerEnvironments.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                viewModel.setSelectedEnvironment(environments[position])
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        })

        binding.etSecondaryName.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                viewModel.setSecondaryName(s.toString())
            }
        })

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnChooseColor.setOnClickListener {
            ColorPickerDialog
                .Builder(this)
                .setTitle("Selecciona un color")
                .setColorShape(ColorShape.CIRCLE)
                .setDefaultColor("#D680BA")
                .setColorListener(object : ColorListener {
                    override fun onColorSelected(color: Int, colorHex: String) {
                        viewModel.setSelectedColor(colorHex)
                        binding.btnChooseColor.setBackgroundColor(android.graphics.Color.parseColor(colorHex))
                    }
                })
                .build()
                .show()
        }

        binding.btnSaveEnvironment.setOnClickListener {
            val environment = viewModel.selectedEnvironment.value
            val secondaryName = viewModel.secondaryName.value
            val color = viewModel.selectedColor.value

            if (environment.isNullOrEmpty()) {
                android.widget.Toast.makeText(this, "Por favor, selecciona un entorno", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (secondaryName.isNullOrEmpty()) {
                android.widget.Toast.makeText(this, "Por favor, ingresa un nombre secundario", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (color.isNullOrEmpty()) {
                android.widget.Toast.makeText(this, "Por favor, selecciona un color", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newEnvironment = "$environment - $secondaryName"

            val resultIntent = Intent()
            resultIntent.putExtra("new_environment", newEnvironment)
            resultIntent.putExtra("environment_color", color)  // Enviar el color
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}
