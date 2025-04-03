package com.example.michistema.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class AddEnvironmentViewModel : ViewModel() {
    private val _selectedEnvironment = MutableLiveData<String>()
    val selectedEnvironment: LiveData<String> get() = _selectedEnvironment

    private val _secondaryName = MutableLiveData<String>()
    val secondaryName: LiveData<String> get() = _secondaryName

    private val _selectedColor = MutableLiveData<String>()
    val selectedColor: LiveData<String> get() = _selectedColor

    fun setSelectedEnvironment(environment: String) {
        _selectedEnvironment.value = environment
    }

    fun setSecondaryName(name: String) {
        _secondaryName.value = name
    }

    fun setSelectedColor(color: String) {
        _selectedColor.value = color
    }
}