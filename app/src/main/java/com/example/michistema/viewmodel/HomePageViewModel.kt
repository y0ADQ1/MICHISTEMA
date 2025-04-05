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
                    _environments.postValue(response.body() ?: emptyList())
                    Log.d("HomePageViewModel", "Environments loaded: ${response.body()?.size} items")
                } else {
                    Log.e("HomePageViewModel", "Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("HomePageViewModel", "Exception: ${e.message}", e)
            }
        }
    }

    // Crear un nuevo entorno en la API
    fun addEnvironment(name: String, description: String, userId: Int, token: String) {
        viewModelScope.launch {
            try {
                val request = CreateEnvironmentRequest(
                    name = name,
                    color = "#FF0000", // Example color, adjust as needed
                    active = true,     // Default to active, adjust as needed
                    userId = userId    // Pass the userId from the activity
                )
                val response = apiService.createEnvironment("Bearer $token", request)
                if (response.isSuccessful) {
                    val newEnvironment = response.body()?.environment // Extract the environment from the response
                    newEnvironment?.let {
                        val updatedList = _environments.value.orEmpty().toMutableList()
                        updatedList.add(it)
                        _environments.postValue(updatedList)
                    }
                    Log.d("HomePageViewModel", "Environment added: ${newEnvironment?.name}")
                } else {
                    Log.e("HomePageViewModel", "Failed to add environment: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("HomePageViewModel", "Exception adding environment: ${e.message}", e)
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
