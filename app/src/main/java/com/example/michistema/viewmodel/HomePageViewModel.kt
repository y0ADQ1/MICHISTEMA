package com.example.michistema.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomePageViewModel : ViewModel() {

    private val _environments = MutableLiveData<List<String>>()
    val environments: LiveData<List<String>> get() = _environments

    private val _navigateToAddEnvironment = MutableLiveData<Boolean>()
    val navigateToAddEnvironment: LiveData<Boolean> get() = _navigateToAddEnvironment

    init {
        _environments.value = listOf(
            "Sala de estar 1 - Sala común",
            "Cocina 1 - Papu Cocina",
            "Sala de estar 2 - Papu Sala",
            "Sala de estar 2 - Papu Sala"
        )
    }

    fun onAddEnvironmentClicked() {
        _navigateToAddEnvironment.value = true
    }

    fun onNavigationHandled() {
        _navigateToAddEnvironment.value = false
    }

    fun addEnvironment(environment: String) {
        val currentList = _environments.value?.toMutableList() ?: mutableListOf()
        currentList.add(environment)
        _environments.value = currentList
    }
}