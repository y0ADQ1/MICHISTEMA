package com.example.michistema.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.michistema.data.model.Environment
import com.example.michistema.data.model.Request.CreateEnvironmentRequest
import com.example.michistema.data.network.ApiService
import kotlinx.coroutines.launch

class HomePageViewModel(private val apiService: ApiService) : ViewModel() {

    private val _environments = MutableLiveData<List<Environment>>()
    val environments: LiveData<List<Environment>> get() = _environments

    private val _navigateToAddEnvironment = MutableLiveData<Boolean>()
    val navigateToAddEnvironment: LiveData<Boolean> get() = _navigateToAddEnvironment

    // Cargar entornos desde la API
    fun loadEnvironments(userId: Int, token: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getEnvironmentsByUserId(userId)
                if (response.isSuccessful) {
                    val envs = response.body() ?: emptyList()
                    Log.d("HomePageViewModel", "Entornos cargados: ${envs.size} items, Colores: ${envs.map { it.color }}")
                    _environments.postValue(envs)
                } else {
                    Log.e("HomePageViewModel", "Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("HomePageViewModel", "Excepción: ${e.message}", e)
            }
        }
    }

    // Crear un nuevo entorno en la API
    fun addEnvironment(name: String, description: String, userId: Int, token: String, color: String? = null) {
        viewModelScope.launch {
            try {
                val request = CreateEnvironmentRequest(
                    name = name,
                    color = color ?: "#FF0000", // Usa el color recibido o un valor por defecto
                    active = true,
                    userId = userId
                )
                Log.d("HomePageViewModel", "Enviando solicitud: name=$name, color=$color, userId=$userId")
                val response = apiService.createEnvironment("Bearer $token", request)
                if (response.isSuccessful) {
                    Log.d("HomePageViewModel", "Entorno creado: ${response.body()?.environment?.name}, Color: ${response.body()?.environment?.color}")
                    // Recarga la lista completa desde la API
                    loadEnvironments(userId, token)
                } else {
                    Log.e("HomePageViewModel", "Error al añadir entorno: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("HomePageViewModel", "Excepción al añadir entorno: ${e.message}", e)
            }
        }
    }

    fun onAddEnvironmentClicked() {
        _navigateToAddEnvironment.postValue(true)
    }

    fun onNavigationHandled() {
        _navigateToAddEnvironment.postValue(false)
    }
}