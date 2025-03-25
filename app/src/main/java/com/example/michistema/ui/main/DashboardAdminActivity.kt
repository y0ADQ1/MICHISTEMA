package com.example.michistema.ui.main

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.michistema.R
import com.example.michistema.data.model.Request.deviceRequest
import com.example.michistema.data.model.Response.deviceResponse
import com.example.michistema.data.network.ApiService
import kotlinx.coroutines.launch
import retrofit2.Response

class DashboardAdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_admin)

        val etName = findViewById<EditText>(R.id.etName)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val etCode = findViewById<EditText>(R.id.etCode)
        val etConstant = findViewById<EditText>(R.id.etConstant)
        val cbActive = findViewById<CheckBox>(R.id.cbActive)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)

        btnSubmit.setOnClickListener {
            val name = etName.text.toString()
            val description = etDescription.text.toString().takeIf { it.isNotBlank() }
            val code = etCode.text.toString().takeIf { it.isNotBlank() }
            val constant = etConstant.text.toString().takeIf { it.isNotBlank() }?.toIntOrNull()
            val active = cbActive.isChecked // Boolean

            if (name.isEmpty()) {
                Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val deviceRequest = deviceRequest(name, description, code, constant, active)

            lifecycleScope.launch {
                try {
                    val response: Response<deviceResponse> = ApiService.create().postDevice(deviceRequest)

                    if (response.isSuccessful) {
                        Toast.makeText(this@DashboardAdminActivity, "Dispositivo creado con éxito", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@DashboardAdminActivity, "Error al crear el dispositivo", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@DashboardAdminActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
