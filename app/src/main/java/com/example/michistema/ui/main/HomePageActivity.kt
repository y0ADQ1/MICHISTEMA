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
            val newEnvironment = result.data?.getStringExtra("new_environment")
            val environmentColor = result.data?.getStringExtra("environment_color") // Recibir el color

            if (!newEnvironment.isNullOrEmpty() && !environmentColor.isNullOrEmpty()) {
                viewModel.addEnvironment(newEnvironment, "Nueva Descripción", userId, token)
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

        apiService = NetworkClient.retrofit.create(ApiService::class.java)

        viewModel = ViewModelProvider(this, ViewModelFactory(apiService)).get(HomePageViewModel::class.java)

        adapter = EnvironmentAdapter(emptyList()) { environment ->
            val intent = Intent(this, EnvironmentDetailActivity::class.java).apply {
                putExtra("environment_name", environment.name)
                putExtra("environment_id", environment.id)
                putExtra("user_id", userId)
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

        binding.btnMyProfile.setOnClickListener {
            if (userId != 0 && token.isNotEmpty()) {
                val intent = Intent(this, ProfileActivity::class.java).apply {
                    putExtra("user_id", userId)
                    putExtra("token", token)
                }
                startActivity(intent)
            }
        }

        binding.btnAddEnvirement.setOnClickListener {
            addEnvironmentLauncher.launch(Intent(this, AddEnvironmentActivity::class.java))
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            loadEnvironmentsFromApi()
        }

        loadEnvironmentsFromApi()
    }

    private fun loadEnvironmentsFromApi() {
        viewModel.loadEnvironments(userId, token)
    }
}

class ViewModelFactory(private val apiService: ApiService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomePageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomePageViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
