package com.example.michistema.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.michistema.data.model.Request.LoginRequest
import com.example.michistema.data.model.Response.LoginResponse
import com.example.michistema.data.network.ApiService
import com.example.michistema.databinding.LoginActivityBinding
import com.example.michistema.ui.components.SplashScreen
import com.example.michistema.ui.main.DashboardAdminActivity
import com.example.michistema.ui.main.HomePageActivity
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

        Log.d("LoginActivity", "Enviando petición de login con:")
        Log.d("LoginActivity", "Email: $email")
        Log.d("LoginActivity", "Password: $password")

        // Realiza la petición de login
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
                        val user = loginResponse.user
                        if (user == null || user.id == null) {
                            Toast.makeText(this@LoginActivity, "Datos de usuario incompletos", Toast.LENGTH_SHORT).show()
                            return@launch
                        }

                        saveUserSession(loginResponse.token, user.role_id, user.name, user.id)
                        navigateToRoleBasedActivity(user.role_id)
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



    private fun saveUserSession(token: String, role: Int, userName: String, userId: Int) {
        val preferences = PreferenceHelper.defaultPrefs(this)
        preferences["token"] = token
        preferences["role"] = role
        preferences["userName"] = userName
        preferences["userId"] = userId // Guarda el userId

        // Agregar un log para ver los valores almacenados
        Log.d("LoginActivity", "Token almacenado: $token")
        Log.d("LoginActivity", "Role almacenado: $role")
        Log.d("LoginActivity", "UserName almacenado: $userName")
        Log.d("LoginActivity", "UserId almacenado: $userId")
    }

    private fun navigateToRoleBasedActivity(role: Int) {
        val intent = when (role) {
            1 -> Intent(this, HomePageActivity::class.java) // Usuario normal
            2 -> Intent(this, DashboardAdminActivity::class.java) // Administrador
            else -> Intent(this, SplashScreen::class.java) // Si no se reconoce el rol, vuelve al splash
        }
        startActivity(intent)
        finish()
    }
}