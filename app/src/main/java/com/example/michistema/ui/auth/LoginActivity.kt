package com.example.michistema.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.michistema.data.model.Request.LoginRequest
import com.example.michistema.databinding.LoginActivityBinding
import com.example.michistema.data.network.ApiService
import com.example.michistema.data.model.Response.LoginResponse
import com.example.michistema.ui.components.SplashScreen
import com.example.michistema.ui.main.DashboardActivity
import com.example.michistema.ui.main.DashboardAdminActivity
import com.example.michistema.utils.PreferenceHelper
import com.example.michistema.utils.PreferenceHelper.get
import com.example.michistema.utils.PreferenceHelper.set
import kotlinx.coroutines.launch
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private val apiService by lazy { ApiService.create() }
    private lateinit var binding: LoginActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val preferences = PreferenceHelper.defaultPrefs(this)
        if (preferences["token", ""].contains(".")) {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnLogin.setOnClickListener {
            login()
        }

        binding.btnForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
        binding.tvLoginTitle3.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)

        }
    }

    private fun login() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese su correo y contraseña", Toast.LENGTH_SHORT).show()
            return
        }

        val loginRequest = LoginRequest(email, password)

        lifecycleScope.launch {
            try {
                val response: Response<LoginResponse> = apiService.postLogin(loginRequest)

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse == null) {
                        Toast.makeText(this@LoginActivity, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    if (loginResponse.status == 200) {
                        createSessionPreference(loginResponse.token, loginResponse.user.role_id)

                        navigateToRoleBasedActivity(loginResponse.user.role_id)
                    } else {
                        Toast.makeText(this@LoginActivity, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Error en la autenticación", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createSessionPreference(token: String, role: Int) {
        val preferences = PreferenceHelper.defaultPrefs(this)
        preferences["token"] = token
        preferences["role"] = role
    }

    private fun navigateToRoleBasedActivity(role: Int) {
        val intent = when (role) {
            1 -> Intent(this, DashboardActivity::class.java)
            2 -> Intent(this, DashboardAdminActivity::class.java)
            else -> Intent(this, SplashScreen::class.java)
        }
        startActivity(intent)
        finish()
    }
}
