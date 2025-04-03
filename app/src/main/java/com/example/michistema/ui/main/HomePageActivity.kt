package com.example.michistema.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import com.example.michistema.utils.PreferenceHelper.get
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
            val data = result.data
            val newEnvironment = data?.getStringExtra("new_environment")
            newEnvironment?.let {
                viewModel.addEnvironment(it) // Opcional si usas la API para agregar
                loadEnvironmentsFromApi() // Recarga desde la API
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val preferences = PreferenceHelper.defaultPrefs(this)
        token = preferences["token", ""].toString()
        userId = preferences["userId", 0] as Int


        // Inicializa ApiService
        apiService = NetworkClient.retrofit.create(ApiService::class.java)

        // Inicializa ViewModel con ApiService
        viewModel = ViewModelProvider(this, ViewModelFactory(apiService)).get(HomePageViewModel::class.java)

        adapter = EnvironmentAdapter(emptyList()) { environment ->
            Toast.makeText(this, "Clicked: ${environment.name}", Toast.LENGTH_SHORT).show()
        }

        binding.recyclerViewHomePage.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewHomePage.adapter = adapter

        viewModel.environments.observe(this) { environments ->
            adapter.updateEnvironments(environments)
            Log.d("HomePageActivity", "Environments updated: ${environments.size} items")
        }

        viewModel.navigateToAddEnvironment.observe(this) { navigate ->
            if (navigate == true) {
                val intent = Intent(this, AddEnvironmentActivity::class.java)
                addEnvironmentLauncher.launch(intent)
                viewModel.onNavigationHandled()
            }
        }

        binding.btnAddEnvirement.setOnClickListener {
            viewModel.onAddEnvironmentClicked()
        }

        binding.btnMyProfile.setOnClickListener {
            val preferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
            val userId = preferences.getString("userId", null)
            val token = preferences.getString("token", null)
            if (userId != null && token != null) {
                val intent = Intent(this, ProfileActivity::class.java)
                intent.putExtra("userId", userId)
                intent.putExtra("token", token)
                startActivity(intent)
                Log.d("HomePageActivity", "userId: $userId")
                Log.d("HomePageActivity", "token: $token")
            }
            startActivity(Intent(this, ProfileActivity::class.java))
        }


        // Cargar entornos desde la API al iniciar
        loadEnvironmentsFromApi()
    }

    private fun loadEnvironmentsFromApi() {
        viewModel.loadEnvironments(userId) // Sin token
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