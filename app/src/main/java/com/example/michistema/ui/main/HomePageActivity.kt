package com.example.michistema.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.michistema.data.network.ApiService
import com.example.michistema.data.network.NetworkClient
import com.example.michistema.databinding.ActivityHomePageBinding
import com.example.michistema.ui.adapter.EnvironmentAdapter
import com.example.michistema.utils.PreferenceHelper
import com.example.michistema.viewmodel.HomePageViewModel


class HomePageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomePageBinding
    private lateinit var viewModel: HomePageViewModel
    private lateinit var adapter: EnvironmentAdapter
    private lateinit var apiService: ApiService

    private lateinit var token: String
    private var userId: Int = 0

    private val addEnvironmentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.getStringExtra("new_environment")?.let {
                viewModel.addEnvironment(it, "Nueva Descripción", userId, token)
                loadEnvironmentsFromApi()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val preferences = PreferenceHelper.defaultPrefs(this)
        token = preferences.getString("token", "") ?: ""
        userId = preferences.getInt("userId", 0)

        // Inicializa ApiService
        apiService = NetworkClient.retrofit.create(ApiService::class.java)

        // Inicializa ViewModel con ApiService
        viewModel = ViewModelProvider(this, ViewModelFactory(apiService)).get(HomePageViewModel::class.java)

        adapter = EnvironmentAdapter(emptyList()) { environment ->
            val intent = Intent(this, EnvironmentDetailActivity::class.java).apply {
                putExtra("environment_name", environment.name)
                putExtra("environment_id", environment.id)
                putExtra("user_id", userId) // Enviar userId como Int al EnvironmentDetailActivity
            }
            startActivity(intent)
        }

        binding.recyclerViewHomePage.apply {
            layoutManager = LinearLayoutManager(this@HomePageActivity)
            adapter = this@HomePageActivity.adapter
        }

        viewModel.environments.observe(this) { environments ->
            adapter.updateEnvironments(environments)
            Log.d("HomePageActivity", "Environments updated: ${environments.size} items")
        }

        viewModel.navigateToAddEnvironment.observe(this) { navigate ->
            if (navigate) {
                val intent = Intent(this, AddEnvironmentActivity::class.java)
                addEnvironmentLauncher.launch(intent)
                viewModel.onNavigationHandled()
            }
        }

        // Single click listener for btnMyProfile
        binding.btnMyProfile.setOnClickListener {
            if (userId != 0 && token.isNotEmpty()) { // Check if userId and token are valid
                val intent = Intent(this, ProfileActivity::class.java).apply {
                    putExtra("user_id", userId) // Enviar userId como Int al ProfileActivity
                    putExtra("token", token)
                }
                startActivity(intent)
                Log.d("HomePageActivity", "Navigating to ProfileActivity with userId: $userId, token: $token")
            } else {
                Log.e("HomePageActivity", "Invalid userId or token: userId=$userId, token=$token")
            }
        }

        binding.btnAddEnvirement.setOnClickListener {
            viewModel.onAddEnvironmentClicked()
        }

        // Configuración de SwipeRefreshLayout
        binding.swipeRefreshLayout.setOnRefreshListener {
            loadEnvironmentsFromApi() // Recargar entornos desde la API
        }

        // Cargar entornos desde la API al iniciar
        loadEnvironmentsFromApi()
    }

    private fun loadEnvironmentsFromApi() {
        viewModel.loadEnvironments(userId, token)
    }
}

// Factory para pasar ApiService al ViewModel
class ViewModelFactory(private val apiService: ApiService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomePageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomePageViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
