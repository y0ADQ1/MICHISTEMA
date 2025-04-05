package com.example.michistema.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.michistema.R
import com.example.michistema.utils.PreferenceHelper
import com.example.michistema.utils.PreferenceHelper.get
import com.example.michistema.data.model.Request.UpdateUserRequest
import com.example.michistema.data.model.User
import com.example.michistema.data.network.ApiService
import com.example.michistema.ui.auth.LoginActivity
import com.example.michistema.ui.auth.RegisterActivity
import kotlinx.coroutines.launch
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnUpdateName: Button
    private lateinit var btnUpdateEmail: Button
    private lateinit var btnUpdatePassword: Button
    private lateinit var btnLogout: Button

    private lateinit var token: String
    private var userId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        etName = findViewById(R.id.et_nombre)
        etEmail = findViewById(R.id.et_correo)
        etPassword = findViewById(R.id.et_password)
        btnUpdateName = findViewById(R.id.btn_update_name)
        btnUpdateEmail = findViewById(R.id.btn_update_email)
        btnUpdatePassword = findViewById(R.id.btn_update_password)
        btnLogout = findViewById(R.id.btn_logout)

        val devices: Button = findViewById(R.id.mis_dispositivos)
        devices.setOnClickListener {
            Log.d("ProfileActivity", "Botón presionado, redirigiendo a AllDevicesUserActivity")

            // Crear el intent para AllDevicesUserActivity
            val intent = Intent(this, AllDevicesUserActivity::class.java)

            // Agregar el userId al intent
            intent.putExtra("userId", userId)

            // Iniciar la actividad
            startActivity(intent)
        }



        val preferences = PreferenceHelper.defaultPrefs(this)
        token = preferences["token", ""].toString()
        userId = preferences["userId", 0] as Int

        Log.d("ProfileActivity", "Token obtenido: '$token'")
        Log.d("ProfileActivity", "UserId obtenido: '$userId'")

        if (token.isEmpty() || userId.toString().isEmpty()) {
            Log.e("ProfileActivity", "No hay sesión activa. Redirigiendo a LoginActivity...")
            Toast.makeText(this, "Por favor, inicia sesión", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }


        btnUpdateName.setOnClickListener { updateUser("name", etName.text.toString()) }
        btnUpdateEmail.setOnClickListener { updateUser("email", etEmail.text.toString()) }
        btnUpdatePassword.setOnClickListener { updateUser("password", etPassword.text.toString()) }

        val btnBack = findViewById<Button>(R.id.btnBack)
        btnBack.setOnClickListener {
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
            finish()
        }
        btnLogout.setOnClickListener { logoutUser() }


    }


    private fun updateUser(field: String, value: String) {
        if (value.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa un valor válido", Toast.LENGTH_SHORT).show()
            return
        }

        val updateUserRequest = when (field) {
            "name" -> UpdateUserRequest(name = value, email = null, password = null)
            "email" -> UpdateUserRequest(name = null, email = value, password = null)
            "password" -> UpdateUserRequest(name = null, email = null, password = value)
            else -> return
        }

        lifecycleScope.launch {
            try {
                val apiService = ApiService.create()
                val response: Response<User> = apiService.updateUser(userId.toString(), updateUserRequest, "Bearer $token")

                if (response.isSuccessful) {
                    Toast.makeText(this@ProfileActivity, "Datos actualizados con éxito", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("ProfileActivity", "Error código: ${response.code()}, cuerpo: ${response.errorBody()?.string()}")
                    Toast.makeText(this@ProfileActivity, "Error al actualizar: ${response.message()}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ProfileActivity, "Error en la conexión: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun logoutUser() {
        val preferences = PreferenceHelper.defaultPrefs(this)
        preferences.edit().clear().apply()

        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
