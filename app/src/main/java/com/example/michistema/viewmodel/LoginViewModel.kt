package com.example.michistema.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.michistema.data.model.Request.LoginRequest
import com.example.michistema.data.network.ApiService
import kotlinx.coroutines.launch
class LoginViewModel : ViewModel() {

    private val apiService = ApiService.create() // Usar ApiService con el retrofit configurado

    fun login(email: String, password: String) {
        val loginRequest = LoginRequest(email, password)

        // Llamada asíncrona con corutinas
        viewModelScope.launch {
            try {
                val response = apiService.postLogin(loginRequest)
                if (response.isSuccessful) {
                    // La respuesta es exitosa
                    val loginResponse = response.body()
                    // Maneja el loginResponse (ej., guarda el token, navega a la siguiente pantalla)
                } else {
                    // Maneja el error (ej., muestra mensaje de error)
                }
            } catch (e: Exception) {
                // Maneja la excepción (ej., error de red)
                e.printStackTrace()
            }
        }
    }
}

