package com.example.michistema.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.michistema.data.model.Request.LoginRequest
import com.example.michistema.data.network.ApiService
import kotlinx.coroutines.launch
class LoginViewModel : ViewModel() {

    private val apiService = ApiService.create()

    fun login(email: String, password: String) {
        val loginRequest = LoginRequest(email, password)

        viewModelScope.launch {
            try {
                val response = apiService.postLogin(loginRequest)
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                } else {
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

