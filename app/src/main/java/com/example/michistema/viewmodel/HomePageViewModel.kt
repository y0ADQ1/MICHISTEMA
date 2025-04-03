package com.example.michistema.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.michistema.data.network.ApiService
import com.example.michistema.data.model.Environment
import kotlinx.coroutines.launch

class HomePageViewModel(private val apiService: ApiService) : ViewModel() {
    private val _environments = MutableLiveData<List<Environment>>(emptyList())
    private var userId: Int = 0
    val environments: LiveData<List<Environment>> get() = _environments



    private val _navigateToAddEnvironment = MutableLiveData<Boolean>()
    val navigateToAddEnvironment: LiveData<Boolean> get() = _navigateToAddEnvironment


    // Cargar entornos desde la API sin token
    fun loadEnvironments(userId: Int) {
        viewModelScope.launch {
            try {
                val response = apiService.getEnvironmentsByUserId(userId)
                if (response.isSuccessful) {
                    _environments.value = response.body() ?: emptyList()
                    Log.d("HomePageViewModel", "Environments loaded: ${response.body()}")
                } else {
                    Log.e("HomePageViewModel", "Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("HomePageViewModel", "Exception: ${e.message}", e)
            }
        }
    }

    // Opcional: para agregar manualmente si aún lo necesitas
    fun addEnvironment(environmentName: String) {
        val currentList = _environments.value ?: emptyList()
        val newEnvironment = Environment(
            id = currentList.size + 1, // Temporal, la API debería asignar el ID
            name = environmentName,
            color = null,
            userId = 1, // Debería venir del usuario autenticado
            active = true,
            createdAt = "",
            updatedAt = ""
        )
        _environments.value = currentList + newEnvironment
    }

    fun onAddEnvironmentClicked() {
        _navigateToAddEnvironment.value = true
    }

    fun onNavigationHandled() {
        _navigateToAddEnvironment.value = false
    }
}